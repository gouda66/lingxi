package com.lingxi.isi.controller;

import com.lingxi.isi.service.IInterviewQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interview/question")
public class InterviewQuestionController {

    private final IInterviewQuestionService questionService;

    public InterviewQuestionController(IInterviewQuestionService questionService) {
        this.questionService = questionService;
    }
}
