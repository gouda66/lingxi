package com.lingxi.isi.models.aggregate;

import com.lingxi.isi.models.entity.SystemConfig;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 系统配置聚合根 - 以系统配置为核心的业务聚合
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class SystemConfigAggregate {
    
    /**
     * 系统配置信息
     */
    private SystemConfig config;
    
    /**
     * 相关的配置列表（如配置分组）
     */
    private List<SystemConfig> relatedConfigs;
}
