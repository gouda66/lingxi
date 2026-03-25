package com.lingxi.isi.models.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 邮件发送记录表 - MCP 调用日志
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("email_record")
public class EmailRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 邮件模板编码：INTERVIEW_PASS-面试通过等
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
     * 关联业务类型：INTERVIEW_DECISION/SYSTEM_NOTICE
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
     * 邮件服务商：SENDGRID/AWS_SES/ALIYUN
     */
    private String mcpProvider;

    /**
     * 状态：PENDING-待发送 SENDING-发送中 SENT-已发送 FAILED-失败
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;


}
