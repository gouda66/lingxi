package com.lingxi.scs.application.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 推荐菜品 DTO
 * 包含推荐分数和推荐理由
 *
 * @author system
 */
@Data
public class RecommendDishDTO {

    /**
     * 菜品ID（String类型，避免JS精度丢失）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 菜品名称
     */
    private String name;

    /**
     * 分类ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 图片
     */
    private String image;

    /**
     * 描述
     */
    private String description;

    /**
     * 月销量
     */
    private Integer monthlySales;

    /**
     * 推荐分数（0-100）
     */
    private Double recommendScore;

    /**
     * 推荐理由
     */
    private String recommendReason;

    /**
     * 菜品类型（DISH-菜品，SETMEAL-套餐）
     */
    private String itemType;
}
