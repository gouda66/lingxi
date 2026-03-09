package com.lingxi.scs.domain.repository;

import com.lingxi.scs.domain.model.entity.DishFlavor;

import java.util.List;
import java.util.Optional;

/**
 * 菜品口味仓储接口
 *
 * @author system
 */
public interface DishFlavorRepository {

    /**
     * 保存菜品口味
     */
    DishFlavor save(DishFlavor dishFlavor);

    /**
     * 根据ID查询菜品口味
     */
    Optional<DishFlavor> findById(Long id);

    /**
     * 根据菜品ID查询口味列表
     */
    List<DishFlavor> findByDishId(Long dishId);

    /**
     * 批量保存菜品口味
     */
    List<DishFlavor> saveAll(List<DishFlavor> dishFlavors);

    /**
     * 根据菜品ID删除所有口味
     */
    void deleteByDishId(Long dishId);

    /**
     * 删除菜品口味
     */
    void deleteById(Long id);
}
