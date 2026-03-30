package com.lingxi.seo.models.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDTO {
    
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
    private String nickName;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 注册消息提示
     */
    private String message;
}
