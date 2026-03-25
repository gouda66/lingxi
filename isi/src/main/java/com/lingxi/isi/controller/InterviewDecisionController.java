package com.lingxi.isi.controller;

import com.lingxi.isi.service.IInterviewDecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interview/decision")
public class InterviewDecisionController {

    private final IInterviewDecisionService decisionService;

    public InterviewDecisionController(IInterviewDecisionService decisionService) {
        this.decisionService = decisionService;
    }
}
