package com.lingxi.isi.controller;

import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.request.AuthRoleRequest;
import com.lingxi.isi.models.request.ResetPasswordRequest;
import com.lingxi.isi.models.request.UserListRequest;
import com.lingxi.isi.models.request.UserRequest;
import com.lingxi.isi.service.ISysRoleService;
import com.lingxi.isi.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/system/user")
public class UserController {

    private final ISysUserService userService;
    private final ISysRoleService roleService;

    public UserController(ISysUserService userService, ISysRoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    /**
     * 查询角色列表
     */
    @GetMapping("/roleOption")
    public R<List<Map<String, Object>>> getRoleOptions() {
        List<Map<String, Object>> roleOptions = roleService.getRoleOptions();
        return R.success(roleOptions);
    }

    /**
     * 查询用户列表
     */
    @GetMapping("/list")
    public R list(UserListRequest request) {
        return userService.listUser(request);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public R add(@RequestBody UserRequest request) {
        return userService.addUser(request);
    }

    /**
     * 修改用户
     */
    @PutMapping
    public R edit(@RequestBody UserRequest request) {
        return userService.updateUser(request);
    }

    /**
     * 根据 ID 获取用户详情
     */
    @GetMapping("/{userId}")
    public R getUserInfo(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    /**
     * 删除用户（支持批量）
     * @param userIds 用户 ID 列表，逗号分隔（如：1,2,3）
     * @return 操作结果
     */
    @DeleteMapping("/{userIds}")
    public R deleteUser(@PathVariable String userIds) {
        return userService.deleteUsers(userIds);
    }

    @PutMapping("/resetPwd")
    public R resetPwd(@RequestBody ResetPasswordRequest request) {
        return userService.resetPassword(request);
    }

    /**
     * 查询用户的角色列表
     */
    @GetMapping("/authRole/{userId}")
    public R getAuthRole(@PathVariable Long userId) {
        return userService.getUserRoles(userId);
    }

    /**
     * 保存授权角色
     */
    @PutMapping("/authRole")
    public R updateAuthRole(AuthRoleRequest request) {
        return userService.updateAuthRole(request);
    }

    @GetMapping("/downloadResume")
    public R downloadResume(@RequestParam String userId) {
        return userService.downloadResume(userId);
    }

    @GetMapping("/downloadInterviewReport")
    public R downloadInterviewReport(@RequestParam String userId) {
        return userService.downloadInterviewReport(userId);
    }

    @PostMapping("/sendEmail")
    public R sendEmail(@RequestParam String userId) {
        return userService.sendEmail(userId);
    }

}

