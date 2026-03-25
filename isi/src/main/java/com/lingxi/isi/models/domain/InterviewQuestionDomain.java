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
 * 面试题目领域模型 - 充血模型
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("interview_question")
public class InterviewQuestionDomain implements Serializable {

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
     * 题目标题
     */
    private String title;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 题目类型：TECHNICAL-技术 BEHAVIORIAL-行为 CODING-编程 SITUATIONAL-情景
     */
    private String questionType;

    /**
     * 考察维度：COMMUNICATION-沟通 LOGIC-逻辑 TECHNICAL_SKILL-技术能力等
     */
    private String dimension;

    /**
     * 难度等级：1-简单 2-中等 3-困难
     */
    private Integer difficultyLevel;

    /**
     * 期望答题要点 JSON
     */
    private String expectedPointsJson;

    /**
     * 参考评分标准 JSON
     */
    private String scoringCriteriaJson;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 答题时限（秒）
     */
    private Integer timeLimitSeconds;

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
