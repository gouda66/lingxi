package com.lingxi.isi.infrastructure.repository.impl;

import com.lingxi.isi.domain.model.entity.User;
import com.lingxi.isi.domain.repository.UserRepository;
import com.lingxi.isi.infrastructure.repository.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaRepository;

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username);
    }

    @Override
    public List<User> findByRole(String role) {
        return jpaRepository.findByRole(role);
    }

    @Override
    public List<User> findByCompanyId(String companyId) {
        return jpaRepository.findByCompanyId(companyId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return jpaRepository.findByPhone(phone);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
       return jpaRepository.findAll(pageable);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll();
    }
}
