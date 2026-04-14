package com.lingxi.seo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lingxi.seo.common.result.R;
import com.lingxi.seo.models.entity.NginxRewriteRule;

import java.util.Map;

public interface INginxRewriteRuleService extends IService<NginxRewriteRule> {
    
    R listPage(Map<String, Object> params);
    
    R getDetail(Long id);
    
    R addRule(Map<String, Object> data);
    
    R updateRule(Map<String, Object> data);
    
    R deleteRule(String ids);
    
    /**
     * 同步配置到 Nginx 服务器
     */
    R syncToNginx();
}
