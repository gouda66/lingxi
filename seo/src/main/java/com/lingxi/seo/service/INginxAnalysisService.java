package com.lingxi.seo.service;

import com.lingxi.seo.common.result.R;

import java.util.Map;

/**
 * Nginx 数据分析服务接口
 */
public interface INginxAnalysisService {
    
    /**
     * 获取统计数据
     */
    R getStatistics();
    
    /**
     * 获取趋势数据
     */
    R getTrendData(Map<String, Object> params);
    
    /**
     * 获取请求方法分布
     */
    R getMethodDistribution();
    
    /**
     * 获取状态码分布
     */
    R getStatusCodeDistribution();
    
    /**
     * 获取热门路径 TOP10
     */
    R getTopPaths();
    
    /**
     * 获取重写规则统计
     */
    R getRewriteStatistics();
}
