package com.lingxi.isi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.entity.SysRole;
import com.lingxi.isi.models.request.ChangeRoleStatusRequest;
import com.lingxi.isi.models.request.RoleListRequest;
import com.lingxi.isi.models.request.RoleRequest;
import com.lingxi.isi.service.ISysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 角色管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/system/role")
public class RoleController {

    private final ISysRoleService roleService;

    public RoleController(ISysRoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 查询角色列表
     */
    @GetMapping("/list")
    public R<Map<String, Object>> list(RoleListRequest request) {
        return roleService.listRoles(request);
    }

    /**
     * 根据角色 ID 查询详情
     */
    @GetMapping("/{roleId}")
    public R<SysRole> getInfo(@PathVariable Long roleId) {
        return roleService.getRoleById(roleId);
    }

    /**
     * 新增角色
     */
    @PostMapping
    public R add(@RequestBody RoleRequest request) {
        return roleService.addRole(request);
    }

    /**
     * 修改角色
     */
    @PutMapping
    public R edit(@RequestBody RoleRequest request) {
        return roleService.updateRole(request);
    }

    /**
     * 删除角色（支持批量）
     */
    @DeleteMapping("/{roleIds}")
    public R remove(@PathVariable String roleIds) {
        return roleService.deleteRoles(roleIds);
    }

    /**
     * 修改角色状态
     */
    @PutMapping("/changeStatus")
    public R changeStatus(@RequestBody ChangeRoleStatusRequest request) {
        return roleService.changeStatus(request);
    }
}
