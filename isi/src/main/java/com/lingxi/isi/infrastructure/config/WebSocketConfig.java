package com.lingxi.isi.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 配置 - 用于面试间实时通信
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单的内存消息代理，将消息广播到订阅的客户端
        config.enableSimpleBroker("/topic", "/queue");
        
        // 设置应用目的地前缀
        config.setApplicationDestinationPrefixes("/app");
        
        // 设置用户相关消息的前缀
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册 STOMP 协议的端点，前端通过此端点连接 WebSocket
        registry.addEndpoint("/ws-interview")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
