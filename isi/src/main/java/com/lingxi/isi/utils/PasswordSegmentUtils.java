package com.lingxi.isi.utils;

import com.lingxi.isi.mapper.SystemSecretKeyMapper;
import com.lingxi.isi.models.entity.SystemSecretKey;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

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
@Component
public class PasswordSegmentUtils {
    
    private static final int SEGMENT_COUNT = 4;
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final int KEY_LENGTH = 128;
    
    private static SecretKey[] MASTER_KEYS = new SecretKey[SEGMENT_COUNT];
    
    @Autowired(required = false)
    private SystemSecretKeyMapper secretKeyMapper;
    
    @PostConstruct
    public void init() {
        loadOrGenerateMasterKeys();
    }
    
    /**
     * 加载或生成主密钥
     */
    private void loadOrGenerateMasterKeys() {
        try {
            // 尝试从数据库加载
            if (secretKeyMapper != null) {
                List<SystemSecretKey> keys = secretKeyMapper.selectActiveKeys();
                
                // 检查密钥是否完整且有效
                boolean allKeysValid = keys != null && keys.size() == SEGMENT_COUNT;
                if (allKeysValid) {
                    for (SystemSecretKey key : keys) {
                        if (key.getKeyValue() == null || key.getKeyValue().trim().isEmpty()) {
                            allKeysValid = false;
                            break;
                        }
                    }
                }
                
                if (allKeysValid) {
                    // 所有密钥都有效，加载到内存
                    for (int i = 0; i < SEGMENT_COUNT; i++) {
                        byte[] keyBytes = Base64.getDecoder().decode(keys.get(i).getKeyValue());
                        MASTER_KEYS[i] = new SecretKeySpec(keyBytes, ALGORITHM);
                    }
                    log.info("从数据库成功加载 {} 个密钥", SEGMENT_COUNT);
                    return;
                }
            }
            
            // 密钥不存在或无效，生成新密钥并保存
            log.warn("数据库中密钥不存在或无效，生成新密钥");
            generateAndSaveMasterKeys();
            
        } catch (Exception e) {
            log.error("加载密钥失败，使用临时密钥（重启后将失效）", e);
            generateMasterKeysInMemory();
        }
    }
    
    /**
     * 生成密钥并保存到数据库
     */
    private void generateAndSaveMasterKeys() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_LENGTH, new SecureRandom());
            
            for (int i = 0; i < SEGMENT_COUNT; i++) {
                SecretKey key = keyGen.generateKey();
                String keyValue = Base64.getEncoder().encodeToString(key.getEncoded());
                
                // 先尝试更新已存在的记录
                SystemSecretKey existingKey = secretKeyMapper.selectByName("MASTER_KEY_" + i);
                
                if (existingKey != null) {
                    // 更新现有记录
                    existingKey.setKeyValue(keyValue);
                    existingKey.setAlgorithm(ALGORITHM);
                    existingKey.setIsActive(1);
                    secretKeyMapper.updateById(existingKey);
                } else {
                    // 插入新记录
                    SystemSecretKey newKey = new SystemSecretKey();
                    newKey.setKeyName("MASTER_KEY_" + i);
                    newKey.setKeyValue(keyValue);
                    newKey.setAlgorithm(ALGORITHM);
                    newKey.setIsActive(1);
                    secretKeyMapper.insert(newKey);
                }
                
                // 加载到内存
                MASTER_KEYS[i] = key;
            }
            
            log.info("已生成并保存 {} 个密钥到数据库", SEGMENT_COUNT);
            
        } catch (Exception e) {
            log.error("生成密钥失败，使用临时密钥", e);
            generateMasterKeysInMemory();
        }
    }
    
    /**
     * 内存中生成密钥（临时方案）
     */
    private void generateMasterKeysInMemory() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_LENGTH, new SecureRandom());
            
            for (int i = 0; i < SEGMENT_COUNT; i++) {
                MASTER_KEYS[i] = keyGen.generateKey();
            }
        } catch (Exception e) {
            log.error("生成临时密钥失败", e);
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
