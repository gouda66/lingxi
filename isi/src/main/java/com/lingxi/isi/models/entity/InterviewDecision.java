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
 * 面试决策表 - HR 操作
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("interview_decision")
public class InterviewDecision implements Serializable {

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
     * 报告 ID
     */
    private Long reportId;

    /**
     * 决策 HR
     */
    private Long hrId;

    /**
     * 决策：PASS-通过 REJECT-拒绝 PENDING-待定
     */
    private String decision;

    /**
     * 决策理由
     */
    private String decisionReason;

    /**
     * 是否进入下一轮：0-否 1-是
     */
    private Integer nextRound;

    /**
     * 下一轮安排说明
     */
    private String nextRoundDesc;

    /**
     * 建议薪资（如通过）
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;


}
