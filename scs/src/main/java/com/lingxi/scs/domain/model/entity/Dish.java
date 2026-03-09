package com.lingxi.scs.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品实体
 *
 * @author system
 */
@Data
@Entity
@Table(name = "t_menu_dish")
@Comment("菜品表")
public class Dish implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Comment("主键ID")
    private Long id;

    @Column(name = "name", nullable = false, length = 64, unique = true)
    @Comment("菜品名称")
    private String name;

    @Column(name = "category_id", nullable = false)
    @Comment("菜品分类ID")
    private Long categoryId;

    @Column(name = "price", precision = 10, scale = 2)
    @Comment("菜品价格")
    private BigDecimal price;

    @Column(name = "code", nullable = false, length = 64)
    @Comment("商品编码")
    private String code;

    @Column(name = "image", nullable = false, length = 200)
    @Comment("菜品图片")
    private String image;

    @Column(name = "description", length = 400)
    @Comment("描述信息")
    private String description;

    @Column(name = "status", nullable = false)
    @Comment("状态 0 停售 1 起售")
    private Integer status;

    @Column(name = "sort", nullable = false)
    @Comment("排序顺序")
    private Integer sort;

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

    /**
     * 是否起售
     */
    public boolean isAvailable() {
        return this.status != null && this.status == 1;
    }

    /**
     * 检查是否可以销售
     */
    public boolean canSell() {
        return isAvailable();
    }

    /**
     * 设置为起售状态
     */
    public void enable() {
        this.status = 1;
    }

    /**
     * 设置为停售状态
     */
    public void disable() {
        this.status = 0;
    }

    /**
     * 获取状态文本
     */
    public String getStatusText() {
        return (this.status != null && this.status == 1) ? "起售" : "停售";
    }

    /**
     * 验证菜品基本信息
     */
    public boolean isValid() {
        return this.name != null && !this.name.trim().isEmpty()
                && this.price != null && this.price.compareTo(BigDecimal.ZERO) > 0
                && this.categoryId != null;
    }
}