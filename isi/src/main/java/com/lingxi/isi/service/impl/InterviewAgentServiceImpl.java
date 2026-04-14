package com.lingxi.isi.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lingxi.isi.client.PythonAiAgentClient;
import com.lingxi.isi.common.context.BaseContext;
import com.lingxi.isi.mapper.*;
import com.lingxi.isi.models.dto.*;
import com.lingxi.isi.models.entity.*;
import com.lingxi.isi.service.IInterviewAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 面试 AI Agent 服务实现
 */
@Service
@RequiredArgsConstructor
public class InterviewAgentServiceImpl implements IInterviewAgentService {
    
    private final PythonAiAgentClient aiAgentClient;
    private final InterviewSessionMapper sessionMapper;
    private final InterviewQuestionMapper questionMapper;
    private final InterviewAnswerMapper answerMapper;
    private final InterviewReportMapper reportMapper;
    private final ResumeMapper resumeMapper;
    private final ResumeSkillMapper skillMapper;
    private final ResumeProjectMapper projectMapper;

    @Override
    @Transactional
    public Map<String, Object> parseAndSaveResume(MultipartFile file, String jobPosition, Long userId) {
        // 1. 调用 Python Agent 解析简历
        Map<String, Object> parsedData = aiAgentClient.parseResume(file);
        
        // 2. 保存到 Resume 表
        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setResumeName(parsedData.getOrDefault("name", "") + "的简历");
        resume.setFileType("pdf");
        resume.setFullName((String) parsedData.get("name"));
        resume.setPhone((String) parsedData.get("phone"));
        resume.setJobTitle(jobPosition);
        resume.setWorkYears((Integer) parsedData.get("workYears"));
        resume.setParseStatus(2); // 解析成功
        // createdAt 由 MyBatis-Plus 自动填充，无需手动设置
        resumeMapper.insert(resume);
        
        // 3. 保存技能
        List<String> skills = (List<String>) parsedData.get("skills");
        if (skills != null) {
            for (String skill : skills) {
                ResumeSkill rs = new ResumeSkill();
                rs.setResumeId(resume.getId());
                rs.setSkillName(skill);
                skillMapper.insert(rs);
            }
        }
        
        // 4. 保存项目经历
        List<String> projects = (List<String>) parsedData.get("projectSummary");
        if (projects != null) {
            for (String proj : projects) {
                ResumeProject rp = new ResumeProject();
                rp.setResumeId(resume.getId());
                rp.setProjectName(proj);
                projectMapper.insert(rp);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("resumeId", resume.getId());
        result.put("parsedData", parsedData);
        return result;
    }

    @Override
    @Transactional
    public List<InterviewQuestion> generateQuestionsForSession(Long sessionId) {
        // 1. 获取会话信息
        InterviewSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }
        
        // 2. 获取简历技能和项目
        Resume resume = resumeMapper.selectById(session.getResumeId());
        List<ResumeSkill> skills = skillMapper.selectList(
            new LambdaQueryWrapper<ResumeSkill>().eq(ResumeSkill::getResumeId, resume.getId())
        );
        List<ResumeProject> projects = projectMapper.selectList(
            new LambdaQueryWrapper<ResumeProject>().eq(ResumeProject::getResumeId, resume.getId())
        );
        
        List<String> skillNames = skills.stream().map(ResumeSkill::getSkillName).collect(Collectors.toList());
        List<String> projectNames = projects.stream().map(ResumeProject::getProjectName).collect(Collectors.toList());
        
        // 3. 调用 Python Agent 生成问题
        List<Map<String, Object>> questions = aiAgentClient.generateQuestions(
            session.getJobPosition(), skillNames, projectNames
        );
        
        // 4. 保存到数据库
        List<InterviewQuestion> savedQuestions = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            Map<String, Object> q = questions.get(i);
            InterviewQuestion question = new InterviewQuestion();
            question.setSessionId(sessionId);
            question.setSequenceNo(i + 1);
            question.setSource("AI");
            question.setQuestionType(1); // 默认技术问题
            question.setContent((String) q.get("title"));
            question.setReferenceAnswer((String) q.get("answer"));
            question.setStatus("PENDING");
            // createdAt 由 MyBatis-Plus 自动填充
            questionMapper.insert(question);
            savedQuestions.add(question);
        }
        
        return savedQuestions;
    }

    @Override
    @Transactional
    public Map<String, Object> scoreAndSaveAnswer(AnswerScoreRequest request) {
        // 1. 获取题目信息
        InterviewQuestion question = questionMapper.selectById(request.getQuestionId());
        
        // 2. 调用 Python Agent 评分
        String scoreLine = aiAgentClient.getScoreLine(
            question.getContent(),
            question.getReferenceAnswer(),
            request.getContentText()
        );
        
        // 3. 解析分数
        String[] scores = scoreLine.split(",");
        Map<String, Integer> dimensions = new LinkedHashMap<>();
        dimensions.put("accuracy", Integer.parseInt(scores[0].trim()));
        dimensions.put("logic", Integer.parseInt(scores[1].trim()));
        dimensions.put("depth", Integer.parseInt(scores[2].trim()));
        dimensions.put("expression", Integer.parseInt(scores[3].trim()));
        dimensions.put("projectCombine", Integer.parseInt(scores[4].trim()));
        dimensions.put("extension", Integer.parseInt(scores[5].trim()));
        
        // 计算总分
        double totalScore = dimensions.values().stream().mapToInt(Integer::intValue).average().orElse(0);
        
        // 4. 保存回答和评分
        InterviewAnswer answer = new InterviewAnswer();
        answer.setStartTime(LocalDateTime.now());
        answer.setCandidateId(BaseContext.getCurrentId());
        answer.setSessionId(request.getSessionId());
        answer.setQuestionId(request.getQuestionId());
        answer.setContentText(request.getContentText());
        answer.setAiScore(BigDecimal.valueOf(totalScore));
        answer.setAiDimensions(JSONUtil.toJsonStr(dimensions));
        answer.setStatus("COMPLETED");
        // createdAt 和 updatedAt 由 MyBatis-Plus 自动填充
        answerMapper.insert(answer);
        
        // 5. 更新题目状态
        question.setStatus("EVALUATED");
        questionMapper.updateById(question);
        
        // 6. 构建雷达图数据
        RadarData radar = new RadarData();
        radar.setValues(new ArrayList<>(dimensions.values()));
        
        Map<String, Object> result = new HashMap<>();
        result.put("answerId", answer.getId());
        result.put("totalScore", totalScore);
        result.put("dimensions", dimensions);
        result.put("radar", radar);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> generateAndSaveReport(ReportGenerateRequest request) {
        // 1. 获取会话信息
        InterviewSession session = sessionMapper.selectById(request.getSessionId());
        Resume resume = resumeMapper.selectById(session.getResumeId());
        
        // 2. 构建问答历史（优先使用前端传来的，如果没有则从数据库构建）
        String qaHistoryText;
        if (request.getQaHistory() != null && !request.getQaHistory().trim().isEmpty()) {
            // 使用前端传来的问答历史（包含所有题目，即使没有回答）
            qaHistoryText = request.getQaHistory();
        } else {
            // 从数据库构建问答历史（只包含有回答的题目）
            List<InterviewAnswer> answers = answerMapper.selectList(
                new LambdaQueryWrapper<InterviewAnswer>()
                    .eq(InterviewAnswer::getSessionId, request.getSessionId())
            );
            
            StringBuilder qaHistory = new StringBuilder();
            for (InterviewAnswer ans : answers) {
                InterviewQuestion q = questionMapper.selectById(ans.getQuestionId());
                qaHistory.append("Q:").append(q.getContent()).append("\n");
                qaHistory.append("A:").append(ans.getContentText()).append("\n\n");
            }
            qaHistoryText = qaHistory.toString();
        }
        
        // 3. 调用 Python Agent 生成报告
        List<ResumeSkill> skills = skillMapper.selectList(
            new LambdaQueryWrapper<ResumeSkill>().eq(ResumeSkill::getResumeId, resume.getId())
        );
        List<String> skillNames = skills.stream().map(ResumeSkill::getSkillName).collect(Collectors.toList());
        
        Map<String, Object> reportData = aiAgentClient.generateReport(
            session.getJobPosition(),
            skillNames,
            request.getScoreJson(),
            qaHistoryText
        );
        
        // 4. 保存报告
        InterviewReport report = new InterviewReport();
        report.setSessionId(request.getSessionId());
        report.setCandidateId(session.getCandidateId());
        report.setTotalScore(new BigDecimal(reportData.getOrDefault("totalScore", "0").toString()));
        report.setStrengths((String) reportData.get("advantage"));
        report.setWeaknesses((String) reportData.get("disadvantage"));
        report.setOverallEvaluation((String) reportData.get("fullReport"));
        report.setGeneratedAt(LocalDateTime.now());
        report.setGeneratedByAi(1);
        // createdAt 由 MyBatis-Plus 自动填充
        reportMapper.insert(report);
        
        // 5. 更新会话状态
        session.setStatus("COMPLETED");
        session.setActualEndTime(LocalDateTime.now());
        sessionMapper.updateById(session);
        
        Map<String, Object> result = new HashMap<>();
        result.put("reportId", report.getId());
        result.put("report", report);
        return result;
    }
}
