package com.lingxi.scs.infrastructure.repository.jpa;

import com.lingxi.scs.domain.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户JPA仓储接口
 *
 * @author system
 */
@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    List<User> findByStatus(Integer status);
}
