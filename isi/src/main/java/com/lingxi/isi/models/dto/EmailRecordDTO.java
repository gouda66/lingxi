package com.lingxi.isi.models.dto;

import lombok.Data;

/**
 * <p>
 * 邮件发送记录数据传输对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class EmailRecordDTO {

    /**
     * 记录 ID
     */
    private Long id;

    /**
     * 邮件模板编码
     */
    private String templateCode;

    /**
     * 收件人邮箱
     */
    private String recipientEmail;

    /**
     * 收件人姓名
     */
    private String recipientName;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 关联业务类型
     */
    private String relatedType;

    /**
     * 关联业务 ID
     */
    private Long relatedId;

    /**
     * 会话 ID
     */
    private Long sessionId;

    /**
     * 邮件服务商
     */
    private String mcpProvider;
}
