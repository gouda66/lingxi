package com.lingxi.isi.utils;
import com.alibaba.dashscope.audio.asr.recognition.Recognition;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionParam;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionResult;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.utils.Constants;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RealtimeRecognitionUtils implements Runnable {
    @Override
    public void run() {
        // 使用 CountDownLatch 等待连接建立
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicBoolean hasError = new AtomicBoolean(false);

        RecognitionParam param = RecognitionParam.builder()
                .model("fun-asr-realtime")
                // 注意：apiKey 应该直接传字符串，而不是作为环境变量名
                .apiKey("sk-42a2860dab6f4c10b2207b2178f64317") 
                .format("pcm")
                .sampleRate(16000)
                .build();
        
        Recognition recognizer = new Recognition();

        ResultCallback<RecognitionResult> callback = new ResultCallback<RecognitionResult>() {
            @Override
            public void onEvent(RecognitionResult result) {
                // 收到第一个事件时，说明连接已成功建立且开始识别
                if (startLatch.getCount() > 0) {
                    startLatch.countDown();
                }
                if (result.isSentenceEnd()) {
                    System.out.println("Final Result: " + result.getSentence().getText());
                } else {
                    System.out.println("Intermediate Result: " + result.getSentence().getText());
                }
            }

            @Override
            public void onComplete() {
                System.out.println("Recognition complete");
                // 如果是正常完成，也释放锁（防止还没收到结果就 stop 了）
                if (startLatch.getCount() > 0) {
                    startLatch.countDown();
                }
            }

            @Override
            public void onError(Exception e) {
                System.out.println("RecognitionCallback error: " + e.getMessage());
                hasError.set(true);
                if (startLatch.getCount() > 0) {
                    startLatch.countDown();
                }
            }
        };

        try {
            // 1. 发起异步调用
            recognizer.call(param, callback);

            // 3. 连接成功后，才开始录音和发送数据
            AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
            TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            long start = System.currentTimeMillis();
            
            while (System.currentTimeMillis() - start < 50000 && !hasError.get()) {
                int read = targetDataLine.read(buffer.array(), 0, buffer.capacity());
                if (read > 0) {
                    // 包装成正确位置的 ByteBuffer
                    ByteBuffer frame = ByteBuffer.wrap(buffer.array(), 0, read);
                    
                    // 4. 此时状态肯定是 started，安全发送
                    recognizer.sendAudioFrame(frame);
                    
                    buffer = ByteBuffer.allocate(1024);
                    Thread.sleep(20);
                }
            }
            
            // 5. 停止识别（这会阻塞直到 onComplete 或 onError 被调用）
            recognizer.stop();
            
            // 6. 打印指标（2.18.0+ 新特性）
            System.out.println("Request ID: " + recognizer.getLastRequestId());
            System.out.println("First Package Delay: " + recognizer.getFirstPackageDelay() + "ms");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 7. 2.18.4 版本建议显式关闭连接以防泄漏
            try {
                if (recognizer.getDuplexApi() != null) {
                    recognizer.getDuplexApi().close(1000, "bye");
                }
            } catch (Exception e) {
                // 忽略关闭时的异常
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 以下为北京地域url，若使用新加坡地域的模型，需将url替换为：wss://dashscope-intl.aliyuncs.com/api-ws/v1/inference
        Constants.baseWebsocketApiUrl = "wss://dashscope.aliyuncs.com/api-ws/v1/inference";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new RealtimeRecognitionUtils());
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        System.exit(0);
    }
}
