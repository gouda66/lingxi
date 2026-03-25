package com.lingxi.isi.models.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * AI 调用日志数据传输对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class AiInvocationLogDTO {

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
}
