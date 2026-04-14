package com.lingxi.scs.application.dto.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;
import java.util.List;

/**
 * 套餐详情VO
 *
 * @author system
 * @param id ID
 * @param name 套餐名称
 * @param categoryId 分类ID
 * @param categoryName 分类名称
 * @param price 价格
 * @param image 图片
 * @param description 描述
 * @param status 状态
 * @param setmealDishes 套餐菜品列表
 */
public record SetmealDetailVO(
        @JsonSerialize(using = ToStringSerializer.class) Long id,
        String name,
        @JsonSerialize(using = ToStringSerializer.class) Long categoryId,
        String categoryName,
        BigDecimal price,
        String image,
        String description,
        Integer status,
        List<SetmealDishVO> setmealDishes
) {}