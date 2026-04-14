package com.lingxi.scs.domain.repository;

import com.lingxi.scs.domain.model.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
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
     * 带条件筛选的分页查询订单
     *
     * @param number 订单号（可选）
     * @param beginTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param pageable 分页参数
     * @return 分页订单数据
     */
    Page<Orders> findAllWithFilters(String number, LocalDateTime beginTime, 
                                     LocalDateTime endTime, Pageable pageable);

    /**
     * 删除订单
     */
    void deleteById(Long id);
}
