package com.lingxi.scs.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车汇总数据传输对象
 * 展示组合多个数据的DTO设计
 *
 * @author system
 */
@Data
public class CartSummaryDTO {

    /**
     * 购物车项列表
     */
    private List<ShoppingCartDTO> items;

    /**
     * 商品总数量
     */
    private Integer totalQuantity;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    private BigDecimal payableAmount;

    /**
     * 购物车项数量
     */
    private Integer itemCount;

    /**
     * 计算汇总数据
     */
    public void calculateSummary() {
        if (items == null || items.isEmpty()) {
            this.itemCount = 0;
            this.totalQuantity = 0;
            this.totalAmount = BigDecimal.ZERO;
            this.payableAmount = BigDecimal.ZERO;
            return;
        }

        this.itemCount = items.size();
        this.totalQuantity = items.stream()
                .mapToInt(ShoppingCartDTO::getNumber)
                .sum();
        
        this.totalAmount = items.stream()
                .map(ShoppingCartDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算实付金额（总金额 - 优惠金额）
        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        this.payableAmount = totalAmount.subtract(discount);
    }
}