package com.lingxi.isi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.entity.SystemConfig;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface ISystemConfigService extends IService<SystemConfig> {
    
    R getConfigKey(String configKey);
    
    R listAll();

}
