package com.lingxi.isi.service;

import com.lingxi.isi.models.dto.*;
import com.lingxi.isi.models.entity.InterviewQuestion;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 面试 AI Agent 服务接口
 */
public interface IInterviewAgentService {
    
    /**
     * 解析简历并更新到数据库
     */
    Map<String, Object> parseAndSaveResume(MultipartFile file, String jobPosition, Long userId);
    
    /**
     * 为会话生成面试问题
     */
    List<InterviewQuestion> generateQuestionsForSession(Long sessionId);
    
    /**
     * 实时评分并保存
     */
    Map<String, Object> scoreAndSaveAnswer(AnswerScoreRequest request);
    
    /**
     * 生成面试报告并保存
     */
    Map<String, Object> generateAndSaveReport(ReportGenerateRequest request);
}
