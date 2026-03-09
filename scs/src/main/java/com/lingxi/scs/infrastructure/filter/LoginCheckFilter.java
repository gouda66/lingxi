package com.lingxi.scs.infrastructure.filter;

import com.alibaba.fastjson2.JSON;
import com.lingxi.scs.common.context.BaseContext;
import com.lingxi.scs.common.result.R;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * 登录检查过滤器（Java 25 优化版）
 * 使用 Java 25 新特性和 Spring Boot 4.x
 * 
 * @author system
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
@Configuration
public class LoginCheckFilter implements Filter {

    /**
     * 路径匹配器，支持 Ant 风格通配符
     */
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 白名单路径（不需要登录验证）
     * 使用 Java 9+ Set.of() 创建不可变集合
     */
    private static final Set<String> WHITE_LIST = Set.of(
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**",
            "/common/**",
            "/user/sendMsg",
            "/user/login",
            "/category/list",
            "/dish/list",
            "/setmeal/list",
            "/setmeal/dish/**"
    );

    /**
     * Session 属性键常量
     */
    private static final String SESSION_EMPLOYEE = "employee";
    private static final String SESSION_USER = "user";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        
        var request = (HttpServletRequest) servletRequest;
        var response = (HttpServletResponse) servletResponse;
        
        var requestURI = request.getRequestURI();
        log.info("拦截请求：{}", requestURI);

        if (isWhiteList(requestURI)) {
            log.info("请求在白名单中，直接放行：{}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        var session = request.getSession(false);
        if (session == null) {
            log.warn("用户未登录，Session 不存在：{}", requestURI);
            writeUnauthorizedResponse(response);
            return;
        }

        var userId = getUserIdFromSession(session);
        log.info("从 Session 获取 userId: {}, requestURI: {}", userId, requestURI);
        if (userId != null) {
            try {
                BaseContext.setCurrentId(userId);
                log.info("已设置 BaseContext.currentId: {}", userId);
                filterChain.doFilter(request, response);
            } finally {
                BaseContext.remove();
                log.info("已清理 BaseContext.currentId");
            }
            return;
        }

        log.warn("用户未登录，Session 中存在属性但无法获取 userId: {}, requestURI: {}", 
                session.getAttributeNames(), requestURI);
        writeUnauthorizedResponse(response);
    }

    /**
     * 从 Session 中获取用户ID
     * 优先检查员工，其次检查用户
     * 
     * @param session HTTP Session
     * @return 用户ID，如果未登录返回 null
     */
    private Long getUserIdFromSession(HttpSession session) {
        // 检查员工登录
        var employeeId = (Long) session.getAttribute(SESSION_EMPLOYEE);
        if (employeeId != null) {
            log.info("员工已登录，ID: {}", employeeId);
            return employeeId;
        }

        // 检查用户登录
        var userId = (Long) session.getAttribute(SESSION_USER);
        if (userId != null) {
            log.info("用户已登录，ID: {}", userId);
            return userId;
        }

        return null;
    }

    /**
     * 检查请求路径是否在白名单中
     * 使用 Stream API + 方法引用（Java 8+）
     * 
     * @param requestURI 请求URI
     * @return true-在白名单中，false-不在
     */
    private boolean isWhiteList(String requestURI) {
        return WHITE_LIST.stream()
                .anyMatch(pattern -> PATH_MATCHER.match(pattern, requestURI));
    }

    /**
     * 写入未授权响应（Java 25 优化版）
     * 使用 Fastjson2 + 现代化响应处理
     * 
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    private void writeUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        var result = R.error("NOTLOGIN");
        var jsonResult = JSON.toJSONString(result);
        
        response.getWriter().write(jsonResult);
    }
}
