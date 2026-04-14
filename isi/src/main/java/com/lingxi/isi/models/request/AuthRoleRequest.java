package com.lingxi.isi.models.request;

import lombok.Data;

/**
 * 分配角色请求 DTO
 */
@Data
public class AuthRoleRequest {
    
    /**
     * 用户 ID
     */
    private Long userId;
    
    /**
     * 角色 ID 列表（逗号分隔的字符串）
     */
    private String roleIds;
}
