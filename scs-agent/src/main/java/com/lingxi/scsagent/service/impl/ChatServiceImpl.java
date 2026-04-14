package com.lingxi.scsagent.service.impl;

import com.lingxi.scsagent.client.ScsClient;
import com.lingxi.scsagent.service.IChatService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatServiceImpl implements IChatService {

    private final ScsClient scsClient;

    public ChatServiceImpl(ScsClient scsClient) {
        this.scsClient = scsClient;
    }

    @Override
    public Flux<String> chat(String message) {
        return scsClient.chat(message);
    }
}
