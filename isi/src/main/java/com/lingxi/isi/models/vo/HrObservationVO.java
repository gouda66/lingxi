package com.lingxi.isi.models.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * HR 观察记录视图对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class HrObservationVO {

    /**
     * 观察 ID
     */
    private Long id;

    /**
     * 面试会话 ID
     */
    private Long sessionId;

    /**
     * HR 用户 ID
     */
    private Long hrId;

    /**
     * 进入时间
     */
    private LocalDateTime joinTime;

    /**
     * 离开时间
     */
    private LocalDateTime leaveTime;

    /**
     * 观看时长（秒）
     */
    private Integer durationSeconds;

    /**
     * 是否在线
     */
    private Integer isActive;

    /**
     * IP 地址
     */
    private String ipAddress;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
