package com.lingxi.scs.application.dto.vo;

import java.math.BigDecimal;

/**
 * 套餐列表VO
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
 * @param dishCount 菜品数量
 */
public record SetmealListVO(
        Long id,
        String name,
        Long categoryId,
        String categoryName,
        BigDecimal price,
        String image,
        String description,
        Integer status,
        Integer dishCount
) {}