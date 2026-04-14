package com.lingxi.seo.models.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Nginx 重写规则配置表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("nginx_rewrite_rule")
public class NginxRewriteRule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 原始 URL 模式
     */
    private String originalPattern;

    /**
     * 替换后的 URL
     */
    private String replacementUrl;

    /**
     * Nginx 标志 (last/break/redirect/permanent)
     */
    private String flag;

    /**
     * 规则描述
     */
    private String description;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 是否启用 (0-禁用 1-启用)
     */
    private Integer isEnabled;

    /**
     * 分类 (GENERAL/SEO/API)
     */
    private String category;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
