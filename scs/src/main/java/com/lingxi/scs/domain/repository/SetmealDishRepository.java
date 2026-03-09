package com.lingxi.scs.domain.repository;

import com.lingxi.scs.domain.model.entity.SetmealDish;

import java.util.List;
import java.util.Optional;

/**
 * 套餐菜品关系仓储接口
 *
 * @author system
 */
public interface SetmealDishRepository {

    /**
     * 保存套餐菜品关系
     */
    SetmealDish save(SetmealDish setmealDish);

    /**
     * 批量保存套餐菜品关系
     */
    List<SetmealDish> saveAll(List<SetmealDish> setmealDishes);

    /**
     * 根据ID查询套餐菜品关系
     */
    Optional<SetmealDish> findById(Long id);

    /**
     * 根据套餐ID查询菜品列表
     */
    List<SetmealDish> findBySetmealId(Long setmealId);

    /**
     * 根据菜品ID查询关联的套餐
     */
    List<SetmealDish> findByDishId(Long dishId);

    /**
     * 根据套餐ID删除所有关系
     */
    void deleteBySetmealId(Long setmealId);

    /**
     * 删除套餐菜品关系
     */
    void deleteById(Long id);
}
