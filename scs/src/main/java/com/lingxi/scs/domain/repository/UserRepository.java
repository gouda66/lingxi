package com.lingxi.scs.domain.repository;

import com.lingxi.scs.domain.model.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口
 *
 * @author system
 */
public interface UserRepository {

    /**
     * 保存用户
     */
    User save(User user);

    /**
     * 根据ID查询用户
     */
    Optional<User> findById(Long id);

    /**
     * 根据手机号查询用户
     */
    Optional<User> findByPhone(String phone);

    /**
     * 查询所有用户
     */
    List<User> findAll();

    /**
     * 根据状态查询用户列表
     */
    List<User> findByStatus(Integer status);

    /**
     * 删除用户
     */
    void deleteById(Long id);
}
