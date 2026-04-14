package com.lingxi.isi.models.dto;

import lombok.Data;

import java.util.List;

/**
 * 问题生成请求 DTO
 */
@Data
public class QuestionGenerateRequest {
    private Long sessionId;
    private String jobPosition;
    private List<String> skills;
    private List<String> projects;
    private Integer questionCount;  // 题目数量，默认 10
}
