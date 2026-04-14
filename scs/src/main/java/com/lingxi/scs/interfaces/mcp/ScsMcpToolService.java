package com.lingxi.scs.interfaces.mcp;

import com.lingxi.scs.application.service.DishApplicationService;
import com.lingxi.scs.application.service.OrderApplicationService;
import com.lingxi.scs.application.service.SetmealApplicationService;
import com.lingxi.scs.application.dto.DishDTO;
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
    public List<DishDTO> searchDishes(
            @ToolParam(description = "菜品名称关键词，可选") String keyword) {
        log.info("AI 查询菜品: {}", keyword);
        if (keyword != null && !keyword.isEmpty()) {
            return dishService.pageByName(1, 100, keyword).getContent();
        }
        return dishService.getAllDishes().stream()
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

    @Tool(description = "获取菜品详细信息")
    public DishDTO getDishDetail(
            @ToolParam(description = "菜品ID") Long dishId) {
        log.info("AI 查询菜品详情: {}", dishId);
        return dishService.getDishWithFlavorById(dishId);
    }

    @Tool(description = "查询所有套餐列表")
    public List<Setmeal> getAllSetmeals() {
        log.info("AI 查询所有套餐");
        return setmealService.getAllSetmeals();
    }

    @Tool(description = "根据分类查询菜品")
    public List<DishDTO> getDishesByCategory(
            @ToolParam(description = "分类ID") Long categoryId) {
        log.info("AI 按分类查询菜品: {}", categoryId);
        return dishService.getAvailableDishesByCategoryId(categoryId);
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
