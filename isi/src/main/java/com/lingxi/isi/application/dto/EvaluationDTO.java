package com.lingxi.isi.application.dto;

import lombok.Data;

/**
 * 面试评估 DTO（包含六边形雷达图数据）
 */
@Data
public class EvaluationDTO {

    private Long id;
    private Long roomId;
    private Long resumeId;
    private Long userId;
    
    // 六个维度得分
    private Double technicalSkill;
    private Double communication;
    private Double problemSolving;
    private Double learningAbility;
    private Double teamwork;
    private Double culturalFit;
    
    // 综合评分
    private Double totalScore;
    
    // 综合评价
    private String overallComment;
    private String strengths;
    private String weaknesses;
    private String recommendation;
    
    // HR 审核信息
    private Boolean hrReviewed;
    private String hrComments;
    
    /**
     * 获取雷达图数据数组（方便前端使用）
     */
    public Double[] getRadarData() {
        return new Double[]{
            this.technicalSkill,
            this.communication,
            this.problemSolving,
            this.learningAbility,
            this.teamwork,
            this.culturalFit
        };
    }
}
