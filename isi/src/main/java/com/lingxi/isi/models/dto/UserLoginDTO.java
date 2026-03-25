package com.lingxi.isi.models.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户信息 DTO - 用于登录响应
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class UserLoginDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户 ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * 性别：0 女 1 男 2 未知
     */
    private Integer sex;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 用户类型：0 系统用户 1 面试者 2 HR 3 管理员
     */
    private Integer userType;

    /**
     * 用户角色（1:面试者 2:HR 3:管理员）
     */
    private Integer role;

    /**
     * 账号状态（0 停用 1 正常）
     */
    private Integer status;

    /**
     * 最后登录 IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    private LocalDateTime loginDate;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否是面试者
     */
    public boolean isCandidate() {
        return this.role != null && this.role == 1;
    }

    /**
     * 是否是 HR
     */
    public boolean isHr() {
        return this.role != null && this.role == 2;
    }

    /**
     * 是否是管理员
     */
    public boolean isAdmin() {
        return this.role != null && this.role == 3;
    }

    /**
     * 获取用户角色名称
     */
    public String getRoleName() {
        if (this.role == null) {
            return "未知";
        }
        return switch (this.role) {
            case 1 -> "面试者";
            case 2 -> "HR";
            case 3 -> "管理员";
            default -> "未知";
        };
    }
}
