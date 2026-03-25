package com.lingxi.isi.models.dto;

import lombok.Data;

/**
 * <p>
 * 面试题目数据传输对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class InterviewQuestionDTO {

    /**
     * 题目 ID
     */
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
     * 来源
     */
    private String source;

    /**
     * 类型：1-技术问题 2-项目经验 3-情景题 4-基础素质 5-算法题
     */
    private Integer questionType;

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
     * 难度：1-3
     */
    private Integer difficulty;

    /**
     * 建议回答时长（秒）
     */
    private Integer expectedDuration;
}
