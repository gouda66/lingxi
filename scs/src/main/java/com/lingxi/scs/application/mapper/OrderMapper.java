package com.lingxi.scs.application.mapper;

import com.lingxi.scs.domain.model.entity.AddressBook;
import com.lingxi.scs.domain.model.entity.OrderDetail;
import com.lingxi.scs.domain.model.entity.Orders;
import com.lingxi.scs.application.dto.vo.CheckoutPreviewVO;
import com.lingxi.scs.application.dto.vo.OrderDetailVO;
import com.lingxi.scs.application.dto.vo.OrderVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订单映射器
 * 负责订单相关对象的转换
 *
 * @author system
 */
@Component
public class OrderMapper {

    /**
     * 转换订单实体为VO
     */
    public OrderVO toOrderVO(Orders order, List<OrderDetail> details) {
        return new OrderVO(
                order.getId(),
                order.getNumber(),
                order.getStatus(),
                getStatusText(order.getStatus()),
                order.getAmount(),
                order.getOrderTime(),
                order.getCheckoutTime(),
                order.getConsignee(),
                order.getPhone(),
                order.getAddress(),
                order.getRemark(),
                details.stream().mapToInt(OrderDetail::getNumber).sum(),
                details.stream().map(this::toOrderDetailVO).toList()
        );
    }

    /**
     * 转换订单明细实体为VO
     */
    public OrderDetailVO toOrderDetailVO(OrderDetail detail) {
        return new OrderDetailVO(
                detail.getId(),
                detail.getName(),
                detail.getImage(),
                detail.getDishId(),
                detail.getSetmealId(),
                detail.getDishFlavor(),
                detail.getNumber(),
                detail.getAmount()
        );
    }

    /**
     * 转换地址为结算预览VO
     */
    public CheckoutPreviewVO toCheckoutPreviewVO(AddressBook address) {
        if (address == null) {
            return null;
        }
        return new CheckoutPreviewVO(
                address.getId(),
                address.getConsignee(),
                address.getPhone(),
                address.getProvinceName(),
                address.getCityName(),
                address.getDistrictName(),
                address.getDetail(),
                address.getLabel()
        );
    }

    /**
     * 获取订单状态文本
     */
    private String getStatusText(Integer status) {
        return switch (status) {
            case 1 -> "待付款";
            case 2 -> "待派送";
            case 3 -> "已派送";
            case 4 -> "已完成";
            case 5 -> "已取消";
            default -> "未知状态";
        };
    }
}