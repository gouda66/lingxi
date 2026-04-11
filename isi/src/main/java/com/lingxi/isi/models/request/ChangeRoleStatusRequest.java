package com.lingxi.isi.models.request;

import lombok.Data;

/**
 * 修改角色状态请求
 */
@Data
public class ChangeRoleStatusRequest {
    
    /**
     * 角色 ID
     */
    private Long roleId;
    
    /**
     * 角色状态（0-正常，1-禁用）
     */
    private String status;
}
