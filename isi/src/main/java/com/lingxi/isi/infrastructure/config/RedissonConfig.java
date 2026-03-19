package com.lingxi.isi.infrastructure.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host:localhost}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port;

    @Value("${spring.redis.password:}")
    private String password;

    @Value("${spring.redis.database:0}")
    private int database;

    /**
     * 创建 RedissonClient Bean
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        
        // 单机模式配置
        String address = String.format("redis://%s:%d", host, port);
        config.useSingleServer()
                .setAddress(address)
                .setDatabase(database)
                .setTimeout(3000)
                .setConnectTimeout(3000)
                .setRetryAttempts(3)
                .setRetryInterval(1500);
        
        // 如果有密码，设置密码
        if (password != null && !password.isEmpty()) {
            config.useSingleServer().setPassword(password);
        }
        
        return Redisson.create(config);
    }
}