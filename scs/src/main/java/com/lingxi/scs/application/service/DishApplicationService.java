package com.lingxi.scs.application.service;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.lingxi.scs.application.dto.DishDTO;
import com.lingxi.scs.application.dto.DishFlavorDTO;
import com.lingxi.scs.common.exception.CustomException;
import com.lingxi.scs.domain.model.entity.Category;
import com.lingxi.scs.domain.model.entity.Dish;
import com.lingxi.scs.domain.model.entity.DishFlavor;
import com.lingxi.scs.domain.repository.CategoryRepository;
import com.lingxi.scs.domain.repository.DishFlavorRepository;
import com.lingxi.scs.domain.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DishApplicationService {

    private final DishRepository dishRepository;
    private final DishFlavorRepository dishFlavorRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    private SnowflakeGenerator snowflakeGenerator;

    @Transactional
    public Dish addDishWithFlavor(Dish dish, List<DishFlavor> flavors, Long operatorId) {
        dish.setStatus(1);
        dish.setCreateTime(LocalDateTime.now());
        dish.setUpdateTime(LocalDateTime.now());
        dish.setCreateUser(operatorId);
        dish.setUpdateUser(operatorId);
        dish.setSort(0);
        dish.setImage("5555555555");

        Dish savedDish = dishRepository.save(dish);

        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor flavor : flavors) {
                flavor.setId(snowflakeGenerator.next());
                flavor.setDishId(savedDish.getId());
            }
            dishFlavorRepository.saveAll(flavors);
        }

        return savedDish;
    }

    @Transactional
    public Dish updateDishWithFlavor(Dish dish, List<DishFlavor> flavors, Long operatorId) {
        dish.setUpdateTime(LocalDateTime.now());
        dish.setUpdateUser(operatorId);

        Dish savedDish = dishRepository.save(dish);

        dishFlavorRepository.deleteByDishId(dish.getId());

        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(savedDish.getId());
            }
            dishFlavorRepository.saveAll(flavors);
        }

        return savedDish;
    }

    public DishDTO getDishWithFlavorById(Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new CustomException("菜品不存在"));

        List<DishFlavor> flavors = dishFlavorRepository.findByDishId(id);

        String categoryName = categoryRepository.findById(dish.getCategoryId())
                .map(Category::getName)
                .orElse(null);

        DishDTO dto = new DishDTO();
        dto.setId(dish.getId());
        dto.setName(dish.getName());
        dto.setCategoryId(dish.getCategoryId());
        dto.setCategoryName(categoryName);
        dto.setPrice(dish.getPrice());
        dto.setCode(dish.getCode());
        dto.setImage(dish.getImage());
        dto.setDescription(dish.getDescription());
        dto.setStatus(dish.getStatus());
        dto.setSort(dish.getSort());
        dto.setCreateTime(dish.getCreateTime());
        dto.setUpdateTime(dish.getUpdateTime());

        List<DishFlavorDTO> flavorDTOs = flavors.stream()
                .map(f -> {
                    DishFlavorDTO dishFlavorDTO = new DishFlavorDTO();
                    dishFlavorDTO.setId(f.getId());
                    dishFlavorDTO.setName(f.getName());
                    dishFlavorDTO.setValue(f.getValue());
                    return dishFlavorDTO;
                })
                .collect(Collectors.toList());
        dto.setFlavors(flavorDTOs);

        return dto;
    }

    public List<Dish> getDishesByCategoryId(Long categoryId) {
        return dishRepository.findByCategoryId(categoryId);
    }

    public List<DishDTO> getAvailableDishesByCategoryId(Long categoryId) {
        List<Dish> dishes = dishRepository.findByCategoryId(categoryId).stream()
                .filter(Dish::isAvailable)
                .collect(Collectors.toList());

        return dishes.stream()
                .map(dish -> {
                    DishDTO dto = new DishDTO();
                    dto.setId(dish.getId());
                    dto.setName(dish.getName());
                    dto.setCategoryId(dish.getCategoryId());
                    dto.setPrice(dish.getPrice());
                    dto.setImage(dish.getImage());
                    dto.setDescription(dish.getDescription());
                    dto.setStatus(dish.getStatus());

                    List<DishFlavor> flavors = dishFlavorRepository.findByDishId(dish.getId());
                    List<DishFlavorDTO> flavorDTOs = flavors.stream()
                            .map(f -> {
                                DishFlavorDTO dishFlavorDTO = new DishFlavorDTO();
                                dishFlavorDTO.setId(f.getId());
                                dishFlavorDTO.setName(f.getName());
                                dishFlavorDTO.setValue(f.getValue());
                                return dishFlavorDTO;
                            })
                            .collect(Collectors.toList());
                    dto.setFlavors(flavorDTOs);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    public void updateStatus(Long id, Integer status, Long operatorId) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new CustomException("菜品不存在"));
        if (status != null && status == 1) {
            dish.enable();
        } else {
            dish.disable();
        }
        dish.setUpdateTime(LocalDateTime.now());
        dish.setUpdateUser(operatorId);
        dishRepository.save(dish);
    }

    public String getDishImage(Long dishId) {
        return dishRepository.findById(dishId)
                .map(Dish::getImage)
                .orElse(null);
    }

    public Page<DishDTO> pageByName(int page, int pageSize, String name) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Dish> dishPage = dishRepository.findByNameContaining(name, pageable);
        
        return dishPage.map(dish -> {
            DishDTO dto = new DishDTO();
            dto.setId(dish.getId());
            dto.setName(dish.getName());
            dto.setCategoryId(dish.getCategoryId());
            dto.setPrice(dish.getPrice());
            dto.setCode(dish.getCode());
            dto.setImage(dish.getImage());
            dto.setDescription(dish.getDescription());
            dto.setStatus(dish.getStatus());
            dto.setSort(dish.getSort());
            dto.setCreateTime(dish.getCreateTime());
            dto.setUpdateTime(dish.getUpdateTime());
            
            String categoryName = categoryRepository.findById(dish.getCategoryId())
                    .map(Category::getName)
                    .orElse(null);
            dto.setCategoryName(categoryName);
            
            return dto;
        });
    }

    @Transactional
    public void batchDelete(List<Long> ids) {
        for (Long id : ids) {
            Dish dish = dishRepository.findById(id)
                    .orElseThrow(() -> new CustomException("菜品不存在"));

            dishFlavorRepository.deleteByDishId(id);
            dishRepository.deleteById(id);
        }
    }

    public List<Dish> searchByName(String keyword) {
        return dishRepository.findByNameContaining(keyword, PageRequest.of(0, 100)).getContent();
    }

    public Dish getById(Long id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new CustomException("菜品不存在"));
    }

    public List<Dish> getByCategoryId(Long categoryId) {
        return dishRepository.findByCategoryId(categoryId);
    }

}