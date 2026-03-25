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
 * 面试回答表 - 实时记录
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("interview_answer")
public class InterviewAnswer implements Serializable {

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
     * 语音回答 URL（如有语音面试）
     */
    private String audioUrl;

    /**
     * 语音时长（秒）
     */
    private Integer audioDuration;

    /**
     * 代码作答内容（编程题）
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
     * AI 评价内容（结构化 JSON）
     */
    private String aiEvaluation;

    /**
     * AI 评分（0-10）
     */
    private BigDecimal aiScore;

    /**
     * 多维度评分：{technical:8.5,communication:9.0,logic:8.0}
     */
    private String aiDimensions;

    /**
     * 状态：ANSWERING-回答中 EVALUATING-评估中 COMPLETED-已完成
     */
    private String status;

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
