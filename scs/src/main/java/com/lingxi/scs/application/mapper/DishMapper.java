package com.lingxi.scs.application.mapper;

import com.lingxi.scs.application.command.CreateDishCommand;
import com.lingxi.scs.application.command.DishFlavorCommand;
import com.lingxi.scs.application.dto.DishDTO;
import com.lingxi.scs.application.dto.DishFlavorDTO;
import com.lingxi.scs.domain.model.entity.Dish;
import com.lingxi.scs.domain.model.entity.DishFlavor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品映射器
 * 展示复杂对象的映射处理
 *
 * @author system
 */
@Component
public class DishMapper {

    /**
     * 将菜品实体转换为DTO
     * 
     * @param dish 菜品实体
     * @param flavors 口味列表
     * @return 菜品DTO
     */
    public DishDTO toDTO(Dish dish, List<DishFlavor> flavors) {
        if (dish == null) {
            return null;
        }

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

        // 设置状态文本
        dto.setStatusText();

        // 转换口味列表
        if (flavors != null && !flavors.isEmpty()) {
            List<DishFlavorDTO> flavorDTOs = flavors.stream()
                    .map(this::toFlavorDTO)
                    .collect(Collectors.toList());
            dto.setFlavors(flavorDTOs);
        }

        return dto;
    }

    /**
     * 口味实体转DTO
     */
    private DishFlavorDTO toFlavorDTO(DishFlavor flavor) {
        DishFlavorDTO dishFlavorDTO = new DishFlavorDTO();
        dishFlavorDTO.setId(flavor.getId());
        dishFlavorDTO.setName(flavor.getName());
        dishFlavorDTO.setValue(flavor.getValue());
        return dishFlavorDTO;
    }

    /**
     * 将创建命令转换为实体
     * 
     * @param command 创建菜品命令
     * @param userId 当前用户ID
     * @return 菜品实体
     */
    public Dish toEntity(CreateDishCommand command, Long userId) {
        if (command == null) {
            return null;
        }

        Dish dish = new Dish();
        dish.setName(command.getName());
        dish.setCategoryId(command.getCategoryId());
        dish.setPrice(command.getPrice());
        dish.setCode(command.getCode());
        dish.setImage(command.getImage());
        dish.setDescription(command.getDescription());
        dish.setSort(command.getSort());
        dish.setStatus(1); // 默认起售

        // 设置创建和更新信息
        LocalDateTime now = LocalDateTime.now();
        dish.setCreateTime(now);
        dish.setUpdateTime(now);
        dish.setCreateUser(userId);
        dish.setUpdateUser(userId);

        return dish;
    }

    /**
     * 将命令中的口味转换为实体
     * 
     * @param flavorCommand 口味命令
     * @param dishId 菜品ID
     * @return 口味实体
     */
    public DishFlavor toFlavorEntity(DishFlavorCommand flavorCommand, Long dishId) {
        if (flavorCommand == null) {
            return null;
        }

        DishFlavor flavor = new DishFlavor();
        flavor.setDishId(dishId);
        flavor.setName(flavorCommand.getName());
        flavor.setValue(flavorCommand.getValue());

        return flavor;
    }

    /**
     * 批量转换口味命令为实体
     */
    public List<DishFlavor> toFlavorEntities(List<DishFlavorCommand> commands, Long dishId) {
        if (commands == null) {
            return null;
        }

        return commands.stream()
                .map(cmd -> toFlavorEntity(cmd, dishId))
                .collect(Collectors.toList());
    }

    public Dish toEntity(DishDTO dto) {
        Dish dish = new Dish();
        dish.setId(dto.getId());
        dish.setName(dto.getName());
        dish.setCategoryId(dto.getCategoryId());
        dish.setPrice(dto.getPrice());
        dish.setCode(dto.getCode());
        dish.setImage(dto.getImage());
        dish.setDescription(dto.getDescription());
        dish.setStatus(dto.getStatus());
        dish.setSort(dto.getSort());
        return dish;
    }

    /**
     * 转换口味DTO列表为实体列表
     */
    public List<DishFlavor> toFlavorEntities(List<DishFlavorDTO> flavorDTOs) {
        if (flavorDTOs == null) {
            return List.of();
        }
        return flavorDTOs.stream()
                .map(dto -> {
                    DishFlavor flavor = new DishFlavor();
                    flavor.setId(dto.getId());
                    flavor.setName(dto.getName());
                    flavor.setValue(dto.getValue());
                    return flavor;
                })
                .toList();
    }

    /**
     * 转换实体为DTO
     */
    public DishDTO toDTO(Dish dish) {
        DishDTO dto = new DishDTO();
        dto.setId(dish.getId());
        dto.setName(dish.getName());
        dto.setCategoryId(dish.getCategoryId());
        dto.setPrice(dish.getPrice());
        dto.setImage(dish.getImage());
        dto.setDescription(dish.getDescription());
        dto.setStatus(dish.getStatus());
        return dto;
    }
}