package com.lingxi.isi.models.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 简历项目经验视图对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class ResumeProjectVO {

    /**
     * 项目 ID
     */
    private Long id;

    /**
     * 简历 ID
     */
    private Long resumeId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 担任角色
     */
    private String role;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 个人职责
     */
    private String responsibilities;

    /**
     * 使用技术
     */
    private String technologies;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
