package com.lingxi.scs.domain.repository;

import com.lingxi.scs.domain.model.entity.OrderDetail;

import java.time.LocalDateTime;
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

    /**
     * 统计菜品在指定时间后的销量（已完成订单）
     *
     * @param dishId 菜品ID
     * @param startTime 开始时间
     * @return 销量数量
     */
    Long countSalesByDishId(Long dishId, LocalDateTime startTime);

    /**
     * 统计菜品在指定时间后的总订单数
     *
     * @param dishId 菜品ID
     * @param startTime 开始时间
     * @return 订单数量
     */
    Long countTotalOrdersByDishId(Long dishId, LocalDateTime startTime);

    /**
     * 统计菜品在指定时间后的已完成订单数
     *
     * @param dishId 菜品ID
     * @param startTime 开始时间
     * @return 完成订单数量
     */
    Long countCompletedOrdersByDishId(Long dishId, LocalDateTime startTime);
}
