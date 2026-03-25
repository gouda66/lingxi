package com.lingxi.isi.models.dto;

import lombok.Data;

/**
 * <p>
 * 简历技能数据传输对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class ResumeSkillDTO {

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
     * 熟练度：1-入门 2-熟练 3-精通 4-专家
     */
    private Integer proficiency;

    /**
     * 分类
     */
    private String category;
}
