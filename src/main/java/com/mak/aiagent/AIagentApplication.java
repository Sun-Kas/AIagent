package com.mak.aiagent;

import org.springframework.ai.model.ollama.autoconfigure.OllamaChatAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = OllamaChatAutoConfiguration.class)
public class AIagentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AIagentApplication.class, args);
    }

}
