package com.lingxi.isi.models.entity;
import java.io.Serial;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 邮件模板表 - 管理员配置
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("email_template")
public class EmailTemplate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板编码（唯一）
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 邮件主题（支持变量：{{userName}}）
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
     * MCP 特定配置（如 SendGrid 模板 ID）
     */
    private String mcpConfig;

    /**
     * 是否启用
     */
    private Integer enabled;

    /**
     * 创建人
     */
    private Long createdBy;

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
