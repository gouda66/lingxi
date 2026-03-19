package com.lingxi.isi.application.service;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import cn.hutool.core.util.IdUtil;
import com.lingxi.isi.application.dto.CaptchaDTO;
import com.lingxi.isi.application.dto.UserDTO;
import com.lingxi.isi.application.mapper.UserMapper;
import com.lingxi.isi.common.exception.CustomException;
import com.lingxi.isi.common.util.CaptchaCacheService;
import com.lingxi.isi.common.util.CaptchaUtil;
import com.lingxi.isi.domain.model.entity.User;
import com.lingxi.isi.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;
    private final CaptchaUtil captchaUtil;
    private final CaptchaCacheService captchaCacheService;

    @Autowired
    private SnowflakeGenerator snowflakeGenerator;

    /**
     * 用户注册
     */
    @Transactional
    public UserDTO register(String username, String password, String realName, 
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

        User savedUser = userRepository.save(user);
        return UserMapper.INSTANCE.toDTO(savedUser);
    }

    /**
     * 用户登录（简化版）
     */
    public UserDTO login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("用户名或密码错误"));

        if (!user.getPassword().equals(password)) {
            throw new CustomException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new CustomException("账号已被禁用");
        }

        return UserMapper.INSTANCE.toDTO(user);
    }

    /**
     * 验证码登录
     */
    public UserDTO captchaLogin(String username, String code, String uuid) {
        // 验证验证码
        if (!captchaCacheService.validate(uuid, code)) {
            throw new CustomException("验证码错误");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("用户名不存在"));

        if (user.getStatus() == 0) {
            throw new CustomException("账号已被禁用");
        }

        return UserMapper.INSTANCE.toDTO(user);
    }

    /**
     * 获取验证码图片
     */
    public CaptchaDTO getCaptchaImage() {
        // 生成验证码图片和对应的验证码字符串
        CaptchaUtil.CaptchaImageResult result = captchaUtil.generateCaptchaImageWithCode();
        BufferedImage captchaImage = result.image();
        String captchaCode = result.code();
        
        // 生成 UUID
        String uuid = IdUtil.fastSimpleUUID();
        
        // 保存验证码到 Redis
        captchaCacheService.set(uuid, captchaCode);
        
        // 将图片转为 Base64
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(captchaImage, "gif", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            return new CaptchaDTO(true, base64Image, uuid);
        } catch (Exception e) {
            throw new CustomException("生成验证码失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户详情
     */
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper.INSTANCE::toDTO)
                .orElseThrow(() -> new CustomException("用户不存在"));
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public UserDTO updateUser(Long id, String realName, String email, String phone) {
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
        User updatedUser = userRepository.save(user);
        return UserMapper.INSTANCE.toDTO(updatedUser);
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
    public Page<UserDTO> pageUsers(int page, int pageSize, String role) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        if (role != null) {
            List<User> users = userRepository.findByRole(role);
            int start = Math.min(page * pageSize, users.size());
            int end = Math.min(start + pageSize, users.size());
            
            if (start >= users.size()) {
                return new PageImpl<>(List.of(), pageable, 0);
            }
            
            return new PageImpl<>(
                users.subList(start, end).stream().map(UserMapper.INSTANCE::toDTO).collect(Collectors.toList()), 
                pageable, 
                users.size()
            );
        }
        return userRepository.findAll(pageable).map(UserMapper.INSTANCE::toDTO);
    }

    /**
     * 获取 HR 列表（按企业）
     */
    public List<UserDTO> getHrByCompanyId(String companyId) {
        return userRepository.findByCompanyId(companyId)
                .stream()
                .map(UserMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
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

    /**
     * 用户登出（可以在这里添加一些清理逻辑，如记录日志等）
     */
    public void logout() {
        // TODO: 可以添加登出后的处理逻辑，如记录操作日志、清理缓存等
        // 目前 JWT 是无状态的，客户端删除 token 即可
    }
}
