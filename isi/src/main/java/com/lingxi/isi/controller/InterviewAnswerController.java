package com.lingxi.isi.controller;


import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.dto.AnswerScoreRequest;
import com.lingxi.isi.service.IInterviewAgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/interview/answer")
public class InterviewAnswerController {

    private final IInterviewAgentService agentService;

    public InterviewAnswerController(IInterviewAgentService agentService) {
        this.agentService = agentService;
    }

    /**
     * 提交回答并实时评分
     */
    @PostMapping("/submit")
    public R<Map<String, Object>> submitAnswer(@RequestBody AnswerScoreRequest request) {
        Map<String, Object> result = agentService.scoreAndSaveAnswer(request);
        return R.success(result);
    }
}
