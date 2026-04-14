package com.lingxi.seo.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户信息响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    
    /**
     * 用户信息
     */
    private UserDetailDTO user;
    
    /**
     * 角色列表
     */
    private List<String> roles;
    
    /**
     * 权限列表
     */
    private List<String> permissions;
    
    /**
     * 是否是默认修改密码（初始密码）
     */
    private Boolean isDefaultModifyPwd = false;
    
    /**
     * 密码是否已过期
     */
    private Boolean isPasswordExpired = false;
}
