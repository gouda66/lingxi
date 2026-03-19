package com.lingxi.isi.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 验证码缓存服务（基于 Redis + Redisson）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CaptchaCacheService {

    private final RedissonClient redissonClient;

    /**
     * Redis Key 前缀
     */
    private static final String CAPTCHA_KEY_PREFIX = "captcha:image:";

    /**
     * 验证码有效期（秒）
     */
    private static final long EXPIRE_SECONDS = 300; // 5 分钟

    /**
     * 保存验证码
     * @param uuid 唯一标识
     * @param code 验证码
     */
    public void set(String uuid, String code) {
        String key = CAPTCHA_KEY_PREFIX + uuid;
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(code, EXPIRE_SECONDS, TimeUnit.SECONDS);
        log.debug("保存验证码到 Redis：key={}, code={}, expire={}s", key, code, EXPIRE_SECONDS);
    }

    /**
     * 获取验证码
     * @param uuid 唯一标识
     * @return 验证码，不存在或过期返回 null
     */
    public String get(String uuid) {
        if (uuid == null) {
            return null;
        }
        
        String key = CAPTCHA_KEY_PREFIX + uuid;
        RBucket<String> bucket = redissonClient.getBucket(key);
        String code = bucket.get();
        
        if (code == null) {
            log.debug("验证码不存在或已过期：key={}", key);
        }
        
        return code;
    }

    /**
     * 删除验证码（验证成功后调用）
     * @param uuid 唯一标识
     */
    public void remove(String uuid) {
        if (uuid != null) {
            String key = CAPTCHA_KEY_PREFIX + uuid;
            RBucket<String> bucket = redissonClient.getBucket(key);
            boolean deleted = bucket.delete();
            log.debug("删除验证码：key={}, result={}", key, deleted);
        }
    }

    /**
     * 验证验证码
     * @param uuid 唯一标识
     * @param code 用户输入的验证码
     * @return 验证结果
     */
    public boolean validate(String uuid, String code) {
        if (uuid == null || code == null) {
            return false;
        }

        String savedCode = get(uuid);
        if (savedCode == null) {
            return false;
        }

        // 验证码不区分大小写
        boolean result = savedCode.equalsIgnoreCase(code);
        if (result) {
            remove(uuid); // 验证成功后删除
        }
        
        return result;
    }
}
