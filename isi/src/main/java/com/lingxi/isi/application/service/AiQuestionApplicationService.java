package com.lingxi.isi.application.service;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.lingxi.isi.common.exception.CustomException;
import com.lingxi.isi.domain.model.entity.AiQuestion;
import com.lingxi.isi.domain.repository.AiQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiQuestionApplicationService {

    private final AiQuestionRepository aiQuestionRepository;

    @Autowired
    private SnowflakeGenerator snowflakeGenerator;

    /**
     * 为简历生成 AI 面试题（调用 AI 接口）
     */
    @Transactional
    public List<AiQuestion> generateQuestionsForResume(Long resumeId, String resumeContent) {
        // TODO: 调用 AI 接口根据简历内容生成面试问题
        // 这里先创建示例问题
        
        AiQuestion q1 = createSampleQuestion(resumeId, "TECHNICAL", "Java");
        AiQuestion q2 = createSampleQuestion(resumeId, "BEHAVIORAL", "沟通能力");
        AiQuestion q3 = createSampleQuestion(resumeId, "TECHNICAL", "系统设计");
        
        return List.of(q1, q2, q3);
    }

    /**
     * HR 手动添加问题
     */
    @Transactional
    public AiQuestion addManualQuestion(Long resumeId, String content, String questionType, 
                                       String category, Integer difficulty) {
        AiQuestion question = new AiQuestion();
        question.setId(snowflakeGenerator.next());
        question.setResumeId(resumeId);
        question.setContent(content);
        question.setQuestionType(questionType);
        question.setCategory(category);
        question.setDifficulty(difficulty != null ? difficulty : 2);
        question.setAiGenerated(false);
        question.setStatus(1);

        return aiQuestionRepository.save(question);
    }

    /**
     * 更新问题
     */
    @Transactional
    public AiQuestion updateQuestion(Long id, String content, Integer difficulty, Integer status) {
        AiQuestion question = aiQuestionRepository.findById(id)
                .orElseThrow(() -> new CustomException("问题不存在"));

        if (content != null) {
            question.setContent(content);
        }
        if (difficulty != null) {
            question.setDifficulty(difficulty);
        }
        if (status != null) {
            question.setStatus(status);
        }
        question.setUpdateTime(LocalDateTime.now());

        return aiQuestionRepository.save(question);
    }

    /**
     * 删除问题
     */
    @Transactional
    public void deleteQuestion(Long id) {
        if (!aiQuestionRepository.findById(id).isPresent()) {
            throw new CustomException("问题不存在");
        }
        aiQuestionRepository.deleteById(id);
    }

    /**
     * 获取简历的所有问题
     */
    public List<AiQuestion> getQuestionsByResumeId(Long resumeId) {
        return aiQuestionRepository.findByResumeId(resumeId);
    }

    /**
     * 按类型获取问题
     */
    public List<AiQuestion> getQuestionsByType(Long resumeId, String questionType) {
        return aiQuestionRepository.findByResumeIdAndQuestionType(resumeId, questionType);
    }

    /**
     * 创建示例问题
     */
    private AiQuestion createSampleQuestion(Long resumeId, String type, String category) {
        AiQuestion question = new AiQuestion();
        question.setId(snowflakeGenerator.next());
        question.setResumeId(resumeId);
        question.setQuestionType(type);
        question.setCategory(category);
        question.setContent("请介绍一下你在项目中遇到的最大技术挑战是什么？你是如何解决的？");
        question.setDifficulty(2);
        question.setReferenceAnswer("候选人应描述具体的技术问题，展示分析问题和解决问题的能力");
        question.setEvaluationCriteria("1. 问题的复杂性 2. 解决思路的清晰度 3. 最终效果");
        question.setWeight(1.0);
        question.setAiGenerated(true);
        question.setStatus(1);
        
        return aiQuestionRepository.save(question);
    }
}
