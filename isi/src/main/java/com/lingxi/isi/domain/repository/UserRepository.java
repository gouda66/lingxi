package com.lingxi.isi.domain.repository;

import com.lingxi.isi.domain.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    List<User> findByRole(String role);

    List<User> findByCompanyId(String companyId);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    void deleteById(Long id);

    List<User> findAll();
}
