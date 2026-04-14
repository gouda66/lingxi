package com.lingxi.scs.domain.model.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serial;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体
 *
 * @author system
 */
@Data
@Entity
@Table(name = "t_sys_employee")
@Comment("员工表")
public class Employee implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Comment("主键ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Column(name = "username", nullable = false, length = 32, unique = true)
    @Comment("用户名")
    private String username;

    @Column(name = "name", nullable = false, length = 32)
    @Comment("姓名")
    private String name;

    @Column(name = "password", nullable = false, length = 64)
    @Comment("密码")
    private String password;

    @Column(name = "phone", nullable = false, length = 11)
    @Comment("手机号")
    private String phone;

    @Column(name = "sex", nullable = false, length = 2)
    @Comment("性别")
    private String sex;

    @Column(name = "id_number", nullable = false, length = 18)
    @Comment("身份证号")
    private String idNumber;

    @Column(name = "status", nullable = false)
    @Comment("状态 0:禁用 1:正常")
    private Integer status;

    @Column(name = "create_time", nullable = false, updatable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @Column(name = "create_user", nullable = false, updatable = false)
    @Comment("创建人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUser;

    @Column(name = "update_user", nullable = false)
    @Comment("修改人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUser;
}
