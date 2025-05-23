package com.mak.aiagent.App;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;

import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;


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
                .advisors(spec -> spec.param("chat_memory_conversation_id", chatId))
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
                .advisors(spec -> spec.param("chat_memory_conversation_id", chatId))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }

    public String doChatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param("chat_memory_conversation_id", chatId))
                // 应用知识库问答
                .advisors(RetrievalAugmentationAdvisor.builder()
                        .documentRetriever(VectorStoreDocumentRetriever.builder()
                                .similarityThreshold(0.50)
                                .vectorStore(vectorStore)
                                .build())
                        .build())
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
