package com.lingxi.scs.domain.repository;

import com.lingxi.scs.domain.model.entity.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DishRepository {

    Dish save(Dish dish);

    Optional<Dish> findById(Long id);

    List<Dish> findByCategoryId(Long categoryId);

    List<Dish> findByStatus(Integer status);

    Optional<Dish> findByName(String name);

    List<Dish> findAll();

    void deleteById(Long id);
    
    Page<Dish> findByNameContaining(String name, Pageable pageable);
}
