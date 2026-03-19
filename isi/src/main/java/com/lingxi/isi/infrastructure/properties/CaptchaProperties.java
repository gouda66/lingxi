package com.lingxi.isi.infrastructure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 验证码配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "captcha")
public class CaptchaProperties {

    /**
     * 验证码长度，默认 4 位
     */
    private int length = 4;

    /**
     * 验证码有效期（秒），默认 5 分钟
     */
    private int expireSeconds = 300;

    /**
     * 是否启用验证码，默认启用
     */
    private boolean enabled = true;

    /**
     * 验证码字符集（去除易混淆字符）
     */
    private String chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
}