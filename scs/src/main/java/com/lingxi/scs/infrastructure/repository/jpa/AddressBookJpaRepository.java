package com.lingxi.scs.infrastructure.repository.jpa;

import com.lingxi.scs.domain.model.entity.AddressBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 地址簿JPA仓储接口
 *
 * @author system
 */
@Repository
public interface AddressBookJpaRepository extends JpaRepository<AddressBook, Long> {
    List<AddressBook> findByUserIdAndIsDeletedFalse(Long userId);
    Optional<AddressBook> findByUserIdAndIsDefaultTrueAndIsDeletedFalse(Long userId);
    List<AddressBook> findByIsDeletedFalse();
}
