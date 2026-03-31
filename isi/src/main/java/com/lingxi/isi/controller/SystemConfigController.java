package com.lingxi.isi.controller;

import com.lingxi.isi.common.result.R;
import com.lingxi.isi.service.ISystemConfigService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/config")
public class SystemConfigController {

    private final ISystemConfigService systemConfigService;

    public SystemConfigController(ISystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }
    
    /**
     * 根据配置键获取配置值
     */
    @GetMapping("/configKey/{configKey}")
    public R getConfigKey(@PathVariable String configKey) {
        return systemConfigService.getConfigKey(configKey);
    }

    /**
     * 查询参数列表
     */
    @GetMapping("/list")
    public R list() {
        return systemConfigService.listAll();
    }

}
