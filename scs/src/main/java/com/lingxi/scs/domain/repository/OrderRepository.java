package com.lingxi.scs.domain.repository;

import com.lingxi.scs.domain.model.entity.Orders;

import java.util.List;
import java.util.Optional;

/**
 * 订单仓储接口
 *
 * @author system
 */
public interface OrderRepository {

    /**
     * 保存订单
     */
    Orders save(Orders orders);

    /**
     * 根据ID查询订单
     */
    Optional<Orders> findById(Long id);

    /**
     * 根据订单号查询订单
     */
    Optional<Orders> findByNumber(String number);

    /**
     * 根据用户ID查询订单列表
     */
    List<Orders> findByUserId(Long userId);

    /**
     * 根据状态查询订单列表
     */
    List<Orders> findByStatus(Integer status);

    /**
     * 查询所有订单
     */
    List<Orders> findAll();

    /**
     * 删除订单
     */
    void deleteById(Long id);
}
