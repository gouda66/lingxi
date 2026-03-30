package com.lingxi.isi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
     * 用户详细信息
     */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {
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
     * 性别
     */
    private Integer sex;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 账号状态（0 禁用 1 正常）
     */
    private Integer status;
}