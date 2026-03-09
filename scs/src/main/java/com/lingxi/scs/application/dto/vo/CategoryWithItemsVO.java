package com.lingxi.scs.application.dto.vo;

import com.lingxi.scs.application.dto.DishDTO;

import java.util.List;

/**
 * 分类及其商品VO
 *
 * @author system
 * @param categoryId 分类ID
 * @param categoryName 分类名称
 * @param type 类型
 * @param dishes 菜品列表
 * @param setmeals 套餐列表
 */
public record CategoryWithItemsVO(
        Long categoryId,
        String categoryName,
        Integer type,
        List<DishDTO> dishes,
        List<SetmealListVO> setmeals
) {}