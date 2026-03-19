package com.lingxi.isi.application.dto;

import lombok.Data;

/**
 * 登录请求 DTO
 */
@Data
public class LoginRequestDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 验证码
     */
    private String code;

    /**
     * 验证码 UUID
     */
    private String uuid;
}
