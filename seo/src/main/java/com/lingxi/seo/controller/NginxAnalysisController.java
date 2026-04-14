package com.lingxi.seo.controller;

import com.lingxi.seo.common.result.R;
import com.lingxi.seo.service.INginxAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Nginx 数据分析控制器
 */
@RestController
@RequestMapping("/seo/nginx/analysis")
public class NginxAnalysisController {

    @Autowired
    private INginxAnalysisService analysisService;

    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public R getStatistics() {
        return analysisService.getStatistics();
    }

    /**
     * 获取趋势数据
     */
    @GetMapping("/trend")
    public R getTrendData(@RequestParam Map<String, Object> params) {
        return analysisService.getTrendData(params);
    }

    /**
     * 获取请求方法分布
     */
    @GetMapping("/methodDistribution")
    public R getMethodDistribution() {
        return analysisService.getMethodDistribution();
    }

    /**
     * 获取状态码分布
     */
    @GetMapping("/statusCodeDistribution")
    public R getStatusCodeDistribution() {
        return analysisService.getStatusCodeDistribution();
    }

    /**
     * 获取热门路径 TOP10
     */
    @GetMapping("/topPaths")
    public R getTopPaths() {
        return analysisService.getTopPaths();
    }

    /**
     * 获取重写规则统计
     */
    @GetMapping("/rewriteStatistics")
    public R getRewriteStatistics() {
        return analysisService.getRewriteStatistics();
    }
}
