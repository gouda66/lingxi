package com.lingxi.isi.infrastructure.filter;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.lingxi.isi.common.exception.CustomException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class LoginCheckFilter implements Filter {

    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();
        
        log.debug("收到请求：{}, Method: {}", uri, httpRequest.getMethod());
        log.debug("Authorization Header: {}", httpRequest.getHeader("Authorization"));
        
        // 放行登录、注册、登出、验证码等接口
        if (uri.contains("/login") || uri.contains("/register") || 
            uri.contains("/logout") || uri.contains("/captchaImage")) {
            chain.doFilter(request, response);
            return;
        }
        
        // 拦截 favicon.ico 请求，直接返回空响应
        if (uri.contains("/favicon.ico")) {
            response.setContentType("image/x-icon");
            ((jakarta.servlet.http.HttpServletResponse) response).setStatus(200);
            return;
        }
        
        // 从 Authorization header 中获取 token (格式：Bearer xxx)
        String authorization = httpRequest.getHeader("Authorization");
        String token = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
            log.debug("从 Authorization 中提取 token: {}", token);
        } else {
            log.warn("未找到有效的 Authorization header: {}", authorization);
        }
        
        Long userId = null;
        if (token != null) {
            try {
                JWT jwt = JWTUtil.parseToken(token);
                Object userIdObj = jwt.getPayload().getClaim("userId");
                
                if (userIdObj != null) {
                    userId = Long.parseLong(userIdObj.toString());
                    log.debug("JWT 解析成功，userId: {}", userId);
                    USER_ID_HOLDER.set(userId);
                } else {
                    log.warn("Token 中未找到 userId 字段");
                }
            } catch (Exception e) {
                log.error("Token 解析失败，token: {}, 错误：{}", token, e.getMessage(), e);
            }
        }

        if (USER_ID_HOLDER.get() == null) {
            log.error("用户未登录，请求 URI: {}", uri);
            throw new CustomException("未登录或 Token 无效");
        }

        try {
            chain.doFilter(request, response);
        } finally {
            USER_ID_HOLDER.remove();
        }
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
}
