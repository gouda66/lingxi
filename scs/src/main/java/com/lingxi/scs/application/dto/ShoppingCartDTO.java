package com.lingxi.scs.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车数据传输对象
 * 
 * <p>DTO模式说明：</p>
 * <ul>
 *     <li>用于数据传输和展示</li>
 *     <li>主要用于查询操作的结果返回</li>
 *     <li>可以组合多个实体的数据</li>
 *     <li>不包含业务逻辑，只是数据容器</li>
 *     <li>命名规范：名词 + DTO</li>
 * </ul>
 *
 * @author system
 */
@Data
public class ShoppingCartDTO {

    /**
     * 购物车项ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 菜品ID
     */
    private Long dishId;

    /**
     * 套餐ID
     */
    private Long setmealId;

    /**
     * 菜品口味
     */
    private String dishFlavor;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 单价
     */
    private BigDecimal amount;

    /**
     * 小计（单价 * 数量）
     */
    private BigDecimal subtotal;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 商品类型（DISH-菜品，SETMEAL-套餐）
     */
    private String itemType;

    /**
     * 计算小计
     */
    public void calculateSubtotal() {
        if (amount != null && number != null) {
            this.subtotal = amount.multiply(BigDecimal.valueOf(number));
        }
    }
}