package com.mak.aiagent.config;

import com.mak.aiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {
    @Bean
    ChatClient chatClient(ChatClient.Builder builder){
        String dir=System.getProperty("user.dir") + "/chat-memory";
        ChatMemory  chatMemory = new InMemoryChatMemory();
        return builder
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new MyLoggerAdvisor()
                )
                .defaultSystem("You are a friendly chat bot that answers question in the voice of a {voice}")
        .build();
    }

}
