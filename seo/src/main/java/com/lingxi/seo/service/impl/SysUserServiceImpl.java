package com.lingxi.seo.service.impl;

import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.seo.common.context.BaseContext;
import com.lingxi.seo.common.result.R;
import com.lingxi.seo.common.util.ValidateCodeUtils;
import com.lingxi.seo.mapper.SysUserMapper;
import com.lingxi.seo.models.convert.UserConvert;
import com.lingxi.seo.models.dto.*;
import com.lingxi.seo.models.entity.SysUser;
import com.lingxi.seo.models.request.other.SysUserLoginRequest;
import com.lingxi.seo.models.request.other.SysUserRegisterRequest;
import com.lingxi.seo.service.ISysUserService;
import com.lingxi.seo.utils.PasswordSegmentUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    public SysUserServiceImpl(SysUserMapper sysUserMapper, ValidateCodeUtils validateCodeUtils) {
        this.sysUserMapper = sysUserMapper;
        this.validateCodeUtils = validateCodeUtils;
    }

    @Override
    public R<LoginResponseDTO> login(SysUserLoginRequest request) {
        // 验证验证码
        if (!validateCodeUtils.validateCaptcha(request.getUuid(), request.getCode())) {
            return R.error("验证码错误");
        }
        
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUserName, request.getUserName());
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
        String token = JWTUtil.createToken(claims, user.getUserName().getBytes());
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
    public R<Void> logout() {
        return R.success(null);
    }

    @Override
    public R<RegisterResponseDTO> register(SysUserRegisterRequest request) {

        if (!validateCodeUtils.validateCaptcha(request.getUuid(), request.getCode())) {
            return R.error("验证码错误");
        }

        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUserName, request.getUserName());
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
}
