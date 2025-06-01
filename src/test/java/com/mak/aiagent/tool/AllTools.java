package com.mak.aiagent.tool;

import com.mak.aiagent.advisor.MyLoggerAdvisor;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AllTools {
    @Autowired
    private ToolCallback[] allTools;

    @Autowired
    private ChatClient chatClient;

    @Test
    public void testAllTools() {
        // Create an instance of AllTools
        ChatResponse response = chatClient
                .prompt()
                .user("你好")
                .advisors(spec -> spec.param("chat_memory_conversation_id", 1))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        System.out.println("content" );
    }
}
