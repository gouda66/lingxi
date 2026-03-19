package com.lingxi.isi.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaDTO {

    /**
     * 是否启用验证码
     */
    private Boolean captchaEnabled;

    /**
     * 验证码图片（Base64）
     */
    private String img;

    /**
     * 验证码唯一标识
     */
    private String uuid;
}
