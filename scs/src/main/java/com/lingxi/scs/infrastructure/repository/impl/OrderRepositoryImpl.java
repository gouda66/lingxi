package com.lingxi.scs.infrastructure.repository.impl;

import com.lingxi.scs.domain.model.entity.Orders;
import com.lingxi.scs.domain.repository.OrderRepository;
import com.lingxi.scs.infrastructure.repository.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 订单仓储实现
 *
 * @author system
 */
@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    @Override
    public Orders save(Orders orders) {
        return jpaRepository.save(orders);
    }

    @Override
    public Optional<Orders> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Orders> findByNumber(String number) {
        return jpaRepository.findByNumber(number);
    }

    @Override
    public List<Orders> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public List<Orders> findByStatus(Integer status) {
        return jpaRepository.findByStatus(status);
    }

    @Override
    public List<Orders> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
