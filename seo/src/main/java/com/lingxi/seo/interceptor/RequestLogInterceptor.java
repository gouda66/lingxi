package com.lingxi.seo.interceptor;

import com.lingxi.seo.models.entity.NginxRequestLog;
import com.lingxi.seo.service.INginxRequestLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * Nginx 请求日志拦截器
 */
@Slf4j
@Component
public class RequestLogInterceptor implements HandlerInterceptor {

    @Autowired
    private INginxRequestLogService requestLogService;

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 记录请求开始时间
        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            // 获取请求信息
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

            // 创建日志对象
            NginxRequestLog logEntry = new NginxRequestLog();
            
            // 基本信息
            logEntry.setRequestPath(wrappedRequest.getRequestURI());
            logEntry.setMethod(wrappedRequest.getMethod());
            logEntry.setOriginalUrl(wrappedRequest.getRequestURL().toString());
            
            // 查询参数
            String queryString = wrappedRequest.getQueryString();
            if (queryString != null) {
                logEntry.setOriginalUrl(logEntry.getOriginalUrl() + "?" + queryString);
            }
            
            // 响应信息
            logEntry.setStatusCode(response.getStatus());
            
            // 计算响应时间
            Long startTime = (Long) request.getAttribute(START_TIME);
            if (startTime != null) {
                logEntry.setResponseTime(System.currentTimeMillis() - startTime);
            }
            
            // 客户端信息
            logEntry.setRemoteAddr(getClientIp(wrappedRequest));
            logEntry.setUserAgent(wrappedRequest.getHeader("User-Agent"));
            logEntry.setReferer(wrappedRequest.getHeader("Referer"));
            
            // 判断是否被重写（根据 URL 变化判断）
            String rewrittenUrl = (String) request.getAttribute("rewrittenUrl");
            if (rewrittenUrl != null && !rewrittenUrl.equals(logEntry.getOriginalUrl())) {
                logEntry.setIsRewritten(1);
                logEntry.setRewrittenUrl(rewrittenUrl);
                
                // 获取规则信息
                String ruleName = (String) request.getAttribute("ruleName");
                String ruleFlag = (String) request.getAttribute("ruleFlag");
                logEntry.setRuleName(ruleName);
                logEntry.setRuleFlag(ruleFlag);
            } else {
                logEntry.setIsRewritten(0);
            }
            
            // 异步保存日志（不阻塞请求）
            requestLogService.logRequest(logEntry);
            
        } catch (Exception e) {
            log.error("记录请求日志失败", e);
        }
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理的情况，取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
