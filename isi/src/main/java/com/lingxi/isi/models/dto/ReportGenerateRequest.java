package com.lingxi.isi.models.dto;

import lombok.Data;

/**
 * 报告生成请求 DTO
 */
@Data
public class ReportGenerateRequest {
    private Long sessionId;
    private String scoreJson;  // 评分 JSON
    private String qaHistory;  // 问答历史
}
