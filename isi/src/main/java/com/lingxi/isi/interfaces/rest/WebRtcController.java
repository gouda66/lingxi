package com.lingxi.isi.interfaces.rest;

import com.lingxi.isi.common.result.R;
import com.lingxi.isi.infrastructure.service.WebRtcSignalingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * WebRTC 信令接口
 * 用于面试间实时音视频通信
 */
@RestController
@RequestMapping("/webrtc")
@RequiredArgsConstructor
public class WebRtcController {

    private final WebRtcSignalingService signalingService;

    /**
     * 创建 Offer（求职者发起视频通话）
     */
    @PostMapping("/offer")
    public R<String> createOffer(
            @RequestParam String roomId,
            @RequestParam Long userId) {
        
        String offer = signalingService.createOffer(roomId, userId);
        return R.success(offer);
    }

    /**
     * 创建 Answer（HR 响应视频通话）
     */
    @PostMapping("/answer")
    public R<String> createAnswer(
            @RequestParam String roomId,
            @RequestParam Long hrUserId,
            @RequestBody String offerSdp) {
        
        String answer = signalingService.createAnswer(roomId, hrUserId, offerSdp);
        return R.success(answer);
    }

    /**
     * 交换 ICE Candidate
     */
    @PostMapping("/ice-candidate")
    public R<Void> exchangeIceCandidate(
            @RequestParam String roomId,
            @RequestParam Long fromUserId,
            @RequestBody String candidate) {
        
        signalingService.exchangeIceCandidate(roomId, fromUserId, candidate);
        return R.success();
    }

    /**
     * 加入房间
     */
    @PostMapping("/room/join")
    public R<Void> joinRoom(
            @RequestParam String roomId,
            @RequestParam Long userId,
            @RequestParam String username) {
        
        signalingService.joinRoom(roomId, userId, username);
        return R.success();
    }

    /**
     * 离开房间
     */
    @PostMapping("/room/leave")
    public R<Void> leaveRoom(
            @RequestParam String roomId,
            @RequestParam Long userId) {
        
        signalingService.leaveRoom(roomId, userId);
        return R.success();
    }

    /**
     * 获取房间信息
     */
    @GetMapping("/room/{roomId}")
    public R<WebRtcSignalingService.RoomInfo> getRoomInfo(@PathVariable String roomId) {
        WebRtcSignalingService.RoomInfo info = signalingService.getRoomInfo(roomId);
        return R.success(info);
    }

    /**
     * 获取 ICE 服务器配置
     */
    @GetMapping("/ice-servers")
    public R<java.util.List<String>> getIceServers() {
        java.util.List<String> servers = java.util.Arrays.asList(
            "stun:stun.l.google.com:19302",
            "stun:stun1.l.google.com:19302"
        );
        return R.success(servers);
    }
}
