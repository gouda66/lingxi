package com.lingxi.scs.infrastructure.repository.jpa;

import com.lingxi.scs.domain.model.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 订单JPA仓储接口
 *
 * @author system
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByNumber(String number);
    List<Orders> findByUserId(Long userId);
    List<Orders> findByStatus(Integer status);
}
