package com.lingxi.scs.interfaces.mcp;

import com.lingxi.scs.application.service.DishApplicationService;
import com.lingxi.scs.application.service.OrderApplicationService;
import com.lingxi.scs.application.service.SetmealApplicationService;
import com.lingxi.scs.application.dto.DishDTO;
import com.lingxi.scs.application.dto.McpDishDTO;
import com.lingxi.scs.domain.model.entity.Dish;
import com.lingxi.scs.domain.model.entity.Setmeal;
import com.lingxi.scs.domain.model.entity.Orders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScsMcpToolService {

    private final DishApplicationService dishService;
    private final SetmealApplicationService setmealService;
    private final OrderApplicationService orderService;

    public ScsMcpToolService(DishApplicationService dishService, SetmealApplicationService setmealService, OrderApplicationService orderService) {
        this.dishService = dishService;
        this.setmealService = setmealService;
        this.orderService = orderService;
    }

    @Tool(description = "查询所有菜品列表，支持按名称搜索")
    public List<McpDishDTO> searchDishes(
            @ToolParam(description = "菜品名称关键词，可选") String keyword) {
        log.info("AI 查询菜品: {}", keyword);
        List<DishDTO> dishDTOs;
        if (keyword != null && !keyword.isEmpty()) {
            dishDTOs = dishService.pageByName(1, 100, keyword).getContent();
        } else {
            dishDTOs = dishService.getAllDishes().stream()
                    .map(dish -> {
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
                        return dto;
                    })
                    .collect(Collectors.toList());
        }
        // 转换为 MCP 专用 DTO（Long -> String）
        return dishDTOs.stream()
                .map(McpDishDTO::from)
                .collect(Collectors.toList());
    }

    @Tool(description = "获取菜品详细信息")
    public McpDishDTO getDishDetail(
            @ToolParam(description = "菜品ID") Long dishId) {
        log.info("AI 查询菜品详情: {}", dishId);
        DishDTO dishDTO = dishService.getDishWithFlavorById(dishId);
        return McpDishDTO.from(dishDTO);
    }

    @Tool(description = "查询所有套餐列表")
    public List<Setmeal> getAllSetmeals() {
        log.info("AI 查询所有套餐");
        return setmealService.getAllSetmeals();
    }

    @Tool(description = "根据分类查询菜品")
    public List<McpDishDTO> getDishesByCategory(
            @ToolParam(description = "分类ID") Long categoryId) {
        log.info("AI 按分类查询菜品: {}", categoryId);
        List<DishDTO> dishDTOs = dishService.getAvailableDishesByCategoryId(categoryId);
        // 转换为 MCP 专用 DTO（Long -> String）
        return dishDTOs.stream()
                .map(McpDishDTO::from)
                .collect(Collectors.toList());
    }

    @Tool(description = "获取订单统计信息")
    public Map<String, Object> getOrderStatistics() {
        log.info("AI 查询订单统计");
        return orderService.getStatistics();
    }

    @Tool(description = "查询最近订单列表")
    public List<Orders> getRecentOrders(
            @ToolParam(description = "数量，默认10") Integer limit) {
        log.info("AI 查询最近订单: {}", limit);
        return orderService.getRecentOrders(limit != null ? limit : 10);
    }
}
