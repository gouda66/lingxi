package com.lingxi.isi.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * WebSocket 消息处理器
 * 用于面试间实时通信
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InterviewMessageHandler {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 发送面试消息到指定房间
     * 
     * @param roomId 房间 ID
     * @param message 消息内容
     */
    public void sendToRoom(String roomId, Object message) {
        String destination = "/topic/room/" + roomId;
        log.debug("发送消息到房间：{}, 消息：{}", destination, message);
        messagingTemplate.convertAndSend(destination, message);
    }

    /**
     * 发送用户加入面试间的通知
     * 
     * @param roomId 房间 ID
     * @param userId 用户 ID
     * @param username 用户名
     */
    public void sendUserJoinedNotification(Long roomId, Long userId, String username) {
        UserJoinedEvent event = new UserJoinedEvent();
        event.setRoomId(roomId);
        event.setUserId(userId);
        event.setUsername(username);
        event.setType("USER_JOINED");
        
        sendToRoom(roomId.toString(), event);
    }

    /**
     * 发送面试开始通知
     */
    public void sendInterviewStarted(Long roomId, String roomCode) {
        InterviewStatusEvent event = new InterviewStatusEvent();
        event.setRoomId(roomId);
        event.setRoomCode(roomCode);
        event.setStatus("STARTED");
        event.setMessage("面试已开始");
        
        sendToRoom(roomId.toString(), event);
    }

    /**
     * 发送面试结束通知
     */
    public void sendInterviewEnded(Long roomId, String roomCode) {
        InterviewStatusEvent event = new InterviewStatusEvent();
        event.setRoomId(roomId);
        event.setRoomCode(roomCode);
        event.setStatus("ENDED");
        event.setMessage("面试已结束");
        
        sendToRoom(roomId.toString(), event);
    }

    /**
     * 发送新问题的通知
     */
    public void sendNewQuestion(Long roomId, Object question) {
        QuestionEvent event = new QuestionEvent();
        event.setRoomId(roomId);
        event.setQuestion(question);
        event.setType("NEW_QUESTION");
        
        sendToRoom(roomId.toString(), event);
    }

    /**
     * 发送评估完成的通知
     */
    public void sendEvaluationCompleted(Long roomId, Object evaluation) {
        EvaluationEvent event = new EvaluationEvent();
        event.setRoomId(roomId);
        event.setEvaluation(evaluation);
        event.setType("EVALUATION_COMPLETED");
        
        sendToRoom(roomId.toString(), event);
    }

    // ========== 事件 DTO ==========

    @lombok.Data
    public static class UserJoinedEvent {
        private Long roomId;
        private Long userId;
        private String username;
        private String type;
    }

    @lombok.Data
    public static class InterviewStatusEvent {
        private Long roomId;
        private String roomCode;
        private String status;
        private String message;
    }

    @lombok.Data
    public static class QuestionEvent {
        private Long roomId;
        private Object question;
        private String type;
    }

    @lombok.Data
    public static class EvaluationEvent {
        private Long roomId;
        private Object evaluation;
        private String type;
    }
}
