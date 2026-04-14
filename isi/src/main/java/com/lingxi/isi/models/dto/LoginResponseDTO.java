package com.lingxi.isi.models.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    
    /**
     * 访问令牌
     */
    private String token;
    
    /**
     * 用户 ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String userName;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 头像 URL
     */
    private String avatar;
    
    /**
     * 角色列表
     */
    private String[] roles;
    
    /**
     * 权限列表
     */
    private String[] permissions;
    
    /**
     * 是否是默认修改密码（初始密码）
     */
    private Boolean isDefaultModifyPwd = false;
    
    /**
     * 密码是否已过期
     */
    private Boolean isPasswordExpired = false;
}
