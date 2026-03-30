package com.lingxi.seo.models.dto;

import com.lingxi.seo.models.entity.NginxRewriteRule;

/**
     * 内部类：匹配的规则
     */
public class MatchedRule {
    public NginxRewriteRule rule;
    public String originalUrl;
    public String rewrittenUrl;

    public MatchedRule(NginxRewriteRule rule, String originalUrl, String rewrittenUrl) {
        this.rule = rule;
        this.originalUrl = originalUrl;
        this.rewrittenUrl = rewrittenUrl;
    }
}