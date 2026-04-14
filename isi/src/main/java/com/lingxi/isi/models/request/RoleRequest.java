package com.lingxi.isi.models.request;

import lombok.Data;

/**
 * 角色新增/修改请求
 */
@Data
public class RoleRequest {
    
    /**
     * 角色 ID（修改时必填）
     */
    private Long roleId;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 权限字符
     */
    private String roleKey;
    
    /**
     * 显示顺序
     */
    private Integer roleSort;
    
    /**
     * 角色状态（0-正常，1-禁用）
     */
    private String status;
    
    /**
     * 备注
     */
    private String remark;
}
