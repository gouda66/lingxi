package com.lingxi.isi.models.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 面试决策领域模型 - 充血模型
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("interview_decision")
public class InterviewDecisionDomain implements Serializable {

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
     * HR 用户 ID
     */
    private Long hrId;

    /**
     * 决策类型：PASS-通过 REJECT-拒绝 HOLD-待定
     */
    private String decisionType;

    /**
     * 决策理由
     */
    private String reason;

    /**
     * 建议职位级别
     */
    private String suggestedLevel;

    /**
     * 建议薪资范围
     */
    private String suggestedSalary;

    /**
     * 入职时间建议
     */
    private String suggestedStartDate;

    /**
     * 是否需要复试
     */
    private Integer needSecondRound;

    /**
     * 备注
     */
    private String remarks;

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
