package com.lingxi.scs.infrastructure.repository.impl;

import com.lingxi.scs.domain.model.entity.Setmeal;
import com.lingxi.scs.domain.repository.SetmealRepository;
import com.lingxi.scs.infrastructure.repository.jpa.SetmealJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 套餐仓储实现
 *
 * @author system
 */
@Component
@RequiredArgsConstructor
public class SetmealRepositoryImpl implements SetmealRepository {

    private final SetmealJpaRepository jpaRepository;

    @Override
    public Setmeal save(Setmeal setmeal) {
        return jpaRepository.save(setmeal);
    }

    @Override
    public Optional<Setmeal> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Setmeal> findByCategoryId(Long categoryId) {
        return jpaRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Setmeal> findByStatus(Integer status) {
        return jpaRepository.findByStatus(status);
    }

    @Override
    public Optional<Setmeal> findByName(String name) {
        return jpaRepository.findByName(name);
    }

    @Override
    public List<Setmeal> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Page<Setmeal> findByNameContaining(String name, Pageable pageable) {
        return jpaRepository.findByNameContaining(name, pageable);
    }
}
