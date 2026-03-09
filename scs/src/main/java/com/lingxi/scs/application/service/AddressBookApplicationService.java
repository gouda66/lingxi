package com.lingxi.scs.application.service;

import com.lingxi.scs.domain.model.entity.AddressBook;
import com.lingxi.scs.domain.repository.AddressBookRepository;
import com.lingxi.scs.domain.service.AddressBookDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地址簿应用服务
 * 负责协调领域对象完成业务用例，处理事务和数据持久化
 *
 * @author system
 */
@Service
@RequiredArgsConstructor
public class AddressBookApplicationService {

    private final AddressBookDomainService domainService = new AddressBookDomainService();

    private final AddressBookRepository addressBookRepository;

    /**
     * 新增地址
     * 业务规则：如果是第一个地址，自动设置为默认地址
     *
     * @param addressBook 地址信息
     * @param userId 用户ID
     * @return 保存后的地址
     */
    @Transactional
    public AddressBook addAddress(AddressBook addressBook, Long userId) {
        addressBook.setUserId(userId);
        addressBook.setCreateTime(LocalDateTime.now());
        addressBook.setUpdateTime(LocalDateTime.now());
        addressBook.setCreateUser(userId);
        addressBook.setUpdateUser(userId);
        addressBook.setIsDeleted(false);

        // 如果是用户的第一个地址，自动设置为默认地址
        List<AddressBook> existingAddresses = addressBookRepository.findByUserId(userId);
        if (existingAddresses.isEmpty()) {
            addressBook.setIsDefault(true);
        } else if (addressBook.getIsDefault() == null) {
            addressBook.setIsDefault(false);
        }

        // 如果新地址设置为默认，需要取消其他默认地址
        if (Boolean.TRUE.equals(addressBook.getIsDefault())) {
            clearDefaultAddress(userId);
        }

        return addressBookRepository.save(addressBook);
    }

    /**
     * 更新地址
     *
     * @param addressBook 地址信息
     * @param userId 用户ID
     * @return 更新后的地址
     */
    @Transactional
    public AddressBook updateAddress(AddressBook addressBook, Long userId) {
        addressBook.setUpdateTime(LocalDateTime.now());
        addressBook.setUpdateUser(userId);

        // 如果设置为默认地址，需要取消其他默认地址
        if (Boolean.TRUE.equals(addressBook.getIsDefault())) {
            clearDefaultAddress(userId);
        }

        return addressBookRepository.save(addressBook);
    }

    /**
     * 设置默认地址
     * 业务规则：一个用户只能有一个默认地址
     *
     * @param addressId 地址ID
     * @param userId 用户ID
     */
    @Transactional
    public void setDefaultAddress(Long addressId, Long userId) {
        // 先取消当前默认地址
        clearDefaultAddress(userId);

        // 设置新的默认地址
        AddressBook addressBook = addressBookRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("地址不存在"));
        
        // 验证地址归属
        if (!addressBook.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作该地址");
        }

        addressBook.setIsDefault(true);
        addressBook.setUpdateTime(LocalDateTime.now());
        addressBook.setUpdateUser(userId);
        addressBookRepository.save(addressBook);
    }

    /**
     * 删除地址（逻辑删除）
     * 业务规则：如果删除的是默认地址，需要将第一个地址设为默认
     *
     * @param addressId 地址ID
     * @param userId 用户ID
     */
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        AddressBook addressBook = addressBookRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("地址不存在"));

        // 验证地址归属
        if (!addressBook.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作该地址");
        }

        boolean wasDefault = Boolean.TRUE.equals(addressBook.getIsDefault());

        // 逻辑删除
        addressBook.setIsDeleted(true);
        addressBook.setUpdateTime(LocalDateTime.now());
        addressBook.setUpdateUser(userId);
        addressBookRepository.save(addressBook);

        // 如果删除的是默认地址，将第一个地址设为默认
        if (wasDefault) {
            List<AddressBook> remainingAddresses = addressBookRepository.findByUserId(userId);
            if (!remainingAddresses.isEmpty()) {
                AddressBook firstAddress = remainingAddresses.get(0);
                firstAddress.setIsDefault(true);
                firstAddress.setUpdateTime(LocalDateTime.now());
                firstAddress.setUpdateUser(userId);
                addressBookRepository.save(firstAddress);
            }
        }
    }

    /**
     * 查询用户的所有地址
     *
     * @param userId 用户ID
     * @return 地址列表
     */
    public List<AddressBook> getUserAddresses(Long userId) {
        return addressBookRepository.findByUserId(userId);
    }

    /**
     * 查询用户的默认地址
     *
     * @param userId 用户ID
     * @return 默认地址（如果存在）
     */
    public AddressBook getDefaultAddress(Long userId) {
        return addressBookRepository.findDefaultByUserId(userId)
                .orElse(null);
    }

    /**
     * 取消用户的所有默认地址
     * 私有方法，用于业务逻辑内部调用
     *
     * @param userId 用户ID
     */
    private void clearDefaultAddress(Long userId) {
        addressBookRepository.findDefaultByUserId(userId).ifPresent(defaultAddress -> {
            defaultAddress.setIsDefault(false);
            defaultAddress.setUpdateTime(LocalDateTime.now());
            defaultAddress.setUpdateUser(userId);
            addressBookRepository.save(defaultAddress);
        });
    }
}
