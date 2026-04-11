package com.lingxi.isi.controller;

import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.entity.InterviewQuestion;
import com.lingxi.isi.service.IInterviewQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/interview/question")
public class InterviewQuestionController {

    private final IInterviewQuestionService questionService;

    public InterviewQuestionController(IInterviewQuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * 获取会话的问题列表
     */
    @GetMapping("/list")
    public R<List<InterviewQuestion>> listQuestions(@RequestParam Long sessionId) {
        return questionService.listQuestions(sessionId);
    }

    /**
     * 更新题目
     */
    @PutMapping("/update")
    public R<Void> updateQuestion(@RequestBody InterviewQuestion question) {
        return questionService.updateQuestion(question);
    }
}
