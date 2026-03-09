package com.lingxi.scs.infrastructure.repository.impl;

import com.lingxi.scs.domain.model.entity.Dish;
import com.lingxi.scs.domain.repository.DishRepository;
import com.lingxi.scs.infrastructure.repository.jpa.DishJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DishRepositoryImpl implements DishRepository {

    private final DishJpaRepository jpaRepository;

    @Override
    public Dish save(Dish dish) {
        return jpaRepository.save(dish);
    }

    @Override
    public Optional<Dish> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Dish> findByCategoryId(Long categoryId) {
        return jpaRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Dish> findByStatus(Integer status) {
        return jpaRepository.findByStatus(status);
    }

    @Override
    public Optional<Dish> findByName(String name) {
        return jpaRepository.findByName(name);
    }

    @Override
    public List<Dish> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Page<Dish> findByNameContaining(String name, Pageable pageable) {
        return jpaRepository.findByNameContaining(name, pageable);
    }
}
