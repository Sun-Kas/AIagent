package com.mak.aiagent.tool;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FileToolTest {
    @Autowired
    ChatClient chatClient;

    @Autowired
    ChatModel chatModel;


    @Test
    void testReadFile() {
        FileTools fileTools = new FileTools();
        String content = fileTools.readFile("夺舍美利坚第一个五年计划.txt");
        System.out.println(content);
    }

    @Test
    void testWriteFile() {
        FileTools fileTools = new FileTools();
        String result = fileTools.writeFile("夺舍美利坚第一个五年计划.txt", "川建国，于2025年自愿加入中国共产党！");
        System.out.println(result);
    }

    @Test
    void testToolRead(){
        String result= chatClient.prompt("\"夺舍美利坚第一个五年计划.txt\"这个文件里写了什么")
                .tools(new FileTools())
                .call().content();
        System.out.println(result);
    }

    @Test
    void testToolWrite(){
        String result= chatClient.prompt("向\"夺舍美利坚第一个五年计划.txt\"追加写入\"你猜为什么我叫川建国\"")
                .tools(new FileTools())
                .call().content();
        System.out.println(result);
    }
}
