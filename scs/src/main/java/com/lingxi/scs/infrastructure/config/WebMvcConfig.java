package com.lingxi.scs.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingxi.scs.common.mapper.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * Web MVC配置（Spring Boot 4.x版本）
 *
 * Spring Boot 已自动配置：
 * - Jackson 消息转换器（JSON 序列化/反序列化）
 * - LocalDateTime/LocalDate 等 Java 8 时间类型支持
 * - 常用的 HttpMessageConverter
 *
 * 如需自定义 Jackson 配置，可在 application.yml 中配置：
 * spring:
 *   jackson:
 *     date-format: yyyy-MM-dd HH:mm:ss
 *     time-zone: GMT+8
 *
 * @author system
 */
@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 设置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射...");
        registry.addResourceHandler("/backend/**")
                .addResourceLocations("classpath:/backend/")
                .setCachePeriod(0);
        registry.addResourceHandler("/front/**")
                .addResourceLocations("classpath:/front/")
                .setCachePeriod(0);
    }

    /**
     * 配置视图控制器，实现路径重定向
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/front", "/front/index.html");
        registry.addRedirectViewController("/backend", "/backend/index.html");
    }

    /**
     * 扩展mvc框架的消息转换器
     * 注意：只在 /api/** 路径下使用自定义的 ObjectMapper
     * MCP 端点 (/sse, /sse/message) 使用默认的 Jackson 配置
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        // 注释掉自定义消息转换器，让所有端点都使用默认配置
        // 这样可以避免影响 MCP 协议的消息格式
        /*
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0,messageConverter);
        */
        log.info("使用默认 Jackson 配置，确保 MCP 协议兼容性");
    }
}
