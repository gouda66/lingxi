package com.lingxi.scs.infrastructure.repository.jpa;

import com.lingxi.scs.domain.model.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 购物车JPA仓储接口
 *
 * @author system
 */
@Repository
public interface ShoppingCartJpaRepository extends JpaRepository<ShoppingCart, Long> {
    List<ShoppingCart> findByUserId(Long userId);
    Optional<ShoppingCart> findByUserIdAndDishId(Long userId, Long dishId);
    Optional<ShoppingCart> findByUserIdAndSetmealId(Long userId, Long setmealId);
    void deleteByUserId(Long userId);
}
