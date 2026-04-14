package com.lingxi.isi.service;

import com.lingxi.isi.common.result.R;
import com.lingxi.isi.service.impl.RealtimeAsrService;

import java.util.Map;

public interface IVoiceRecognitionService {

    /**
     * 启动实时语音识别会话
     */
    R<Map<String, Object>> startSession(String sessionId);

    /**
     * 发送音频帧到识别会话
     */
    R<Map<String, Object>> sendAudioFrame(String sessionId, byte[] audioFrame);

    /**
     * 停止语音识别会话并获取最终结果
     */
    R<Map<String, Object>> stopSession(String sessionId);

    /**
     * 取消语音识别会话
     */
    R<Map<String, Object>> cancelSession(String sessionId);
}
