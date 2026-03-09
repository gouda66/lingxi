package com.lingxi.isi.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 面试问题记录实体（面试过程中实际提问的问题）
 */
@Data
@Entity
@Table(name = "isi_interview_question_record")
@Comment("面试问题记录表")
public class InterviewQuestionRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("主键 ID")
    private Long id;

    @Column(name = "room_id", nullable = false)
    @Comment("面试间 ID")
    private Long roomId;

    @Column(name = "question_id")
    @Comment("AI 问题 ID（如果是 AI 题库中的问题）")
    private Long questionId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @Comment("问题内容")
    private String content;

    @Column(name = "question_type", nullable = false, length = 32)
    @Comment("问题类型")
    private String questionType;

    @Column(name = "asked_by", nullable = false, length = 32)
    @Comment("提问者 AI/HR/SYSTEM")
    private String askedBy = "AI";

    @Column(name = "user_answer", columnDefinition = "TEXT")
    @Comment("用户回答")
    private String userAnswer;

    @Column(name = "answer_duration")
    @Comment("回答时长（秒）")
    private Integer answerDuration;

    @Column(name = "ai_score")
    @Comment("AI 评分（百分制）")
    private Double aiScore;

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    @Comment("AI 评价反馈")
    private String aiFeedback;

    @Column(name = "hr_manual_score")
    @Comment("HR 手动评分（可选）")
    private Double hrManualScore;

    @Column(name = "hr_notes", columnDefinition = "TEXT")
    @Comment("HR 备注")
    private String hrNotes;

    @Column(name = "status", nullable = false)
    @Comment("状态 0-待回答 1-已回答 2-已跳过")
    private Integer status = 0;

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
     * 提交回答
     */
    public void submitAnswer(String answer, Integer duration) {
        this.userAnswer = answer;
        this.answerDuration = duration;
        this.status = 1;
    }

    /**
     * 判断是否已回答
     */
    public boolean isAnswered() {
        return this.status != null && this.status == 1;
    }
}
