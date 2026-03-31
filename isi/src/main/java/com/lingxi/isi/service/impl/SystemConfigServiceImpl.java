package com.lingxi.isi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.mapper.SystemConfigMapper;
import com.lingxi.isi.models.entity.SystemConfig;
import com.lingxi.isi.service.ISysUserService;
import com.lingxi.isi.service.ISystemConfigService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements ISystemConfigService {

    @Override
    public R getConfigKey(String configKey) {
        try {
            LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SystemConfig::getConfigKey, configKey);
            queryWrapper.eq(SystemConfig::getEditable, 1);
            SystemConfig config = this.getOne(queryWrapper);
            
            if (config != null) {
                return R.success(config.getConfigValue());
            } else {
                return R.success("");
            }
        } catch (Exception e) {
            log.error("获取配置失败", e);
            return R.error("获取配置失败：" + e.getMessage());
        }
    }

    @Override
    public R listAll() {
        try {
            List<SystemConfig> list = this.baseMapper.selectList(null);
            return R.success(Map.of(
                "rows", list,
                "total", list.size()
            ));
        } catch (Exception e) {
            log.error("查询配置列表失败", e);
            return R.error("查询配置列表失败：" + e.getMessage());
        }
    }

}
