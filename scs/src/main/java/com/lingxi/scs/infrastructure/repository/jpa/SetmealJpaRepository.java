package com.lingxi.scs.infrastructure.repository.jpa;

import com.lingxi.scs.domain.model.entity.Setmeal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 套餐JPA仓储接口
 *
 * @author system
 */
@Repository
public interface SetmealJpaRepository extends JpaRepository<Setmeal, Long> {
    List<Setmeal> findByCategoryId(Long categoryId);
    List<Setmeal> findByStatus(Integer status);
    Optional<Setmeal> findByName(String name);
    
    /**
     * 根据名称模糊查询套餐（分页）
     */
    Page<Setmeal> findByNameContaining(String name, Pageable pageable);
}
