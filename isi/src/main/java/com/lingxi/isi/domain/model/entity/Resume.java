package com.lingxi.isi.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 简历实体
 */
@Data
@Entity
@Table(name = "isi_resume")
@Comment("简历表")
public class Resume implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("主键 ID")
    private Long id;

    @Column(name = "user_id", nullable = false)
    @Comment("用户 ID（求职者）")
    private Long userId;

    @Column(name = "title", nullable = false, length = 128)
    @Comment("简历标题")
    private String title;

    @Column(name = "file_path", nullable = false, length = 256)
    @Comment("简历文件存储路径")
    private String filePath;

    @Column(name = "file_name", nullable = false, length = 128)
    @Comment("原始文件名")
    private String fileName;

    @Column(name = "file_type", length = 32)
    @Comment("文件类型 PDF/DOC/DOCX")
    private String fileType;

    @Column(name = "content_text", columnDefinition = "TEXT")
    @Comment("简历解析后的文本内容")
    private String contentText;

    @Column(name = "structured_data", columnDefinition = "JSON")
    @Comment("结构化数据 JSON（个人信息、教育经历、工作经历等）")
    private String structuredData;

    @Column(name = "skills", length = 512)
    @Comment("技能标签（逗号分隔）")
    private String skills;

    @Column(name = "experience_years")
    @Comment("工作年限")
    private Integer experienceYears;

    @Column(name = "education", length = 32)
    @Comment("最高学历")
    private String education;

    @Column(name = "major", length = 128)
    @Comment("专业")
    private String major;

    @Column(name = "expected_position", length = 128)
    @Comment("期望职位")
    private String expectedPosition;

    @Column(name = "expected_salary", length = 64)
    @Comment("期望薪资")
    private String expectedSalary;

    @Column(name = "status", nullable = false)
    @Comment("状态 0-待处理 1-已解析 2-解析失败 3-面试中 4-已完成")
    private Integer status = 0;

    @Column(name = "ai_score")
    @Comment("AI 综合评分（百分制）")
    private Double aiScore;

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
     * 判断简历是否已解析完成
     */
    public boolean isParsed() {
        return this.status != null && this.status == 1;
    }

    /**
     * 判断是否可以开始面试
     */
    public boolean canInterview() {
        return this.status != null && (this.status == 1 || this.status == 3);
    }
}
