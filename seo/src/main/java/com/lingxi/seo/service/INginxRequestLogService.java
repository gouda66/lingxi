package com.lingxi.seo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lingxi.seo.common.result.R;
import com.lingxi.seo.models.entity.NginxRequestLog;

import java.util.Map;

/**
 * Nginx 请求日志服务接口
 */
public interface INginxRequestLogService extends IService<NginxRequestLog> {
    
    /**
     * 分页查询请求日志
     */
    R listPage(Map<String, Object> params);
    
    /**
     * 记录请求
     */
    void logRequest(NginxRequestLog log);
}
