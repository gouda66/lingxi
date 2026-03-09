package com.lingxi.isi.infrastructure.config;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ID 生成器配置
 */
@Configuration
public class IdGeneratorConfig {

    @Bean
    public SnowflakeGenerator snowflakeGenerator() {
        // 使用 Hutool 的雪花算法生成唯一 ID
        // 数据中心 ID 和机器 ID 都设置为 0（单机环境）
        return new SnowflakeGenerator(0, 0);
    }
}
