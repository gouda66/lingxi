package com.lingxi.isi.models.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 面试题目视图对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class InterviewQuestionVO {

    /**
     * 题目 ID
     */
    private Long id;

    /**
     * 所属会话
     */
    private Long sessionId;

    /**
     * 题号
     */
    private Integer sequenceNo;

    /**
     * 来源
     */
    private String source;

    /**
     * 类型
     */
    private Integer questionType;

    /**
     * AI 原始生成内容
     */
    private String originalContent;

    /**
     * 最终展示内容
     */
    private String content;

    /**
     * 参考答案
     */
    private String referenceAnswer;

    /**
     * 考察知识点
     */
    private String knowledgePoints;

    /**
     * 难度
     */
    private Integer difficulty;

    /**
     * 建议回答时长
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
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
