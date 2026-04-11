package com.lingxi.isi.models.request;

import lombok.Data;

/**
 * 角色列表查询请求
 */
@Data
public class RoleListRequest {
    
    /**
     * 当前页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页条数
     */
    private Integer pageSize = 10;
    
    /**
     * 角色 ID
     */
    private Long roleId;
    
    /**
     * 角色名称（模糊查询）
     */
    private String roleName;
    
    /**
     * 权限字符（模糊查询）
     */
    private String roleKey;
    
    /**
     * 角色状态（0-正常，1-禁用）
     */
    private String status;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
}
