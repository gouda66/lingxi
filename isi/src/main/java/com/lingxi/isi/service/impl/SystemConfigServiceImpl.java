package com.lingxi.isi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.isi.mapper.SystemConfigMapper;
import com.lingxi.isi.models.entity.SystemConfig;
import com.lingxi.isi.service.ISystemConfigService;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements ISystemConfigService {

}
