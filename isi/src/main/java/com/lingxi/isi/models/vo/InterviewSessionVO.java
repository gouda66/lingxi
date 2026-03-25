package com.lingxi.isi.models.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 面试会话视图对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class InterviewSessionVO {

    /**
     * 会话 ID
     */
    private Long id;

    /**
     * 房间号/会话码
     */
    private String sessionCode;

    /**
     * 面试者用户 ID
     */
    private Long candidateId;

    /**
     * 使用的简历 ID
     */
    private Long resumeId;

    /**
     * 负责 HR 用户 ID
     */
    private Long hrId;

    /**
     * 面试职位
     */
    private String jobPosition;

    /**
     * 状态
     */
    private String status;

    /**
     * 使用的 AI 模型
     */
    private String aiModel;

    /**
     * 面试类型
     */
    private Integer interviewType;

    /**
     * 难度等级
     */
    private Integer difficultyLevel;

    /**
     * 预计时长（分钟）
     */
    private Integer expectedDuration;

    /**
     * 预约时间
     */
    private LocalDateTime scheduledTime;

    /**
     * 实际开始时间
     */
    private LocalDateTime actualStartTime;

    /**
     * 实际结束时间
     */
    private LocalDateTime actualEndTime;

    /**
     * 实际耗时（秒）
     */
    private Integer durationSeconds;

    /**
     * 直播房间 ID
     */
    private String liveRoomId;

    /**
     * 直播是否进行中
     */
    private Integer isLiveActive;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
