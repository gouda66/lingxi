package com.lingxi.isi.mcp;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailMcpService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * MCP Tool: 发送邮件
     */
    @Tool(name = "send_email", description = "发送邮件给指定收件人")
    public String sendEmail(
            @ToolParam(description = "收件人邮箱地址") String to,
            @ToolParam(description = "邮件主题") String subject,
            @ToolParam(description = "邮件正文内容") String body,
            @ToolParam(description = "抄送地址，多个用逗号分隔", required = false) String cc,
            @ToolParam(description = "密送地址，多个用逗号分隔", required = false) String bcc) {
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            
            if (cc != null && !cc.isEmpty()) {
                message.setCc(cc.split(","));
            }
            if (bcc != null && !bcc.isEmpty()) {
                message.setBcc(bcc.split(","));
            }
            
            mailSender.send(message);
            return "✅ 邮件发送成功！收件人: " + to;
            
        } catch (Exception e) {
            return "❌ 发送失败: " + e.getMessage();
        }
    }

    /**
     * MCP Tool: 验证邮箱格式
     */
    @Tool(name = "validate_email", description = "验证邮箱地址格式是否正确")
    public boolean validateEmail(
            @ToolParam(description = "邮箱地址") String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}