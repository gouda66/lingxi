package com.lingxi.isi.service.impl;

import com.alibaba.dashscope.audio.asr.recognition.Recognition;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionParam;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionResult;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.utils.Constants;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * 实时语音识别服务
 * 基于阿里云 DashScope 实时语音识别 API
 */
@Slf4j
@Service
public class RealtimeAsrService {

    private static final String API_KEY = "sk-42a2860dab6f4c10b2207b2178f64317";
    private static final String MODEL = "fun-asr-realtime";
    private static final int SAMPLE_RATE = 16000;
    private static final String FORMAT = "pcm";
    private static final int TIMEOUT_SECONDS = 60;

    @PostConstruct
    public void init() {
        Constants.baseWebsocketApiUrl = "wss://dashscope.aliyuncs.com/api-ws/v1/inference";
        log.info("RealtimeAsrService 初始化完成");
    }

    /**
     * 启动实时语音识别会话
     * 
     * @param audioFrameConsumer 音频帧消费者，用于外部传入音频数据
     * @param resultCallback 识别结果回调
     * @return RecognitionSession 会话对象
     */
    public RecognitionSession startRecognition(Consumer<byte[]> audioFrameConsumer, 
                                               RecognitionResultHandler resultCallback) throws Exception {
        log.info("启动实时语音识别会话");

        AtomicReference<StringBuilder> fullText = new AtomicReference<>(new StringBuilder());
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean hasError = new AtomicBoolean(false);
        AtomicReference<Recognition> recognizerRef = new AtomicReference<>();

        RecognitionParam param = RecognitionParam.builder()
                .model(MODEL)
                .apiKey(API_KEY)
                .format(FORMAT)
                .sampleRate(SAMPLE_RATE)
                .build();

        Recognition recognizer = new Recognition();
        recognizerRef.set(recognizer);

        ResultCallback<RecognitionResult> callback = new ResultCallback<RecognitionResult>() {
            @Override
            public void onEvent(RecognitionResult result) {
                if (result.getSentence() != null && result.getSentence().getText() != null) {
                    String text = result.getSentence().getText();
                    
                    if (result.isSentenceEnd()) {
                        fullText.get().append(text);
                        log.debug("句子结束，当前累计文本: {}", fullText.toString());
                        if (resultCallback != null) {
                            resultCallback.onFinalResult(text, fullText.get().toString());
                        }
                    } else {
                        log.debug("中间结果: {}", text);
                        if (resultCallback != null) {
                            resultCallback.onIntermediateResult(text);
                        }
                    }
                }
            }

            @Override
            public void onComplete() {
                log.info("识别完成，最终结果: {}", fullText.get().toString());
                if (resultCallback != null) {
                    resultCallback.onComplete(fullText.get().toString());
                }
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                log.error("识别出错: {}", e.getMessage(), e);
                hasError.set(true);
                if (resultCallback != null) {
                    resultCallback.onError(e);
                }
                latch.countDown();
            }
        };

        try {
            recognizer.call(param, callback);

            RecognitionSession session = new RecognitionSession() {
                @Override
                public void sendAudioFrame(byte[] audioData) throws Exception {
                    if (hasError.get()) {
                        throw new RuntimeException("识别会话已出错");
                    }
                    ByteBuffer frame = ByteBuffer.wrap(audioData);
                    recognizer.sendAudioFrame(frame);
                }

                @Override
                public String stop() throws Exception {
                    log.info("停止语音识别");
                    recognizer.stop();
                    
                    boolean completed = latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                    if (!completed) {
                        log.warn("识别超时");
                        throw new RuntimeException("语音识别超时");
                    }

                    if (hasError.get()) {
                        throw new RuntimeException("语音识别过程中发生错误");
                    }

                    return fullText.get().toString();
                }

                @Override
                public void close() {
                    try {
                        if (recognizer.getDuplexApi() != null) {
                            recognizer.getDuplexApi().close(1000, "bye");
                        }
                    } catch (Exception e) {
                        log.warn("关闭识别器时出错", e);
                    }
                }
            };

            log.info("实时语音识别会话已启动");
            return session;

        } catch (Exception e) {
            log.error("启动语音识别失败", e);
            throw e;
        }
    }

    /**
     * 识别结果处理器接口
     */
    public interface RecognitionResultHandler {
        void onIntermediateResult(String text);
        void onFinalResult(String sentenceText, String fullText);
        void onComplete(String fullText);
        void onError(Exception e);
    }

    /**
     * 识别会话接口
     */
    public interface RecognitionSession {
        void sendAudioFrame(byte[] audioData) throws Exception;
        String stop() throws Exception;
        void close();
    }
}
