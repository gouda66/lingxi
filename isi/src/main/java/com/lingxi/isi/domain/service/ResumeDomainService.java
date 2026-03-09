package com.lingxi.isi.domain.service;

import com.lingxi.isi.domain.model.entity.Resume;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 简历领域服务
 * 处理简历相关的核心业务逻辑
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeDomainService {

    /**
     * 验证简历是否可以被解析
     */
    public boolean canParse(Resume resume) {
        if (resume == null) {
            return false;
        }
        
        // 只有待处理的简历才能解析
        if (!resume.getStatus().equals(0)) {
            log.warn("简历状态不是待处理，无法解析：status={}", resume.getStatus());
            return false;
        }
        
        // 文件类型必须是 PDF/DOC/DOCX
        String fileType = resume.getFileType();
        if (fileType == null || fileType.isEmpty()) {
            return false;
        }
        
        return fileType.equals(".PDF") || 
               fileType.equals(".DOC") || 
               fileType.equals(".DOCX");
    }

    /**
     * 验证简历是否完整
     */
    public boolean isComplete(Resume resume) {
        if (resume == null) {
            return false;
        }
        
        // 必填字段检查
        if (resume.getTitle() == null || resume.getTitle().trim().isEmpty()) {
            return false;
        }
        
        if (resume.getFilePath() == null || resume.getFilePath().trim().isEmpty()) {
            return false;
        }
        
        return true;
    }

    /**
     * 计算简历匹配度得分
     */
    public Double calculateMatchScore(Resume resume, String jobRequirements) {
        if (resume == null || jobRequirements == null) {
            return 0.0;
        }
        
        double score = 0.0;
        
        // 技能匹配（权重 40%）
        if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
            String[] skills = resume.getSkills().split(",");
            long matchCount = java.util.Arrays.stream(skills)
                .map(String::trim)
                .filter(skill -> jobRequirements.toLowerCase().contains(skill.toLowerCase()))
                .count();
            
            score += (matchCount * 1.0 / skills.length) * 40;
        }
        
        // 学历匹配（权重 30%）
        if (resume.getEducation() != null) {
            String education = resume.getEducation().toLowerCase();
            if (education.contains("博士")) {
                score += 30;
            } else if (education.contains("硕士")) {
                score += 25;
            } else if (education.contains("本科")) {
                score += 20;
            } else if (education.contains("专科")) {
                score += 15;
            }
        }
        
        // 工作年限匹配（权重 30%）
        if (resume.getExperienceYears() != null) {
            int years = resume.getExperienceYears();
            if (years >= 5) {
                score += 30;
            } else if (years >= 3) {
                score += 25;
            } else if (years >= 1) {
                score += 20;
            } else {
                score += 10;
            }
        }
        
        return Math.min(100.0, score);
    }

    /**
     * 更新简历状态
     */
    public void updateStatus(Resume resume, Integer status) {
        if (resume == null) {
            throw new IllegalArgumentException("简历不能为空");
        }
        
        resume.setStatus(status);
        
        // 如果状态变为面试中，记录时间
        if (status.equals(3)) {
            log.info("简历进入面试状态：userId={}, resumeId={}", 
                    resume.getUserId(), resume.getId());
        }
    }
}
