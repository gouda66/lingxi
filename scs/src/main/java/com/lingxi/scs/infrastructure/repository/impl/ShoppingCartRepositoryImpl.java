package com.lingxi.scs.infrastructure.repository.impl;

import com.lingxi.scs.domain.model.entity.ShoppingCart;
import com.lingxi.scs.domain.repository.ShoppingCartRepository;
import com.lingxi.scs.infrastructure.repository.jpa.ShoppingCartJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 购物车仓储实现
 *
 * @author system
 */
@Component
@RequiredArgsConstructor
public class ShoppingCartRepositoryImpl implements ShoppingCartRepository {

    private final ShoppingCartJpaRepository jpaRepository;

    @Override
    public ShoppingCart save(ShoppingCart shoppingCart) {
        return jpaRepository.save(shoppingCart);
    }

    @Override
    public Optional<ShoppingCart> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<ShoppingCart> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<ShoppingCart> findByUserIdAndDishId(Long userId, Long dishId) {
        return jpaRepository.findByUserIdAndDishId(userId, dishId);
    }

    @Override
    public Optional<ShoppingCart> findByUserIdAndSetmealId(Long userId, Long setmealId) {
        return jpaRepository.findByUserIdAndSetmealId(userId, setmealId);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        jpaRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
