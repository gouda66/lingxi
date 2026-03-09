package com.lingxi.isi.application.service;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.lingxi.isi.common.exception.CustomException;
import com.lingxi.isi.domain.model.entity.User;
import com.lingxi.isi.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;

    @Autowired
    private SnowflakeGenerator snowflakeGenerator;

    /**
     * 用户注册
     */
    @Transactional
    public User register(String username, String password, String realName, 
                        String email, String phone, String role) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(username).isPresent()) {
            throw new CustomException("用户名已存在");
        }

        User user = new User();
        user.setId(snowflakeGenerator.next());
        user.setUsername(username);
        user.setPassword(password); // TODO: 实际项目中应该加密
        user.setRealName(realName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role != null ? role : "USER");
        user.setStatus(1);

        return userRepository.save(user);
    }

    /**
     * 用户登录（简化版）
     */
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("用户名或密码错误"));

        if (!user.getPassword().equals(password)) {
            throw new CustomException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new CustomException("账号已被禁用");
        }

        return user;
    }

    /**
     * 获取用户详情
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException("用户不存在"));
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public User updateUser(Long id, String realName, String email, String phone) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("用户不存在"));

        if (realName != null) {
            user.setRealName(realName);
        }
        if (email != null) {
            user.setEmail(email);
        }
        if (phone != null) {
            user.setPhone(phone);
        }

        user.setUpdateTime(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("用户不存在"));

        if (!user.getPassword().equals(oldPassword)) {
            throw new CustomException("原密码错误");
        }

        user.setPassword(newPassword); // TODO: 实际项目中应该加密
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 获取所有用户（管理员）
     */
    public Page<User> pageUsers(int page, int pageSize, String role) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        if (role != null) {
            List<User> users = userRepository.findByRole(role);
            int start = Math.min(page * pageSize, users.size());
            int end = Math.min(start + pageSize, users.size());
            
            if (start >= users.size()) {
                return new org.springframework.data.domain.PageImpl<>(List.of(), pageable, 0);
            }
            
            return new org.springframework.data.domain.PageImpl<>(
                users.subList(start, end), pageable, users.size());
        }
        return userRepository.findAll(pageable);
    }

    /**
     * 获取 HR 列表（按企业）
     */
    public List<User> getHrByCompanyId(String companyId) {
        return userRepository.findByCompanyId(companyId);
    }

    /**
     * 禁用/启用用户
     */
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("用户不存在"));

        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new CustomException("用户不存在");
        }
        userRepository.deleteById(userId);
    }
}
