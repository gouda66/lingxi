package com.lingxi.isi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaImageDTO {
    
    /**
     * 是否开启验证码
     */
    private Boolean captchaEnabled = true;
    
    /**
     * 验证码图片 Base64
     */
    private String img;
    
    /**
     * 验证码 UUID（用于验证）
     */
    private String uuid;
}
