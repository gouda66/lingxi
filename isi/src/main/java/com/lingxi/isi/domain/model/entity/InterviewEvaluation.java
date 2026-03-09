package com.lingxi.isi.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 面试评估结果实体（包含六边形雷达图数据）
 */
@Data
@Entity
@Table(name = "isi_interview_evaluation")
@Comment("面试评估结果表")
public class InterviewEvaluation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("主键 ID")
    private Long id;

    @Column(name = "room_id", nullable = false, unique = true)
    @Comment("面试间 ID")
    private Long roomId;

    @Column(name = "resume_id", nullable = false)
    @Comment("简历 ID")
    private Long resumeId;

    @Column(name = "user_id", nullable = false)
    @Comment("求职者用户 ID")
    private Long userId;

    // ===== 六边形雷达图六个维度评分（0-100 分）=====
    
    @Column(name = "technical_skill", nullable = false)
    @Comment("专业技能得分")
    private Double technicalSkill = 0.0;

    @Column(name = "communication", nullable = false)
    @Comment("沟通能力得分")
    private Double communication = 0.0;

    @Column(name = "problem_solving", nullable = false)
    @Comment("问题解决能力得分")
    private Double problemSolving = 0.0;

    @Column(name = "learning_ability", nullable = false)
    @Comment("学习能力得分")
    private Double learningAbility = 0.0;

    @Column(name = "teamwork", nullable = false)
    @Comment("团队协作能力得分")
    private Double teamwork = 0.0;

    @Column(name = "cultural_fit", nullable = false)
    @Comment("文化匹配度得分")
    private Double culturalFit = 0.0;

    // ===== 综合评估 =====

    @Column(name = "total_score", nullable = false)
    @Comment("综合总分（百分制）")
    private Double totalScore = 0.0;

    @Column(name = "overall_comment", columnDefinition = "TEXT")
    @Comment("综合评价")
    private String overallComment;

    @Column(name = "strengths", columnDefinition = "TEXT")
    @Comment("优势分析")
    private String strengths;

    @Column(name = "weaknesses", columnDefinition = "TEXT")
    @Comment("劣势分析")
    private String weaknesses;

    @Column(name = "recommendation", length = 32)
    @Comment("推荐建议 STRONGLY_RECOMMEND-强烈推荐 RECOMMEND-推荐 CONSIDER-待定 NOT_RECOMMEND-不推荐")
    private String recommendation;

    @Column(name = "ai_generated", nullable = false)
    @Comment("是否 AI 生成")
    private Boolean aiGenerated = true;

    @Column(name = "hr_reviewed")
    @Comment("HR 是否已审核")
    private Boolean hrReviewed = false;

    @Column(name = "hr_comments", columnDefinition = "TEXT")
    @Comment("HR 评语")
    private String hrComments;

    @Column(name = "create_time", nullable = false, updatable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 计算综合总分（六个维度的平均值）
     */
    public void calculateTotalScore() {
        this.totalScore = (this.technicalSkill + this.communication + 
                          this.problemSolving + this.learningAbility + 
                          this.teamwork + this.culturalFit) / 6.0;
    }

    /**
     * 获取六边形雷达图数据数组
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
