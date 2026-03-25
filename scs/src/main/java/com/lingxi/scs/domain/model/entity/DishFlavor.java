package com.lingxi.scs.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serial;

import java.io.Serializable;

/**
 * 菜品口味实体
 *
 * @author system
 */
@Data
@Entity
@Table(name = "t_menu_dish_flavor")
@Comment("菜品口味表")
public class DishFlavor implements Serializable {

    @Serial
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Comment("主键ID")
    private Long id;

    @Column(name = "dish_id", nullable = false)
    @Comment("菜品ID")
    private Long dishId;

    @Column(name = "name", nullable = false, length = 32)
    @Comment("口味名称")
    private String name;

    @Column(name = "value", nullable = false, length = 100)
    @Comment("口味值")
    private String value;

    @Column(name = "is_deleted", columnDefinition = "tinyint(1) default 0")
    @Comment("是否删除 0未删除 1已删除")
    private Boolean isDeleted = false;

    /**
     * 验证口味基本信息
     */
    public boolean isValid() {
        return this.name != null && !this.name.trim().isEmpty()
                && this.value != null && !this.value.trim().isEmpty()
                && this.dishId != null;
    }
}