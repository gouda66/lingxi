package com.lingxi.isi.models.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 邮件发送记录视图对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class EmailRecordVO {

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
     * 邮件 HTML 内容
     */
    private String contentHtml;

    /**
     * 邮件纯文本内容
     */
    private String contentText;

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
     * MCP 请求 ID
     */
    private String mcpRequestId;

    /**
     * 邮件服务商
     */
    private String mcpProvider;

    /**
     * 状态
     */
    private String status;

    /**
     * 实际发送时间
     */
    private LocalDateTime sentAt;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 是否已打开
     */
    private Integer opened;

    /**
     * 打开时间
     */
    private LocalDateTime openedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
