package com.lingxi.scs.domain.repository;

import com.lingxi.scs.domain.model.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(Long id);

    List<Category> findAll();

    List<Category> findByType(Integer type);

    void deleteById(Long id);
    
    Page<Category> findByNameContaining(String name, Pageable pageable);
}
