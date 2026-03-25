package com.lingxi.isi.models.aggregate;

import com.lingxi.isi.models.entity.Resume;
import com.lingxi.isi.models.entity.ResumeSkill;
import com.lingxi.isi.models.entity.ResumeProject;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 简历聚合根 - 以简历为核心的业务聚合
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class ResumeAggregate {
    
    /**
     * 简历基本信息
     */
    private Resume resume;
    
    /**
     * 技能列表
     */
    private List<ResumeSkill> skills;
    
    /**
     * 项目经历列表
     */
    private List<ResumeProject> projects;
}
