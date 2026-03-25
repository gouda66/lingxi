package com.lingxi.isi.models.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 简历技能视图对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class ResumeSkillVO {

    /**
     * 技能 ID
     */
    private Long id;

    /**
     * 简历 ID
     */
    private Long resumeId;

    /**
     * 技能名称
     */
    private String skillName;

    /**
     * 熟练度
     */
    private Integer proficiency;

    /**
     * 分类
     */
    private String category;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
