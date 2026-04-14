package com.lingxi.isi.models.request;

import lombok.Data;

/**
 * 重置密码请求 DTO
 */
@Data
public class ResetPasswordRequest {
    
    /**
     * 用户 ID
     */
    private Long userId;
    
    /**
     * 新密码
     */
    private String password;
}