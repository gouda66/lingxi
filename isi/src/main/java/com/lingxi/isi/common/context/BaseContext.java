package com.lingxi.isi.common.context;

/**
 * 基于 ThreadLocal 的用户上下文
 * 使用 ThreadLocal 存储当前登录用户 ID
 */
public class BaseContext {
    
    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();
    
    /**
     * 设置当前用户 ID
     */
    public static void setCurrentId(Long userId) {
        CURRENT_USER_ID.set(userId);
    }
    
    /**
     * 获取当前用户 ID
     */
    public static Long getCurrentId() {
        return CURRENT_USER_ID.get();
    }
    
    /**
     * 移除当前用户 ID（防止内存泄漏）
     */
    public static void remove() {
        CURRENT_USER_ID.remove();
    }
}
