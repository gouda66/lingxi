package com.lingxi.isi.interfaces.rest;

import com.lingxi.isi.application.service.UserApplicationService;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.domain.model.entity.User;
import com.lingxi.isi.infrastructure.filter.LoginCheckFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理接口
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public R<User> register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(defaultValue = "USER") String role) {
        
        User user = userApplicationService.register(username, password, realName, email, phone, role);
        return R.success(user);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R<User> login(
            @RequestParam String username,
            @RequestParam String password) {
        
        User user = userApplicationService.login(username, password);
        // 返回用户信息（实际项目中应该返回 Token）
        return R.success(user);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public R<User> getCurrentUser() {
        Long userId = LoginCheckFilter.getCurrentUserId();
        User user = userApplicationService.getUserById(userId);
        return R.success(user);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public R<User> getUserById(@PathVariable Long id) {
        User user = userApplicationService.getUserById(id);
        return R.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public R<User> updateUser(
            @PathVariable Long id,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone) {
        
        User user = userApplicationService.updateUser(id, realName, email, phone);
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
    public R<Page<User>> pageUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String role) {
        
        Page<User> pageResult = userApplicationService.pageUsers(page, pageSize, role);
        return R.success(pageResult);
    }

    /**
     * 获取企业的 HR 列表
     */
    @GetMapping("/hr/company/{companyId}")
    public R<List<User>> getHrByCompany(@PathVariable String companyId) {
        List<User> hrs = userApplicationService.getHrByCompanyId(companyId);
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
}
