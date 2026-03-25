package com.lingxi.isi.models.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * AI 调用日志表 - MCP/AI 成本追踪
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_invocation_log")
public class AiInvocationLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话 ID
     */
    private Long sessionId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * AI 提供商：OPENAI/ANTHROPIC/LOCAL
     */
    private String provider;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 调用类型：GENERATE_QUESTION/EVALUATE_ANSWER/GENERATE_REPORT
     */
    private String invocationType;

    /**
     * 提示词 token 数
     */
    private Integer promptTokens;

    /**
     * 完成 token 数
     */
    private Integer completionTokens;

    /**
     * 总 token 数
     */
    private Integer totalTokens;

    /**
     * 估算成本（USD）
     */
    private BigDecimal costUsd;

    /**
     * 响应延迟（ms）
     */
    private Integer latencyMs;

    /**
     * 请求内容（脱敏后）
     */
    private String requestContent;

    /**
     * 响应内容
     */
    private String responseContent;

    /**
     * 状态
     */
    private String status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;


}
