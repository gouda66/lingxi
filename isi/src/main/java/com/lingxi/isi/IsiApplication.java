package com.lingxi.isi;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IsiApplication {

    public static void main(String[] args) {
        SpringApplication.run(IsiApplication.class, args);
    }

    @Bean
    public SnowflakeGenerator snowflakeGenerator() {
        return new SnowflakeGenerator();
    }
}
