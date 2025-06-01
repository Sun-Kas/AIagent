package com.mak.aiagent.tool;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ResourceDownloadTest {
    @Autowired
    ChatClient  chatClient;
    @Test
    public void testResourceDownload() {
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        String url = "https://www.codefather.cn/logo.png";
        String fileName = "C:\\Users\\DELL\\Desktop\\Spring AI 原版\\AIagent\\AIagent\\tmp\\test.png";
        String result = resourceDownloadTool.downloadResource(url, fileName);
        System.out.println(result);
    }

    @Test
    public void testResourceDownloadAI() {
        String result=chatClient.prompt("请帮我下载https://www.codefather.cn/logo.png，并保存到桌面C:\\Users\\DELL\\Desktop")
                .tools(new ResourceDownloadTool())
                .call()
                .content();
        System.out.println(result);
    }
}
