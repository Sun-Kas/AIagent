package com.mak.aiagent.tool;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WeatherToolTest {

    @Autowired
    ChatModel chatModel;

    @Test
    void testTool(){
        String content = ChatClient.create(chatModel)
                .prompt("What's the weather in Beijing?")
                .tools(new WeatherTools())
                .call().content();
        System.out.println(content);
    }
}
