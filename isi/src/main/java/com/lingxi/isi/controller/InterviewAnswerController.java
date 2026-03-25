package com.lingxi.isi.controller;

import com.lingxi.isi.service.IInterviewAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interview/answer")
public class InterviewAnswerController {

    private final IInterviewAnswerService answerService;

    public InterviewAnswerController(IInterviewAnswerService answerService) {
        this.answerService = answerService;
    }
}
