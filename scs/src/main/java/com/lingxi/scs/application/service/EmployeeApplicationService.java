package com.lingxi.scs.application.service;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.lingxi.scs.common.exception.CustomException;
import com.lingxi.scs.domain.model.entity.Employee;
import com.lingxi.scs.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeApplicationService {

    private final EmployeeRepository employeeRepository;

    public Employee login(String username, String password) {
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());

        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("登录失败"));

        if (!employee.getPassword().equals(encryptedPassword)) {
            throw new CustomException("登录失败");
        }

        if (employee.getStatus() == 0) {
            throw new CustomException("账号已禁用");
        }

        return employee;
    }

    @Transactional
    public Employee addEmployee(Employee employee, Long operatorId) {
        if (employeeRepository.findByUsername(employee.getUserName()).isPresent()) {
            throw new CustomException("用户名已存在");
        }
        
        SnowflakeGenerator snowflakeGenerator = new SnowflakeGenerator();
        employee.setId(snowflakeGenerator.next());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setStatus(1);
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setCreateUser(operatorId);
        employee.setUpdateUser(operatorId);

        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(Employee employee, Long operatorId) {
        Optional<Employee> old = employeeRepository.findById(employee.getId());
        Employee oldEmployee = old.orElseThrow(() -> new CustomException("员工不存在"));
        copyNonNullProperties(employee, oldEmployee);
        oldEmployee.setUpdateTime(LocalDateTime.now());
        oldEmployee.setUpdateUser(operatorId);
        return employeeRepository.save(oldEmployee);
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new CustomException("员工不存在"));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Page<Employee> pageByName(int page, int pageSize, String name) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return employeeRepository.findByNameContaining(name, pageable);
    }

    @Transactional
    public void updateStatus(Long id, Integer status, Long operatorId) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new CustomException("员工不存在"));
        employee.setStatus(status);
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(operatorId);
        employeeRepository.save(employee);
    }

    private void copyNonNullProperties(Object source, Object target) {
        PropertyDescriptor[] sourcePds = BeanUtils.getPropertyDescriptors(source.getClass());
        for (PropertyDescriptor sourcePd : sourcePds) {
            Method readMethod = sourcePd.getReadMethod();
            if (readMethod != null && !"class".equals(sourcePd.getName())) {
                try {
                    Object value = readMethod.invoke(source);
                    if (value != null) {
                        PropertyDescriptor targetPd = BeanUtils.getPropertyDescriptor(target.getClass(), sourcePd.getName());
                        if (targetPd != null) {
                            Method writeMethod = targetPd.getWriteMethod();
                            if (writeMethod != null) {
                                writeMethod.invoke(target, value);
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("属性拷贝失败", e);
                }
            }
        }
    }
}
