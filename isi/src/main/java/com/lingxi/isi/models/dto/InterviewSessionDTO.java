package com.lingxi.isi.models.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 面试会话数据传输对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class InterviewSessionDTO {

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
     * 面试类型：1-技术面试 2-综合面试 3-HR 面试
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
}
