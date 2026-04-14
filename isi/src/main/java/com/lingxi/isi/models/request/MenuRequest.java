package com.lingxi.isi.models.request;

import lombok.Data;

/**
 * 菜单查询请求 DTO
 */
@Data
public class MenuRequest {
    
    /**
     * 菜单名称（模糊查询）
     */
    private String menuName;
    
    /**
     * 菜单状态（0-正常，1-停用）
     */
    private String status;
}
