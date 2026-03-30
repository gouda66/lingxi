package com.lingxi.seo.controller;

import com.lingxi.seo.common.result.R;
import com.lingxi.seo.service.INginxRequestLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Nginx 请求日志控制器
 */
@RestController
@RequestMapping("/seo/nginx/requestLog")
public class NginxRequestLogController {

    @Autowired
    private INginxRequestLogService requestLogService;

    /**
     * 分页查询请求日志
     */
    @GetMapping("/listPage")
    public R listPage(@RequestParam Map<String, Object> params) {
        return requestLogService.listPage(params);
    }
}
