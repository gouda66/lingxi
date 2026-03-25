package com.lingxi.isi.controller;

import com.lingxi.isi.service.IResumeSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resume/skill")
public class ResumeSkillController {

    private final IResumeSkillService resumeSkillService;

    public ResumeSkillController(IResumeSkillService resumeSkillService) {
        this.resumeSkillService = resumeSkillService;
    }
}
