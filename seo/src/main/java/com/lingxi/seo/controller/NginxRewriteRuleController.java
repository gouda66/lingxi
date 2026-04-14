package com.lingxi.seo.controller;

import com.lingxi.seo.common.result.R;
import com.lingxi.seo.service.INginxRewriteRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/seo/nginx/rewrite")
public class NginxRewriteRuleController {
    
    private final INginxRewriteRuleService nginxRewriteRuleService;
    
    public NginxRewriteRuleController(INginxRewriteRuleService nginxRewriteRuleService) {
        this.nginxRewriteRuleService = nginxRewriteRuleService;
    }
    
    /**
     * 分页查询重写规则列表
     */
    @GetMapping("/list")
    public R listPage(@RequestParam Map<String, Object> params) {
        return nginxRewriteRuleService.listPage(params);
    }
    
    /**
     * 获取重写规则详情
     */
    @GetMapping("/{id}")
    public R getDetail(@PathVariable Long id) {
        return nginxRewriteRuleService.getDetail(id);
    }
    
    /**
     * 添加重写规则
     */
    @PostMapping
    public R addRule(@RequestBody Map<String, Object> data) {
        return nginxRewriteRuleService.addRule(data);
    }
    
    /**
     * 更新重写规则
     */
    @PutMapping
    public R updateRule(@RequestBody Map<String, Object> data) {
        return nginxRewriteRuleService.updateRule(data);
    }
    
    /**
     * 删除重写规则
     */
    @DeleteMapping("/{ids}")
    public R deleteRule(@PathVariable String ids) {
        return nginxRewriteRuleService.deleteRule(ids);
    }
    
    /**
     * 同步配置到 Nginx 服务器
     */
    @PostMapping("/sync")
    public R syncToNginx() {
        return nginxRewriteRuleService.syncToNginx();
    }
}
