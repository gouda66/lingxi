package com.lingxi.isi.models.dto;

import lombok.Data;
import java.util.List;

/**
 * 用户新增请求 DTO
 */
@Data
public class UserAddRequest {
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 用户昵称
     */
    private String nickName;
    
    /**
     * 用户密码
     */
    private String password;
    
    /**
     * 手机号码
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 用户性别
     */
    private String sex;
    
    /**
     * 用户状态（0-正常 1-禁用）
     */
    private String status;
    
    /**
     * 岗位 ID 列表
     */
    private List<Long> postIds;
    
    /**
     * 角色 ID 列表
     */
    private List<Long> roleIds;
    
    /**
     * 备注
     */
    private String remark;
}
