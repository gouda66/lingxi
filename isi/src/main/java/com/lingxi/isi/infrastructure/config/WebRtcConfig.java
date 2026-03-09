package com.lingxi.isi.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

/**
 * WebRTC 信令服务器配置
 * 用于面试间实时音视频通信
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebRtcConfig {

    // STUN/TURN 服务器配置（用于穿透 NAT）
    public static final String STUN_SERVER = "stun:stun.l.google.com:19302";
    
    // 如果需要 TURN 服务器（对称 NAT），需要配置：
    // turn:your-turn-server.com:3478
    // username/password
    
    /**
     * 获取 ICE 服务器配置
     */
    public java.util.List<String> getIceServers() {
        java.util.List<String> servers = new java.util.ArrayList<>();
        servers.add(STUN_SERVER);
        // 如果有 TURN 服务器，添加到这里
        // servers.add("turn:your-turn-server.com:3478");
        return servers;
    }
}
