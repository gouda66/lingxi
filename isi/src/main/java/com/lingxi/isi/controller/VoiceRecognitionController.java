package com.lingxi.isi.controller;

import com.lingxi.isi.common.result.R;
import com.lingxi.isi.service.IVoiceRecognitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 语音识别控制器
 * 提供实时流式语音识别功能
 */
@Slf4j
@RestController
@RequestMapping("/interview/voice")
@CrossOrigin(origins = "*")
public class VoiceRecognitionController {

    private final IVoiceRecognitionService voiceRecognitionService;

    public VoiceRecognitionController(IVoiceRecognitionService voiceRecognitionService) {
        this.voiceRecognitionService = voiceRecognitionService;
    }

    /**
     * 启动实时语音识别会话
     *
     * @param sessionId 会话ID（由客户端生成）
     * @return 会话启动结果
     */
    @PostMapping("/session/start")
    public R<Map<String, Object>> startSession(@RequestParam("sessionId") String sessionId) {
        return voiceRecognitionService.startSession(sessionId);
    }

    /**
     * 发送音频帧到识别会话
     *
     * @param sessionId 会话ID
     * @param audioFrame 音频帧数据（PCM格式，16kHz, 16bit, 单声道）
     * @return 识别结果
     */
    @PostMapping("/session/send")
    public R<Map<String, Object>> sendAudioFrame(@RequestParam("sessionId") String sessionId,
                                                  @RequestBody byte[] audioFrame) {
        return voiceRecognitionService.sendAudioFrame(sessionId, audioFrame);
    }

    /**
     * 停止语音识别会话并获取最终结果
     *
     * @param sessionId 会话ID
     * @return 完整的识别文本
     */
    @PostMapping("/session/stop")
    public R<Map<String, Object>> stopSession(@RequestParam("sessionId") String sessionId) {
        return voiceRecognitionService.stopSession(sessionId);
    }

    /**
     * 取消语音识别会话
     *
     * @param sessionId 会话ID
     * @return 操作结果
     */
    @DeleteMapping("/session/cancel")
    public R<Map<String, Object>> cancelSession(@RequestParam("sessionId") String sessionId) {
        return voiceRecognitionService.cancelSession(sessionId);
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public R<String> health() {
        return R.success("语音识别服务运行正常");
    }
}
