package com.mak.aiagent.App;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class ChatApp {

    @Resource
    private VectorStore vectorStore;

    private final ChatClient chatClient;

    public ChatApp(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .system(sp -> sp.param("voice", "Trump"))
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        //log.info("content: {}", content);
        return content;
    }

    public LoveReport discuss(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(sp -> sp.param("voice", "Trump"))
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }

    public String doChatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 应用知识库问答
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        return content;
    }

    /**
     * BeanOutputConverter 支持通过 @JsonPropertyOrder 注解在生成的 JSON 模式中自定义属性的顺序。
     * 该注解允许你指定属性在模式中出现的精确顺序，而不管它们在类或记录中的声明顺序如何
     * @param title
     * @param suggestions
     */
    @JsonPropertyOrder({"title", "suggestions"})
    record LoveReport(String title, List<String> suggestions) {
    }
}
