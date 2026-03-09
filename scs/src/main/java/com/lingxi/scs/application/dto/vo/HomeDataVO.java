package com.lingxi.scs.application.dto.vo;

import java.util.List;

/**
 * 首页数据VO
 *
 * @author system
 * @param categories 分类列表
 * @param dishCategories 菜品分类商品列表
 * @param setmealCategories 套餐分类商品列表
 */
public record HomeDataVO(
        List<CategoryVO> categories,
        List<CategoryWithItemsVO> dishCategories,
        List<CategoryWithItemsVO> setmealCategories
) {}