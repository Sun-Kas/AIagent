package com.mak.aiagent.mcp;

import com.mak.aiagent.App.ChatApp;
import com.mak.aiagent.advisor.MyLoggerAdvisor;
import io.modelcontextprotocol.client.McpClient;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

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

//    @Test
//    public void testMCP1(){
//        // 客户端代码示例
//        var stdioParams = ServerParameters.builder("java")
//                // 设置必要的系统属性
//                .args("-Dspring.ai.mcp.server.stdio=true",
//                        "-Dspring.main.web-application-type=none",
//                        "-Dlogging.pattern.console=",
//                        "-jar",
//                        "C:\\Users\\DELL\\Desktop\\SpringAI1.0\\MCPserver\\target\\MCPserver-0.0.1-SNAPSHOT.jar")
//                .build();
//
//// 创建基于stdio的传输层
//        var transport = new StdioClientTransport(stdioParams);
//// 构建同步MCP客户端
//        var client = McpClient.sync(transport).build();
//        client.initialize();
//        ToolCallbackProvider provider = new SyncMcpToolCallbackProvider(client);
//
//        // Create an instance of AllTools
//        ChatResponse response = chatClient
//                .prompt()
//                .user("星际穿越这部电影怎么样")
//                .advisors(spec -> spec.param("chat_memory_conversation_id", "mcp"))
//                .toolCallbacks(provider)
//                .call()
//                .chatResponse();
//    }
}
