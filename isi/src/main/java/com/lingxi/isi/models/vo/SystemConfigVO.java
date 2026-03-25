package com.lingxi.isi.models.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 系统配置视图对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class SystemConfigVO {

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

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
