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
 * 面试报告领域模型 - 充血模型
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("interview_report")
public class InterviewReportDomain implements Serializable {

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
     * 面试者用户 ID
     */
    private Long candidateId;

    /**
     * HR 用户 ID
     */
    private Long hrId;

    /**
     * 总分（0-10）
     */
    private BigDecimal totalScore;

    /**
     * 技术能力分（0-10）
     */
    private BigDecimal technicalScore;

    /**
     * 沟通表达分（0-10）
     */
    private BigDecimal communicationScore;

    /**
     * 逻辑思维分（0-10）
     */
    private BigDecimal logicScore;

    /**
     * 文化匹配分（0-10）
     */
    private BigDecimal cultureFitScore;

    /**
     * 学习能力分（0-10）
     */
    private BigDecimal learningAbilityScore;

    /**
     * 优势总结
     */
    private String strengths;

    /**
     * 劣势/不足
     */
    private String weaknesses;

    /**
     * 技术评估
     */
    private String technicalAssessment;

    /**
     * 综合评价
     */
    private String overallEvaluation;

    /**
     * 录用建议：1-强烈推荐 2-推荐 3-待定 4-不推荐 5-强烈不推荐
     */
    private Integer recommendation;

    /**
     * AI 生成标记：0-人工 1-AI 生成
     */
    private Integer aiGenerated;

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
