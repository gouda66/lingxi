package com.lingxi.seo.models.entity;
import java.io.Serial;
import com.baomidou.mybatisplus.annotation.*;
import com.lingxi.seo.models.request.other.SysUserRegisterRequest;
import com.lingxi.seo.utils.PasswordSegmentUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表 - 基础用户实体（面试者、HR、管理员）
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_user")
public class SysUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱（唯一）
     */
    private String email;

    /**
     * 加密密码
     */
    private String password;

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

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除：0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;


    /**
     * 使用注册请求初始化用户信息（MD5 加密密码）
     */
    public void initFromRegister(SysUserRegisterRequest request) {
        this.username = request.getUsername();
        this.password = PasswordSegmentUtils.encrypt(request.getPassword());
        this.email = request.getEmail();
        this.phone = request.getPhone();
        this.realName = request.getRealName();
        this.role = 1;
        this.status = 1;
        this.deleted = 0;
    }

}
