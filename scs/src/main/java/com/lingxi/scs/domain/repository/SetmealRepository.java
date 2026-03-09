package com.lingxi.scs.domain.repository;

import com.lingxi.scs.domain.model.entity.Setmeal;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 套餐仓储接口
 *
 * @author system
 */
public interface SetmealRepository {

    /**
     * 保存套餐
     */
    Setmeal save(Setmeal setmeal);

    /**
     * 根据 ID 查询套餐
     */
    Optional<Setmeal> findById(Long id);

    /**
     * 根据分类 ID 查询套餐列表
     */
    List<Setmeal> findByCategoryId(Long categoryId);

    /**
     * 根据状态查询套餐列表
     */
    List<Setmeal> findByStatus(Integer status);

    /**
     * 根据名称查询套餐
     */
    Optional<Setmeal> findByName(String name);

    /**
     * 查询所有套餐
     */
    List<Setmeal> findAll();

    /**
     * 删除套餐
     */
    void deleteById(Long id);
    
    /**
     * 根据名称模糊查询套餐（分页）
     */
    Page<Setmeal> findByNameContaining(String name, Pageable pageable);
}
