package com.mak.aiagent.tool;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WebSearchTest {
    @Autowired
    ChatClient chatClient;

    @Test
    public void testWebSearch() {
        WebSearchTool webSearchTool = new WebSearchTool();
        String result = webSearchTool.searchWeb("华为");
        System.out.println(result);
    }

    @Test
    public void testWebSearchAI() {
        String result=chatClient.prompt("5060显卡现在多少钱")
                .tools(new WebSearchTool())
                .call()
                .content();
        System.out.println(result);
    }
}
