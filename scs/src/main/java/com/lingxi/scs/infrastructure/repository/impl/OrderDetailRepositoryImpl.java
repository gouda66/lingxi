package com.lingxi.scs.infrastructure.repository.impl;

import com.lingxi.scs.domain.model.entity.OrderDetail;
import com.lingxi.scs.domain.repository.OrderDetailRepository;
import com.lingxi.scs.infrastructure.repository.jpa.OrderDetailJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 订单明细仓储实现
 *
 * @author system
 */
@Component
@RequiredArgsConstructor
public class OrderDetailRepositoryImpl implements OrderDetailRepository {

    private final OrderDetailJpaRepository jpaRepository;

    @Override
    public OrderDetail save(OrderDetail orderDetail) {
        return jpaRepository.save(orderDetail);
    }

    @Override
    public List<OrderDetail> saveAll(List<OrderDetail> orderDetails) {
        return jpaRepository.saveAll(orderDetails);
    }

    @Override
    public Optional<OrderDetail> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId);
    }

    @Override
    public List<OrderDetail> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
