package com.lingxi.isi.models.dto;

import lombok.Data;

/**
 * <p>
 * HR 干预记录数据传输对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class HrInterventionDTO {

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
     * 修改后内容
     */
    private String afterContent;

    /**
     * 备注/原因
     */
    private String remark;
}
