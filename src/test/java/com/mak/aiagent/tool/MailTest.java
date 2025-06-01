package com.mak.aiagent.tool;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MailTest {
    @Autowired
    ChatClient chatClient;

    @Test
    public void testMail() {
        EmailTool emailTool = new EmailTool();
        String result= emailTool.sendEmail("akai.ma@sjtu.edu.cn", "Test Email", "This is a test email.");
        System.out.println(result);
    }

    @Test
    public void testMailAI() {
        String result=chatClient.prompt("请帮我写一封介绍信给李明(akai.ma@sjtu.edu.cn)，向他推荐特朗普先生，希望他能在大选时支持特朗普")
                        .tools(new EmailTool())
                                .call().content();
        System.out.println(result);
    }
}
