package com.lingxi.scs.infrastructure.repository.impl;

import com.lingxi.scs.domain.model.entity.AddressBook;
import com.lingxi.scs.domain.repository.AddressBookRepository;
import com.lingxi.scs.infrastructure.repository.jpa.AddressBookJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 用户地址仓储实现
 *
 * @author system
 */
@Component
@RequiredArgsConstructor
public class AddressBookRepositoryImpl implements AddressBookRepository {

    private final AddressBookJpaRepository jpaRepository;

    @Override
    public AddressBook save(AddressBook addressBook) {
        return jpaRepository.save(addressBook);
    }

    @Override
    public Optional<AddressBook> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<AddressBook> findByUserId(Long userId) {
        return jpaRepository.findByUserIdAndIsDeletedFalse(userId);
    }

    @Override
    public Optional<AddressBook> findDefaultByUserId(Long userId) {
        return jpaRepository.findByUserIdAndIsDefaultTrueAndIsDeletedFalse(userId);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<AddressBook> findAll() {
        return jpaRepository.findByIsDeletedFalse();
    }
}
