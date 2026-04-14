package com.lingxi.scs.domain.model.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 套餐菜品关系实体
 *
 * @author system
 */
@Data
@Entity
@Table(name = "t_menu_combo_dish")
@Comment("套餐菜品关系表")
public class SetmealDish implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Comment("主键ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Column(name = "setmeal_id", nullable = false, length = 32)
    @Comment("套餐ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setmealId;

    @Column(name = "dish_id", nullable = false, length = 32)
    @Comment("菜品ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dishId;

    @Column(name = "name", length = 32)
    @Comment("菜品名称（冗余字段）")
    private String name;

    @Column(name = "price", precision = 10, scale = 2)
    @Comment("菜品原价（冗余字段）")
    private BigDecimal price;

    @Column(name = "copies", nullable = false)
    @Comment("份数")
    private Integer copies;

    @Column(name = "sort", nullable = false)
    @Comment("排序")
    private Integer sort;

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

    @Column(name = "is_deleted", nullable = false)
    @Comment("是否删除")
    private Boolean isDeleted;
}
