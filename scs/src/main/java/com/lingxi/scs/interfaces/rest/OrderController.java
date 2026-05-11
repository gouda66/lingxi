package com.lingxi.scs.interfaces.rest;

import com.lingxi.scs.application.dto.OrderStatusDTO;
import com.lingxi.scs.application.service.OrderApplicationService;
import com.lingxi.scs.common.context.BaseContext;
import com.lingxi.scs.common.result.R;
import com.lingxi.scs.domain.model.entity.Orders;
import com.lingxi.scs.interfaces.facade.OrderFacade;
import com.lingxi.scs.application.dto.vo.CheckoutPreviewVO;
import com.lingxi.scs.application.dto.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
     * 分页查询用户订单
     *
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页订单数据
     */
    @GetMapping("/userPage")
    public R<Page<Orders>> userPage(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        log.info("分页查询用户订单，页码：{}, 每页大小：{}", page, pageSize);
        Long userId = BaseContext.getCurrentId();
        Page<Orders> orderPage = orderService.getUserOrderPage(userId, page, pageSize);
        return R.success(orderPage);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{orderId}/detail")
    public R<OrderVO> getOrderDetails(@PathVariable String orderId) {
        log.info("查询订单详情: {}", orderId);
        OrderVO orderVO = orderFacade.getOrderDetail(Long.parseLong(orderId));
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

    @PutMapping
    public R<String> send(@RequestBody Orders orders) {
        log.info("更新订单: {}", orders);
        orderService.updateOrder(orders);
        return R.success("订单更新成功");
    }

    @PutMapping("/status")
    public R<String> updateStatus(@RequestBody OrderStatusDTO dto) {
        log.info("更新订单状态: orderId={}, status={}", dto.getId(), dto.getStatus());
        orderService.updateOrderStatus(dto.getId(), dto.getStatus());
        return R.success("订单状态更新成功");
    }

    /**
     * 分页查询订单（管理端）
     *
     * @param page 页码
     * @param pageSize 每页大小
     * @param number 订单号（可选）
     * @param beginTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页订单数据
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int pageSize,
                                @RequestParam(required = false) String number,
                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime,
                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("分页查询订单，页码：{}, 每页大小：{}, 订单号：{}, 开始时间：{}, 结束时间：{}", 
                page, pageSize, number, beginTime, endTime);
        Page<Orders> orderPage = orderService.getOrderPage(page, pageSize, number, beginTime, endTime);
        return R.success(orderPage);
    }
}
