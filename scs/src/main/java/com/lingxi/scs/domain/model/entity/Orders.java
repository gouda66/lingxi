package com.lingxi.scs.domain.model.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 *
 * @author system
 */
@Data
@Entity
@Table(name = "t_order")
@Comment("订单表")
public class Orders implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Comment("主键ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Column(name = "number", length = 50)
    @Comment("订单号")
    private String number;

    @Column(name = "status", nullable = false)
    @Comment("订单状态 1待付款 2待派送 3已派送 4已完成 5已取消")
    private Integer status;

    @Column(name = "user_id", nullable = false)
    @Comment("下单用户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @Column(name = "address_book_id", nullable = false)
    @Comment("地址ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long addressBookId;

    @Column(name = "order_time", nullable = false)
    @Comment("下单时间")
    private LocalDateTime orderTime;

    @Column(name = "checkout_time", nullable = false)
    @Comment("结账时间")
    private LocalDateTime checkoutTime;

    @Column(name = "pay_method", nullable = false)
    @Comment("支付方式 1微信 2支付宝")
    private Integer payMethod;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    @Comment("实收金额")
    private BigDecimal amount;

    @Column(name = "remark", length = 100)
    @Comment("备注")
    private String remark;

    @Column(name = "user_name", length = 255)
    @Comment("用户姓名")
    private String userName;

    @Column(name = "phone", length = 255)
    @Comment("手机号")
    private String phone;

    @Column(name = "address", length = 255)
    @Comment("送货地址")
    private String address;

    @Column(name = "consignee", length = 255)
    @Comment("收货人")
    private String consignee;
}
