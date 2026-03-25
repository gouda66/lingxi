package com.lingxi.isi.models.entity;
import java.io.Serial;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 面试会话表 - 核心业务表
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("interview_session")
public class InterviewSession implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 房间号/会话码（如：IV-20240324-XXXX）
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
     * 负责 HR 用户 ID（可选，分配后）
     */
    private Long hrId;

    /**
     * 面试职位
     */
    private String jobPosition;

    /**
     * 状态：CREATED-创建 ONGOING-进行中 PAUSED-暂停 COMPLETED-完成 TERMINATED-终止
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
     * 难度等级：1-简单 2-中等 3-困难
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
     * 直播房间 ID（WebRTC 或三方服务）
     */
    private String liveRoomId;

    /**
     * 直播是否进行中
     */
    private Integer isLiveActive;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除：0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;


}
