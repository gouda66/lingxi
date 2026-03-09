package com.lingxi.scs.domain.repository;

import com.lingxi.scs.domain.model.entity.OrderDetail;

import java.util.List;
import java.util.Optional;

/**
 * 订单明细仓储接口
 *
 * @author system
 */
public interface OrderDetailRepository {

    /**
     * 保存订单明细
     */
    OrderDetail save(OrderDetail orderDetail);

    /**
     * 批量保存订单明细
     */
    List<OrderDetail> saveAll(List<OrderDetail> orderDetails);

    /**
     * 根据ID查询订单明细
     */
    Optional<OrderDetail> findById(Long id);

    /**
     * 根据订单ID查询明细列表
     */
    List<OrderDetail> findByOrderId(Long orderId);

    /**
     * 查询所有订单明细
     */
    List<OrderDetail> findAll();

    /**
     * 删除订单明细
     */
    void deleteById(Long id);
}
