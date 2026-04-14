package com.lingxi.isi.controller;

import com.lingxi.isi.common.context.BaseContext;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.dto.QuestionGenerateRequest;
import com.lingxi.isi.models.dto.ReportGenerateRequest;
import com.lingxi.isi.models.entity.InterviewQuestion;
import com.lingxi.isi.models.entity.InterviewSession;
import com.lingxi.isi.service.IInterviewAgentService;
import com.lingxi.isi.service.IInterviewSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/interview/session")
@RequiredArgsConstructor
public class InterviewSessionController {

    private final IInterviewSessionService sessionService;
    private final IInterviewAgentService agentService;

    /**
     * 创建面试会话
     */
    @PostMapping("/create")
    public R<Map<String, Object>> createSession(@RequestBody Map<String, Object> params) {
        Long resumeId = Long.valueOf(params.get("resumeId").toString());
        String jobPosition = params.get("jobPosition").toString();
        Long candidateId = BaseContext.getCurrentId();
        return sessionService.createSession(resumeId, jobPosition, candidateId);
    }

    /**
     * 获取会话详情
     */
    @GetMapping("/{id}")
    public R<InterviewSession> getSession(@PathVariable Long id) {
        return sessionService.getSession(id);
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/list")
    public R<List<InterviewSession>> listSessions() {
        return sessionService.listSessions();
    }

    /**
     * 关闭面试间
     */
    @PutMapping("/close/{sessionId}")
    public R<Void> closeSession(@PathVariable Long sessionId) {
        return sessionService.closeSession(sessionId);
    }

    /**
     * 获取在线用户（模拟实现，实际应该从 WebSocket 连接中获取）
     */
    @GetMapping("/online-users/{roomId}")
    public R<List<Map<String, Object>>> getOnlineUsers(@PathVariable String roomId) {
        return sessionService.getOnlineUsers(roomId);
    }

    /**
     * 上传简历并解析
     */
    @PostMapping("/upload-resume")
    public R<Map<String, Object>> uploadResume(
            @RequestPart("file") MultipartFile file,
            @RequestParam String jobPosition,
            @RequestParam Long userId
    ) {
        Map<String, Object> result = agentService.parseAndSaveResume(file, jobPosition, userId);
        return R.success(result);
    }

    /**
     * 为会话生成面试问题
     */
    @PostMapping("/generate-questions")
    public R<List<InterviewQuestion>> generateQuestions(@RequestBody QuestionGenerateRequest request) {
        List<InterviewQuestion> questions = agentService.generateQuestionsForSession(request.getSessionId());
        return R.success(questions);
    }

    /**
     * 生成面试报告
     */
    @PostMapping("/generate-report")
    public R<Map<String, Object>> generateReport(@RequestBody ReportGenerateRequest request) {
        Map<String, Object> result = agentService.generateAndSaveReport(request);
        return R.success(result);
    }
}
