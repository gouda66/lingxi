package com.lingxi.scs.domain.repository;

import com.lingxi.scs.domain.model.entity.ShoppingCart;

import java.util.List;
import java.util.Optional;

/**
 * 购物车仓储接口
 *
 * @author system
 */
public interface ShoppingCartRepository {

    /**
     * 保存购物车项
     */
    ShoppingCart save(ShoppingCart shoppingCart);

    /**
     * 根据ID查询购物车项
     */
    Optional<ShoppingCart> findById(Long id);

    /**
     * 根据用户ID查询购物车列表
     */
    List<ShoppingCart> findByUserId(Long userId);

    /**
     * 根据用户ID和菜品ID查询
     */
    Optional<ShoppingCart> findByUserIdAndDishId(Long userId, Long dishId);

    /**
     * 根据用户ID和套餐ID查询
     */
    Optional<ShoppingCart> findByUserIdAndSetmealId(Long userId, Long setmealId);

    /**
     * 清空用户购物车
     */
    void deleteByUserId(Long userId);

    /**
     * 删除购物车项
     */
    void deleteById(Long id);
}
