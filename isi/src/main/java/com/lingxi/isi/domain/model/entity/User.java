package com.lingxi.isi.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体（求职者）
 */
@Data
@Entity
@Table(name = "isi_user")
@Comment("求职者用户表")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("主键 ID")
    private Long id;

    @Column(name = "username", nullable = false, length = 64, unique = true)
    @Comment("用户名")
    private String username;

    @Column(name = "password", nullable = false, length = 128)
    @Comment("密码（加密）")
    private String password;

    @Column(name = "real_name", length = 64)
    @Comment("真实姓名")
    private String realName;

    @Column(name = "email", length = 64)
    @Comment("邮箱")
    private String email;

    @Column(name = "phone", length = 32)
    @Comment("手机号")
    private String phone;

    @Column(name = "status", nullable = false)
    @Comment("状态 0 禁用 1 正常")
    private Integer status = 1;

    @Column(name = "role", nullable = false)
    @Comment("角色 USER-求职者 HR-企业HR ADMIN-管理员")
    private String role = "USER";

    @Column(name = "company_id", length = 64)
    @Comment("所属企业 ID（仅 HR 用户有值）")
    private String companyId;

    @Column(name = "company_name", length = 128)
    @Comment("所属企业名称（仅 HR 用户有值）")
    private String companyName;

    @Column(name = "create_time", nullable = false, updatable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 判断是否为 HR 角色
     */
    public boolean isHr() {
        return "HR".equals(this.role);
    }

    /**
     * 判断是否为管理员角色
     */
    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    /**
     * 判断是否为求职者角色
     */
    public boolean isCandidate() {
        return "USER".equals(this.role);
    }
}
