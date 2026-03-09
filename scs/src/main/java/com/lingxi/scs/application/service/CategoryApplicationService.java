package com.lingxi.scs.application.service;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.lingxi.scs.common.exception.CustomException;
import com.lingxi.scs.domain.model.entity.Category;
import com.lingxi.scs.domain.repository.CategoryRepository;
import com.lingxi.scs.domain.repository.DishRepository;
import com.lingxi.scs.domain.repository.SetmealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryApplicationService {

    private final CategoryRepository categoryRepository;
    private final DishRepository dishRepository;
    private final SetmealRepository setmealRepository;
    private final SnowflakeGenerator snowflakeGenerator;

    @Transactional
    public Category addCategory(Category category, Long operatorId) {
        category.setId(snowflakeGenerator.next());
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser(operatorId);
        category.setUpdateUser(operatorId);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Category category, Long operatorId) {
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(operatorId);
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!dishRepository.findByCategoryId(id).isEmpty()) {
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        if (!setmealRepository.findByCategoryId(id).isEmpty()) {
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        categoryRepository.deleteById(id);
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException("分类不存在"));
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getCategoriesByType(Integer type) {
        return categoryRepository.findByType(type);
    }

    public Page<Category> pageByName(int page, int pageSize, String name) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return categoryRepository.findByNameContaining(name, pageable);
    }
}
