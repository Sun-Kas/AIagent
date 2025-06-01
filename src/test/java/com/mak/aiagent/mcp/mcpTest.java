package com.mak.aiagent.mcp;

import com.mak.aiagent.App.ChatApp;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class mcpTest {
    @Autowired
    ChatClient  chatClient;

    @Autowired
    ChatApp chatApp;

    @Test
    public void testMCP(){
        String result = chatApp.doChatWithMCP("请帮我查询一下小明的成绩", "mcp");
    }
}
