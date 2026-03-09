package com.lingxi.isi.interfaces.rest;

import com.lingxi.isi.application.service.AiQuestionApplicationService;
import com.lingxi.isi.application.service.EvaluationApplicationService;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.domain.model.entity.AiQuestion;
import com.lingxi.isi.domain.model.entity.InterviewEvaluation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI 面试问题和评估接口
 */
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiInterviewController {

    private final AiQuestionApplicationService aiQuestionApplicationService;
    private final EvaluationApplicationService evaluationApplicationService;

    /**
     * 为简历生成 AI 面试题
     */
    @PostMapping("/question/generate/{resumeId}")
    public R<List<AiQuestion>> generateQuestions(@PathVariable Long resumeId,
                                                  @RequestParam String resumeContent) {
        List<AiQuestion> questions = aiQuestionApplicationService.generateQuestionsForResume(resumeId, resumeContent);
        return R.success(questions);
    }

    /**
     * HR 手动添加问题
     */
    @PostMapping("/question/add")
    public R<AiQuestion> addManualQuestion(@RequestParam Long resumeId,
                                           @RequestParam String content,
                                           @RequestParam(defaultValue = "TECHNICAL") String questionType,
                                           @RequestParam(required = false) String category,
                                           @RequestParam(defaultValue = "2") Integer difficulty) {
        AiQuestion question = aiQuestionApplicationService.addManualQuestion(
                resumeId, content, questionType, category, difficulty);
        return R.success(question);
    }

    /**
     * 更新问题（HR 可以在面试途中修改）
     */
    @PutMapping("/question/{id}")
    public R<AiQuestion> updateQuestion(@PathVariable Long id,
                                        @RequestParam(required = false) String content,
                                        @RequestParam(required = false) Integer difficulty,
                                        @RequestParam(required = false) Integer status) {
        AiQuestion question = aiQuestionApplicationService.updateQuestion(id, content, difficulty, status);
        return R.success(question);
    }

    /**
     * 删除问题
     */
    @DeleteMapping("/question/{id}")
    public R<Void> deleteQuestion(@PathVariable Long id) {
        aiQuestionApplicationService.deleteQuestion(id);
        return R.success();
    }

    /**
     * 获取简历的所有问题（HR 可查看题库）
     */
    @GetMapping("/question/list/{resumeId}")
    public R<List<AiQuestion>> getQuestionsByResume(@PathVariable Long resumeId) {
        List<AiQuestion> questions = aiQuestionApplicationService.getQuestionsByResumeId(resumeId);
        return R.success(questions);
    }

    /**
     * 生成面试评估（六边形雷达图数据）
     */
    @PostMapping("/evaluation/generate")
    public R<InterviewEvaluation> generateEvaluation(@RequestParam Long roomId,
                                                      @RequestParam Long resumeId,
                                                      @RequestParam Long userId) {
        InterviewEvaluation evaluation = evaluationApplicationService.generateEvaluation(roomId, resumeId, userId);
        return R.success(evaluation);
    }

    /**
     * 获取评估结果（包含雷达图数据）
     */
    @GetMapping("/evaluation/{roomId}")
    public R<InterviewEvaluation> getEvaluation(@PathVariable Long roomId) {
        InterviewEvaluation evaluation = evaluationApplicationService.getEvaluationByRoomId(roomId);
        return R.success(evaluation);
    }

    /**
     * HR 手动修改评估分数
     */
    @PutMapping("/evaluation/{id}")
    public R<InterviewEvaluation> updateEvaluation(@PathVariable Long id,
                                                    @RequestParam(required = false) Double technicalSkill,
                                                    @RequestParam(required = false) Double communication,
                                                    @RequestParam(required = false) Double problemSolving,
                                                    @RequestParam(required = false) Double learningAbility,
                                                    @RequestParam(required = false) Double teamwork,
                                                    @RequestParam(required = false) Double culturalFit,
                                                    @RequestParam(required = false) String hrComments) {
        InterviewEvaluation evaluation = evaluationApplicationService.updateEvaluation(
                id, technicalSkill, communication, problemSolving, learningAbility, teamwork, culturalFit, hrComments);
        return R.success(evaluation);
    }
}
