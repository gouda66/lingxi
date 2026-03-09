package com.lingxi.scs.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serial;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 套餐实体
 *
 * @author system
 */
@Data
@Entity
@Table(name = "t_menu_combo")
@Comment("套餐表")
public class Setmeal implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Comment("主键ID")
    private Long id;

    @Column(name = "category_id", nullable = false)
    @Comment("分类ID")
    private Long categoryId;

    @Column(name = "name", nullable = false, length = 64, unique = true)
    @Comment("套餐名称")
    private String name;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @Comment("套餐价格")
    private BigDecimal price;

    @Column(name = "status")
    @Comment("状态 0:停用 1:启用")
    private Integer status;

    @Column(name = "code", length = 32)
    @Comment("套餐编码")
    private String code;

    @Column(name = "description", length = 512)
    @Comment("描述信息")
    private String description;

    @Column(name = "image", length = 255)
    @Comment("套餐图片")
    private String image;

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
     * 判断套餐是否启用
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }
}