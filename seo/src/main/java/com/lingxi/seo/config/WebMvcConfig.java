package com.lingxi.seo.config;

import com.lingxi.seo.interceptor.RequestLogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private RequestLogInterceptor requestLogInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLogInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(
                    "/static/**",        // 排除静态资源
                    "/favicon.ico",      // 排除 favicon
                    "/actuator/**"       // 排除监控端点
                );
    }
}
