package com.lingxi.isi.models.dto;

import lombok.Data;

/**
 * WebSocket 消息 DTO
 */
@Data
public class WebSocketMessage {
    private String roomId;       // 房间 ID (sessionCode)
    private Long sessionId;      // 会话 ID
    private Long questionId;     // 题目 ID
    private String question;     // 问题内容
    private String userAnswer;   // 用户回答
    private String standardAnswer; // 标准答案
    private String type;         // 消息类型：QUESTION/ANSWER/SCORE
}
