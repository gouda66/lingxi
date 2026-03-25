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
 * HR 干预记录表
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("hr_intervention")
public class HrIntervention implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 面试会话 ID
     */
    private Long sessionId;

    /**
     * HR 用户 ID
     */
    private Long hrId;

    /**
     * 干预类型：MODIFY_QUESTION-修改题目 ADD_QUESTION-追加题目 SKIP_QUESTION-跳过题目 HINT-提示
     */
    private String interventionType;

    /**
     * 目标题目 ID
     */
    private Long targetQuestionId;

    /**
     * 修改前内容
     */
    private String beforeContent;

    /**
     * 修改后内容
     */
    private String afterContent;

    /**
     * 备注/原因
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;


}
