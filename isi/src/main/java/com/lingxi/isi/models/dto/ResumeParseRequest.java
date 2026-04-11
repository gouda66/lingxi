package com.lingxi.isi.models.dto;

import lombok.Data;

/**
 * 简历解析请求 DTO
 */
@Data
public class ResumeParseRequest {
    private String jobPosition;  // 目标职位
}
