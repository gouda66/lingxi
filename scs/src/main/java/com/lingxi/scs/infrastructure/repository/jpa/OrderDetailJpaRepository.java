package com.lingxi.scs.infrastructure.repository.jpa;

import com.lingxi.scs.domain.model.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单明细JPA仓储接口
 *
 * @author system
 */
@Repository
public interface OrderDetailJpaRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderId(Long orderId);

    /**
     * 统计菜品在指定时间后的销量（已完成订单）
     */
    @Query("SELECT COALESCE(SUM(od.number), 0) FROM OrderDetail od " +
           "JOIN Orders o ON od.orderId = o.id " +
           "WHERE od.dishId = :dishId AND o.orderTime >= :startTime AND o.status = 5")
    Long countSalesByDishId(@Param("dishId") Long dishId, @Param("startTime") LocalDateTime startTime);

    /**
     * 统计菜品在指定时间后的总订单数
     */
    @Query("SELECT COUNT(DISTINCT od.orderId) FROM OrderDetail od " +
           "JOIN Orders o ON od.orderId = o.id " +
           "WHERE od.dishId = :dishId AND o.orderTime >= :startTime")
    Long countTotalOrdersByDishId(@Param("dishId") Long dishId, @Param("startTime") LocalDateTime startTime);

    /**
     * 统计菜品在指定时间后的已完成订单数
     */
    @Query("SELECT COUNT(DISTINCT od.orderId) FROM OrderDetail od " +
           "JOIN Orders o ON od.orderId = o.id " +
           "WHERE od.dishId = :dishId AND o.orderTime >= :startTime AND o.status = 5")
    Long countCompletedOrdersByDishId(@Param("dishId") Long dishId, @Param("startTime") LocalDateTime startTime);
}
