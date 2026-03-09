package com.lingxi.scs.domain.repository;

import com.lingxi.scs.domain.model.entity.AddressBook;

import java.util.List;
import java.util.Optional;

/**
 * 用户地址仓储接口
 *
 * @author system
 */
public interface AddressBookRepository {

    /**
     * 保存地址信息
     */
    AddressBook save(AddressBook addressBook);

    /**
     * 根据ID查询地址
     */
    Optional<AddressBook> findById(Long id);

    /**
     * 根据用户ID查询所有地址
     */
    List<AddressBook> findByUserId(Long userId);

    /**
     * 查询用户的默认地址
     */
    Optional<AddressBook> findDefaultByUserId(Long userId);

    /**
     * 删除地址
     */
    void deleteById(Long id);

    /**
     * 查询所有未删除的地址
     */
    List<AddressBook> findAll();
}
