package com.lingxi.isi.service.impl;

import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.isi.common.context.BaseContext;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.common.util.ValidateCodeUtils;
import com.lingxi.isi.mapper.SysMenuMapper;
import com.lingxi.isi.mapper.SysUserMapper;
import com.lingxi.isi.models.convert.MenuConvert;
import com.lingxi.isi.models.convert.UserConvert;
import com.lingxi.isi.models.dto.*;
import com.lingxi.isi.models.entity.SysMenu;
import com.lingxi.isi.models.entity.SysUser;
import com.lingxi.isi.models.request.UserListRequest;
import com.lingxi.isi.models.request.other.SysUserLoginRequest;
import com.lingxi.isi.models.request.other.SysUserRegisterRequest;
import com.lingxi.isi.service.ISysUserService;
import com.lingxi.isi.utils.PasswordSegmentUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
    
    private final SysUserMapper sysUserMapper;
    private final ValidateCodeUtils validateCodeUtils;
    private final SysMenuMapper sysMenuMapper;
    
    public SysUserServiceImpl(SysUserMapper sysUserMapper, ValidateCodeUtils validateCodeUtils, SysMenuMapper sysMenuMapper) {
        this.sysUserMapper = sysUserMapper;
        this.validateCodeUtils = validateCodeUtils;
        this.sysMenuMapper = sysMenuMapper;
    }

    @Override
    public R<LoginResponseDTO> login(SysUserLoginRequest request) {
        // 验证验证码
        if (!validateCodeUtils.validateCaptcha(request.getUuid(), request.getCode())) {
            return R.error("验证码错误");
        }
        
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, request.getUsername());
        queryWrapper.eq(SysUser::getDeleted, 0);
        SysUser user = sysUserMapper.selectOne(queryWrapper);
        
        if (user == null) {
            return R.error("用户名或密码错误");
        }

        if (!PasswordSegmentUtils.validatePassword(user.getPassword(), request.getPassword())) {
            return R.error("用户名或密码错误");
        }
        
        if (user.getStatus() == 0) {
            return R.error("账号已被禁用");
        }
        
        // 使用 Convert 构建登录响应
        LoginResponseDTO response = UserConvert.INSTANCE.convertToLoginResponseDTO(user);
        
        // 手动设置角色和权限（因为 MapStruct 无法直接映射常量数组）
        response.setRoles(new String[]{"admin", "common"});
        response.setPermissions(new String[]{"*:*:*"});

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        String token = JWTUtil.createToken(claims, user.getUsername().getBytes());
        response.setToken(token);
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        updateById(user);
        
        return R.success(response);
    }
    
    @Override
    public R<UserInfoDTO> getUserInfo(HttpServletRequest request) {
        Long userId = BaseContext.getCurrentId();
        
        SysUser user = getById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }
        
        // 使用 Convert 转换用户信息
        UserDetailDTO userDetail = UserConvert.INSTANCE.convertToUserDetailDTO(user);
        
        // 手动设置 sex 字段（SysUser 中没有 sex 字段）
        userDetail.setSex(0); // 默认设置为未知
        
        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setUser(userDetail);
        
        // 设置角色列表
        List<String> roles = Arrays.asList("admin", "common");
        userInfo.setRoles(roles);
        
        // 设置权限列表
        List<String> permissions = Arrays.asList("*:*:*");
        userInfo.setPermissions(permissions);
        
        // 初始密码判断（假设第一次登录需要修改密码）
        userInfo.setIsDefaultModifyPwd(false);
        userInfo.setIsPasswordExpired(false);
        
        return R.success(userInfo);
    }
    
    @Override
    public R<List<MenuDTO>> getRouters() {
        // TODO: 从 Token 或 Session 中获取当前用户的角色 ID
        // 这里暂时用管理员角色（ID=1）作为示例
        Long roleId = BaseContext.getCurrentId();
        
        // 获取该角色的菜单权限
        List<SysMenu> menus = sysMenuMapper.selectMenuIdsByRoleId(roleId);
        
        // 使用 MenuConvert 构建菜单树
        List<MenuDTO> menuTree = buildMenuTree(menus, 0L);
        
        return R.success(menuTree);
    }
    
    /**
     * 构建菜单树（委托给 MenuConvert）
     */
    private List<MenuDTO> buildMenuTree(List<SysMenu> menus, Long parentId) {
        return menus.stream()
            .filter(menu -> menu.getParentId().equals(parentId))
            .map(MenuConvert.INSTANCE::convertToDTO)
            .peek(dto -> dto.setChildren(buildMenuTree(menus, dto.getId())))
            .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public R<Void> logout() {
        return R.success(null);
    }

    @Override
    public R<RegisterResponseDTO> register(SysUserRegisterRequest request) {

        if (!validateCodeUtils.validateCaptcha(request.getUuid(), request.getCode())) {
            return R.error("验证码错误");
        }

        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, request.getUsername());
        queryWrapper.eq(SysUser::getDeleted, 0);
        SysUser existUser = sysUserMapper.selectOne(queryWrapper);

        if (existUser != null) {
            return R.error("用户名已存在");
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysUser::getEmail, request.getEmail());
            queryWrapper.eq(SysUser::getDeleted, 0);
            existUser = sysUserMapper.selectOne(queryWrapper);

            if (existUser != null) {
                return R.error("邮箱已被注册");
            }
        }

        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysUser::getPhone, request.getPhone());
            queryWrapper.eq(SysUser::getDeleted, 0);
            existUser = sysUserMapper.selectOne(queryWrapper);

            if (existUser != null) {
                return R.error("手机号已被注册");
            }
        }

        SysUser newUser = new SysUser();
        newUser.initFromRegister(request);

        boolean saved = save(newUser);

        if (saved) {
            // 使用 Convert 转换为响应 DTO
            RegisterResponseDTO response = UserConvert.INSTANCE.convertToRegisterResponseDTO(newUser);
            return R.success(response);
        } else {
            return R.error("注册失败");
        }
    }

    @Override
    public R listUser(UserListRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getDeleted, 0);
        
        // 添加查询条件
        if (StringUtils.hasText(request.getUserName())) {
            queryWrapper.like(SysUser::getUsername, request.getUserName());
        }
        if (StringUtils.hasText(request.getPhonenumber())) {
            queryWrapper.like(SysUser::getPhone, request.getPhonenumber());
        }
        if (StringUtils.hasText(request.getStatus())) {
            queryWrapper.eq(SysUser::getStatus, request.getStatus());
        }
        
        // 分页查询
        Page<SysUser> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<SysUser> resultPage = this.page(page, queryWrapper);
        
        // 转换为 DTO
        List<UserDetailDTO> userDetailDTOS = resultPage.getRecords().stream()
                .map(UserConvert.INSTANCE::convertToUserDetailDTO)
                .toList();
        
        return R.success(Map.of(
                "rows", userDetailDTOS,
                "total", resultPage.getTotal()
        ));
    }

    @Override
    public R addUser(UserAddRequest request) {
        // 1. 校验用户名是否已存在
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, request.getUserName());
        queryWrapper.eq(SysUser::getDeleted, 0);
        SysUser existUser = this.getOne(queryWrapper);
        if (existUser != null) {
            return R.error("用户名已存在");
        }

        // 2. 校验手机号是否已存在
        if (StringUtils.hasText(request.getPhone())) {
            queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysUser::getPhone, request.getPhone());
            queryWrapper.eq(SysUser::getDeleted, 0);
            existUser = this.getOne(queryWrapper);
            if (existUser != null) {
                return R.error("手机号已被注册");
            }
        }

        // 3. 创建新用户
        SysUser newUser = new SysUser();
        newUser.initFromAdd(request, newUser);

        // 4. 保存用户
        boolean saved = this.save(newUser);

        if (saved) {
            return R.success("新增成功");
        } else {
            return R.error("新增失败");
        }
    }
}
