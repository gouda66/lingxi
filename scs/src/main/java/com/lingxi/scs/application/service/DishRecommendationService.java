package com.lingxi.scs.application.service;

import com.lingxi.scs.application.dto.RecommendDishDTO;
import com.lingxi.scs.domain.model.entity.Dish;
import com.lingxi.scs.domain.model.entity.OrderDetail;
import com.lingxi.scs.domain.model.entity.Orders;
import com.lingxi.scs.domain.repository.DishRepository;
import com.lingxi.scs.domain.repository.OrderDetailRepository;
import com.lingxi.scs.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜品推荐服务
 * 基于多维度算法智能推荐菜品
 *
 * @author system
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DishRecommendationService {

    private final DishRepository dishRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    // 推荐算法权重配置
    private static final double SALES_WEIGHT = 0.4;      // 销量权重 40%
    private static final double RATING_WEIGHT = 0.3;     // 评分权重 30%
    private static final double NEWNESS_WEIGHT = 0.15;   // 新品权重 15%
    private static final double PRICE_WEIGHT = 0.15;     // 价格权重 15%

    /**
     * 获取推荐菜品列表
     *
     * @param limit 返回数量，默认10
     * @return 推荐菜品列表（按推荐分数降序）
     */
    public List<RecommendDishDTO> getRecommendedDishes(int limit) {
        log.info("开始计算菜品推荐，请求数量: {}", limit);

        // 1. 获取所有起售的菜品
        List<Dish> availableDishes = dishRepository.findAll().stream()
                .filter(Dish::isAvailable)
                .collect(Collectors.toList());

        if (availableDishes.isEmpty()) {
            log.warn("没有可用的菜品");
            return Collections.emptyList();
        }

        // 2. 计算每个菜品的推荐分数
        List<RecommendDishDTO> scoredDishes = availableDishes.stream()
                .map(this::calculateRecommendScore)
                .filter(dto -> dto != null)
                .sorted((a, b) -> Double.compare(b.getRecommendScore(), a.getRecommendScore()))
                .limit(limit)
                .collect(Collectors.toList());

        log.info("推荐菜品计算完成，共 {} 个菜品", scoredDishes.size());
        return scoredDishes;
    }

    /**
     * 计算单个菜品的推荐分数
     *
     * @param dish 菜品实体
     * @return 推荐DTO
     */
    private RecommendDishDTO calculateRecommendScore(Dish dish) {
        try {
            RecommendDishDTO dto = new RecommendDishDTO();
            dto.setId(dish.getId());
            dto.setName(dish.getName());
            dto.setCategoryId(dish.getCategoryId());
            dto.setPrice(dish.getPrice());
            dto.setImage(dish.getImage());
            dto.setDescription(dish.getDescription());
            dto.setItemType("DISH");

            // 计算各维度分数
            double salesScore = calculateSalesScore(dish.getId());
            double ratingScore = calculateRatingScore(dish.getId());
            double newnessScore = calculateNewnessScore(dish.getCreateTime());
            double priceScore = calculatePriceScore(dish.getPrice());

            // 加权计算总分
            double totalScore = salesScore * SALES_WEIGHT
                    + ratingScore * RATING_WEIGHT
                    + newnessScore * NEWNESS_WEIGHT
                    + priceScore * PRICE_WEIGHT;

            dto.setRecommendScore(Math.round(totalScore * 100.0) / 100.0);

            // 计算月销量
            dto.setMonthlySales(getMonthlySales(dish.getId()));

            // 生成推荐理由
            dto.setRecommendReason(generateRecommendReason(salesScore, ratingScore, newnessScore, priceScore));

            return dto;
        } catch (Exception e) {
            log.error("计算菜品推荐分数失败，dishId: {}", dish.getId(), e);
            return null;
        }
    }

    /**
     * 计算销量分数（0-100）
     * 基于最近30天的订单数量
     */
    private double calculateSalesScore(Long dishId) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        // 查询最近30天包含该菜品的已完成订单
        Long salesCount = orderDetailRepository.countSalesByDishId(dishId, thirtyDaysAgo);
        
        if (salesCount == null || salesCount == 0) {
            return 0.0;
        }

        // 归一化：假设最高销量为1000份对应100分
        double normalizedScore = Math.min(salesCount / 10.0, 100.0);
        return normalizedScore;
    }

    /**
     * 计算评分分数（0-100）
     * 基于订单完成率（简化版，实际应该用用户评价）
     */
    private double calculateRatingScore(Long dishId) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        // 查询该菜品的总订单数
        Long totalOrders = orderDetailRepository.countTotalOrdersByDishId(dishId, thirtyDaysAgo);
        
        if (totalOrders == null || totalOrders == 0) {
            return 50.0; // 新菜品给中等分数
        }

        // 查询已完成的订单数（状态=5表示已完成）
        Long completedOrders = orderDetailRepository.countCompletedOrdersByDishId(dishId, thirtyDaysAgo);
        
        if (completedOrders == null) {
            completedOrders = 0L;
        }

        // 完成率 = 完成订单数 / 总订单数
        double completionRate = (double) completedOrders / totalOrders;
        return completionRate * 100.0;
    }

    /**
     * 计算新品分数（0-100）
     * 上架时间越近，分数越高
     */
    private double calculateNewnessScore(LocalDateTime createTime) {
        if (createTime == null) {
            return 0.0;
        }

        long daysSinceCreated = java.time.Duration.between(createTime, LocalDateTime.now()).toDays();

        if (daysSinceCreated <= 7) {
            return 100.0; // 7天内上架，满分
        } else if (daysSinceCreated <= 30) {
            return 80.0 - (daysSinceCreated - 7) * 0.67; // 30天内线性递减
        } else if (daysSinceCreated <= 90) {
            return 60.0 - (daysSinceCreated - 30) * 0.33; // 90天内继续递减
        } else {
            return Math.max(20.0, 50.0 - (daysSinceCreated - 90) * 0.1); // 超过90天最低20分
        }
    }

    /**
     * 计算价格分数（0-100）
     * 性价比高的菜品得分更高（假设10-30元为最佳价格区间）
     */
    private double calculatePriceScore(java.math.BigDecimal price) {
        if (price == null || price.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return 0.0;
        }

        double priceValue = price.doubleValue();

        // 最佳价格区间：10-30元
        if (priceValue >= 10 && priceValue <= 30) {
            return 100.0;
        } else if (priceValue < 10) {
            // 低于10元，价格越低分数越低（可能质量不好）
            return priceValue * 10.0;
        } else if (priceValue <= 50) {
            // 30-50元，线性递减
            return 100.0 - (priceValue - 30) * 2.5;
        } else {
            // 超过50元，分数较低
            return Math.max(10.0, 50.0 - (priceValue - 50) * 0.5);
        }
    }

    /**
     * 获取菜品月销量
     */
    private int getMonthlySales(Long dishId) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Long salesCount = orderDetailRepository.countSalesByDishId(dishId, thirtyDaysAgo);
        return salesCount != null ? salesCount.intValue() : 0;
    }

    /**
     * 生成推荐理由
     */
    private String generateRecommendReason(double salesScore, double ratingScore, 
                                           double newnessScore, double priceScore) {
        List<String> reasons = new ArrayList<>();

        if (salesScore >= 70) {
            reasons.add("🔥 热销爆款");
        } else if (salesScore >= 50) {
            reasons.add("👍 人气好菜");
        }

        if (ratingScore >= 80) {
            reasons.add("⭐ 好评如潮");
        } else if (ratingScore >= 60) {
            reasons.add("✅ 品质保证");
        }

        if (newnessScore >= 80) {
            reasons.add("✨ 新品上市");
        }

        if (priceScore >= 80) {
            reasons.add("💰 超值优惠");
        } else if (priceScore >= 60) {
            reasons.add("💎 性价比高");
        }

        if (reasons.isEmpty()) {
            return "推荐尝试";
        }

        // 最多返回2个理由
        return String.join(" ", reasons.subList(0, Math.min(2, reasons.size())));
    }
}
