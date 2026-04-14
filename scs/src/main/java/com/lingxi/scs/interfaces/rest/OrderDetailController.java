package com.lingxi.scs.interfaces.rest;

import com.lingxi.scs.application.service.OrderApplicationService;
import com.lingxi.scs.common.result.R;
import com.lingxi.scs.domain.model.entity.OrderDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单明细管理
 *
 * @author system
 */
@Slf4j
@RestController
@RequestMapping("/orderDetail")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderApplicationService orderService;

    /**
     * 根据订单ID查询订单明细
     */
    @GetMapping("/{orderId}")
    public R<List<OrderDetail>> getByOrderId(@PathVariable String orderId) {
        log.info("查询订单明细: orderId={}", orderId);
        List<OrderDetail> details = orderService.getOrderDetails(Long.parseLong(orderId));
        return R.success(details);
    }
}