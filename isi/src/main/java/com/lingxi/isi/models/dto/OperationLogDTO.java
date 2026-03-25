package com.lingxi.isi.models.dto;

import lombok.Data;

/**
 * <p>
 * 操作日志数据传输对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class OperationLogDTO {

    /**
     * 日志 ID
     */
    private Long id;

    /**
     * 操作用户
     */
    private Long userId;

    /**
     * 用户角色
     */
    private Integer userRole;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作描述
     */
    private String operationDesc;

    /**
     * 请求 URL
     */
    private String requestUrl;

    /**
     * IP 地址
     */
    private String ipAddress;

    /**
     * 执行时长（ms）
     */
    private Integer executionTime;

    /**
     * 状态
     */
    private Integer status;
}
