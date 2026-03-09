package com.lingxi.isi.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 面试问题实体
 */
@Data
@Entity
@Table(name = "isi_ai_question")
@Comment("AI 面试问题表")
public class AiQuestion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("主键 ID")
    private Long id;

    @Column(name = "resume_id", nullable = false)
    @Comment("关联简历 ID")
    private Long resumeId;

    @Column(name = "question_type", nullable = false, length = 32)
    @Comment("问题类型 TECHNICAL-技术 BEHAVIORAL-行为 CULTURAL-文化匹配 GENERAL-通用")
    private String questionType;

    @Column(name = "category", length = 64)
    @Comment("问题分类（如 Java、Python、沟通能力等）")
    private String category;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @Comment("问题内容")
    private String content;

    @Column(name = "difficulty", nullable = false)
    @Comment("难度等级 1-简单 2-中等 3-困难")
    private Integer difficulty = 2;

    @Column(name = "reference_answer", columnDefinition = "TEXT")
    @Comment("参考答案要点")
    private String referenceAnswer;

    @Column(name = "evaluation_criteria", columnDefinition = "TEXT")
    @Comment("评分标准")
    private String evaluationCriteria;

    @Column(name = "weight", nullable = false)
    @Comment("权重（用于综合评分）")
    private Double weight = 1.0;

    @Column(name = "ai_generated", nullable = false)
    @Comment("是否 AI 生成")
    private Boolean aiGenerated = true;

    @Column(name = "status", nullable = false)
    @Comment("状态 0-草稿 1-启用 2-禁用")
    private Integer status = 1;

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
     * 判断问题是否可用
     */
    public boolean isAvailable() {
        return this.status != null && this.status == 1;
    }
}
