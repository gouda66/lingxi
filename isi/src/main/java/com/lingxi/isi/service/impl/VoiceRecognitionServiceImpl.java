package com.lingxi.isi.service.impl;

import com.lingxi.isi.common.result.R;
import com.lingxi.isi.service.IVoiceRecognitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class VoiceRecognitionServiceImpl implements IVoiceRecognitionService {

    @Autowired
    private RealtimeAsrService realtimeAsrService;

    private final Map<String, RealtimeAsrService.RecognitionSession> sessions = new ConcurrentHashMap<>();

    @Override
    public R<Map<String, Object>> startSession(String sessionId) {
        try {
            log.info("启动语音识别会话: {}", sessionId);

            if (sessions.containsKey(sessionId)) {
                return R.error("会话已存在: " + sessionId);
            }

            RealtimeAsrService.RecognitionResultHandler handler = new RealtimeAsrService.RecognitionResultHandler() {
                @Override
                public void onIntermediateResult(String text) {
                    log.debug("会话 {} 中间结果: {}", sessionId, text);
                }

                @Override
                public void onFinalResult(String sentenceText, String fullText) {
                    log.info("会话 {} 句子结束: {}", sessionId, sentenceText);
                }

                @Override
                public void onComplete(String fullText) {
                    log.info("会话 {} 识别完成: {}", sessionId, fullText);
                }

                @Override
                public void onError(Exception e) {
                    log.error("会话 {} 识别错误: {}", sessionId, e.getMessage(), e);
                }
            };

            RealtimeAsrService.RecognitionSession session = realtimeAsrService.startRecognition(null, handler);
            sessions.put(sessionId, session);

            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", sessionId);
            result.put("status", "started");

            return R.success(result);

        } catch (Exception e) {
            log.error("启动语音识别会话失败", e);
            return R.error("启动会话失败: " + e.getMessage());
        }
    }

    @Override
    public R<Map<String, Object>> sendAudioFrame(String sessionId, byte[] audioFrame) {
        try {
            log.debug("接收音频帧，会话: {}, 大小: {} bytes", sessionId, audioFrame.length);

            RealtimeAsrService.RecognitionSession session = sessions.get(sessionId);
            if (session == null) {
                return R.error("会话不存在: " + sessionId);
            }

            session.sendAudioFrame(audioFrame);

            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", sessionId);
            result.put("status", "sent");

            return R.success(result);

        } catch (Exception e) {
            log.error("发送音频帧失败", e);
            return R.error("发送音频帧失败: " + e.getMessage());
        }
    }

    @Override
    public R<Map<String, Object>> stopSession(String sessionId) {
        try {
            log.info("停止语音识别会话: {}", sessionId);

            RealtimeAsrService.RecognitionSession session = sessions.remove(sessionId);
            if (session == null) {
                return R.error("会话不存在: " + sessionId);
            }

            String fullText = session.stop();
            session.close();

            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", sessionId);
            result.put("text", fullText);
            result.put("status", "completed");

            return R.success(result);

        } catch (Exception e) {
            log.error("停止语音识别会话失败", e);
            return R.error("停止会话失败: " + e.getMessage());
        }
    }

    @Override
    public R<Map<String, Object>> cancelSession(String sessionId) {
        try {
            log.info("取消语音识别会话: {}", sessionId);

            RealtimeAsrService.RecognitionSession session = sessions.remove(sessionId);
            if (session == null) {
                return R.error("会话不存在: " + sessionId);
            }

            session.close();

            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", sessionId);
            result.put("status", "cancelled");

            return R.success(result);

        } catch (Exception e) {
            log.error("取消语音识别会话失败", e);
            return R.error("取消会话失败: " + e.getMessage());
        }
    }
}
