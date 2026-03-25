package com.lingxi.isi.models.entity;
import java.io.Serial;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 面试题目表 - AI 生成 + HR 修改
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("interview_question")
public class InterviewQuestion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属会话
     */
    private Long sessionId;

    /**
     * 题号（顺序）
     */
    private Integer sequenceNo;

    /**
     * 来源：AI-AI 生成 HR-HR 录入 HR_MODIFIED-HR 修改 AI
     */
    private String source;

    /**
     * 类型：1-技术问题 2-项目经验 3-情景题 4-基础素质 5-算法题
     */
    private Integer questionType;

    /**
     * AI 原始生成内容
     */
    private String originalContent;

    /**
     * 最终展示内容（HR 可能修改）
     */
    private String content;

    /**
     * 参考答案
     */
    private String referenceAnswer;

    /**
     * 考察知识点（JSON 或逗号分隔）
     */
    private String knowledgePoints;

    /**
     * 难度：1-3
     */
    private Integer difficulty;

    /**
     * 建议回答时长（秒）
     */
    private Integer expectedDuration;

    /**
     * 修改人 HR ID
     */
    private Long modifiedBy;

    /**
     * 修改时间
     */
    private LocalDateTime modifiedAt;

    /**
     * 修改原因
     */
    private String modifyReason;

    /**
     * 状态：PENDING-待提问 ASKED-已提问 ANSWERED-已回答 EVALUATED-已评估
     */
    private String status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;


}
