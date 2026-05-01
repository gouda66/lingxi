package com.lingxi.scs.application.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.BCrypt;
import co.paralleluniverse.common.util.ConcurrentSet;
import com.lingxi.scs.common.exception.CustomException;
import com.lingxi.scs.domain.model.entity.Employee;
import com.lingxi.scs.domain.model.entity.User;
import com.lingxi.scs.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

/**
 * 用户应用服务
 *
 * @author system
 */
@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final ReentrantLock lock = new ReentrantLock();

    public void test() {
        try {
            Condition condition = lock.newCondition();
//            等待机制
            condition.await();
//            唤醒机制
            condition.signal();
            Semaphore semaphore = new Semaphore(2);
            semaphore.release();
            CountDownLatch countDownLatch = new CountDownLatch(1);
//            等待
            countDownLatch.await();
//            计数减一
            countDownLatch.countDown();
            CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
            cyclicBarrier.await();
            Phaser phaser = new Phaser(2);
            phaser.awaitAdvance(0);
            cyclicBarrier.reset();
            Exchanger<String> exchanger = new Exchanger<>();
            String result = exchanger.exchange("Hello");
            System.out.println(result);

//            卖家
            new Thread(() -> {
                try {
                    String goods = exchanger.exchange("goods");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

//            买家
            new Thread(() -> {
                try {
                    String money = exchanger.exchange("money");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            // 业务逻辑




        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    private final UserRepository userRepository;

    /**
     * 用户登录（通过账号和密码）
     *
     * @param username 账号
     * @param password 密码
     * @return 用户信息
     */
    public User login(String username, String password) {
        // 查询用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("账号或密码错误"));
        
        // 验证密码（Hutool BCrypt 加密比对）
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new CustomException("账号或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new CustomException("账号已被禁用");
        }
        
        return user;
    }

    /**
     * 用户登录（通过手机号）
     *
     * @param phone 手机号
     * @return 用户信息
     */
    @Transactional
    public User loginByPhone(String phone) {
        // 根据手机号查询或创建用户
        return getOrCreateUserByPhone(phone);
    }

    /**
     * 根据手机号查询或创建用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    @Transactional
    public User getOrCreateUserByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setId(IdUtil.getSnowflakeNextId());
                    newUser.setPhone(phone);
                    newUser.setStatus(1);
                    return userRepository.save(newUser);
                });
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException("用户不存在"));
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 更新后的用户
     */
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * 注册用户（通过账号和密码）
     *
     * @param username 账号
     * @param password 密码
     * @return 注册后的用户信息
     */
    @Transactional
    public User registerUser(String username, String password) {
        // 检查账号是否已注册
        if (userRepository.findByUsername(username).isPresent()) {
            throw new CustomException("该账号已注册");
        }
        
        User newUser = new User();
        newUser.setId(IdUtil.getSnowflakeNextId());
        newUser.setUsername(username);
        // Hutool BCrypt 加密密码
        newUser.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        newUser.setStatus(1);
        return userRepository.save(newUser);
    }


    static void main() {
        Thread currentThread = Thread.currentThread(); // 获取主线程引用

        Thread thread = new Thread(() -> {
            System.out.println("Thread is running");
            LockSupport.unpark(currentThread);  // 唤醒主线程，而不是当前线程
            System.out.println("Thread is resumed");
        });
        thread.start();
        System.out.println("Main thread is waiting");
        LockSupport.park();
        System.out.println("Main thread is resumed");
        CopyOnWriteArrayList<User> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        copyOnWriteArrayList.add(new User());
        copyOnWriteArrayList.get(0).setName("test");

    }

}
