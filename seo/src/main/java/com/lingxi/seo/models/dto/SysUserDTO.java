package com.lingxi.seo.models.dto;

import lombok.Data;

/**
 * <p>
 * 用户数据传输对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class SysUserDTO {

    /**
     * 用户 ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 头像 URL
     */
    private String avatarUrl;

    /**
     * 角色：1-面试者 2-HR 3-管理员
     */
    private Integer role;

    /**
     * 状态：0-禁用 1-正常
     */
    private Integer status;
}
