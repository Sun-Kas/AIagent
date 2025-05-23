package com.mak.aiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

@Slf4j
public class MyLoggerAdvisor implements CallAdvisor, StreamAdvisor{
    private final String name="MyLoggerAdvisor";
    private final int order=0;


    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getOrder() {
        return order;
    }

    private ChatClientRequest beforeCall(ChatClientRequest advisedRequest) {
        log.info("UserQuery:{}",advisedRequest.prompt().getContents());
        return advisedRequest;
    }

    private ChatClientResponse afterCall(ChatClientResponse  respone) {
        log.info("Assistant:{}",respone.chatResponse().getResult().getOutput().getText());
        return respone;
    }

    @NotNull
    @Override
    public ChatClientResponse adviseCall(@NotNull ChatClientRequest advisedRequest, CallAdvisorChain chain) {
        // 1. 处理请求（前置处理）
        ChatClientRequest modifiedRequest = beforeCall(advisedRequest);

        // 2. 调用链中的下一个Advisor
        ChatClientResponse response = chain.nextCall(modifiedRequest);

        // 3. 处理响应（后置处理）
        return afterCall(response);
    }

    @NotNull
    @Override
    public Flux<ChatClientResponse> adviseStream(@NotNull ChatClientRequest advisedRequest, StreamAdvisorChain chain) {
        // 1. 处理请求
        ChatClientRequest modifiedRequest = beforeCall(advisedRequest);

        // 2. 调用链中的下一个Advisor并处理流式响应
        return chain.nextStream(modifiedRequest)
                .map(this::afterCall);

//        Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);
//        return (new MessageAggregator()).aggregateAdvisedResponse(advisedResponses, this::afterCall);
    }
}
