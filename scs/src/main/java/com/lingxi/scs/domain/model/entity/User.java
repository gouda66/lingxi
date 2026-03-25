package com.lingxi.scs.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serial;

import java.io.Serializable;

/**
 * 用户实体
 *
 * @author system
 */
@Data
@Entity
@Table(name = "t_user")
@Comment("用户表")
public class User implements Serializable {

    @Serial
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Comment("主键ID")
    private Long id;

    @Column(name = "name", length = 50)
    @Comment("姓名")
    private String name;

    @Column(name = "phone", nullable = false, length = 100)
    @Comment("手机号")
    private String phone;

    @Column(name = "sex", length = 2)
    @Comment("性别 0 女 1 男")
    private String sex;

    @Column(name = "id_number", length = 18)
    @Comment("身份证号")
    private String idNumber;

    @Column(name = "avatar", length = 500)
    @Comment("头像")
    private String avatar;

    @Column(name = "status")
    @Comment("状态 0:禁用 1:正常")
    private Integer status;
}
