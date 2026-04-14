package com.lingxi.isi.models.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 面试回答数据传输对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class InterviewAnswerDTO {

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
     * 文字回答内容
     */
    private String contentText;

    /**
     * 代码作答内容
     */
    private String codeContent;

    /**
     * 编程语言
     */
    private String codeLanguage;

    /**
     * 思考时长（秒）
     */
    private Integer thinkingSeconds;

    /**
     * 回答时长（秒）
     */
    private Integer answerSeconds;
}
