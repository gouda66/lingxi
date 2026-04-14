package com.lingxi.scsagent.service;

import reactor.core.publisher.Flux;

public interface IChatService {
    Flux<String> chat(String message);
}
