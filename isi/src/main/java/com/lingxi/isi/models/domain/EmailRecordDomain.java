package com.lingxi.isi.models.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 邮件发送记录领域模型 - 充血模型
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("email_record")
public class EmailRecordDomain implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联会话 ID
     */
    private Long sessionId;

    /**
     * 接收人用户 ID
     */
    private Long recipientId;

    /**
     * 接收人邮箱
     */
    private String recipientEmail;

    /**
     * 使用的模板 ID
     */
    private Long templateId;

    /**
     * 邮件标题
     */
    private String subject;

    /**
     * 邮件内容（渲染后的 HTML）
     */
    private String contentHtml;

    /**
     * 发送状态：PENDING-待发送 SENT-已发送 FAILED-发送失败
     */
    private String sendStatus;

    /**
     * 发送时间
     */
    private LocalDateTime sentAt;

    /**
     * 失败原因
     */
    private String errorMsg;

    /**
     * 重试次数
     */
    private Integer retryCount;

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
