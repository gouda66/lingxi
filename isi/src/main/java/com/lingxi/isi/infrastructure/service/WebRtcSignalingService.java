package com.lingxi.isi.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * WebRTC 信令服务
 * 处理面试间音视频通信的信令交换
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebRtcSignalingService {

    /**
     * 创建 Offer（提议）
     * 由发起方（求职者）创建
     */
    public String createOffer(String roomId, Long userId) {
        log.info("创建 Offer: roomId={}, userId={}", roomId, userId);
        
        // 实际项目中这里应该：
        // 1. 生成 SDP Offer
        // 2. 保存到数据库
        // 3. 通过 WebSocket 发送给 HR
        
        return "{\"type\":\"offer\",\"roomId\":\"" + roomId + "\",\"userId\":" + userId + "}";
    }

    /**
     * 创建 Answer（应答）
     * 由接收方（HR）创建
     */
    public String createAnswer(String roomId, Long hrUserId, String offerSdp) {
        log.info("创建 Answer: roomId={}, hrUserId={}", roomId, hrUserId);
        
        // 实际项目中：
        // 1. 解析 Offer SDP
        // 2. 生成 Answer SDP
        // 3. 保存并发送回发起方
        
        return "{\"type\":\"answer\",\"roomId\":\"" + roomId + "\",\"hrUserId\":" + hrUserId + "}";
    }

    /**
     * 交换 ICE Candidate
     */
    public void exchangeIceCandidate(String roomId, Long fromUserId, String candidate) {
        log.info("交换 ICE Candidate: roomId={}, fromUserId={}, candidate={}", 
                roomId, fromUserId, candidate);
        
        // 通过 WebSocket 转发给其他参与者
    }

    /**
     * 加入房间
     */
    public void joinRoom(String roomId, Long userId, String username) {
        log.info("用户加入房间：roomId={}, userId={}, username={}", roomId, userId, username);
        
        // 1. 记录用户在线状态
        // 2. 通知房间内其他用户
        // 3. 开始信令交换流程
    }

    /**
     * 离开房间
     */
    public void leaveRoom(String roomId, Long userId) {
        log.info("用户离开房间：roomId={}, userId={}", roomId, userId);
        
        // 1. 清理用户连接
        // 2. 通知其他用户
        // 3. 如果房间空了，关闭房间
    }

    /**
     * 获取房间信息
     */
    public RoomInfo getRoomInfo(String roomId) {
        RoomInfo info = new RoomInfo();
        info.setRoomId(roomId);
        info.setStatus("ACTIVE");
        info.setParticipantCount(0);
        info.setIceServers(java.util.Arrays.asList(
            "stun:stun.l.google.com:19302"
        ));
        return info;
    }

    /**
     * 房间信息 DTO
     */
    @lombok.Data
    public static class RoomInfo {
        private String roomId;
        private String status;
        private Integer participantCount;
        private java.util.List<String> iceServers;
    }
}
