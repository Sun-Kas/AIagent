package com.mak.aiagent.App;

import cn.hutool.core.lang.UUID;
import com.mak.aiagent.component.DocumentLoader;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.join.DocumentJoiner;
//import org.springframework.ai.transformer.KeywordMetadataEnricher;
//import org.springframework.ai.transformer.SummaryMetadataEnricher;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.model.transformer.SummaryMetadataEnricher;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ChatAppTest {
    @Resource
    ChatModel chatModel;

    @Resource
    ChatApp chatApp;

    @Resource
    ChatClient chatClient;

    @Resource
    ChatClient.Builder builder;

    @Autowired
    EmbeddingModel embeddingModel;

    @Autowired
    DocumentLoader documentLoader;

    @Autowired
    VectorStore vectorStore;
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

    @Test
    void doTokenTextSplitter(){
        Document doc1 = new Document("This is a long piece of text that needs to be split into smaller chunks for processing.",
                Map.of("source", "example.txt"));
        Document doc2 = new Document("Another document with content that will be split based on token count.",
                Map.of("source", "example2.txt"));

        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> splitDocuments = splitter.apply(List.of(doc1, doc2));

        for (Document doc : splitDocuments) {
            System.out.println("Chunk: " + doc.getText());
            System.out.println("Metadata: " + doc.getMetadata());
        }
    }

    @Test
    void KeywordMetadataEnricher(){
        KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(chatModel, 5);

        Document doc = new Document("This is a document about artificial intelligence and its applications in modern technology.");

        List<Document> enrichedDocs = enricher.apply(List.of(doc));

        Document enrichedDoc = enrichedDocs.get(0);
        String keywords = (String) enrichedDoc.getMetadata().get("excerpt_keywords");
        System.out.println("Extracted keywords: " + keywords);
    }

    @Test
    void SummaryMetadataEnricher (){
        SummaryMetadataEnricher enricher = new SummaryMetadataEnricher(chatModel,
                List.of(SummaryMetadataEnricher.SummaryType.PREVIOUS, SummaryMetadataEnricher.SummaryType.CURRENT, SummaryMetadataEnricher.SummaryType.NEXT));

        Document doc1 = new Document("Content of document 1");
        Document doc2 = new Document("Content of document 2");

        List<Document> enrichedDocs = enricher.apply(List.of(doc1, doc2));

// Check the metadata of the enriched documents
        for (Document doc : enrichedDocs) {
            System.out.println("Current summary: " + doc.getMetadata().get("section_summary"));
            System.out.println("Previous summary: " + doc.getMetadata().get("prev_section_summary"));
            System.out.println("Next summary: " + doc.getMetadata().get("next_section_summary"));
        }
    }

    @Test
    void doRewriteQueryTransformer() {
        Query query = new Query("I'm studying machine learning. What is an LLM?");

        QueryTransformer queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(builder)
                .build();

        Query transformedQuery = queryTransformer.transform(query);

        System.out.println(transformedQuery.toString());
    }

    @Test
    void doCompressionQueryTransformer() {
        Query query = Query.builder()
                .text("And what is its second largest city?")
                .history(new UserMessage("What is the capital of Denmark?"),
                        new AssistantMessage("Copenhagen is the capital of Denmark."))
                .build();

        QueryTransformer queryTransformer = CompressionQueryTransformer.builder()
                .chatClientBuilder(builder)
                .build();

        Query transformedQuery = queryTransformer.transform(query);

        System.out.println(transformedQuery.toString());
    }

    @Test
    void doTranslationQueryTransformer() {
        Query query = new Query("hello, shabi");

        QueryTransformer queryTransformer = TranslationQueryTransformer.builder()
                .chatClientBuilder(builder)
                .targetLanguage("chinese")
                .build();

        Query transformedQuery = queryTransformer.transform(query);

        System.out.println(transformedQuery.toString());
    }

    @Test
    void doMultiQueryExpander() {
        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(builder)
                .numberOfQueries(3)
                .build();
        List<Query> queries = queryExpander.expand(new Query("How to run a Spring Boot app?"));


        for(Query query : queries)
            System.out.println(query.toString());
    }

    @Test
    void doVector() {
        List <Document> documents = List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")));

// Add the documents to Redis
        vectorStore.add(documentLoader.loadMarkdowns());

// Retrieve documents similar to a query
        List<Document> results = this.vectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(2).build());
        for (Document document : results) {
            System.out.println(document.getText());
        }
    }
}