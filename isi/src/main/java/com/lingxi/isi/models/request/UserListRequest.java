package com.lingxi.isi.models.request;

import lombok.Data;

/**
 * 用户列表查询请求
 */
@Data
public class UserListRequest {
    
    /**
     * 当前页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页条数
     */
    private Integer pageSize = 10;
    
    /**
     * 用户名称（模糊查询）
     */
    private String userName;
    
    /**
     * 手机号码
     */
    private String phonenumber;
    
    /**
     * 用户状态（0-正常，1-禁用）
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
