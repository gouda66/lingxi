package com.lingxi.seo.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.seo.common.result.R;
import com.lingxi.seo.mapper.NginxRequestLogMapper;
import com.lingxi.seo.models.entity.NginxRequestLog;
import com.lingxi.seo.service.INginxRequestLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Nginx 请求日志服务实现
 */
@Slf4j
@Service
public class NginxRequestLogServiceImpl extends ServiceImpl<NginxRequestLogMapper, NginxRequestLog> implements INginxRequestLogService {


    @Override
    public R listPage(Map<String, Object> params) {
        Integer pageNum = Integer.parseInt(params.getOrDefault("pageNum", "1").toString());
        Integer pageSize = Integer.parseInt(params.getOrDefault("pageSize", "10").toString());
        String requestPath = (String) params.get("requestPath");
        String method = (String) params.get("method");
        String isRewritten = (String) params.get("isRewritten");
        
        Page<NginxRequestLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<NginxRequestLog> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(requestPath)) {
            wrapper.like(NginxRequestLog::getRequestPath, requestPath);
        }
        
        if (StrUtil.isNotBlank(method)) {
            wrapper.eq(NginxRequestLog::getMethod, method);
        }
        
        if (StrUtil.isNotBlank(isRewritten)) {
            wrapper.eq(NginxRequestLog::getIsRewritten, Integer.parseInt(isRewritten));
        }
        
        wrapper.orderByDesc(NginxRequestLog::getCreatedAt);
        
        Page<NginxRequestLog> resultPage = this.page(page, wrapper);
        
        return R.success(resultPage);
    }

    @Override
    public void logRequest(NginxRequestLog requestLog) {
        try {
            this.save(requestLog);
        } catch (Exception e) {
            log.error("保存请求日志失败", e);
        }
    }
}
