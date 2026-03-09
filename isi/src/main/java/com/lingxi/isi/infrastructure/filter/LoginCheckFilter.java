package com.lingxi.isi.infrastructure.filter;

import cn.hutool.core.util.StrUtil;
import com.lingxi.isi.common.exception.CustomException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 登录检查过滤器（简化版，实际项目需要实现完整的 JWT 或 Session 验证）
 */
@Slf4j
@Component
public class LoginCheckFilter implements Filter {

    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> COMPANY_ID_HOLDER = new ThreadLocal<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();
        
        // 放行登录、注册等接口
        if (uri.contains("/login") || uri.contains("/register")) {
            chain.doFilter(request, response);
            return;
        }

        // TODO: 实际项目中应该验证 Token
        // 这里简化处理，从请求头获取用户 ID
        String userIdStr = httpRequest.getHeader("X-User-Id");
        String companyId = httpRequest.getHeader("X-Company-Id");
        
        if (StrUtil.isNotBlank(userIdStr)) {
            try {
                Long userId = Long.parseLong(userIdStr);
                USER_ID_HOLDER.set(userId);
                if (StrUtil.isNotBlank(companyId)) {
                    COMPANY_ID_HOLDER.set(companyId);
                }
            } catch (NumberFormatException e) {
                throw new CustomException("用户 ID 格式错误");
            }
        }
        
        // 如果没有用户 ID，但访问的是需要登录的接口，抛出异常
        if (USER_ID_HOLDER.get() == null && !isPublicUri(uri)) {
            throw new CustomException("未登录");
        }

        try {
            chain.doFilter(request, response);
        } finally {
            // 清理 ThreadLocal
            USER_ID_HOLDER.remove();
            COMPANY_ID_HOLDER.remove();
        }
    }

    /**
     * 判断是否是公开接口（不需要登录）
     */
    private boolean isPublicUri(String uri) {
        return uri.contains("/common/") || 
               uri.contains("/public/") ||
               uri.contains("/actuator/");
    }

    /**
     * 获取当前登录用户 ID
     */
    public static Long getCurrentUserId() {
        Long userId = USER_ID_HOLDER.get();
        if (userId == null) {
            throw new CustomException("未登录");
        }
        return userId;
    }

    /**
     * 获取当前用户所属企业 ID
     */
    public static String getCurrentUserCompanyId() {
        return COMPANY_ID_HOLDER.get();
    }
}
