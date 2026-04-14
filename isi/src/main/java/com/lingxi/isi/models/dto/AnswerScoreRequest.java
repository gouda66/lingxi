package com.lingxi.isi.models.dto;

import lombok.Data;

/**
 * 答案评分请求 DTO
 */
@Data
public class AnswerScoreRequest {
    private Long sessionId;
    private Long questionId;
    private String contentText;  // 用户回答内容
}
