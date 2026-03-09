package com.lingxi.scs.interfaces.facade;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.lingxi.scs.application.dto.DishDTO;
import com.lingxi.scs.application.mapper.DishMapper;
import com.lingxi.scs.application.service.DishApplicationService;
import com.lingxi.scs.domain.model.entity.Dish;
import com.lingxi.scs.domain.model.entity.DishFlavor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DishFacade {

    private final DishApplicationService dishService;
    private final DishMapper dishMapper;

    @Autowired
    private SnowflakeGenerator snowflakeGenerator;

    public void addDish(DishDTO dishDTO, Long operatorId) {
        Dish dish = dishMapper.toEntity(dishDTO);
        List<DishFlavor> flavors = dishMapper.toFlavorEntities(dishDTO.getFlavors());
        dish.setId(snowflakeGenerator.next());
        dishService.addDishWithFlavor(dish, flavors, operatorId);
    }

    public void updateDish(DishDTO dishDTO, Long operatorId) {
        Dish dish = dishMapper.toEntity(dishDTO);
        List<DishFlavor> flavors = dishMapper.toFlavorEntities(dishDTO.getFlavors());
        dishService.updateDishWithFlavor(dish, flavors, operatorId);
    }

    public DishDTO getDishById(Long id) {
        return dishService.getDishWithFlavorById(id);
    }

    public List<DishDTO> getDishList(Long categoryId) {
        if (categoryId != null) {
            return dishService.getAvailableDishesByCategoryId(categoryId);
        }
        return dishService.getAllDishes().stream()
                .filter(d -> d.getStatus() == 1)
                .map(dishMapper::toDTO)
                .toList();
    }

    public void updateStatus(Long id, Integer status, Long operatorId) {
        dishService.updateStatus(id, status, operatorId);
    }

    public void batchUpdateStatus(List<Long> ids, Integer status, Long operatorId) {
        for (Long id : ids) {
            dishService.updateStatus(id, status, operatorId);
        }
    }

    public Page<DishDTO> getPageByName(int page, int pageSize, String name) {
        return dishService.pageByName(page, pageSize, name);
    }

    public void batchDelete(List<Long> ids) {
        dishService.batchDelete(ids);
    }
}
