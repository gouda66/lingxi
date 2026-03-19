package com.lingxi.isi.interfaces.rest;

import cn.hutool.jwt.JWTUtil;
import com.lingxi.isi.application.dto.CaptchaDTO;
import com.lingxi.isi.application.dto.LoginRequestDTO;
import com.lingxi.isi.application.dto.LoginResponseDTO;
import com.lingxi.isi.application.dto.UserDTO;
import com.lingxi.isi.application.service.UserApplicationService;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.infrastructure.filter.LoginCheckFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 用户管理接口
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;

    @GetMapping("/captchaImage")
    public R<CaptchaDTO> getCaptchaImage() {
        CaptchaDTO captcha = userApplicationService.getCaptchaImage();
        return R.success(captcha);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public R<UserDTO> register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(defaultValue = "USER") String role) {
        
        UserDTO user = userApplicationService.register(username, password, realName, email, phone, role);
        return R.success(user);
    }

    /**
     * 用户登录（支持验证码）
     */
    @PostMapping("/login")
    public R<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        String code = loginRequest.getCode();
        String uuid = loginRequest.getUuid();
        
        UserDTO user;
        // 如果提供了验证码参数，则使用验证码登录
        if (code != null && uuid != null) {
            user = userApplicationService.captchaLogin(username, code, uuid);
        } else {
            // 否则使用密码登录
            user = userApplicationService.login(username, password);
        }

        // 获取当前登录用户的 ID（从数据库查询出来的 user 对象中获取）
        Long currentUserId = user.getId(); // 假设 UserDTO 中有 getId() 方法
        
        Map<String, Object> claims = Map.of("userId", currentUserId);
        String token = JWTUtil.createToken(claims, username.getBytes());
        
        LoginResponseDTO response = new LoginResponseDTO(token, user);
        return R.success(response);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public R<UserDTO> getCurrentUser() {
        Long userId = LoginCheckFilter.getCurrentUserId();
        UserDTO user = userApplicationService.getUserById(userId);
        return R.success(user);
    }

    /**
     * 获取用户详细信息（用于登录后获取用户信息）
     */
    @GetMapping("/getInfo")
    public R<Map<String, Object>> getInfo() {
        Long userId = LoginCheckFilter.getCurrentUserId();
        UserDTO user = userApplicationService.getUserById(userId);
        
        Map<String, Object> result = new HashMap<>();
        
        // 构建用户信息对象
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getId());
        userInfo.put("userName", user.getUsername());
        userInfo.put("nickName", user.getRealName() != null ? user.getRealName() : user.getUsername());
        userInfo.put("avatar", ""); 
        userInfo.put("email", user.getEmail());
        userInfo.put("phone", user.getPhone());
        userInfo.put("sex", ""); 
        userInfo.put("dept", new HashMap<>());
        
        List<String> roles;
        if ("ADMIN".equals(user.getRole())) {
            roles = List.of("admin");
        } else if ("HR".equals(user.getRole())) {
            roles = List.of("hr");
        } else {
            roles = List.of("common");
        }
        
        // 设置权限信息（暂时返回空列表）
        List<String> permissions = List.of();

        boolean isDefaultModifyPwd = false;
        boolean isPasswordExpired = false;
        
        result.put("user", userInfo);
        result.put("roles", roles);
        result.put("permissions", permissions);
        result.put("isDefaultModifyPwd", isDefaultModifyPwd);
        result.put("isPasswordExpired", isPasswordExpired);
        
        return R.success(result);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public R<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userApplicationService.getUserById(id);
        return R.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public R<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone) {
        
        UserDTO user = userApplicationService.updateUser(id, realName, email, phone);
        return R.success(user);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public R<Void> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        
        Long userId = LoginCheckFilter.getCurrentUserId();
        userApplicationService.changePassword(userId, oldPassword, newPassword);
        return R.success();
    }

    /**
     * 分页查询用户（管理员）
     */
    @GetMapping("/page")
    public R<Page<UserDTO>> pageUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String role) {
        
        Page<UserDTO> pageResult = userApplicationService.pageUsers(page, pageSize, role);
        return R.success(pageResult);
    }

    /**
     * 获取企业的 HR 列表
     */
    @GetMapping("/hr/company/{companyId}")
    public R<List<UserDTO>> getHrByCompany(@PathVariable String companyId) {
        List<UserDTO> hrs = userApplicationService.getHrByCompanyId(companyId);
        return R.success(hrs);
    }

    /**
     * 禁用/启用用户（管理员）
     */
    @PutMapping("/{id}/status")
    public R<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        
        userApplicationService.updateUserStatus(id, status);
        return R.success();
    }

    /**
     * 删除用户（管理员）
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteUser(@PathVariable Long id) {
        userApplicationService.deleteUser(id);
        return R.success();
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        userApplicationService.logout();
        return R.success();
    }
}
