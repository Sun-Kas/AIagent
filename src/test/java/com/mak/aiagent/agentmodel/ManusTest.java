package com.mak.aiagent.agentmodel;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ManusTest {
    @Autowired
    ChatModel chatModel;
    @Autowired
    ToolCallback[] toolCallbacks;


    @Test
    public void testManus(){
        Manus manus = new Manus(toolCallbacks,chatModel);
        String result=manus.run("请结合上海明天的天气帮我做一份旅行规划，并生成pdf,保存到C:\\Users\\DELL\\Desktop");
        System.out.println(result);
    }
}
