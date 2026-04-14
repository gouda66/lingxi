package com.lingxi.seo.models.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Nginx 请求日志实体
 */
@Data
@TableName("nginx_request_log")
public class NginxRequestLog {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 请求名称
     */
    private String requestName;

    /**
     * 请求路径
     */
    private String requestPath;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 原始 URL
     */
    private String originalUrl;

    /**
     * 重写后的 URL
     */
    private String rewrittenUrl;

    /**
     * 匹配的规则名称
     */
    private String ruleName;

    /**
     * 规则标志
     */
    private String ruleFlag;

    /**
     * 响应状态码
     */
    private Integer statusCode;

    /**
     * 响应时间 (ms)
     */
    private Long responseTime;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 客户端 IP
     */
    private String remoteAddr;

    /**
     * 来源页面
     */
    private String referer;

    /**
     * 是否被重写
     */
    private Integer isRewritten;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
