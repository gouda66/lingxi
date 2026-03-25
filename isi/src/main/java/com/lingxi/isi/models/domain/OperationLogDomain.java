package com.lingxi.isi.models.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 操作日志领域模型 - 充血模型
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("operation_log")
public class OperationLogDomain implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作用户 ID
     */
    private Long userId;

    /**
     * 操作模块：USER-用户管理 SESSION-面试管理等
     */
    private String module;

    /**
     * 操作类型：CREATE-创建 UPDATE-更新 DELETE-删除 QUERY-查询 OTHER-其他
     */
    private String operationType;

    /**
     * 操作说明
     */
    private String description;

    /**
     * 请求 URL
     */
    private String requestUrl;

    /**
     * 请求方法：GET/POST/PUT/DELETE
     */
    private String method;

    /**
     * 请求参数 JSON
     */
    private String paramsJson;

    /**
     * 响应结果 JSON
     */
    private String responseJson;

    /**
     * IP 地址
     */
    private String ipAddress;

    /**
     * User-Agent
     */
    private String userAgent;

    /**
     * 执行时长（毫秒）
     */
    private Integer executionTimeMs;

    /**
     * 操作状态：SUCCESS-成功 FAIL-失败
     */
    private String status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

}
