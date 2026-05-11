package com.lingxi.scs.application.service;

import cn.hutool.core.util.IdUtil;
import com.lingxi.scs.common.exception.CustomException;
import com.lingxi.scs.domain.model.entity.AddressBook;
import com.lingxi.scs.domain.model.entity.OrderDetail;
import com.lingxi.scs.domain.model.entity.Orders;
import com.lingxi.scs.domain.model.entity.ShoppingCart;
import com.lingxi.scs.domain.repository.AddressBookRepository;
import com.lingxi.scs.domain.repository.OrderDetailRepository;
import com.lingxi.scs.domain.repository.OrderRepository;
import com.lingxi.scs.domain.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 订单应用服务
 *
 * @author system
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final AddressBookRepository addressBookRepository;

    /**
     * 提交订单
     *
     * @param orders 订单信息
     * @param userId 用户ID
     * @return 创建的订单
     */
    @Transactional
    public Orders submitOrder(Orders orders, Long userId) {
        // 获取购物车数据
        List<ShoppingCart> cartList = shoppingCartRepository.findByUserId(userId);
        if (cartList.isEmpty()) {
            throw new CustomException("购物车为空，不能下单");
        }

        // 获取用户的默认地址
        AddressBook defaultAddress = addressBookRepository.findDefaultByUserId(userId)
                .orElseThrow(() -> new CustomException("请先添加收货地址"));

        // 生成订单ID
        orders.setId(IdUtil.getSnowflakeNextId());
        
        // 生成订单号
        orders.setNumber(UUID.randomUUID().toString().replace("-", "").substring(0, 20));
        orders.setUserId(userId);
        orders.setStatus(2); // 待派送
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        
        // 填充用户地址信息
        orders.setAddressBookId(defaultAddress.getId());
        orders.setConsignee(defaultAddress.getConsignee());
        orders.setPhone(defaultAddress.getPhone());
        
        // 组装完整地址
        StringBuilder addressBuilder = new StringBuilder();
        if (defaultAddress.getProvinceName() != null) {
            addressBuilder.append(defaultAddress.getProvinceName());
        }
        if (defaultAddress.getCityName() != null) {
            addressBuilder.append(defaultAddress.getCityName());
        }
        if (defaultAddress.getDistrictName() != null) {
            addressBuilder.append(defaultAddress.getDistrictName());
        }
        if (defaultAddress.getDetail() != null) {
            addressBuilder.append(defaultAddress.getDetail());
        }
        orders.setAddress(addressBuilder.toString());
        orders.setUserName(defaultAddress.getConsignee());

        // 计算总金额
        BigDecimal totalAmount = cartList.stream()
                .map(cart -> cart.getAmount().multiply(BigDecimal.valueOf(cart.getNumber())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        orders.setAmount(totalAmount);

        // 保存订单
        Orders savedOrder = orderRepository.save(orders);

        // 保存订单明细
        List<OrderDetail> orderDetails = cartList.stream()
                .map(cart -> {
                    OrderDetail detail = new OrderDetail();
                    // 生成订单明细ID
                    detail.setId(IdUtil.getSnowflakeNextId());
                    detail.setOrderId(savedOrder.getId());
                    detail.setName(cart.getName());
                    detail.setDishId(cart.getDishId());
                    detail.setSetmealId(cart.getSetmealId());
                    detail.setDishFlavor(cart.getDishFlavor());
                    detail.setNumber(cart.getNumber());
                    detail.setAmount(cart.getAmount());
                    detail.setImage(cart.getImage());
                    return detail;
                })
                .toList();
        orderDetailRepository.saveAll(orderDetails);

        // 清空购物车
        shoppingCartRepository.deleteByUserId(userId);

        return savedOrder;
    }

    /**
     * 查询用户的订单列表
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    public List<Orders> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    /**
     * 分页查询用户订单
     *
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @return 分页订单数据
     */
    public Page<Orders> getUserOrderPage(Long userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "orderTime"));
        return orderRepository.findByUserId(userId, pageable);
    }

    /**
     * 查询订单详情
     *
     * @param orderId 订单ID
     * @return 订单明细列表
     */
    public List<OrderDetail> getOrderDetails(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    /**
     * 查询所有订单
     *
     * @return 订单列表
     */
    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * 更新订单状态
     *
     * @param orderId 订单ID
     * @param status 状态
     */
    @Transactional
    public void updateOrderStatus(Long orderId, Integer status) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("订单不存在"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    /**
     * 更新订单信息
     *
     * @param orders 订单信息
     */
    @Transactional
    public void updateOrder(Orders orders) {
        if (orders.getId() == null) {
            throw new CustomException("订单ID不能为空");
        }
        
        Orders existingOrder = orderRepository.findById(orders.getId())
                .orElseThrow(() -> new CustomException("订单不存在"));
        
        // 更新允许修改的字段
        if (orders.getStatus() != null) {
            existingOrder.setStatus(orders.getStatus());
        }
        if (orders.getRemark() != null) {
            existingOrder.setRemark(orders.getRemark());
        }
        if (orders.getPayMethod() != null) {
            existingOrder.setPayMethod(orders.getPayMethod());
        }
        
        orderRepository.save(existingOrder);
    }


    /**
     * 分页查询订单
     *
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @param number 订单号（可选）
     * @param beginTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页订单数据
     */
    public Page<Orders> getOrderPage(int page, int pageSize, String number, 
                                      LocalDateTime beginTime, LocalDateTime endTime) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "orderTime"));
        Page<Orders> orderPage = orderRepository.findAllWithFilters(number, beginTime, endTime, pageable);
        
        // 为每个订单补充用户信息（如果为空）
        orderPage.forEach(order -> enrichOrderWithUserInfo(order));
        
        return orderPage;
    }
    
    /**
     * 补充订单的用户信息（如果为空）
     */
    private void enrichOrderWithUserInfo(Orders order) {
        // 如果已有用户信息，则跳过
        if (order.getUserName() != null && order.getPhone() != null && order.getAddress() != null) {
            return;
        }
        
        try {
            // 查询用户的默认地址
            AddressBook address = addressBookRepository.findDefaultByUserId(order.getUserId())
                    .orElse(null);
            
            if (address != null) {
                // 补充缺失的字段
                if (order.getUserName() == null) {
                    order.setUserName(address.getConsignee());
                }
                if (order.getConsignee() == null) {
                    order.setConsignee(address.getConsignee());
                }
                if (order.getPhone() == null) {
                    order.setPhone(address.getPhone());
                }
                if (order.getAddress() == null) {
                    StringBuilder addressBuilder = new StringBuilder();
                    if (address.getProvinceName() != null) {
                        addressBuilder.append(address.getProvinceName());
                    }
                    if (address.getCityName() != null) {
                        addressBuilder.append(address.getCityName());
                    }
                    if (address.getDistrictName() != null) {
                        addressBuilder.append(address.getDistrictName());
                    }
                    if (address.getDetail() != null) {
                        addressBuilder.append(address.getDetail());
                    }
                    order.setAddress(addressBuilder.toString());
                }
            }
        } catch (Exception e) {
            // 如果查询失败，记录日志但不影响主流程
            log.warn("补充订单用户信息失败，orderId: {}", order.getId(), e);
        }
    }

    public Map<String, Object> getStatistics() {
        List<Orders> allOrders = orderRepository.findAll();

        long totalOrders = allOrders.size();
        BigDecimal totalAmount = allOrders.stream()
                .map(Orders::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingOrders = allOrders.stream()
                .filter(order -> order.getStatus() == 2)
                .count();

        long completedOrders = allOrders.stream()
                .filter(order -> order.getStatus() == 5)
                .count();

        Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("totalOrders", totalOrders);
        statistics.put("totalAmount", totalAmount);
        statistics.put("pendingOrders", pendingOrders);
        statistics.put("completedOrders", completedOrders);

        return statistics;
    }

    public List<Orders> getRecentOrders(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "orderTime"));
        return orderRepository.findAllWithFilters(null, null, null, pageable).getContent();
    }


}
