package com.lingxi.isi.controller;

import com.lingxi.isi.service.IAiInvocationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/ai-log")
public class AiInvocationLogController {

    private final IAiInvocationLogService aiInvocationLogService;

    public AiInvocationLogController(IAiInvocationLogService aiInvocationLogService) {
        this.aiInvocationLogService = aiInvocationLogService;
    }

}
