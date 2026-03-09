package com.lingxi.scs.infrastructure.repository.impl;

import com.lingxi.scs.domain.model.entity.SetmealDish;
import com.lingxi.scs.domain.repository.SetmealDishRepository;
import com.lingxi.scs.infrastructure.repository.jpa.SetmealDishJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 套餐菜品关系仓储实现
 *
 * @author system
 */
@Component
@RequiredArgsConstructor
public class SetmealDishRepositoryImpl implements SetmealDishRepository {

    private final SetmealDishJpaRepository jpaRepository;

    @Override
    public SetmealDish save(SetmealDish setmealDish) {
        return jpaRepository.save(setmealDish);
    }

    @Override
    public List<SetmealDish> saveAll(List<SetmealDish> setmealDishes) {
        return jpaRepository.saveAll(setmealDishes);
    }

    @Override
    public Optional<SetmealDish> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<SetmealDish> findBySetmealId(Long setmealId) {
        return jpaRepository.findBySetmealIdAndIsDeletedFalse(setmealId);
    }

    @Override
    public List<SetmealDish> findByDishId(Long dishId) {
        return jpaRepository.findByDishIdAndIsDeletedFalse(dishId);
    }

    @Override
    @Transactional
    public void deleteBySetmealId(Long setmealId) {
        jpaRepository.deleteBySetmealId(setmealId);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
