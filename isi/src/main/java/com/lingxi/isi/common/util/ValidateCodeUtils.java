package com.lingxi.isi.common.util;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 随机生成验证码工具类
 */
@Slf4j
@Component
public class ValidateCodeUtils {

    /**
     * 验证码缓存前缀
     */
    private static final String CAPTCHA_PREFIX = "captcha:";

    /**
     * 验证码过期时间（秒）
     */
    private static final long CAPTCHA_EXPIRE_SECONDS = 120L;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public ValidateCodeUtils() {
    }

    /**
     * 随机生成验证码
     * @param length 长度为4位或者6位
     * @return
     */
    public Integer generateValidateCode(int length){
        Integer code =null;
        if(length == 4){
            code = new Random().nextInt(9999);//生成随机数，最大为9999
            if(code < 1000){
                code = code + 1000;//保证随机数为4位数字
            }
        }else if(length == 6){
            code = new Random().nextInt(999999);//生成随机数，最大为999999
            if(code < 100000){
                code = code + 100000;//保证随机数为6位数字
            }
        }else{
            throw new RuntimeException("只能生成4位或6位数字验证码");
        }
        return code;
    }

    /**
     * 生成验证码图片
     * 
     * @return Map 包含验证码图片和 UUID
     */
    public Map<String, Object> generateCaptchaImage() {
        try {
            // 生成 UUID 作为验证码标识
            String uuid = IdUtil.fastSimpleUUID();
            
            // 定义验证码 cache key
            String cacheKey = getCacheKey(uuid);
            
            // 使用 Hutool 生成圆形干扰验证码
            CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(300, 100, 5, 20);
            
            // 获取验证码文本
            String code = captcha.getCode();
            
            // 存入 Redis，设置过期时间
            redisTemplate.opsForValue().set(cacheKey, code, CAPTCHA_EXPIRE_SECONDS, TimeUnit.SECONDS);

            // 构建响应数据
            Map<String, Object> result = new HashMap<>();
            result.put("captchaEnabled", true);
            result.put("img", captcha.getImageBase64());
            result.put("uuid", uuid);
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("生成验证码失败：" + e.getMessage(), e);
        }
    }

    /**
     * 验证验证码是否正确
     */
    public boolean validateCaptcha(String uuid, String code) {
        if (uuid == null || code == null) {
            return false;
        }

        try {
            String cacheKey = getCacheKey(uuid);

            // 从 Redis 获取验证码
            String captchaCode = redisTemplate.opsForValue().get(cacheKey);

            // 验证失败（不存在或已过期）
            if (captchaCode == null) {
                return false;
            }

            // 删除已使用的验证码（一次性使用）
            redisTemplate.delete(cacheKey);

            return captchaCode.equalsIgnoreCase(code.trim());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取缓存 Key
     */
    private String getCacheKey(String uuid) {
        return CAPTCHA_PREFIX + "0:" + uuid;
    }
}
