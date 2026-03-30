package com.lingxi.seo.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lingxi.seo.common.result.R;
import com.lingxi.seo.mapper.NginxRequestLogMapper;
import com.lingxi.seo.models.entity.NginxRequestLog;
import com.lingxi.seo.service.INginxAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Nginx 数据分析服务实现
 */
@Slf4j
@Service
public class NginxAnalysisServiceImpl implements INginxAnalysisService {

    @Autowired
    private NginxRequestLogMapper requestLogMapper;

    @Override
    public R getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总请求数
        Long totalRequests = requestLogMapper.selectCount(null);
        stats.put("totalRequests", totalRequests);
        
        // 总重写次数
        LambdaQueryWrapper<NginxRequestLog> rewriteWrapper = new LambdaQueryWrapper<>();
        rewriteWrapper.eq(NginxRequestLog::getIsRewritten, 1);
        Long totalRewrites = requestLogMapper.selectCount(rewriteWrapper);
        stats.put("totalRewrites", totalRewrites);
        
        // 重写率
        double rewriteRate = totalRequests > 0 ? (totalRewrites * 100.0 / totalRequests) : 0;
        stats.put("rewriteRate", String.format("%.2f", rewriteRate) + "%");
        
        // 今日请求数
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LambdaQueryWrapper<NginxRequestLog> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.between(NginxRequestLog::getCreatedAt, todayStart, todayEnd);
        Long todayRequests = requestLogMapper.selectCount(todayWrapper);
        stats.put("todayRequests", todayRequests);
        
        // 平均响应时间
        List<NginxRequestLog> allLogs = requestLogMapper.selectList(null);
        double avgResponseTime = allLogs.stream()
                .filter(log -> log.getResponseTime() != null)
                .mapToDouble(log -> log.getResponseTime())
                .average()
                .orElse(0.0);
        stats.put("avgResponseTime", String.format("%.2f", avgResponseTime));
        
        // 200 状态码数量
        LambdaQueryWrapper<NginxRequestLog> successWrapper = new LambdaQueryWrapper<>();
        successWrapper.between(NginxRequestLog::getStatusCode, 200, 299);
        Long successCount = requestLogMapper.selectCount(successWrapper);
        stats.put("successRate", totalRequests > 0 ? String.format("%.2f", (successCount * 100.0 / totalRequests)) + "%" : "0%");
        
        return R.success(stats);
    }

    @Override
    public R getTrendData(Map<String, Object> params) {
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");
        
        // 默认查询最近 7 天
        if (startDate == null || endDate == null) {
            endDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            startDate = LocalDate.now().minusDays(6).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE), LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE), LocalTime.MAX);
        
        LambdaQueryWrapper<NginxRequestLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(NginxRequestLog::getCreatedAt, startDateTime, endDateTime)
                .orderByAsc(NginxRequestLog::getCreatedAt);
        
        List<NginxRequestLog> logs = requestLogMapper.selectList(wrapper);
        
        // 按日期分组统计
        Map<String, List<NginxRequestLog>> groupedByDate = logs.stream()
                .collect(Collectors.groupingBy(log -> 
                    DateUtil.format(log.getCreatedAt(), "yyyy-MM-dd")));
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<NginxRequestLog>> entry : groupedByDate.entrySet()) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", entry.getKey());
            dayData.put("requests", (long) entry.getValue().size());
            
            // 计算平均响应时间
            double avgTime = entry.getValue().stream()
                    .filter(log -> log.getResponseTime() != null)
                    .mapToDouble(log -> log.getResponseTime())
                    .average()
                    .orElse(0.0);
            dayData.put("avgResponseTime", String.format("%.2f", avgTime));
            
            // 重写次数
            long rewriteCount = entry.getValue().stream()
                    .filter(log -> log.getIsRewritten() == 1)
                    .count();
            dayData.put("rewrites", rewriteCount);
            
            result.add(dayData);
        }
        
        // 按日期排序
        result.sort(Comparator.comparing(d -> (String) ((Map) d).get("date")));
        
        return R.success(result);
    }

    @Override
    public R getMethodDistribution() {
        List<NginxRequestLog> allLogs = requestLogMapper.selectList(null);
        
        // 按方法分组统计
        Map<String, Long> methodCount = allLogs.stream()
                .collect(Collectors.groupingBy(
                        NginxRequestLog::getMethod,
                        Collectors.counting()
                ));
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : methodCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            result.add(item);
        }
        
        return R.success(result);
    }

    @Override
    public R getStatusCodeDistribution() {
        List<NginxRequestLog> allLogs = requestLogMapper.selectList(null);
        
        // 按状态码分组统计
        Map<Integer, Long> statusCodeCount = allLogs.stream()
                .filter(log -> log.getStatusCode() != null)
                .collect(Collectors.groupingBy(
                        NginxRequestLog::getStatusCode,
                        Collectors.counting()
                ));
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry : statusCodeCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey().toString());
            item.put("value", entry.getValue());
            result.add(item);
        }
        
        // 按状态码排序
        result.sort(Comparator.comparing(d -> Integer.parseInt((String) ((Map) d).get("name"))));
        
        return R.success(result);
    }

    @Override
    public R getTopPaths() {
        List<NginxRequestLog> allLogs = requestLogMapper.selectList(null);
        
        // 按路径分组统计
        Map<String, Long> pathCount = allLogs.stream()
                .collect(Collectors.groupingBy(
                        NginxRequestLog::getRequestPath,
                        Collectors.counting()
                ));
        
        // 排序并取 TOP10
        List<Map<String, Object>> result = pathCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("path", entry.getKey());
                    item.put("count", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
        
        return R.success(result);
    }

    @Override
    public R getRewriteStatistics() {
        LambdaQueryWrapper<NginxRequestLog> rewriteWrapper = new LambdaQueryWrapper<>();
        rewriteWrapper.eq(NginxRequestLog::getIsRewritten, 1);
        List<NginxRequestLog> rewriteLogs = requestLogMapper.selectList(rewriteWrapper);
        
        // 按规则名称分组统计
        Map<String, Long> ruleCount = rewriteLogs.stream()
                .filter(log -> log.getRuleName() != null)
                .collect(Collectors.groupingBy(
                        NginxRequestLog::getRuleName,
                        Collectors.counting()
                ));
        
        List<Map<String, Object>> result = ruleCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("ruleName", entry.getKey());
                    item.put("count", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
        
        return R.success(result);
    }
}
