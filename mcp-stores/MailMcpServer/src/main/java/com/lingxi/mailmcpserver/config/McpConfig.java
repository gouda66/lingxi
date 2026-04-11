package com.lingxi.mailmcpserver.config;

import com.lingxi.mailmcpserver.service.MailService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider mailTools(MailService mailService) {
        return MethodToolCallbackProvider.builder().toolObjects(mailService).build();
    }
}
