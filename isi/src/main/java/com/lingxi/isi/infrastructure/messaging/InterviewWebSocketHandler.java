package com.lingxi.isi.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 面试间 WebSocket 处理器
 */
@Slf4j
@Component
public class InterviewWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 存储房间和用户会话的映射：roomId -> (userId -> WebSocketSession)
    private final Map<String, Map<Long, WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    
    // 存储用户会话信息：sessionId -> UserInfo
    private final Map<String, UserInfo> sessionUserInfoMap = new ConcurrentHashMap<>();
    
    // 存储面试间的问题记录：roomId -> List<Question>
    private final Map<String, List<Map<String, Object>>> roomQuestions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket 连接建立：{}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            // 解析消息
            Map<String, Object> msg = objectMapper.readValue(message.getPayload(), Map.class);
            String type = (String) msg.get("type");
            Map<String, Object> data = (Map<String, Object>) msg.get("data");
            
            // 获取房间 ID（从 URL 路径参数）
            String roomId = extractRoomIdFromSession(session);
            
            log.info("收到消息 - 房间：{}, 类型：{}, 数据：{}", roomId, type, data);
            
            // 根据消息类型处理
            switch (type) {
                case "join":
                    handleJoin(session, roomId, data);
                    break;
                case "leave":
                    handleLeave(session, roomId, data);
                    break;
                case "offer":
                case "answer":
                case "ice-candidate":
                    // WebRTC 信令消息，转发给目标用户
                    forwardSignalingMessage(session, roomId, msg);
                    break;
                case "chat-message":
                    // 聊天消息，广播给房间内所有人
                    broadcastToRoom(roomId, msg);
                    break;
                case "new-question":
                    // 新问题，保存到房间并广播
                    handleNewQuestion(roomId, data);
                    break;
                case "answer-question":
                    // 回答问题，更新并广播
                    handleAnswerQuestion(roomId, data);
                    break;
            }
        } catch (Exception e) {
            log.error("处理 WebSocket 消息失败", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket 连接关闭：{}", session.getId());
        removeSession(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket 传输错误：{}", session.getId(), exception);
        removeSession(session);
    }

    /**
     * 处理用户加入
     */
    private void handleJoin(WebSocketSession session, String roomId, Map<String, Object> data) throws IOException {
        Long userId = Long.valueOf(data.get("userId").toString());
        String userName = (String) data.get("userName");
        
        // 保存用户信息
        sessionUserInfoMap.put(session.getId(), new UserInfo(userId, userName, roomId));
        
        // 将用户添加到房间
        roomSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>())
                   .put(userId, session);
        
        log.info("用户加入房间 - userId: {}, userName: {}, roomId: {}", userId, userName, roomId);
        
        // 通知房间内其他用户有新用户加入
        broadcastToRoomExcept(roomId, createMessage("user-joined", data), session);
        
        // 向新用户发送当前房间内的所有用户
        sendExistingUsers(session, roomId);
        
        // 发送问题列表给新用户
        sendQuestionList(session, roomId);
    }

    /**
     * 处理用户离开
     */
    private void handleLeave(WebSocketSession session, String roomId, Map<String, Object> data) throws IOException {
        UserInfo userInfo = sessionUserInfoMap.get(session.getId());
        if (userInfo != null) {
            log.info("用户离开房间 - userId: {}, roomId: {}", userInfo.userId, roomId);
            
            // 通知房间内其他用户
            Map<String, Object> leaveData = Map.of("userId", userInfo.userId);
            broadcastToRoomExcept(roomId, createMessage("user-left", leaveData), session);
        }
        
        removeSession(session);
    }

    /**
     * 转发信令消息（WebRTC 的 offer/answer/ICE）
     */
    private void forwardSignalingMessage(WebSocketSession session, String roomId, 
                                         Map<String, Object> msg) throws IOException {
        Map<String, Object> data = (Map<String, Object>) msg.get("data");
        Long toUserId = Long.valueOf(data.get("to").toString());
        
        WebSocketSession targetSession = getTargetSession(roomId, toUserId);
        if (targetSession != null && targetSession.isOpen()) {
            targetSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
        }
    }

    /**
     * 向房间内所有用户广播消息
     */
    private void broadcastToRoom(String roomId, Map<String, Object> msg) throws IOException {
        Map<Long, WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            String jsonMsg = objectMapper.writeValueAsString(msg);
            for (WebSocketSession session : sessions.values()) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(jsonMsg));
                }
            }
        }
    }

    /**
     * 向房间内除指定会话外的所有用户广播消息
     */
    private void broadcastToRoomExcept(String roomId, Map<String, Object> msg, 
                                        WebSocketSession excludeSession) throws IOException {
        Map<Long, WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            String jsonMsg = objectMapper.writeValueAsString(msg);
            for (WebSocketSession session : sessions.values()) {
                if (session != excludeSession && session.isOpen()) {
                    session.sendMessage(new TextMessage(jsonMsg));
                }
            }
        }
    }

    /**
     * 向新用户发送现有用户列表
     */
    private void sendExistingUsers(WebSocketSession session, String roomId) throws IOException {
        Map<Long, WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            for (Map.Entry<Long, WebSocketSession> entry : sessions.entrySet()) {
                UserInfo userInfo = sessionUserInfoMap.get(entry.getValue().getId());
                if (userInfo != null) {
                    Map<String, Object> userData = Map.of(
                        "userId", userInfo.userId,
                        "userName", userInfo.userName
                    );
                    sendMessage(session, createMessage("user-joined", userData));
                }
            }
        }
    }

    /**
     * 发送问题列表
     */
    private void sendQuestionList(WebSocketSession session, String roomId) throws IOException {
        List<Map<String, Object>> questions = roomQuestions.getOrDefault(roomId, new ArrayList<>());
        sendMessage(session, createMessage("question-list", questions));
    }

    /**
     * 处理新问题
     */
    private void handleNewQuestion(String roomId, Map<String, Object> data) throws IOException {
        List<Map<String, Object>> questions = roomQuestions.computeIfAbsent(roomId, k -> new ArrayList<>());
        
        // 添加时间和 ID
        data.put("id", UUID.randomUUID().toString());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        data.put("time", sdf.format(new Date()));
        data.put("answer", null);
        
        questions.add(data);
        
        // 广播给房间内所有人
        broadcastToRoom(roomId, createMessage("new-question", data));
    }

    /**
     * 处理回答问题
     */
    private void handleAnswerQuestion(String roomId, Map<String, Object> data) throws IOException {
        List<Map<String, Object>> questions = roomQuestions.get(roomId);
        if (questions != null) {
            String questionId = (String) data.get("id");
            for (Map<String, Object> question : questions) {
                if (questionId.equals(question.get("id"))) {
                    question.put("answer", data.get("answer"));
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    question.put("answerTime", sdf.format(new Date()));
                    break;
                }
            }
            
            // 广播更新
            broadcastToRoom(roomId, createMessage("answer-question", data));
        }
    }

    /**
     * 移除会话
     */
    private void removeSession(WebSocketSession session) {
        UserInfo userInfo = sessionUserInfoMap.remove(session.getId());
        if (userInfo != null) {
            Map<Long, WebSocketSession> sessions = roomSessions.get(userInfo.roomId);
            if (sessions != null) {
                sessions.remove(userInfo.userId);
                if (sessions.isEmpty()) {
                    roomSessions.remove(userInfo.roomId);
                }
            }
        }
    }

    /**
     * 获取目标用户的会话
     */
    private WebSocketSession getTargetSession(String roomId, Long userId) {
        Map<Long, WebSocketSession> sessions = roomSessions.get(roomId);
        return sessions != null ? sessions.get(userId) : null;
    }

    /**
     * 从会话中提取房间 ID
     */
    private String extractRoomIdFromSession(WebSocketSession session) {
        String path = session.getUri().getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    /**
     * 创建消息
     */
    private Map<String, Object> createMessage(String type, Object data) {
        return Map.of("type", type, "data", data);
    }

    /**
     * 发送消息
     */
    private void sendMessage(WebSocketSession session, Map<String, Object> msg) throws IOException {
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
        }
    }

    /**
     * 用户信息内部类
     */
    private static class UserInfo {
        Long userId;
        String userName;
        String roomId;

        UserInfo(Long userId, String userName, String roomId) {
            this.userId = userId;
            this.userName = userName;
            this.roomId = roomId;
        }
    }
}
