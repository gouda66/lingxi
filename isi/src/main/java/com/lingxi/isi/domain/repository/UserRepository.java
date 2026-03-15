package com.lingxi.isi.domain.repository;

import com.lingxi.isi.domain.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

   Page<User> findAll(Pageable pageable);

    List<User> findAll();
}
