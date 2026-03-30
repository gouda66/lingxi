package com.lingxi.scs.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serial;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 菜品及套餐分类实体
 *
 * @author system
 */
@Data
@Entity
@Table(name = "t_menu_category")
@Comment("菜品及套餐分类")
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Comment("主键ID")
    private Long id;

    @Column(name = "type")
    @Comment("类型 1 菜品分类 2 套餐分类")
    private Integer type;

    @Column(name = "name", nullable = false, length = 64, unique = true)
    @Comment("分类名称")
    private String name;

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
     * 判断是否为菜品分类
     */
    public boolean isDishCategory() {
        return type != null && type == 1;
    }
    
    /**
     * 判断是否为套餐分类
     */
    public boolean isSetmealCategory() {
        return type != null && type == 2;
    }
}