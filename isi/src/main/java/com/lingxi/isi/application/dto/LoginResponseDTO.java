package com.lingxi.isi.application.dto;

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
     * Token
     */
    private String token;

    /**
     * 用户信息
     */
    private UserDTO user;
}
