package com.lingxi.scs.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serial;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户地址实体
 *
 * @author system
 */
@Data
@Entity
@Table(name = "t_user_address")
@Comment("用户地址表")
public class AddressBook implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Comment("主键ID")
    private Long id;

    @Column(name = "user_id", nullable = false)
    @Comment("用户ID")
    private Long userId;

    @Column(name = "consignee", nullable = false, length = 50)
    @Comment("收货人")
    private String consignee;

    @Column(name = "phone", nullable = false, length = 11)
    @Comment("手机号")
    private String phone;

    @Column(name = "sex", nullable = false, length = 2)
    @Comment("性别 0 女 1 男")
    private String sex;

    @Column(name = "province_code", length = 12)
    @Comment("省级区划编号")
    private String provinceCode;

    @Column(name = "province_name", length = 32)
    @Comment("省级名称")
    private String provinceName;

    @Column(name = "city_code", length = 12)
    @Comment("市级区划编号")
    private String cityCode;

    @Column(name = "city_name", length = 32)
    @Comment("市级名称")
    private String cityName;

    @Column(name = "district_code", length = 12)
    @Comment("区级区划编号")
    private String districtCode;

    @Column(name = "district_name", length = 32)
    @Comment("区级名称")
    private String districtName;

    @Column(name = "detail", length = 200)
    @Comment("详细地址")
    private String detail;

    @Column(name = "label", length = 100)
    @Comment("地址标签")
    private String label;

    @Column(name = "is_default", nullable = false)
    @Comment("是否默认地址")
    private Boolean isDefault;

    @Column(name = "create_time", nullable = false, updatable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @Column(name = "create_user", nullable = false, updatable = false)
    @Comment("创建人ID")
    private Long createUser;

    @Column(name = "update_user", nullable = false)
    @Comment("修改人ID")
    private Long updateUser;

    @Column(name = "is_deleted", nullable = false)
    @Comment("是否删除")
    private Boolean isDeleted;
}
