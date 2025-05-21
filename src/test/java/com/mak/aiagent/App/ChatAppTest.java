package com.mak.aiagent.App;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ChatAppTest {

    @Resource
    ChatApp chatApp;

    @Resource
    ChatClient chatClient;

    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是程序员鱼皮";
        String answer = chatApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第二轮
        message = "我想让另一半（编程导航）更爱我";
        answer = chatApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "我的另一半叫什么来着？刚跟你说过，帮我回忆一下";
        answer = chatApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void discuss() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你如何看当下的中美关系";
        chatApp.discuss(message, chatId);
    }

    @Test
    void structureOutput() {
        // 定义一个记录类
        record ActorsFilms(String actor, List<String> movies) {}
        ActorsFilms actorsFilms = chatClient.prompt()
                .user("Generate 5 movies for Tom Hanks.")
                .call()
                .entity(ActorsFilms.class);
        assertNotNull(actorsFilms);
        System.out.println(actorsFilms);

        Map<String, Object> result = chatClient.prompt()
                .user(u -> u.text("Provide me a List of {subject}")
                        .param("subject", "an array of numbers from 1 to 9 under they key name 'numbers'"))
                .call()
                .entity(new ParameterizedTypeReference<Map<String, Object>>() {});
        assertNotNull(result);
        System.out.println(result.toString());

        Map<String, Object> result1 = chatClient.prompt()
                .user(u -> u.text("Provide me a List of {subject}")
                        .param("subject", "an array of numbers from 11 to 96 under they key name 'numbers'"))
                .call()
                .entity(new MapOutputConverter());
        assertNotNull(result1);
        System.out.println(result1.toString());

        List<String> flavors = chatClient.prompt()
                .user(u -> u.text("List five {subject}")
                        .param("subject", "ice cream flavors"))
                .call()
                .entity(new ListOutputConverter(new DefaultConversionService()));
        assertNotNull(flavors);
        System.out.println(flavors.toString());

        List<String> flavors2 = chatClient.prompt()
                .user(u -> u.text("List five {subject}")
                        .param("subject", "ice cream flavors"))
                .call()
                .entity(new ParameterizedTypeReference<List<String>>() {});
        assertNotNull(flavors2);
        System.out.println(flavors2.toString());
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
        String answer =  chatApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }
}