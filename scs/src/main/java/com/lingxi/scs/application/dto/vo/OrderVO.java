package com.lingxi.scs.application.dto.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单视图对象
 *
 * @author system
 * @param id 订单ID
 * @param number 订单号
 * @param status 订单状态
 * @param statusText 状态文本
 * @param amount 订单金额
 * @param orderTime 下单时间
 * @param checkoutTime 结账时间
 * @param consignee 收货人
 * @param phone 手机号
 * @param address 送货地址
 * @param remark 备注
 * @param itemCount 商品数量
 * @param orderDetails 订单详情列表
 */
public record OrderVO(
        @JsonSerialize(using = ToStringSerializer.class) Long id,
        String number,
        Integer status,
        String statusText,
        BigDecimal amount,
        LocalDateTime orderTime,
        LocalDateTime checkoutTime,
        String consignee,
        String phone,
        String address,
        String remark,
        Integer itemCount,
        List<OrderDetailVO> orderDetails
) {}