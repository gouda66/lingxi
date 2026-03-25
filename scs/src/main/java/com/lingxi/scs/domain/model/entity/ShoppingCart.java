package com.lingxi.scs.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车实体
 *
 * @author system
 */
@Data
@Entity
@Table(name = "t_user_cart")
@Comment("购物车表")
public class ShoppingCart implements Serializable {

    @Serial
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Comment("主键ID")
    private Long id;

    @Column(name = "name", length = 50)
    @Comment("商品名称")
    private String name;

    @Column(name = "user_id", nullable = false)
    @Comment("用户ID")
    private Long userId;

    @Column(name = "dish_id")
    @Comment("菜品ID")
    private Long dishId;

    @Column(name = "setmeal_id")
    @Comment("套餐ID")
    private Long setmealId;

    @Column(name = "dish_flavor", length = 50)
    @Comment("菜品口味")
    private String dishFlavor;

    @Column(name = "number", nullable = false)
    @Comment("购买数量")
    private Integer number;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    @Comment("金额")
    private BigDecimal amount;

    @Column(name = "image", length = 100)
    @Comment("商品图片")
    private String image;

    @Column(name = "create_time")
    @Comment("创建时间")
    private LocalDateTime createTime;
}
