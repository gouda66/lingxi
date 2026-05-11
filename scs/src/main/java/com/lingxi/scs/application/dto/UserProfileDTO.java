package com.lingxi.scs.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户画像 DTO
 * 用于个性化推荐算法
 *
 * @author system
 */
@Data
public class UserProfileDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 总订单数
     */
    private Integer totalOrders;

    /**
     * 总消费金额
     */
    private BigDecimal totalSpent;

    /**
     * 平均客单价
     */
    private BigDecimal avgOrderAmount;

    /**
     * 最常点的菜品TOP5（菜品ID -> 次数）
     */
    private Map<Long, Integer> favoriteDishes;

    /**
     * 最常点的套餐TOP5（套餐ID -> 次数）
     */
    private Map<Long, Integer> favoriteSetmeals;

    /**
     * 偏好的口味列表
     */
    private List<String> preferredFlavors;

    /**
     * 偏好的价格区间（最小值）
     */
    private BigDecimal minPreferredPrice;

    /**
     * 偏好的价格区间（最大值）
     */
    private BigDecimal maxPreferredPrice;

    /**
     * 常点分类ID列表
     */
    private List<Long> preferredCategories;

    /**
     * 下单时间段偏好（0-23小时）
     */
    private List<Integer> preferredTimeSlots;

    /**
     * 用户标签（如：辣味爱好者、性价比追求者、新品尝试者等）
     */
    private List<String> userTags;
}
