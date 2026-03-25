package com.lingxi.isi.models.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 面试决策视图对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class InterviewDecisionVO {

    /**
     * 决策 ID
     */
    private Long id;

    /**
     * 会话 ID
     */
    private Long sessionId;

    /**
     * 报告 ID
     */
    private Long reportId;

    /**
     * 决策 HR
     */
    private Long hrId;

    /**
     * 决策
     */
    private String decision;

    /**
     * 决策理由
     */
    private String decisionReason;

    /**
     * 是否进入下一轮
     */
    private Integer nextRound;

    /**
     * 下一轮安排说明
     */
    private String nextRoundDesc;

    /**
     * 建议薪资
     */
    private String salaryOffer;

    /**
     * 是否发送通知邮件
     */
    private Integer sendNotification;

    /**
     * 决策时间
     */
    private LocalDateTime decidedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
