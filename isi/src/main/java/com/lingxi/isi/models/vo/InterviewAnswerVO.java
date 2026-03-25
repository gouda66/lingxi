package com.lingxi.isi.models.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 面试回答视图对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class InterviewAnswerVO {

    /**
     * 回答 ID
     */
    private Long id;

    /**
     * 会话 ID
     */
    private Long sessionId;

    /**
     * 关联题目
     */
    private Long questionId;

    /**
     * 面试者用户 ID
     */
    private Long candidateId;

    /**
     * 文字回答内容
     */
    private String contentText;

    /**
     * 语音回答 URL
     */
    private String audioUrl;

    /**
     * 语音时长（秒）
     */
    private Integer audioDuration;

    /**
     * 代码作答内容
     */
    private String codeContent;

    /**
     * 编程语言
     */
    private String codeLanguage;

    /**
     * 开始作答时间
     */
    private LocalDateTime startTime;

    /**
     * 结束作答时间
     */
    private LocalDateTime endTime;

    /**
     * 思考时长（秒）
     */
    private Integer thinkingSeconds;

    /**
     * 回答时长（秒）
     */
    private Integer answerSeconds;

    /**
     * AI 评价内容
     */
    private String aiEvaluation;

    /**
     * AI 评分（0-10）
     */
    private BigDecimal aiScore;

    /**
     * 多维度评分
     */
    private String aiDimensions;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
