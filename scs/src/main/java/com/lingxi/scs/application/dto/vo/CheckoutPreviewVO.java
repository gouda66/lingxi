package com.lingxi.scs.application.dto.vo;

/**
 * 结算预览数据VO
 *
 * @author system
 * @param addressId 地址ID
 * @param consignee 收货人
 * @param phone 手机号
 * @param provinceName 省份名称
 * @param cityName 城市名称
 * @param districtName 区域名称
 * @param detail 详细地址
 * @param label 标签
 */
public record CheckoutPreviewVO(
        Long addressId,
        String consignee,
        String phone,
        String provinceName,
        String cityName,
        String districtName,
        String detail,
        String label
) {}