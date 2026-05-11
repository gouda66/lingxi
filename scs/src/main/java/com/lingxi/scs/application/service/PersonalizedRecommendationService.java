package com.lingxi.scs.application.service;

import com.lingxi.scs.application.dto.RecommendDishDTO;
import com.lingxi.scs.application.dto.UserProfileDTO;
import com.lingxi.scs.domain.model.entity.*;
import com.lingxi.scs.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 个性化菜品推荐服务
 * 基于用户画像实现千人千面的智能推荐
 *
 * @author system
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalizedRecommendationService {

    private final DishRepository dishRepository;
    private final SetmealRepository setmealRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final DishFlavorRepository dishFlavorRepository;
    private final CategoryRepository categoryRepository;

    // 推荐算法权重
    private static final double USER_PREFERENCE_WEIGHT = 0.5;  // 用户偏好权重 50%
    private static final double COLLABORATIVE_WEIGHT = 0.3;     // 协同过滤权重 30%
    private static final double POPULARITY_WEIGHT = 0.2;        // 热度权重 20%

    /**
     * 获取个性化推荐菜品
     *
     * @param userId 用户ID
     * @param limit 返回数量
     * @return 推荐菜品列表
     */
    public List<RecommendDishDTO> getPersonalizedRecommendations(Long userId, int limit) {
        log.info("开始为用户 {} 生成个性化推荐", userId);

        // 1. 构建用户画像
        UserProfileDTO userProfile = buildUserProfile(userId);

        // 2. 获取所有可用菜品
        List<Dish> availableDishes = dishRepository.findAll().stream()
                .filter(Dish::isAvailable)
                .collect(Collectors.toList());

        if (availableDishes.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 计算每个菜品的个性化推荐分数
        List<RecommendDishDTO> scoredDishes = availableDishes.stream()
                .map(dish -> calculatePersonalizedScore(dish, userProfile))
                .filter(dto -> dto != null)
                .sorted((a, b) -> Double.compare(b.getRecommendScore(), a.getRecommendScore()))
                .limit(limit)
                .collect(Collectors.toList());

        log.info("为用户 {} 生成 {} 个个性化推荐", userId, scoredDishes.size());
        return scoredDishes;
    }

    /**
     * 构建用户画像
     */
    private UserProfileDTO buildUserProfile(Long userId) {
        UserProfileDTO profile = new UserProfileDTO();
        profile.setUserId(userId);

        // 查询用户所有已完成订单
        List<Orders> userOrders = orderRepository.findByUserIdAndStatus(userId, 5);
        
        if (userOrders.isEmpty()) {
            // 新用户，返回空画像
            profile.setTotalOrders(0);
            profile.setUserTags(Collections.singletonList("新用户"));
            return profile;
        }

        // 基本统计
        profile.setTotalOrders(userOrders.size());
        BigDecimal totalSpent = userOrders.stream()
                .map(Orders::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        profile.setTotalSpent(totalSpent);
        profile.setAvgOrderAmount(totalSpent.divide(BigDecimal.valueOf(userOrders.size()), 2, BigDecimal.ROUND_HALF_UP));

        // 分析菜品偏好
        analyzeDishPreferences(userId, userOrders, profile);

        // 分析口味偏好
        analyzeFlavorPreferences(userId, userOrders, profile);

        // 分析价格偏好
        analyzePricePreferences(userOrders, profile);

        // 分析分类偏好
        analyzeCategoryPreferences(userId, userOrders, profile);

        // 分析时间段偏好
        analyzeTimePreferences(userOrders, profile);

        // 生成用户标签
        generateUserTags(profile);

        return profile;
    }

    /**
     * 分析菜品偏好
     */
    private void analyzeDishPreferences(Long userId, List<Orders> orders, UserProfileDTO profile) {
        Map<Long, Integer> dishCountMap = new HashMap<>();
        Map<Long, Integer> setmealCountMap = new HashMap<>();

        for (Orders order : orders) {
            List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
            for (OrderDetail detail : details) {
                if (detail.getDishId() != null) {
                    dishCountMap.merge(detail.getDishId(), detail.getNumber(), Integer::sum);
                }
                if (detail.getSetmealId() != null) {
                    setmealCountMap.merge(detail.getSetmealId(), detail.getNumber(), Integer::sum);
                }
            }
        }

        // TOP5 菜品
        profile.setFavoriteDishes(dishCountMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                )));

        // TOP5 套餐
        profile.setFavoriteSetmeals(setmealCountMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                )));
    }

    /**
     * 分析口味偏好
     */
    private void analyzeFlavorPreferences(Long userId, List<Orders> orders, UserProfileDTO profile) {
        Map<String, Integer> flavorCountMap = new HashMap<>();

        for (Orders order : orders) {
            List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
            for (OrderDetail detail : details) {
                if (detail.getDishFlavor() != null && !detail.getDishFlavor().isEmpty()) {
                    String[] flavors = detail.getDishFlavor().split(",");
                    for (String flavor : flavors) {
                        flavorCountMap.merge(flavor.trim(), 1, Integer::sum);
                    }
                }
            }
        }

        // 取出现频率最高的3种口味
        profile.setPreferredFlavors(flavorCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()));
    }

    /**
     * 分析价格偏好
     */
    private void analyzePricePreferences(List<Orders> orders, UserProfileDTO profile) {
        List<BigDecimal> prices = orders.stream()
                .flatMap(order -> orderDetailRepository.findByOrderId(order.getId()).stream())
                .map(OrderDetail::getAmount)
                .filter(price -> price != null && price.compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        if (!prices.isEmpty()) {
            prices.sort(BigDecimal::compareTo);
            int size = prices.size();
            
            // 取中间60%的价格区间作为偏好区间
            int startIdx = (int) (size * 0.2);
            int endIdx = (int) (size * 0.8);
            
            profile.setMinPreferredPrice(prices.get(startIdx));
            profile.setMaxPreferredPrice(prices.get(endIdx));
        }
    }

    /**
     * 分析分类偏好
     */
    private void analyzeCategoryPreferences(Long userId, List<Orders> orders, UserProfileDTO profile) {
        Map<Long, Integer> categoryCountMap = new HashMap<>();

        for (Orders order : orders) {
            List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
            for (OrderDetail detail : details) {
                if (detail.getDishId() != null) {
                    Dish dish = dishRepository.findById(detail.getDishId()).orElse(null);
                    if (dish != null && dish.getCategoryId() != null) {
                        categoryCountMap.merge(dish.getCategoryId(), 1, Integer::sum);
                    }
                }
            }
        }

        // TOP3 分类
        profile.setPreferredCategories(categoryCountMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()));
    }

    /**
     * 分析时间段偏好
     */
    private void analyzeTimePreferences(List<Orders> orders, UserProfileDTO profile) {
        Map<Integer, Integer> hourCountMap = new HashMap<>();

        for (Orders order : orders) {
            if (order.getOrderTime() != null) {
                int hour = order.getOrderTime().getHour();
                hourCountMap.merge(hour, 1, Integer::sum);
            }
        }

        // TOP3 时间段
        profile.setPreferredTimeSlots(hourCountMap.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()));
    }

    /**
     * 生成用户标签
     */
    private void generateUserTags(UserProfileDTO profile) {
        List<String> tags = new ArrayList<>();

        // 基于订单数
        if (profile.getTotalOrders() > 50) {
            tags.add("忠实粉丝");
        } else if (profile.getTotalOrders() > 20) {
            tags.add("常客");
        } else if (profile.getTotalOrders() > 5) {
            tags.add("活跃用户");
        }

        // 基于客单价
        if (profile.getAvgOrderAmount() != null) {
            if (profile.getAvgOrderAmount().compareTo(new BigDecimal("50")) > 0) {
                tags.add("高端消费者");
            } else if (profile.getAvgOrderAmount().compareTo(new BigDecimal("20")) < 0) {
                tags.add("性价比追求者");
            }
        }

        // 基于口味
        if (profile.getPreferredFlavors() != null) {
            if (profile.getPreferredFlavors().contains("辣")) {
                tags.add("辣味爱好者");
            }
            if (profile.getPreferredFlavors().contains("甜")) {
                tags.add("甜食控");
            }
        }

        // 基于新品尝试
        if (profile.getTotalOrders() > 10) {
            tags.add("美食探索家");
        }

        if (tags.isEmpty()) {
            tags.add("普通用户");
        }

        profile.setUserTags(tags);
    }

    /**
     * 计算个性化推荐分数
     */
    private RecommendDishDTO calculatePersonalizedScore(Dish dish, UserProfileDTO profile) {
        try {
            RecommendDishDTO dto = new RecommendDishDTO();
            dto.setId(dish.getId());
            dto.setName(dish.getName());
            dto.setCategoryId(dish.getCategoryId());
            dto.setPrice(dish.getPrice());
            dto.setImage(dish.getImage());
            dto.setDescription(dish.getDescription());
            dto.setItemType("DISH");

            // 获取分类名称
            if (dish.getCategoryId() != null) {
                Category category = categoryRepository.findById(dish.getCategoryId()).orElse(null);
                if (category != null) {
                    dto.setCategoryName(category.getName());
                }
            }

            // 1. 用户偏好分数（0-100）
            double preferenceScore = calculatePreferenceScore(dish, profile);

            // 2. 协同过滤分数（简化版：相似用户也喜欢）
            double collaborativeScore = calculateCollaborativeScore(dish, profile);

            // 3. 热度分数
            double popularityScore = calculatePopularityScore(dish.getId());

            // 加权总分
            double totalScore = preferenceScore * USER_PREFERENCE_WEIGHT
                    + collaborativeScore * COLLABORATIVE_WEIGHT
                    + popularityScore * POPULARITY_WEIGHT;

            dto.setRecommendScore(Math.round(totalScore * 100.0) / 100.0);

            // 月销量
            dto.setMonthlySales(getMonthlySales(dish.getId()));

            // 生成个性化推荐理由
            dto.setRecommendReason(generatePersonalizedReason(dish, profile, preferenceScore));

            return dto;
        } catch (Exception e) {
            log.error("计算个性化推荐分数失败，dishId: {}", dish.getId(), e);
            return null;
        }
    }

    /**
     * 计算用户偏好分数
     */
    private double calculatePreferenceScore(Dish dish, UserProfileDTO profile) {
        double score = 0.0;

        // 1. 是否是用户常点的菜品
        if (profile.getFavoriteDishes() != null && profile.getFavoriteDishes().containsKey(dish.getId())) {
            score += 40; // 直接加分
        }

        // 2. 是否属于用户偏好的分类
        if (profile.getPreferredCategories() != null && 
            profile.getPreferredCategories().contains(dish.getCategoryId())) {
            score += 25;
        }

        // 3. 价格是否在偏好区间
        if (profile.getMinPreferredPrice() != null && profile.getMaxPreferredPrice() != null) {
            if (dish.getPrice().compareTo(profile.getMinPreferredPrice()) >= 0 &&
                dish.getPrice().compareTo(profile.getMaxPreferredPrice()) <= 0) {
                score += 20;
            }
        }

        // 4. 口味匹配度
        if (profile.getPreferredFlavors() != null && !profile.getPreferredFlavors().isEmpty()) {
            List<DishFlavor> flavors = dishFlavorRepository.findByDishId(dish.getId());
            for (DishFlavor flavor : flavors) {
                if (flavor.getValue() != null) {
                    for (String prefFlavor : profile.getPreferredFlavors()) {
                        if (flavor.getValue().contains(prefFlavor)) {
                            score += 15;
                            break;
                        }
                    }
                }
            }
        }

        return Math.min(score, 100.0);
    }

    /**
     * 计算协同过滤分数（简化版）
     */
    private double calculateCollaborativeScore(Dish dish, UserProfileDTO profile) {
        // TODO: 完整实现需要找到相似用户，这里简化为基于全局热度
        // 实际应该：找到与当前用户品味相似的其他用户，看他们喜欢什么
        
        Long salesCount = orderDetailRepository.countSalesByDishId(
                dish.getId(), 
                LocalDateTime.now().minusDays(30)
        );

        if (salesCount == null || salesCount == 0) {
            return 0.0;
        }

        // 归一化到0-100
        return Math.min(salesCount / 10.0, 100.0);
    }

    /**
     * 计算热度分数
     */
    private double calculatePopularityScore(Long dishId) {
        Long salesCount = orderDetailRepository.countSalesByDishId(
                dishId, 
                LocalDateTime.now().minusDays(30)
        );

        if (salesCount == null || salesCount == 0) {
            return 0.0;
        }

        return Math.min(salesCount / 10.0, 100.0);
    }

    /**
     * 获取月销量
     */
    private int getMonthlySales(Long dishId) {
        Long salesCount = orderDetailRepository.countSalesByDishId(
                dishId, 
                LocalDateTime.now().minusDays(30)
        );
        return salesCount != null ? salesCount.intValue() : 0;
    }

    /**
     * 生成个性化推荐理由
     */
    private String generatePersonalizedReason(Dish dish, UserProfileDTO profile, double preferenceScore) {
        List<String> reasons = new ArrayList<>();

        // 基于用户标签
        if (profile.getUserTags() != null) {
            if (profile.getUserTags().contains("辣味爱好者")) {
                List<DishFlavor> flavors = dishFlavorRepository.findByDishId(dish.getId());
                for (DishFlavor flavor : flavors) {
                    if (flavor.getValue() != null && flavor.getValue().contains("辣")) {
                        reasons.add("🌶️ 符合您的辣味偏好");
                        break;
                    }
                }
            }

            if (profile.getUserTags().contains("性价比追求者") && 
                dish.getPrice().compareTo(new BigDecimal("20")) < 0) {
                reasons.add("💰 超值优惠");
            }
        }

        // 基于偏好匹配
        if (preferenceScore >= 60) {
            reasons.add("❤️ 根据您的喜好推荐");
        }

        // 基于分类
        if (profile.getPreferredCategories() != null && 
            profile.getPreferredCategories().contains(dish.getCategoryId())) {
            reasons.add("🍽️ 您常点的类型");
        }

        if (reasons.isEmpty()) {
            return "为您推荐";
        }

        return String.join(" ", reasons.subList(0, Math.min(2, reasons.size())));
    }
}
