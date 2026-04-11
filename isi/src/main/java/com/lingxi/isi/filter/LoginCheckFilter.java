package com.lingxi.isi.filter;

import cn.hutool.jwt.JWTUtil;
import com.alibaba.fastjson2.JSON;
import com.lingxi.isi.common.context.BaseContext;
import com.lingxi.isi.common.result.R;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * 登录检查过滤器
 * 用于验证用户是否已登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
@Configuration
public class LoginCheckFilter implements Filter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 白名单路径（不需要登录验证）
     */
    private static final Set<String> WHITE_LIST = Set.of(
            "/login",
            "/logout",
            "/register",
            "/captchaImage",
            "/ws/**"
    );


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        
        var request = (HttpServletRequest) servletRequest;
        var response = (HttpServletResponse) servletResponse;
        
        var requestURI = request.getRequestURI();
        log.info("拦截请求：{}", requestURI);

        // 白名单路径直接放行
        if (isWhiteList(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 从 Authorization header 获取 token
        var authorization = request.getHeader("Authorization");
        
        // 检查 token 是否存在且格式正确
        if (authorization != null && !authorization.trim().isEmpty()) {
            try {
                // 移除可能的 "Bearer " 前缀
                var token = authorization.startsWith("Bearer ")
                    ? authorization.substring(7) 
                    : authorization;
                
                var jwt = JWTUtil.parseToken(token);
                var userId = jwt.getPayloads().get("userId");
                if (userId != null) {
                    try {
                        BaseContext.setCurrentId(Long.valueOf(userId.toString()));
                        filterChain.doFilter(request, response);
                    } finally {
                        BaseContext.remove();
                    }
                    return;
                }
            } catch (Exception e) {
                log.error("Token 解析失败：{}", e.getMessage());
            }
        }

        // 未登录
        log.warn("用户未登录：{}", requestURI);
        writeUnauthorizedResponse(response);
    }

    /**
     * 检查请求路径是否在白名单中
     */
    private boolean isWhiteList(String requestURI) {
        return WHITE_LIST.stream()
                .anyMatch(pattern -> PATH_MATCHER.match(pattern, requestURI));
    }

    /**
     * 写入未授权响应
     */
    private void writeUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        var result = R.error("用户未登录");
        var jsonResult = JSON.toJSONString(result);
        
        response.getWriter().write(jsonResult);
    }
}
