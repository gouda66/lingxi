package com.lingxi.scs.domain.repository;

import com.lingxi.scs.domain.model.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {

    Employee save(Employee employee);

    Optional<Employee> findById(Long id);

    Optional<Employee> findByUsername(String username);

    List<Employee> findAll();

    List<Employee> findByStatus(Integer status);

    void deleteById(Long id);
    
    Page<Employee> findByNameContaining(String name, Pageable pageable);
}
