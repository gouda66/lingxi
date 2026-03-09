package com.lingxi.scs.interfaces.facade;

import com.lingxi.scs.application.service.CategoryApplicationService;
import com.lingxi.scs.application.service.DishApplicationService;
import com.lingxi.scs.application.service.SetmealApplicationService;
import com.lingxi.scs.domain.model.entity.Category;
import com.lingxi.scs.application.mapper.CategoryMapper;
import com.lingxi.scs.application.mapper.SetmealMapper;
import com.lingxi.scs.application.dto.vo.CategoryVO;
import com.lingxi.scs.application.dto.vo.CategoryWithItemsVO;
import com.lingxi.scs.application.dto.vo.HomeDataVO;
import com.lingxi.scs.application.dto.vo.SetmealListVO;
import com.lingxi.scs.domain.model.entity.Setmeal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户首页门面
 * 编排分类、菜品、套餐数据，为前端首页提供聚合数据
 *
 * @author system
 */
@Component
@RequiredArgsConstructor
public class UserHomeFacade {

    private final CategoryApplicationService categoryService;
    private final DishApplicationService dishService;
    private final SetmealApplicationService setmealService;
    private final CategoryMapper categoryMapper;
    private final SetmealMapper setmealMapper;

    /**
     * 获取首页聚合数据
     */
    public HomeDataVO getHomeData() {
        // 获取所有分类
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryVO> categoryVOs = categoryMapper.toCategoryVOList(categories);

        // 获取菜品分类下的菜品（type=1）
        List<CategoryWithItemsVO> dishCategories = categories.stream()
                .filter(Category::isDishCategory)
                .map(category -> categoryMapper.toDishCategoryVO(
                        category,
                        dishService.getAvailableDishesByCategoryId(category.getId())))
                .toList();

        // 获取套餐分类下的套餐（type=2）
        List<CategoryWithItemsVO> setmealCategories = categories.stream()
                .filter(Category::isSetmealCategory)
                .map(category -> {
                    List<SetmealListVO> setmeals = setmealService.getSetmealsByCategoryId(category.getId())
                            .stream()
                            .filter(Setmeal::isEnabled)
                            .map(s -> setmealMapper.toListVO(s, category.getName(), 0))
                            .toList();
                    return categoryMapper.toSetmealCategoryVO(category, setmeals);
                })
                .toList();

        return new HomeDataVO(categoryVOs, dishCategories, setmealCategories);
    }

    /**
     * 根据分类ID获取商品列表
     */
    public CategoryWithItemsVO getCategoryItems(Long categoryId, Integer type) {
        Category category = categoryService.getCategoryById(categoryId);

        if (type == 1) {
            return categoryMapper.toDishCategoryVO(
                    category,
                    dishService.getAvailableDishesByCategoryId(categoryId));
        } else {
            List<SetmealListVO> setmeals = setmealService.getSetmealsByCategoryId(categoryId)
                    .stream()
                    .filter(Setmeal::isEnabled)
                    .map(s -> setmealMapper.toListVO(s, category.getName(), 0))
                    .toList();
            return categoryMapper.toSetmealCategoryVO(category, setmeals);
        }
    }
}