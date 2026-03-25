package com.lingxi.isi.controller;

import com.lingxi.isi.service.IResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 简历表 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/resume")
public class ResumeController {

    private final IResumeService resumeService;

    public ResumeController(IResumeService resumeService) {
        this.resumeService = resumeService;
    }
}
