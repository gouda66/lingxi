package com.lingxi.seo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 * 密码 MD5 加密工具类
 * </p>
 * 
 * <p>
 * 使用 MD5 算法对密码进行加密
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Slf4j
@Component
public class PasswordSegmentUtils {
    
    /**
     * 加密密码（MD5）
     * 
     * @param password 原始密码
     * @return 加密后的字符串（32 位十六进制）
     */
    public static String encrypt(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
            
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString((b & 0xFF) | 0x100).substring(1);
                sb.append(hex);
            }
            return sb.toString();
                
        } catch (NoSuchAlgorithmException e) {
            log.error("加密密码失败", e);
            throw new RuntimeException("加密密码失败", e);
        }
    }
    
    /**
     * 验证密码（MD5 验证）
     * 
     * @param encryptedPassword 数据库中存储的加密密码
     * @param inputPassword 用户输入的明文密码
     * @return 是否匹配
     */
    public static boolean validatePassword(String encryptedPassword, String inputPassword) {
        if (encryptedPassword == null || encryptedPassword.isEmpty()) {
            log.warn("数据库中的加密密码为空");
            return false;
        }
        if (inputPassword == null || inputPassword.isEmpty()) {
            log.warn("输入的明文密码为空");
            return false;
        }
        
        try {
            // 对输入密码进行 MD5 加密
            String encryptedInput = encrypt(inputPassword);
            
            // 对比加密结果
            boolean matches = encryptedPassword.equals(encryptedInput);
            log.debug("密码验证结果：{}", matches ? "成功" : "失败");
            
            return matches;
            
        } catch (Exception e) {
            log.error("验证密码失败", e);
            return false;
        }
    }
}
