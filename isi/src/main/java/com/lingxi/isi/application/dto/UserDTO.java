package com.lingxi.isi.application.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户 DTO
 */
@Data
public class UserDTO {

    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态 0 禁用 1 正常
     */
    private Integer status;

    /**
     * 角色 USER-求职者 HR-企业 HR ADMIN-管理员
     */
    private String role;

    /**
     * 所属企业 ID（仅 HR 用户有值）
     */
    private String companyId;

    /**
     * 所属企业名称（仅 HR 用户有值）
     */
    private String companyName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
