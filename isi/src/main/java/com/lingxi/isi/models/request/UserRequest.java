package com.lingxi.isi.models.request;

import lombok.Data;
import java.util.List;

/**
 * 用户新增/修改请求 DTO
 */
@Data
public class UserRequest {
    
    /**
     * 用户 ID（新增时不填，修改时必填）
     */
    private Long userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 用户真实名称
     */
    private String realName;
    
    /**
     * 用户密码（新增时必填，修改时可选，不填则不修改）
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
     * 角色 ID 列表
     */
    private List<Long> roleIds;
    
    /**
     * 备注
     */
    private String remark;
}
