package com.lingxi.isi.controller;

import com.lingxi.isi.service.IResumeProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resume/project")
public class ResumeProjectController {

    private final IResumeProjectService resumeProjectService;

    public ResumeProjectController(IResumeProjectService resumeProjectService) {
        this.resumeProjectService = resumeProjectService;
    }
}
