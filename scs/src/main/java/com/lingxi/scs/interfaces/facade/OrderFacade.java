package com.lingxi.scs.interfaces.facade;

import com.lingxi.scs.application.service.AddressBookApplicationService;
import com.lingxi.scs.application.service.OrderApplicationService;
import com.lingxi.scs.domain.model.entity.AddressBook;
import com.lingxi.scs.domain.model.entity.OrderDetail;
import com.lingxi.scs.domain.model.entity.Orders;
import com.lingxi.scs.application.mapper.OrderMapper;
import com.lingxi.scs.application.dto.vo.CheckoutPreviewVO;
import com.lingxi.scs.application.dto.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订单门面
 * 编排订单、订单详情、地址等数据，提供完整订单视图
 *
 * @author system
 */
@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderApplicationService orderService;
    private final AddressBookApplicationService addressBookService;
    private final OrderMapper orderMapper;

    /**
     * 获取用户订单列表（包含订单详情）
     */
    public List<OrderVO> getUserOrdersWithDetails(Long userId) {
        List<Orders> orders = orderService.getUserOrders(userId);

        return orders.stream()
                .map(order -> {
                    List<OrderDetail> details = orderService.getOrderDetails(order.getId());
                    return orderMapper.toOrderVO(order, details);
                })
                .toList();
    }

    /**
     * 获取单个订单详情
     */
    public OrderVO getOrderDetail(Long orderId) {
        List<OrderDetail> details = orderService.getOrderDetails(orderId);

        return new OrderVO(
                orderId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                details.stream().mapToInt(OrderDetail::getNumber).sum(),
                details.stream().map(orderMapper::toOrderDetailVO).toList()
        );
    }

    /**
     * 准备下单数据（默认地址信息）
     */
    public CheckoutPreviewVO prepareCheckout(Long userId) {
        AddressBook defaultAddress = addressBookService.getDefaultAddress(userId);
        return orderMapper.toCheckoutPreviewVO(defaultAddress);
    }
}