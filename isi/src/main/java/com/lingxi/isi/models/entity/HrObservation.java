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
 * HR 观看记录表 - 支持多个 HR 同时观看
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("hr_observation")
public class HrObservation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;


}
