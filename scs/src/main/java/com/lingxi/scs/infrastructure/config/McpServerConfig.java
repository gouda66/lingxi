package com.lingxi.scs.infrastructure.config;

import com.lingxi.scs.interfaces.mcp.ScsMcpToolService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider scsTools(ScsMcpToolService scsMcpToolService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(scsMcpToolService)
                .build();
    }
}
