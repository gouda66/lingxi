package com.lingxi.isi.models.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 面试报告数据传输对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class InterviewReportDTO {

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
     * 综合得分（0-100）
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
}
