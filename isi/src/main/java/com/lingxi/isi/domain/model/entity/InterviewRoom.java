package com.lingxi.isi.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 面试间实体
 */
@Data
@Entity
@Table(name = "isi_interview_room")
@Comment("面试间表")
public class InterviewRoom implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("主键 ID")
    private Long id;

    @Column(name = "room_id", nullable = false, length = 64, unique = true)
    @Comment("房间号（唯一标识）")
    private String roomId;

    @Column(name = "resume_id", nullable = false)
    @Comment("简历 ID")
    private Long resumeId;

    @Column(name = "user_id", nullable = false)
    @Comment("求职者用户 ID")
    private Long userId;

    @Column(name = "hr_user_id")
    @Comment("HR 用户 ID（可为空，支持多 HR）")
    private Long hrUserId;

    @Column(name = "title", nullable = false, length = 128)
    @Comment("面试主题")
    private String title;

    @Column(name = "status", nullable = false)
    @Comment("状态 0-待开始 1-进行中 2-已结束 3-已取消")
    private Integer status = 0;

    @Column(name = "start_time")
    @Comment("预计开始时间")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    @Comment("预计结束时间")
    private LocalDateTime endTime;

    @Column(name = "actual_start_time")
    @Comment("实际开始时间")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    @Comment("实际结束时间")
    private LocalDateTime actualEndTime;

    @Column(name = "ai_enabled", nullable = false)
    @Comment("是否启用 AI 辅助")
    private Boolean aiEnabled = true;

    @Column(name = "recording_url", length = 256)
    @Comment("面试录像 URL")
    private String recordingUrl;

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
     * 开始面试
     */
    public void start() {
        if (this.status != 0) {
            throw new IllegalStateException("面试无法开始");
        }
        this.status = 1;
        this.actualStartTime = LocalDateTime.now();
    }

    /**
     * 结束面试
     */
    public void end() {
        if (this.status != 1) {
            throw new IllegalStateException("面试无法结束");
        }
        this.status = 2;
        this.actualEndTime = LocalDateTime.now();
    }

    /**
     * 判断面试是否进行中
     */
    public boolean isOngoing() {
        return this.status != null && this.status == 1;
    }

    /**
     * 判断是否可以加入面试
     */
    public boolean canJoin() {
        return this.status != null && (this.status == 0 || this.status == 1);
    }

    /**
     * 取消面试
     */
    public void cancel() {
        if (this.status != 0 && this.status != 1) {
            throw new IllegalStateException("只有待开始或进行中的面试可以取消");
        }
        this.status = 3;
    }
}
