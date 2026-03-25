package com.lingxi.isi.models.domain;

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
 * AI 调用日志领域模型 - 充血模型
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_invocation_log")
public class AiInvocationLogDomain implements Serializable {

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
     * 回答 ID
     */
    private Long answerId;

    /**
     * 使用的 AI 模型
     */
    private String aiModel;

    /**
     * 场景：EVALUATION-评分 GENERATE_QUESTION-出题 ANALYZE-分析 GENERATE_REPORT-生成报告
     */
    private String scenario;

    /**
     * 输入内容
     */
    private String inputContent;

    /**
     * AI 返回内容
     */
    private String outputContent;

    /**
     * Token 消耗：输入
     */
    private Integer inputTokens;

    /**
     * Token 消耗：输出
     */
    private Integer outputTokens;

    /**
     * 耗时（毫秒）
     */
    private Integer costMs;

    /**
     * 调用状态：SUCCESS-成功 FAIL-失败
     */
    private String status;

    /**
     * 错误信息
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

}
