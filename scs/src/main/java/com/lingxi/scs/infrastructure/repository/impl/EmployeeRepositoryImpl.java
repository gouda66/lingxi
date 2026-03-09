package com.lingxi.scs.infrastructure.repository.impl;

import com.lingxi.scs.domain.model.entity.Employee;
import com.lingxi.scs.domain.repository.EmployeeRepository;
import com.lingxi.scs.infrastructure.repository.jpa.EmployeeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final EmployeeJpaRepository jpaRepository;

    @Override
    public Employee save(Employee employee) {
        return jpaRepository.save(employee);
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Employee> findByUsername(String username) {
        return jpaRepository.findByUsername(username);
    }

    @Override
    public List<Employee> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Employee> findByStatus(Integer status) {
        return jpaRepository.findByStatus(status);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Page<Employee> findByNameContaining(String name, Pageable pageable) {
        return jpaRepository.findByNameContaining(name, pageable);
    }
}
