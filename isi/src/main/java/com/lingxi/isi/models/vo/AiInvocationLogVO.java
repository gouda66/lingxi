package com.lingxi.isi.models.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * AI 调用日志视图对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class AiInvocationLogVO {

    /**
     * 日志 ID
     */
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
     * AI 提供商
     */
    private String provider;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 调用类型
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
    private LocalDateTime createdAt;
}
