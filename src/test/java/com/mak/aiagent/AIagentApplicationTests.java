package com.mak.aiagent;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
class AIagentApplicationTests {

    @Autowired
    ChatModel chatModel;
    @Test
    void contextLoads() {
        // 高级用法(ChatClient)
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultSystem("你是恋爱顾问")
                .build();
        Flux<String> streamResponse = chatClient.prompt().user("你好").stream().content();
        System.out.println(streamResponse);
    }

}
