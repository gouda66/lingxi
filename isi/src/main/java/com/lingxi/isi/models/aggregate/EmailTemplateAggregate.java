package com.lingxi.isi.models.aggregate;

import com.lingxi.isi.models.entity.EmailTemplate;
import com.lingxi.isi.models.entity.EmailRecord;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 邮件模板聚合根 - 以邮件模板为核心的业务聚合
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class EmailTemplateAggregate {
    
    /**
     * 邮件模板基本信息
     */
    private EmailTemplate template;
    
    /**
     * 使用该模板发送的邮件记录列表
     */
    private List<EmailRecord> emailRecords;
}
