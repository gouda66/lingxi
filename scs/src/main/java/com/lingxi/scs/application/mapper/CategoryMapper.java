package com.lingxi.scs.application.mapper;

import com.lingxi.scs.application.dto.DishDTO;
import com.lingxi.scs.application.dto.vo.CategoryVO;
import com.lingxi.scs.application.dto.vo.CategoryWithItemsVO;
import com.lingxi.scs.application.dto.vo.SetmealListVO;
import com.lingxi.scs.domain.model.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 分类映射器
 * 负责分类相关对象的转换
 *
 * @author system
 */
@Component
public class CategoryMapper {

    /**
     * 转换分类实体为VO
     */
    public CategoryVO toCategoryVO(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryVO(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getSort()
        );
    }

    /**
     * 批量转换分类实体为VO列表
     */
    public List<CategoryVO> toCategoryVOList(List<Category> categories) {
        if (categories == null) {
            return List.of();
        }
        return categories.stream()
                .map(this::toCategoryVO)
                .toList();
    }

    /**
     * 创建菜品分类商品VO
     */
    public CategoryWithItemsVO toDishCategoryVO(Category category, List<DishDTO> dishes) {
        return new CategoryWithItemsVO(
                category.getId(),
                category.getName(),
                1,
                dishes,
                null
        );
    }

    /**
     * 创建套餐分类商品VO
     */
    public CategoryWithItemsVO toSetmealCategoryVO(Category category, List<SetmealListVO> setmeals) {
        return new CategoryWithItemsVO(
                category.getId(),
                category.getName(),
                2,
                null,
                setmeals
        );
    }
}