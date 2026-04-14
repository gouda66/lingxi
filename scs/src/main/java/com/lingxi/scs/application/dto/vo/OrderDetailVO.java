package com.lingxi.scs.application.dto.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;

/**
 * 订单明细VO
 *
 * @author system
 * @param id ID
 * @param name 商品名称
 * @param image 商品图片
 * @param dishId 菜品ID
 * @param setmealId 套餐ID
 * @param dishFlavor 菜品口味
 * @param number 数量
 * @param amount 金额
 */
public record OrderDetailVO(
        @JsonSerialize(using = ToStringSerializer.class) Long id,
        String name,
        String image,
        @JsonSerialize(using = ToStringSerializer.class) Long dishId,
        @JsonSerialize(using = ToStringSerializer.class) Long setmealId,
        String dishFlavor,
        Integer number,
        BigDecimal amount
) {}