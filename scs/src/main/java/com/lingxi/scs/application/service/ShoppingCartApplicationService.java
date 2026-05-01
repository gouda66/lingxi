package com.lingxi.scs.application.service;

import cn.hutool.core.util.IdUtil;
import com.lingxi.scs.application.command.AddToCartCommand;
import com.lingxi.scs.application.dto.ShoppingCartDTO;
import com.lingxi.scs.application.mapper.ShoppingCartMapper;
import com.lingxi.scs.common.exception.CustomException;
import com.lingxi.scs.domain.model.entity.Dish;
import com.lingxi.scs.domain.model.entity.Setmeal;
import com.lingxi.scs.domain.model.entity.ShoppingCart;
import com.lingxi.scs.domain.repository.DishRepository;
import com.lingxi.scs.domain.repository.SetmealRepository;
import com.lingxi.scs.domain.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车应用服务
 *
 * @author system
 */
@Service
@RequiredArgsConstructor
public class ShoppingCartApplicationService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final DishRepository dishRepository;
    private final SetmealRepository setmealRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    /**
     * 添加商品到购物车
     *
     * @param command 添加购物车命令
     * @param userId 用户ID
     * @return 购物车项DTO
     */
    @Transactional
    public ShoppingCartDTO addToCart(AddToCartCommand command, Long userId) {
        command.validate();

        ShoppingCart cart;

        if (command.getDishId() != null) {
            // 添加菜品
            cart = addDishToCart(command.getDishId(), command.getDishFlavor(), command.getNumber(), userId);
        } else {
            // 添加套餐
            cart = addSetmealToCart(command.getSetmealId(), command.getNumber(), userId);
        }

        return shoppingCartMapper.toDTO(cart);
    }

    /**
     * 添加菜品到购物车
     */
    private ShoppingCart addDishToCart(Long dishId, String flavor, Integer number, Long userId) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new CustomException("菜品不存在"));

        if (dish.getStatus() != 1) {
            throw new CustomException("菜品已下架");
        }

        // 查询是否已存在
        ShoppingCart existingCart = shoppingCartRepository.findByUserIdAndDishId(userId, dishId)
                .orElse(null);

        if (existingCart != null) {
            existingCart.setNumber(existingCart.getNumber() + number);
            return shoppingCartRepository.save(existingCart);
        } else {
            ShoppingCart cart = new ShoppingCart();
            cart.setId(IdUtil.getSnowflakeNextId());
            cart.setUserId(userId);
            cart.setDishId(dishId);
            cart.setName(dish.getName());
            cart.setImage(dish.getImage());
            // 将分转换为元（除以100）
            cart.setAmount(dish.getPrice());
            cart.setNumber(number);
            cart.setDishFlavor(flavor);
            cart.setCreateTime(LocalDateTime.now());
            return shoppingCartRepository.save(cart);
        }
    }

    /**
     * 添加套餐到购物车
     */
    private ShoppingCart addSetmealToCart(Long setmealId, Integer number, Long userId) {
        Setmeal setmeal = setmealRepository.findById(setmealId)
                .orElseThrow(() -> new CustomException("套餐不存在"));

        if (setmeal.getStatus() != 1) {
            throw new CustomException("套餐已下架");
        }

        // 查询是否已存在
        ShoppingCart existingCart = shoppingCartRepository.findByUserIdAndSetmealId(userId, setmealId)
                .orElse(null);

        if (existingCart != null) {
            existingCart.setNumber(existingCart.getNumber() + number);
            return shoppingCartRepository.save(existingCart);
        } else {
            ShoppingCart cart = new ShoppingCart();
            cart.setId(IdUtil.getSnowflakeNextId());
            cart.setUserId(userId);
            cart.setSetmealId(setmealId);
            cart.setName(setmeal.getName());
            cart.setImage(setmeal.getImage());
            // 将分转换为元（除以100）
            cart.setAmount(setmeal.getPrice().divide(new BigDecimal("100")));
            cart.setNumber(number);
            cart.setCreateTime(LocalDateTime.now());
            return shoppingCartRepository.save(cart);
        }
    }

    /**
     * 查询用户的购物车
     *
     * @param userId 用户ID
     * @return 购物车列表
     */
    public List<ShoppingCartDTO> getCartList(Long userId) {
        List<ShoppingCart> carts = shoppingCartRepository.findByUserId(userId);
        return shoppingCartMapper.toDTOList(carts);
    }

    /**
     * 清空购物车
     *
     * @param userId 用户ID
     */
    @Transactional
    public void clearCart(Long userId) {
        shoppingCartRepository.deleteByUserId(userId);
    }

    /**
     * 减少购物车项数量
     *
     * @param dishId 菜品ID
     * @param setmealId 套餐ID
     * @param userId 用户ID
     * @return 更新后的DTO，如果数量为0则返回null表示已删除
     */
    @Transactional
    public ShoppingCartDTO decreaseCartItem(Long dishId, Long setmealId, Long userId) {
        // 根据dishId或setmealId查找购物车项
        ShoppingCart cart;
        if (dishId != null) {
            cart = shoppingCartRepository.findByUserIdAndDishId(userId, dishId)
                    .orElseThrow(() -> new CustomException("购物车项不存在"));
        } else if (setmealId != null) {
            cart = shoppingCartRepository.findByUserIdAndSetmealId(userId, setmealId)
                    .orElseThrow(() -> new CustomException("购物车项不存在"));
        } else {
            throw new CustomException("菜品ID和套餐ID不能同时为空");
        }

        if (!cart.getUserId().equals(userId)) {
            throw new CustomException("无权操作");
        }

        if (cart.getNumber() <= 1) {
            // 数量为1或更少，直接删除
            shoppingCartRepository.deleteById(cart.getId());
            return null;
        } else {
            // 数量减1
            cart.setNumber(cart.getNumber() - 1);
            return shoppingCartMapper.toDTO(shoppingCartRepository.save(cart));
        }
    }
}
