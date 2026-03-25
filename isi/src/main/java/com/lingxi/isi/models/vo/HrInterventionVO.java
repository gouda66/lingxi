package com.lingxi.isi.models.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * HR 干预记录视图对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class HrInterventionVO {

    /**
     * 干预 ID
     */
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
     * 干预类型
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
    private LocalDateTime createdAt;
}
