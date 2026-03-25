package com.lingxi.isi.models.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 邮件模板领域模型 - 充血模型
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("email_template")
public class EmailTemplateDomain implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板编码（唯一标识）
     */
    private String templateCode;

    /**
     * 邮件标题
     */
    private String subject;

    /**
     * 邮件内容（HTML）
     */
    private String contentHtml;

    /**
     * 纯文本内容
     */
    private String contentText;

    /**
     * 模板变量说明：{variables: ["candidateName","sessionCode"]}
     */
    private String variablesJson;

    /**
     * 使用场景：INTERVIEW_INVITATION-面试邀请 RESULT_NOTIFICATION-结果通知等
     */
    private String usageScenario;

    /**
     * 语言：zh_CN-en_US
     */
    private String language;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 是否启用
     */
    private Integer isEnabled;

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

    /**
     * 逻辑删除：0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;

}
