package com.lingxi.scs.infrastructure.repository.jpa;

import com.lingxi.scs.domain.model.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryJpaRepository extends JpaRepository<Category, Long> {
    List<Category> findByType(Integer type);
    Optional<Category> findByName(String name);
    
    Page<Category> findByNameContaining(String name, Pageable pageable);
}
