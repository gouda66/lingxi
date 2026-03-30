package com.lingxi.seo.models.dto;

/**
     * 内部类：解析后的日志条目
     */
    public class LogEntry {
        public int lineIndex;
        public String remoteAddr;
        public String method;
        public String requestPath;
        public int statusCode;
        public String referer;
        public String userAgent;
        public long responseTime;
    }
