package com.lingxi.scs.domain.service;

import com.lingxi.scs.domain.model.entity.AddressBook;

import java.util.List;

/**
 * 地址簿领域服务
 * 负责处理地址相关的核心业务规则（纯业务逻辑，不依赖基础设施）
 *
 * @author system
 */
public class AddressBookDomainService {

    /**
     * 验证地址归属权限
     * 领域规则：用户只能操作自己的地址
     *
     * @param addressBook 地址对象
     * @param userId 用户ID
     * @throws IllegalArgumentException 如果权限验证失败
     */
    public void validateOwnership(AddressBook addressBook, Long userId) {
        if (addressBook == null) {
            throw new IllegalArgumentException("地址不存在");
        }
        if (!addressBook.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作该地址");
        }
    }

    /**
     * 判断是否需要自动设置为默认地址
     * 领域规则：如果是用户的第一个地址，应该自动设置为默认地址
     *
     * @param existingAddresses 用户现有的地址列表
     * @param isDefault 用户指定的默认标识
     * @return 是否应该设置为默认
     */
    public boolean shouldSetAsDefault(List<AddressBook> existingAddresses, Boolean isDefault) {
        // 如果是第一个地址，强制设置为默认
        if (existingAddresses == null || existingAddresses.isEmpty()) {
            return true;
        }
        // 否则遵循用户指定的值
        return Boolean.TRUE.equals(isDefault);
    }

    /**
     * 确定删除默认地址后的新默认地址
     * 领域规则：删除默认地址后，应该将第一个地址设为默认
     *
     * @param remainingAddresses 删除后剩余的地址列表
     * @return 应该设为默认的地址，如果没有剩余地址则返回null
     */
    public AddressBook determineNewDefaultAddress(List<AddressBook> remainingAddresses) {
        if (remainingAddresses == null || remainingAddresses.isEmpty()) {
            return null;
        }
        return remainingAddresses.get(0);
    }

    /**
     * 格式化完整地址
     * 领域规则：按照省-市-区-详细地址的格式拼接
     *
     * @param addressBook 地址对象
     * @return 格式化后的完整地址
     */
    public String formatFullAddress(AddressBook addressBook) {
        if (addressBook == null) {
            return "";
        }
        StringBuilder fullAddress = new StringBuilder();
        if (addressBook.getProvinceName() != null) {
            fullAddress.append(addressBook.getProvinceName());
        }
        if (addressBook.getCityName() != null) {
            fullAddress.append(addressBook.getCityName());
        }
        if (addressBook.getDistrictName() != null) {
            fullAddress.append(addressBook.getDistrictName());
        }
        if (addressBook.getDetail() != null) {
            fullAddress.append(addressBook.getDetail());
        }
        return fullAddress.toString();
    }

    /**
     * 验证地址信息的完整性
     * 领域规则：必填字段检查
     *
     * @param addressBook 地址对象
     * @throws IllegalArgumentException 如果必填字段缺失
     */
    public void validateAddressCompleteness(AddressBook addressBook) {
        if (addressBook.getConsignee() == null || addressBook.getConsignee().trim().isEmpty()) {
            throw new IllegalArgumentException("收货人不能为空");
        }
        if (addressBook.getPhone() == null || addressBook.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (addressBook.getDetail() == null || addressBook.getDetail().trim().isEmpty()) {
            throw new IllegalArgumentException("详细地址不能为空");
        }
    }

    /**
     * 验证手机号格式
     * 领域规则：手机号必须是11位数字
     *
     * @param phone 手机号
     * @return 是否有效
     */
    public boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        return phone.matches("^1[3-9]\\d{9}$");
    }
}
