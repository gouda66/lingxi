package com.lingxi.isi.controller;

import com.lingxi.isi.common.result.R;
import com.lingxi.isi.service.ISystemConfigService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/system/user")
public class SystemConfigController {

    private final ISystemConfigService systemConfigService;

    public SystemConfigController(ISystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }
    
    /**
     * 根据配置键获取配置值
     */
    @GetMapping("/getConfigKey/{configKey}")
    public R getConfigKey(@PathVariable String configKey) {
        return systemConfigService.getConfigKey(configKey);
    }

    /**
     * 查询用户列表
     */
    @GetMapping("/list")
    public R list(HttpServletRequest request) {
        return systemConfigService.listUser(request);
    }

    /**
     * 查询用户详细
     */
    @GetMapping("/{userId}")
    public R getUser(@PathVariable Long userId) {
        return systemConfigService.getUser(userId);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public R add(@RequestBody Map<String, Object> data) {
        return systemConfigService.addUser(data);
    }

    /**
     * 修改用户
     */
    @PutMapping
    public R update(@RequestBody Map<String, Object> data) {
        return systemConfigService.updateUser(data);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userIds}")
    public R delete(@PathVariable String userIds) {
        return systemConfigService.deleteUser(userIds);
    }

    /**
     * 用户密码重置
     */
    @PutMapping("/resetPwd")
    public R resetPwd(@RequestBody Map<String, Object> data) {
        return systemConfigService.resetUserPwd(data);
    }

    /**
     * 用户状态修改
     */
    @PutMapping("/changeStatus")
    public R changeStatus(@RequestBody Map<String, Object> data) {
        return systemConfigService.changeUserStatus(data);
    }

    /**
     * 查询部门下拉树结构
     */
    @GetMapping("/deptTree")
    public R deptTree() {
        return systemConfigService.getDeptTree();
    }
}
