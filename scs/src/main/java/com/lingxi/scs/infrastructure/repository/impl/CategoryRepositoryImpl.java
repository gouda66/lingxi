package com.lingxi.scs.infrastructure.repository.impl;

import com.lingxi.scs.domain.model.entity.Category;
import com.lingxi.scs.domain.repository.CategoryRepository;
import com.lingxi.scs.infrastructure.repository.jpa.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;

    @Override
    public Category save(Category category) {
        return jpaRepository.save(category);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Category> findByType(Integer type) {
        return jpaRepository.findByType(type);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Page<Category> findByNameContaining(String name, Pageable pageable) {
        return jpaRepository.findByNameContaining(name, pageable);
    }
}
