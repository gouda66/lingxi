package com.lingxi.isi.models.request;

import lombok.Data;

/**
 * 邮件发送请求
 */
@Data
public class EmailRequest {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收件人邮箱
     */
    private String toEmail;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 邮件内容
     */
    private String content;
}
