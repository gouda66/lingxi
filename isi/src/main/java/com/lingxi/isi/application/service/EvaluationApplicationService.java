package com.lingxi.isi.application.service;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.lingxi.isi.common.exception.CustomException;
import com.lingxi.isi.domain.model.entity.InterviewEvaluation;
import com.lingxi.isi.domain.model.entity.InterviewQuestionRecord;
import com.lingxi.isi.domain.repository.InterviewEvaluationRepository;
import com.lingxi.isi.domain.repository.InterviewQuestionRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationApplicationService {

    private final InterviewEvaluationRepository evaluationRepository;
    private final InterviewQuestionRecordRepository questionRecordRepository;

    @Autowired
    private SnowflakeGenerator snowflakeGenerator;

    /**
     * 生成面试评估（包含六边形雷达图数据）
     */
    @Transactional
    public InterviewEvaluation generateEvaluation(Long roomId, Long resumeId, Long userId) {
        // 获取所有面试问题记录
        List<InterviewQuestionRecord> records = questionRecordRepository.findByRoomId(roomId);

        if (records.isEmpty()) {
            throw new CustomException("没有面试记录，无法生成评估");
        }

        // 计算六个维度的得分
        double technicalSkill = calculateTechnicalScore(records);
        double communication = calculateCommunicationScore(records);
        double problemSolving = calculateProblemSolvingScore(records);
        double learningAbility = calculateLearningAbilityScore(records);
        double teamwork = calculateTeamworkScore(records);
        double culturalFit = calculateCulturalFitScore(records);

        // 创建评估对象
        InterviewEvaluation evaluation = new InterviewEvaluation();
        evaluation.setId(snowflakeGenerator.next());
        evaluation.setRoomId(roomId);
        evaluation.setResumeId(resumeId);
        evaluation.setUserId(userId);
        
        // 设置六个维度得分
        evaluation.setTechnicalSkill(technicalSkill);
        evaluation.setCommunication(communication);
        evaluation.setProblemSolving(problemSolving);
        evaluation.setLearningAbility(learningAbility);
        evaluation.setTeamwork(teamwork);
        evaluation.setCulturalFit(culturalFit);
        
        // 计算总分
        evaluation.calculateTotalScore();
        
        // 生成综合评价
        evaluation.setOverallComment(generateOverallComment(evaluation));
        evaluation.setStrengths(generateStrengths(evaluation));
        evaluation.setWeaknesses(generateWeaknesses(evaluation));
        evaluation.setRecommendation(determineRecommendation(evaluation.getTotalScore()));
        
        evaluation.setAiGenerated(true);
        evaluation.setHrReviewed(false);

        return evaluationRepository.save(evaluation);
    }

    /**
     * HR 手动修改评估
     */
    @Transactional
    public InterviewEvaluation updateEvaluation(Long id, Double technicalSkill, 
                                                Double communication, Double problemSolving,
                                                Double learningAbility, Double teamwork, 
                                                Double culturalFit, String hrComments) {
        InterviewEvaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new CustomException("评估不存在"));

        if (technicalSkill != null) {
            evaluation.setTechnicalSkill(technicalSkill);
        }
        if (communication != null) {
            evaluation.setCommunication(communication);
        }
        if (problemSolving != null) {
            evaluation.setProblemSolving(problemSolving);
        }
        if (learningAbility != null) {
            evaluation.setLearningAbility(learningAbility);
        }
        if (teamwork != null) {
            evaluation.setTeamwork(teamwork);
        }
        if (culturalFit != null) {
            evaluation.setCulturalFit(culturalFit);
        }
        
        // 重新计算总分
        evaluation.calculateTotalScore();
        
        if (hrComments != null) {
            evaluation.setHrComments(hrComments);
        }
        evaluation.setHrReviewed(true);
        evaluation.setUpdateTime(LocalDateTime.now());

        return evaluationRepository.save(evaluation);
    }

    /**
     * 获取评估详情
     */
    public InterviewEvaluation getEvaluationByRoomId(Long roomId) {
        return evaluationRepository.findByRoomId(roomId)
                .orElseThrow(() -> new CustomException("未找到评估结果"));
    }

    /**
     * 获取用户的评估历史
     */
    public List<InterviewEvaluation> getUserEvaluations(Long userId) {
        return evaluationRepository.findByUserId(userId);
    }

    // ===== 六个维度评分计算方法 =====

    /**
     * 计算专业技能得分
     */
    private double calculateTechnicalScore(List<InterviewQuestionRecord> records) {
        List<InterviewQuestionRecord> techRecords = records.stream()
                .filter(r -> "TECHNICAL".equals(r.getQuestionType()))
                .toList();
        
        if (techRecords.isEmpty()) return 60.0;
        
        double avgScore = techRecords.stream()
                .mapToInt(r -> r.getAiScore() != null ? r.getAiScore().intValue() : 70)
                .average()
                .orElse(60.0);
        
        return Math.min(100.0, Math.max(0.0, avgScore));
    }

    /**
     * 计算沟通能力得分
     */
    private double calculateCommunicationScore(List<InterviewQuestionRecord> records) {
        // 基于回答时长和答案质量综合评估
        if (records.isEmpty()) return 60.0;
        
        double totalScore = records.stream()
                .mapToDouble(r -> {
                    double score = 70.0;
                    if (r.getAiScore() != null) {
                        score = r.getAiScore();
                    }
                    // 回答时长适中加分（30 秒 -3 分钟）
                    if (r.getAnswerDuration() != null && 
                        r.getAnswerDuration() >= 30 && r.getAnswerDuration() <= 180) {
                        score += 10;
                    }
                    return score;
                })
                .average()
                .orElse(60.0);
        
        return Math.min(100.0, Math.max(0.0, totalScore));
    }

    /**
     * 计算问题解决能力得分
     */
    private double calculateProblemSolvingScore(List<InterviewQuestionRecord> records) {
        List<InterviewQuestionRecord> problemRecords = records.stream()
                .filter(r -> r.getContent().contains("解决") || r.getContent().contains("问题"))
                .toList();
        
        if (problemRecords.isEmpty()) return 65.0;
        
        double avgScore = problemRecords.stream()
                .mapToDouble(r -> r.getAiScore() != null ? r.getAiScore() : 70.0)
                .average()
                .orElse(65.0);
        
        return Math.min(100.0, Math.max(0.0, avgScore));
    }

    /**
     * 计算学习能力得分
     */
    private double calculateLearningAbilityScore(List<InterviewQuestionRecord> records) {
        // 基于对新知识、新技术相关问题的回答
        if (records.isEmpty()) return 65.0;
        
        double baseScore = records.stream()
                .mapToDouble(r -> r.getAiScore() != null ? r.getAiScore() : 70.0)
                .average()
                .orElse(65.0);
        
        // 如果有持续学习相关问题，加权
        long learningCount = records.stream()
                .filter(r -> r.getContent().contains("学习") || r.getContent().contains("新技术"))
                .count();
        
        if (learningCount > 0) {
            baseScore += 5;
        }
        
        return Math.min(100.0, Math.max(0.0, baseScore));
    }

    /**
     * 计算团队协作能力得分
     */
    private double calculateTeamworkScore(List<InterviewQuestionRecord> records) {
        List<InterviewQuestionRecord> teamRecords = records.stream()
                .filter(r -> r.getContent().contains("团队") || r.getContent().contains("合作"))
                .toList();
        
        if (teamRecords.isEmpty()) return 65.0;
        
        double avgScore = teamRecords.stream()
                .mapToDouble(r -> r.getAiScore() != null ? r.getAiScore() : 70.0)
                .average()
                .orElse(65.0);
        
        return Math.min(100.0, Math.max(0.0, avgScore));
    }

    /**
     * 计算文化匹配度得分
     */
    private double calculateCulturalFitScore(List<InterviewQuestionRecord> records) {
        List<InterviewQuestionRecord> cultureRecords = records.stream()
                .filter(r -> "CULTURAL".equals(r.getQuestionType()))
                .toList();
        
        if (cultureRecords.isEmpty()) return 70.0;
        
        double avgScore = cultureRecords.stream()
                .mapToDouble(r -> r.getAiScore() != null ? r.getAiScore() : 75.0)
                .average()
                .orElse(70.0);
        
        return Math.min(100.0, Math.max(0.0, avgScore));
    }

    /**
     * 生成综合评价
     */
    private String generateOverallComment(InterviewEvaluation evaluation) {
        double score = evaluation.getTotalScore();
        if (score >= 90) {
            return "表现卓越，各方面能力都非常出色，强烈建议录用";
        } else if (score >= 80) {
            return "表现优秀，具备良好的专业素养和综合能力，建议录用";
        } else if (score >= 70) {
            return "表现良好，基本符合岗位要求，可以考虑录用";
        } else if (score >= 60) {
            return "表现一般，部分能力有待提升，建议慎重考虑";
        } else {
            return "表现不佳，与岗位要求有一定差距，不建议录用";
        }
    }

    /**
     * 生成优势分析
     */
    private String generateStrengths(InterviewEvaluation evaluation) {
        StringBuilder sb = new StringBuilder();
        
        if (evaluation.getTechnicalSkill() >= 80) {
            sb.append("• 专业技能扎实，技术基础好\n");
        }
        if (evaluation.getCommunication() >= 80) {
            sb.append("• 沟通表达清晰，逻辑性强\n");
        }
        if (evaluation.getProblemSolving() >= 80) {
            sb.append("• 问题分析能力强，善于解决复杂问题\n");
        }
        if (evaluation.getLearningAbility() >= 80) {
            sb.append("• 学习能力强，能快速掌握新知识\n");
        }
        if (evaluation.getTeamwork() >= 80) {
            sb.append("• 团队协作意识好，善于配合他人\n");
        }
        if (evaluation.getCulturalFit() >= 80) {
            sb.append("• 文化匹配度高，价值观契合\n");
        }
        
        return sb.length() > 0 ? sb.toString() : "各方面表现均衡，无明显短板";
    }

    /**
     * 生成劣势分析
     */
    private String generateWeaknesses(InterviewEvaluation evaluation) {
        StringBuilder sb = new StringBuilder();
        
        if (evaluation.getTechnicalSkill() < 70) {
            sb.append("• 专业技能有待提升\n");
        }
        if (evaluation.getCommunication() < 70) {
            sb.append("• 沟通表达能力需要加强\n");
        }
        if (evaluation.getProblemSolving() < 70) {
            sb.append("• 问题分析能力需要提高\n");
        }
        if (evaluation.getLearningAbility() < 70) {
            sb.append("• 学习能力有待提升\n");
        }
        if (evaluation.getTeamwork() < 70) {
            sb.append("• 团队协作能力需要加强\n");
        }
        if (evaluation.getCulturalFit() < 70) {
            sb.append("• 文化匹配度有待考察\n");
        }
        
        return sb.length() > 0 ? sb.toString() : "无明显劣势，整体表现良好";
    }

    /**
     * 给出推荐建议
     */
    private String determineRecommendation(double totalScore) {
        if (totalScore >= 90) {
            return "STRONGLY_RECOMMEND"; // 强烈推荐
        } else if (totalScore >= 80) {
            return "RECOMMEND"; // 推荐
        } else if (totalScore >= 70) {
            return "CONSIDER"; // 待定
        } else {
            return "NOT_RECOMMEND"; // 不推荐
        }
    }
}
