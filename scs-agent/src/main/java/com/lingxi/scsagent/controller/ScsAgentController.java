package com.lingxi.scsagent.controller;

import com.lingxi.scsagent.service.IChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/scs")
public class ScsAgentController {

    private final IChatService chatService;

    public ScsAgentController(IChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody ChatRequest request) {
        return chatService.chat(request.message());
    }

    public record ChatRequest(String message) {}
}
