package com.lingxi.isi.models.entity;
import java.io.Serial;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 面试综合报告表 - AI 生成
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("interview_report")
public class InterviewReport implements Serializable {

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
     * AI 建议：0-待定 1-强烈推荐 2-推荐 3-一般 4-不推荐
     */
    private Integer recommendation;

    /**
     * 问答摘要：[{question,answer,keyPoints}]
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;


}
