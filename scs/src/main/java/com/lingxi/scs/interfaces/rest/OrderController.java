package com.lingxi.scs.interfaces.rest;

import com.lingxi.scs.application.service.OrderApplicationService;
import com.lingxi.scs.common.context.BaseContext;
import com.lingxi.scs.common.result.R;
import com.lingxi.scs.domain.model.entity.Orders;
import com.lingxi.scs.interfaces.facade.OrderFacade;
import com.lingxi.scs.application.dto.vo.CheckoutPreviewVO;
import com.lingxi.scs.application.dto.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单管理
 *
 * @author system
 */
@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderApplicationService orderService;
    private final OrderFacade orderFacade;

    /**
     * 用户下单
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("提交订单: {}", orders);
        Long userId = BaseContext.getCurrentId();
        orderService.submitOrder(orders, userId);
        return R.success("下单成功");
    }

    /**
     * 查询用户订单列表（包含订单详情）
     */
    @GetMapping("/list")
    public R<List<OrderVO>> list() {
        log.info("查询用户订单");
        Long userId = BaseContext.getCurrentId();
        List<OrderVO> orders = orderFacade.getUserOrdersWithDetails(userId);
        return R.success(orders);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{orderId}/detail")
    public R<OrderVO> getOrderDetails(@PathVariable Long orderId) {
        log.info("查询订单详情: {}", orderId);
        OrderVO orderVO = orderFacade.getOrderDetail(orderId);
        return R.success(orderVO);
    }

    /**
     * 获取结算预览数据（默认地址等）
     */
    @GetMapping("/checkout/preview")
    public R<CheckoutPreviewVO> checkoutPreview() {
        log.info("获取结算预览数据");
        Long userId = BaseContext.getCurrentId();
        CheckoutPreviewVO preview = orderFacade.prepareCheckout(userId);
        return R.success(preview);
    }

    /**
     * 查询所有订单（管理端）
     */
    @GetMapping("/all")
    public R<List<Orders>> getAllOrders() {
        log.info("查询所有订单");
        List<Orders> orders = orderService.getAllOrders();
        return R.success(orders);
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/status")
    public R<String> updateStatus(@RequestParam Long orderId, @RequestParam Integer status) {
        log.info("更新订单状态: orderId={}, status={}", orderId, status);
        orderService.updateOrderStatus(orderId, status);
        return R.success("订单状态更新成功");
    }
}