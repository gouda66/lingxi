package com.lingxi.scs.interfaces.facade;

import com.lingxi.scs.application.service.CategoryApplicationService;
import com.lingxi.scs.application.service.DishApplicationService;
import com.lingxi.scs.application.service.SetmealApplicationService;
import com.lingxi.scs.domain.model.entity.Category;
import com.lingxi.scs.domain.model.entity.Setmeal;
import com.lingxi.scs.domain.model.entity.SetmealDish;
import com.lingxi.scs.application.mapper.SetmealMapper;
import com.lingxi.scs.application.dto.vo.SetmealDetailVO;
import com.lingxi.scs.application.dto.vo.SetmealDishVO;
import com.lingxi.scs.application.dto.vo.SetmealListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 套餐门面
 * 编排套餐、分类、菜品数据，提供完整套餐视图
 *
 * @author system
 */
@Component
@RequiredArgsConstructor
public class SetmealFacade {

    private final SetmealApplicationService setmealService;
    private final CategoryApplicationService categoryService;
    private final DishApplicationService dishService;
    private final SetmealMapper setmealMapper;

    /**
     * 获取套餐详情（包含分类名称和菜品列表）
     */
    public SetmealDetailVO getSetmealDetail(Long setmealId) {
        Setmeal setmeal = setmealService.getSetmealById(setmealId);
        List<SetmealDish> setmealDishes = setmealService.getSetmealDishes(setmealId);

        // 获取分类名称
        Category category = categoryService.getCategoryById(setmeal.getCategoryId());

        // 转换套餐菜品
        List<SetmealDishVO> dishVOs = setmealDishes.stream()
                .map(sd -> {
                    String dishImage = dishService.getDishImage(sd.getDishId());
                    return setmealMapper.toDishVO(sd, dishImage);
                })
                .toList();

        return setmealMapper.toDetailVO(setmeal, category.getName(), dishVOs);
    }

    /**
     * 获取套餐列表（包含分类名称）
     */
    public List<SetmealListVO> getSetmealList(Long categoryId) {
        List<Setmeal> setmeals;

        if (categoryId != null) {
            setmeals = setmealService.getSetmealsByCategoryId(categoryId);
        } else {
            setmeals = setmealService.getAllSetmeals();
        }

        return setmeals.stream()
                .filter(s -> s.getStatus() == 1)
                .map(setmeal -> {
                    Category category = categoryService.getCategoryById(setmeal.getCategoryId());
                    int dishCount = setmealService.getSetmealDishes(setmeal.getId()).size();
                    return setmealMapper.toListVO(setmeal, category.getName(), dishCount);
                })
                .toList();
    }

    /**
     * 根据名称分页查询套餐（包含分类名称）
     */
    public Page<SetmealListVO> getPageByName(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = setmealService.pageByName(page, pageSize, name);
        
        return setmealPage.map(setmeal -> {
            Category category = categoryService.getCategoryById(setmeal.getCategoryId());
            int dishCount = setmealService.getSetmealDishes(setmeal.getId()).size();
            return setmealMapper.toListVO(setmeal, category.getName(), dishCount);
        });
    }
}
