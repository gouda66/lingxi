package com.lingxi.scs.infrastructure.repository.impl;

import com.lingxi.scs.domain.model.entity.DishFlavor;
import com.lingxi.scs.domain.repository.DishFlavorRepository;
import com.lingxi.scs.infrastructure.repository.jpa.DishFlavorJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 菜品口味仓储实现
 *
 * @author system
 */
@Component
@RequiredArgsConstructor
public class DishFlavorRepositoryImpl implements DishFlavorRepository {

    private final DishFlavorJpaRepository jpaRepository;

    @Override
    public DishFlavor save(DishFlavor dishFlavor) {
        return jpaRepository.save(dishFlavor);
    }

    @Override
    public Optional<DishFlavor> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<DishFlavor> findByDishId(Long dishId) {
        return jpaRepository.findByDishIdAndIsDeletedFalse(dishId);
    }

    @Override
    public List<DishFlavor> saveAll(List<DishFlavor> dishFlavors) {
        return jpaRepository.saveAll(dishFlavors);
    }

    @Override
    @Transactional
    public void deleteByDishId(Long dishId) {
        jpaRepository.deleteByDishId(dishId);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
