package com.lingxi.isi.models.dto;

import lombok.Data;

/**
 * <p>
 * 邮件模板数据传输对象
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class EmailTemplateDTO {

    /**
     * 模板 ID
     */
    private Long id;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * HTML 模板
     */
    private String contentHtml;

    /**
     * 纯文本模板
     */
    private String contentText;

    /**
     * 可用变量列表
     */
    private String variables;

    /**
     * MCP 特定配置
     */
    private String mcpConfig;

    /**
     * 是否启用
     */
    private Integer enabled;
}
