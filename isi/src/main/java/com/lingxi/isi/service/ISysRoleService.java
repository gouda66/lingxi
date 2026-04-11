package com.lingxi.isi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.entity.SysRole;
import com.lingxi.isi.models.request.ChangeRoleStatusRequest;
import com.lingxi.isi.models.request.RoleListRequest;
import com.lingxi.isi.models.request.RoleRequest;

import java.util.List;
import java.util.Map;

public interface ISysRoleService extends IService<SysRole> {
    /**
     * 获取角色选项列表
     * @return 角色列表
     */
    List<Map<String, Object>> getRoleOptions();

    /**
     * 分页查询角色列表
     * @param request 查询请求参数
     * @return 角色列表
     */
    R<Map<String, Object>> listRoles(RoleListRequest request);

    /**
     * 根据 ID 查询角色
     * @param roleId 角色 ID
     * @return 角色信息
     */
    R<SysRole> getRoleById(Long roleId);

    /**
     * 新增角色
     * @param request 角色请求参数
     * @return 操作结果
     */
    R<Void> addRole(RoleRequest request);

    /**
     * 修改角色
     * @param request 角色请求参数
     * @return 操作结果
     */
    R<Void> updateRole(RoleRequest request);

    /**
     * 删除角色（支持批量）
     * @param roleIds 角色 ID 列表，逗号分隔
     * @return 操作结果
     */
    R<Void> deleteRoles(String roleIds);

    /**
     * 修改角色状态
     * @param request 角色状态请求参数
     * @return 操作结果
     */
    R<Void> changeStatus(ChangeRoleStatusRequest request);
}
