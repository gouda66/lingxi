package com.lingxi.isi.models.dto;

import com.lingxi.isi.models.entity.SysRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户授权角色信息 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRoleDTO {
    
    /**
     * 用户信息
     */
    private UserDetailDTO user;
    
    /**
     * 角色列表
     */
    private List<SysRole> roles;
}
