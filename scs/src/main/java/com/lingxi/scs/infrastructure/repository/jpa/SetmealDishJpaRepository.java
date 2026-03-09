package com.lingxi.scs.infrastructure.repository.jpa;

import com.lingxi.scs.domain.model.entity.SetmealDish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 套餐菜品关系JPA仓储接口
 *
 * @author system
 */
@Repository
public interface SetmealDishJpaRepository extends JpaRepository<SetmealDish, Long> {
    List<SetmealDish> findBySetmealIdAndIsDeletedFalse(Long setmealId);
    List<SetmealDish> findByDishIdAndIsDeletedFalse(Long dishId);
    void deleteBySetmealId(Long setmealId);
}
