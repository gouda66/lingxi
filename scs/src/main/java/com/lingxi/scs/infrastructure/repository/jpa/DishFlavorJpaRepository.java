package com.lingxi.scs.infrastructure.repository.jpa;

import com.lingxi.scs.domain.model.entity.DishFlavor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜品口味JPA仓储接口
 *
 * @author system
 */
@Repository
public interface DishFlavorJpaRepository extends JpaRepository<DishFlavor, Long> {
    List<DishFlavor> findByDishIdAndIsDeletedFalse(Long dishId);
    void deleteByDishId(Long dishId);
}
