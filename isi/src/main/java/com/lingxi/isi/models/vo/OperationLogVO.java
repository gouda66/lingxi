package com.lingxi.isi.models.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 操作日志视图对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class OperationLogVO {

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
     * HTTP 方法
     */
    private String requestMethod;

    /**
     * 请求 URL
     */
    private String requestUrl;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应数据
     */
    private String responseData;

    /**
     * IP 地址
     */
    private String ipAddress;

    /**
     * User-Agent
     */
    private String userAgent;

    /**
     * 执行时长（ms）
     */
    private Integer executionTime;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
