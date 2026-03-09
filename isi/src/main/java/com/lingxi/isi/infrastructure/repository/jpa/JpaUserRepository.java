package com.lingxi.isi.infrastructure.repository.jpa;

import com.lingxi.isi.domain.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    List<User> findByRole(String role);

    List<User> findByCompanyId(String companyId);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);
}
