package com.lingxi.isi.service.impl;

import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.isi.client.MailClient;
import com.lingxi.isi.common.context.BaseContext;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.common.util.ValidateCodeUtils;
import com.lingxi.isi.mapper.*;
import com.lingxi.isi.models.convert.UserConvert;
import com.lingxi.isi.models.dto.*;
import com.lingxi.isi.models.entity.*;
import com.lingxi.isi.models.request.*;
import com.lingxi.isi.service.ISysUserService;
import com.lingxi.isi.utils.PasswordSegmentUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.*;

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
    private final SysRoleMapper sysRoleMapper;
    private final ResumeMapper resumeMapper;
    private final InterviewReportMapper interviewReportMapper;
    private final MailClient mailClient;
    
    public SysUserServiceImpl(SysUserMapper sysUserMapper, ValidateCodeUtils validateCodeUtils, SysMenuMapper sysMenuMapper, SysRoleMapper sysRoleMapper, ResumeMapper resumeMapper, InterviewReportMapper interviewReportMapper, MailClient mailClient) {
        this.sysUserMapper = sysUserMapper;
        this.validateCodeUtils = validateCodeUtils;
        this.sysMenuMapper = sysMenuMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.resumeMapper = resumeMapper;
        this.interviewReportMapper = interviewReportMapper;
        this.mailClient = mailClient;
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
        queryWrapper.eq(SysUser::getStatus, 0);
        SysUser user = sysUserMapper.selectOne(queryWrapper);
        
        if (user == null) {
            return R.error("用户名或密码错误");
        }

        if (!PasswordSegmentUtils.validatePassword(user.getPassword(), request.getPassword())) {
            return R.error("用户名或密码错误");
        }
        
        if (user.getStatus() == 1) {
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
        
        // 设置用户上下文 (重要！)
        BaseContext.setCurrentId(user.getId());
        
        return R.success(response);
    }
    
    @Override
    public R<UserInfoDTO> getUserInfo(HttpServletRequest request) {
        Long userId = BaseContext.getCurrentId();
        
        // 检查用户是否登录
        if (userId == null) {
            return R.error("用户未登录");
        }
        
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
        Long userId = BaseContext.getCurrentId();
        
        // 检查用户是否登录
        if (userId == null) {
            return R.error("用户未登录");
        }
        
        // 查询用户信息获取角色 ID
        SysUser user = getById(userId);
        if (user == null || user.getDeleted() != null && user.getDeleted() == 1) {
            return R.success(new ArrayList<>());
        }
        
        // 从用户的 role 字段解析角色 ID（将数字如 123 拆分成 [1,2,3]）
        List<Long> roleIds = new ArrayList<>();
        if (user.getRole() != null) {
            String roleStr = String.valueOf(user.getRole());
            for (int i = 0; i < roleStr.length(); i++) {
                try {
                    Long roleId = Long.parseLong(String.valueOf(roleStr.charAt(i)));
                    roleIds.add(roleId);
                } catch (NumberFormatException e) {
                    log.warn("角色 ID 解析失败：{}", roleStr.charAt(i));
                }
            }
        }
        
        // 如果没有角色，返回空列表
        if (roleIds.isEmpty()) {
            return R.success(new ArrayList<>());
        }
        
        // 查询该用户所有角色的菜单权限（去重）
        Set<SysMenu> menuSet = new HashSet<>();
        for (Long roleId : roleIds) {
            List<SysMenu> menus = sysMenuMapper.selectMenuIdsByRoleId(roleId);
            if (menus != null) {
                menuSet.addAll(menus);
            }
        }
        
        // 转换为列表并按父级 ID 和排序号排序
        List<SysMenu> menus = new java.util.ArrayList<>(menuSet);
        menus.sort(java.util.Comparator.comparing(SysMenu::getParentId)
            .thenComparing(SysMenu::getOrderNum));
        
        // 使用 SysMenu 实体类的静态方法构建菜单树（充血模型）
        List<MenuDTO> menuTree = SysMenu.buildMenuTree(menus, 0L);
        
        return R.success(menuTree);
    }
    
    @Override
    public R<Void> logout() {
        BaseContext.remove();
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

    @Override
    public R listUser(UserListRequest request) {
        // 获取当前登录用户ID
        Long currentUserId = BaseContext.getCurrentId();

        // 构建查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getDeleted, 0);

        // 检查当前登录用户是否只有HR角色(role=2)
        boolean isOnlyHR = false;
        if (currentUserId != null) {
            SysUser currentUser = getById(currentUserId);
            if (currentUser != null && currentUser.getRole() != null) {
                String roleStr = String.valueOf(currentUser.getRole());
                // 解析角色：只有一位且为2
                isOnlyHR = roleStr.length() == 1 && "2".equals(roleStr);
            }
        }

        // 如果当前用户是纯HR，只查询面试者(role=1)
        if (isOnlyHR) {
            queryWrapper.eq(SysUser::getRole, 1);
            log.info("HR用户查询，仅返回面试者列表");
        }

        // 添加查询条件
        if (StringUtils.hasText(request.getUserName())) {
            queryWrapper.like(SysUser::getUserName, request.getUserName());
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
    public R addUser(UserRequest request) {
        // 1. 校验用户名是否已存在
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUserName, request.getUserName());
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
        newUser.initFromAdd(request);

        // 4. 保存用户
        boolean saved = this.save(newUser);

        if (saved) {
            return R.success("新增成功");
        } else {
            return R.error("新增失败");
        }
    }

    @Override
    public R<Void> updateUser(UserRequest request) {
        // 1. 校验用户是否存在
        SysUser existUser = getById(request.getUserId());
        if (existUser == null) {
            return R.error("用户不存在");
        }

        if (existUser.getDeleted() != null && existUser.getDeleted() == 1) {
            return R.error("用户不存在");
        }

        // 2. 校验用户名是否已被其他用户使用
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUserName, request.getUserName());
        queryWrapper.ne(SysUser::getId, request.getUserId());
        queryWrapper.eq(SysUser::getDeleted, 0);
        SysUser duplicateUser = this.getOne(queryWrapper);

        if (duplicateUser != null) {
            return R.error("用户名已存在");
        }

        // 3. 更新用户信息
        existUser.setUserName(request.getUserName());
        existUser.setRealName(request.getRealName());
        
        // 如果提供了新密码则更新密码
        if (StringUtils.hasText(request.getPassword())) {
            existUser.setPassword(PasswordSegmentUtils.encrypt(request.getPassword()));
        }
        
        existUser.setPhone(request.getPhone());
        existUser.setEmail(request.getEmail());
        existUser.setSex(Integer.parseInt(request.getSex() != null ? request.getSex() : "0"));
        existUser.setStatus(Integer.parseInt(request.getStatus() != null ? request.getStatus() : "0"));
        existUser.setUpdatedAt(LocalDateTime.now());

        // 4. 处理角色（将角色 ID 数组拼接成字符串）
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            StringBuilder roleBuilder = new StringBuilder();
            for (Long roleId : request.getRoleIds()) {
                roleBuilder.append(roleId);
            }
            try {
                existUser.setRole(Integer.parseInt(roleBuilder.toString()));
            } catch (NumberFormatException e) {
                log.warn("角色 ID 拼接后超出 Integer 范围，忽略角色更新", e);
            }
        }

        // 5. 更新用户
        boolean updated = updateById(existUser);
        return updated ? R.success(null) : R.error("修改失败");
    }

    @Override
    public R<UserDetailDTO> getUserById(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }

        // 检查是否已删除
        if (user.getDeleted() != null && user.getDeleted() == 1) {
            return R.error("用户不存在");
        }

        // 转换为 DTO
        UserDetailDTO userDetailDTO = UserConvert.INSTANCE.convertToUserDetailDTO(user);
        return R.success(userDetailDTO);
    }

    @Override
    public R<Void> deleteUsers(String userIds) {
        if (userIds == null || userIds.trim().isEmpty()) {
            return R.error("用户 ID 不能为空");
        }

        // 解析用户 ID 列表（逗号分隔）
        String[] idArray = userIds.split(",");
        
        for (String idStr : idArray) {
            try {
                Long userId = Long.parseLong(idStr.trim());
                
                // 查询并验证用户
                SysUser user = getById(userId);
                if (user == null) {
                    return R.error("用户不存在：" + userId);
                }

                if (user.getDeleted() != null && user.getDeleted() == 1) {
                    return R.error("用户不存在：" + userId);
                }
                
                // 逻辑删除
                LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SysUser::getId, userId);
                int delete = sysUserMapper.delete(queryWrapper);
                if (delete <= 0) {
                    return R.error("删除失败：" + userId);
                }
            } catch (NumberFormatException e) {
                return R.error("无效的用户 ID 格式：" + idStr);
            }
        }

        return R.success(null);
    }

    @Override
    public R<Void> resetPassword(ResetPasswordRequest request) {
        // 校验参数
        if (request.getUserId() == null) {
            return R.error("用户 ID 不能为空");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return R.error("新密码不能为空");
        }

        // 查询用户
        SysUser user = getById(request.getUserId());
        if (user == null) {
            return R.error("用户不存在");
        }

        // 检查是否已删除
        if (user.getDeleted() != null && user.getDeleted() == 1) {
            return R.error("用户不存在");
        }

        // 加密并更新密码
        user.setPassword(PasswordSegmentUtils.encrypt(request.getPassword()));
        updateById(user);

        return R.success(null);
    }

    @Override
    public R<AuthRoleDTO> getUserRoles(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }

        if (user.getDeleted() != null && user.getDeleted() == 1) {
            return R.error("用户不存在");
        }

        // 构建用户信息
        UserDetailDTO userDetailDTO = UserConvert.INSTANCE.convertToUserDetailDTO(user);

        // 查询所有角色
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getStatus, "0");
        List<SysRole> allRoles = sysRoleMapper.selectList(queryWrapper);

        // 解析用户已有的角色 ID（将 role 字段拆分成单独的角色 ID）
        List<Long> ownedRoleIds = new ArrayList<>();
        if (user.getRole() != null) {
            String roleStr = String.valueOf(user.getRole());
            for (int i = 0; i < roleStr.length(); i++) {
                try {
                    Long roleId = Long.parseLong(String.valueOf(roleStr.charAt(i)));
                    ownedRoleIds.add(roleId);
                } catch (NumberFormatException e) {
                    // 忽略无效数字
                }
            }
        }

        // 设置选中状态
        for (SysRole role : allRoles) {
            role.setFlag(ownedRoleIds.contains(role.getRoleId()));
        }

        AuthRoleDTO authRoleDTO = new AuthRoleDTO(userDetailDTO, allRoles);
        return R.success(authRoleDTO);
    }

    @Override
    public R<Void> updateAuthRole(AuthRoleRequest request) {
        if (request.getUserId() == null) {
            return R.error("用户 ID 不能为空");
        }

        if (request.getRoleIds() == null || request.getRoleIds().trim().isEmpty()) {
            return R.error("角色 ID 不能为空");
        }

        // 查询用户
        SysUser user = getById(request.getUserId());
        if (user == null) {
            return R.error("用户不存在");
        }

        if (user.getDeleted() != null && user.getDeleted() == 1) {
            return R.error("用户不存在");
        }

        // 解析角色 ID（逗号分隔，如 "1,3"）
        String[] roleIdArray = request.getRoleIds().split(",");
        if (roleIdArray.length == 0) {
            return R.error("角色 ID 不能为空");
        }

        // 验证并拼接角色 ID
        StringBuilder roleBuilder = new StringBuilder();
        for (String roleIdStr : roleIdArray) {
            try {
                Long roleId = Long.parseLong(roleIdStr.trim());
                
                // 验证角色是否存在
                SysRole role = sysRoleMapper.selectById(roleId);
                if (role == null) {
                    return R.error("无效的角色 ID: " + roleId);
                }
                
                roleBuilder.append(roleId);
            } catch (NumberFormatException e) {
                return R.error("无效的角色 ID 格式：" + roleIdStr);
            }
        }

        // 保存为整数（如 "13" -> 13）
        Integer roleValue = Integer.parseInt(roleBuilder.toString());
        user.setRole(roleValue);
        updateById(user);

        return R.success(null);
    }

    @Override
    public R downloadResume(String userId) {
        Long selectUserId = Long.parseLong(userId);
        LambdaQueryWrapper<Resume> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Resume::getUserId, selectUserId);
        queryWrapper.orderByDesc(Resume::getCreatedAt);
        queryWrapper.last("LIMIT 1");
        Resume latestResume = resumeMapper.selectOne(queryWrapper);
        if (latestResume == null) {
            return R.error("用户没有上传过简历");
        }
        return R.success(latestResume.getOriginalFileUrl());
    }

    @Override
    public R downloadInterviewReport(String userId) {
        Long selectUserId = Long.parseLong(userId);
        LambdaQueryWrapper<InterviewReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InterviewReport::getCandidateId, selectUserId);
        queryWrapper.orderByDesc(InterviewReport::getGeneratedAt);
        queryWrapper.last("LIMIT 1");
        InterviewReport report = interviewReportMapper.selectOne(queryWrapper);
        Map<String, Object> result = new HashMap<>();
        if (report != null) {
            result.put("reportId", report.getId());
            result.put("report", report);
        }
        result.put("reportId", 1L);
        result.put("report", new InterviewReport());
        return R.success(result);
    }

    /**
     * 发送邮件
     */
    @Override
    public R sendEmail(String userId) {
        try {
            // 查询用户信息
            SysUser sysUser = getById(Long.parseLong(userId));
            if (sysUser == null) {
                return R.error("用户不存在");
            }

            if (!StringUtils.hasText(sysUser.getEmail())) {
                return R.error("用户未配置邮箱");
            }

            // 构建邮件发送指令
            String emailCommand = String.format(
                "请给 %s 发送邮件，邮件：%s, 主题：%s，内容：%s",
                sysUser.getUserName(),
                sysUser.getEmail(),
                "恭喜你面试通过",
                "尊敬的"+sysUser.getUserName()+(sysUser.getSex() == 0 ? "女士" : "先生")+"：\n" +
                        "\n" +
                        "恭喜您！您已经通过了我们的面试。\n" +
                        "\n" +
                        "经过综合评估，我们认为您的专业能力与岗位要求高度匹配，诚挚邀请您加入我们的团队。\n" +
                        "\n" +
                        "后续流程：\n" +
                        "1. 我们将在3个工作日内向您发送正式录用通知书（Offer）\n" +
                        "2. 请您在收到Offer后仔细查阅并确认\n" +
                        "3. 如有任何疑问，可随时联系我们\n" +
                        "\n" +
                        "期待与您共创美好未来！\n" +
                        "\n" +
                        "【灵曦系统】"
            );
            emailCommand += "不用向我确认了，直接发送邮件就行。";

            // 调用 MCP 邮件客户端
            Flux<String> response = mailClient.sendMail(emailCommand);

            // 收集响应结果
            StringBuilder result = new StringBuilder();
            response.doOnNext(result::append).blockLast();

            log.info("MCP邮件发送完成，用户ID: {}, 结果: {}", Long.parseLong(userId), result);

            return R.success("邮件发送成功");
        } catch (Exception e) {
            log.error("发送邮件失败", e);
            return R.error("邮件发送失败: " + e.getMessage());
        }
    }


}

