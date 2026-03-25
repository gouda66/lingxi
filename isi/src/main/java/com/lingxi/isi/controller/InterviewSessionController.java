package com.lingxi.isi.controller;

import com.lingxi.isi.service.IInterviewSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interview/session")
public class InterviewSessionController {

    private final IInterviewSessionService sessionService;

    public InterviewSessionController(IInterviewSessionService sessionService) {
        this.sessionService = sessionService;
    }
}
