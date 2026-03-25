package com.lingxi.isi.models.dto;

import lombok.Data;

/**
 * <p>
 * HR 观察记录数据传输对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class HrObservationDTO {

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
     * IP 地址
     */
    private String ipAddress;
}
