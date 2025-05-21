package com.mak.aiagent.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/health")
@Slf4j
public class HealthController {

    private final ChatClient chatClient;
    HealthController(ChatClient chatClient){
        this.chatClient = chatClient;
    }

    @GetMapping("/ai")
    String completion(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message, String voice) {
        return this.chatClient.prompt()
                        .system(sp -> sp.param("voice", voice))
                        .user(message)
                        .call()
                        .content();
    }
}
