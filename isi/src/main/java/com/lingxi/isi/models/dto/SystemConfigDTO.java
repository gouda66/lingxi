package com.lingxi.isi.models.dto;

import lombok.Data;

/**
 * <p>
 * 系统配置数据传输对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class SystemConfigDTO {

    /**
     * 配置 ID
     */
    private Long id;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 描述
     */
    private String description;

    /**
     * 分类
     */
    private String category;

    /**
     * 是否可编辑
     */
    private Integer editable;
}
