package com.lingxi.isi.models.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 面试报告视图对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class InterviewReportVO {

    /**
     * 报告 ID
     */
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
     * 处理 HR
     */
    private Long hrId;

    /**
     * 综合得分
     */
    private BigDecimal totalScore;

    /**
     * 技术能力
     */
    private BigDecimal technicalScore;

    /**
     * 沟通能力
     */
    private BigDecimal communicationScore;

    /**
     * 逻辑思维
     */
    private BigDecimal logicScore;

    /**
     * 文化匹配度
     */
    private BigDecimal cultureFitScore;

    /**
     * 学习能力
     */
    private BigDecimal learningAbilityScore;

    /**
     * 优势亮点
     */
    private String strengths;

    /**
     * 待提升点
     */
    private String weaknesses;

    /**
     * 技术能力评估
     */
    private String technicalAssessment;

    /**
     * 综合评价
     */
    private String overallEvaluation;

    /**
     * AI 建议
     */
    private Integer recommendation;

    /**
     * 问答摘要
     */
    private String qaSummary;

    /**
     * 报告生成时间
     */
    private LocalDateTime generatedAt;

    /**
     * 是否 AI 生成
     */
    private Integer generatedByAi;

    /**
     * HR 是否已查看
     */
    private Integer hrReviewed;

    /**
     * HR 查看时间
     */
    private LocalDateTime hrReviewedAt;

    /**
     * HR 补充评价
     */
    private String hrComments;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
