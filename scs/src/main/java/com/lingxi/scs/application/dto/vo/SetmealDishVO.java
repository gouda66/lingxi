package com.lingxi.scs.application.dto.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;

/**
 * 套餐菜品VO
 *
 * @author system
 * @param dishId 菜品ID
 * @param name 菜品名称
 * @param price 价格
 * @param copies 份数
 * @param image 图片
 */
public record SetmealDishVO(
        @JsonSerialize(using = ToStringSerializer.class) Long dishId,
        String name,
        BigDecimal price,
        Integer copies,
        String image
) {}