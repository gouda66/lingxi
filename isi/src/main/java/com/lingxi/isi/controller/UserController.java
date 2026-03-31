package com.lingxi.isi.controller;

import com.lingxi.isi.common.result.R;
import com.lingxi.isi.common.util.ValidateCodeUtils;
import com.lingxi.isi.models.dto.UserAddRequest;
import com.lingxi.isi.models.request.UserListRequest;
import com.lingxi.isi.service.ISysMenuService;
import com.lingxi.isi.service.ISysUserService;
import com.lingxi.isi.service.ISystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/system/user")
public class UserController {

    private final ISysUserService userService;

    public UserController(ISysUserService userService, ValidateCodeUtils validateCodeUtils, ISysMenuService menuService, ISystemConfigService systemConfigService) {
        this.userService = userService;
    }

    /**
     * 查询角色列表
     */
    @GetMapping("/roleOption")
    public R<List<Map<String, Object>>> getRoleOptions() {
        List<Map<String, Object>> roleOptions = Arrays.asList(
            Map.of(
                    "roleId", 1,
                    "roleName", "面试者",
                    "roleKey", "candidate",
                    "status", "0"
            ),
            Map.of(
                    "roleId", 2,
                    "roleName", "HR",
                    "roleKey", "hr",
                    "status", "0"
            ),
            Map.of(
                    "roleId", 3,
                    "roleName", "管理员",
                    "roleKey", "admin",
                    "status", "0"
            )
        );
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
    public R add(@RequestBody UserAddRequest request) {
        return userService.addUser(request);
    }

}
// ... existing code ...
