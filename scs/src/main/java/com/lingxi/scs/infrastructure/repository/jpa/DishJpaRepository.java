package com.lingxi.scs.infrastructure.repository.jpa;

import com.lingxi.scs.domain.model.entity.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DishJpaRepository extends JpaRepository<Dish, Long> {
    List<Dish> findByCategoryId(Long categoryId);
    List<Dish> findByStatus(Integer status);
    Optional<Dish> findByName(String name);
    
    Page<Dish> findByNameContaining(String name, Pageable pageable);
}
