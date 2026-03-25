package com.lingxi.isi.controller;

import com.lingxi.isi.service.IInterviewReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interview/report")
public class InterviewReportController {

    private final IInterviewReportService reportService;

    public InterviewReportController(IInterviewReportService reportService) {
        this.reportService = reportService;
    }
}
