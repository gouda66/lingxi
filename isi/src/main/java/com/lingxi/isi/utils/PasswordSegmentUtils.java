package com.lingxi.isi.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * <p>
 * 密码分段加密工具类
 * </p>
 * 
 * <p>
 * 加密流程：
 * 1. 将密码均分成 N 段
 * 2. 每段使用不同的密钥独立加密
 * 3. 将加密后的片段合并存储
 * 
 * 解密流程：
 * 1. 将加密数据分割成 N 段
 * 2. 每段使用对应的密钥独立解密
 * 3. 将解密后的片段合并，对比验证
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Slf4j
public class PasswordSegmentUtils {
    
    /**
     * 分段数量（可配置）
     */
    private static final int SEGMENT_COUNT = 4;
    
    /**
     * 加密算法
     */
    private static final String ALGORITHM = "AES";
    
    /**
     * 转换格式
     */
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    
    /**
     * 密钥长度（位）
     */
    private static final int KEY_LENGTH = 128;
    
    /**
     * 主密钥数组（实际应用中应从配置文件或密钥管理系统获取）
     */
    private static final SecretKey[] MASTER_KEYS = generateMasterKeys();
    
    /**
     * 生成主密钥（系统启动时生成一次）
     */
    private static SecretKey[] generateMasterKeys() {
        try {
            SecretKey[] keys = new SecretKey[SEGMENT_COUNT];
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_LENGTH, new SecureRandom());
            
            for (int i = 0; i < SEGMENT_COUNT; i++) {
                keys[i] = keyGen.generateKey();
            }
            return keys;
        } catch (Exception e) {
            log.error("生成主密钥失败", e);
            throw new RuntimeException("生成主密钥失败", e);
        }
    }
    
    /**
     * 加密密码（分段加密）
     * 
     * @param password 原始密码
     * @return 加密后的字符串（Base64 编码，各段用|分隔）
     */
    public static String encrypt(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        
        try {
            // 1. 将密码均分成 N 段
            String[] segments = splitPassword(password);
            
            // 2. 分别加密每一段
            String[] encryptedSegments = new String[segments.length];
            for (int i = 0; i < segments.length; i++) {
                encryptedSegments[i] = encryptSegment(segments[i], MASTER_KEYS[i]);
            }
            
            // 3. 合并所有加密段
            return String.join("|", encryptedSegments);
            
        } catch (Exception e) {
            log.error("加密密码失败", e);
            throw new RuntimeException("加密密码失败", e);
        }
    }
    
    /**
     * 解密密码（分段解密并验证）
     * 
     * @param encryptedPassword 加密后的密码
     * @param inputPassword 输入的明文密码（用于对比）
     * @return 是否匹配
     */
    public static boolean decryptAndVerify(String encryptedPassword, String inputPassword) {
        if (encryptedPassword == null || encryptedPassword.isEmpty()) {
            return false;
        }
        if (inputPassword == null || inputPassword.isEmpty()) {
            return false;
        }
        
        try {
            // 1. 分割加密数据
            String[] encryptedSegments = encryptedPassword.split("\\|");
            
            if (encryptedSegments.length != SEGMENT_COUNT) {
                log.warn("加密数据段数不匹配：期望{}, 实际{}", SEGMENT_COUNT, encryptedSegments.length);
                return false;
            }
            
            // 2. 分别解密每一段
            StringBuilder decryptedPassword = new StringBuilder();
            for (int i = 0; i < encryptedSegments.length; i++) {
                String decryptedSegment = decryptSegment(encryptedSegments[i], MASTER_KEYS[i]);
                decryptedPassword.append(decryptedSegment);
            }
            
            // 3. 对比解密结果和输入密码
            boolean matches = decryptedPassword.toString().equals(inputPassword);
            log.debug("密码验证结果：{}", matches ? "成功" : "失败");
            
            return matches;
            
        } catch (Exception e) {
            log.error("解密密码失败", e);
            return false;
        }
    }
    
    /**
     * 将密码均分成 N 段
     */
    private static String[] splitPassword(String password) {
        int length = password.length();
        int segmentSize = (int) Math.ceil((double) length / SEGMENT_COUNT);
        String[] segments = new String[SEGMENT_COUNT];
        
        for (int i = 0; i < SEGMENT_COUNT; i++) {
            int start = i * segmentSize;
            int end = Math.min(start + segmentSize, length);
            
            if (start >= length) {
                segments[i] = ""; // 空段
            } else {
                segments[i] = password.substring(start, end);
            }
        }
        
        return segments;
    }
    
    /**
     * 加密单个片段
     */
    private static String encryptSegment(String segment, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(segment.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    /**
     * 解密单个片段
     */
    private static String decryptSegment(String encryptedSegment, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedSegment));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    /**
     * 导出主密钥（用于持久化到配置文件）
     * ⚠️ 仅首次启动时使用，之后应禁用此方法
     */
    public static String[] exportMasterKeys() {
        String[] keyStrings = new String[SEGMENT_COUNT];
        for (int i = 0; i < SEGMENT_COUNT; i++) {
            keyStrings[i] = Base64.getEncoder().encodeToString(MASTER_KEYS[i].getEncoded());
        }
        return keyStrings;
    }
    
    /**
     * 从 Base64 字符串导入主密钥
     * 
     * @param keyStrings Base64 编码的密钥数组
     */
    public static void importMasterKeys(String[] keyStrings) {
        if (keyStrings == null || keyStrings.length != SEGMENT_COUNT) {
            throw new IllegalArgumentException("密钥数组无效");
        }
        
        try {
            for (int i = 0; i < SEGMENT_COUNT; i++) {
                byte[] keyBytes = Base64.getDecoder().decode(keyStrings[i]);
                MASTER_KEYS[i] = new SecretKeySpec(keyBytes, ALGORITHM);
            }
            log.info("成功导入{}个主密钥", SEGMENT_COUNT);
        } catch (Exception e) {
            log.error("导入主密钥失败", e);
            throw new RuntimeException("导入主密钥失败", e);
        }
    }
    
    /**
     * 验证密码（推荐用于登录场景）
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
            // 1. 分割加密数据
            String[] encryptedSegments = encryptedPassword.split("\\|");
            
            if (encryptedSegments.length != SEGMENT_COUNT) {
                log.warn("加密数据段数不匹配：期望{}, 实际{}", SEGMENT_COUNT, encryptedSegments.length);
                return false;
            }
            
            // 2. 分别解密每一段
            StringBuilder decryptedPassword = new StringBuilder();
            for (int i = 0; i < encryptedSegments.length; i++) {
                String decryptedSegment = decryptSegment(encryptedSegments[i], MASTER_KEYS[i]);
                decryptedPassword.append(decryptedSegment);
            }
            
            // 3. 对比解密结果和输入密码
            boolean matches = decryptedPassword.toString().equals(inputPassword);
            log.debug("密码验证结果：{}", matches ? "成功" : "失败");
            
            return matches;
            
        } catch (Exception e) {
            log.error("解密密码失败", e);
            return false;
        }
    }


}
