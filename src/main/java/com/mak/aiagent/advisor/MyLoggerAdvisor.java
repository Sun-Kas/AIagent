package com.mak.aiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

@Slf4j
public class MyLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {
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

    private AdvisedRequest beforeCall(AdvisedRequest advisedRequest) {
        log.info("UserQuery:{}",advisedRequest.userText());
        return advisedRequest;
    }

    private AdvisedResponse afterCall(AdvisedResponse  respone) {
        log.info("Assistant:{}",respone.response().getResult().getOutput().getText());
        return respone;
    }

    @NotNull
    @Override
    public AdvisedResponse aroundCall(@NotNull AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        // 1. 处理请求（前置处理）
        AdvisedRequest modifiedRequest = beforeCall(advisedRequest);

        // 2. 调用链中的下一个Advisor
        AdvisedResponse response = chain.nextAroundCall(modifiedRequest);

        // 3. 处理响应（后置处理）
        return afterCall(response);
    }

    @NotNull
    @Override
    public Flux<AdvisedResponse> aroundStream(@NotNull AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        // 1. 处理请求
        AdvisedRequest modifiedRequest = beforeCall(advisedRequest);

        // 2. 调用链中的下一个Advisor并处理流式响应
        return chain.nextAroundStream(modifiedRequest)
                .map(this::afterCall);

//        Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);
//        return (new MessageAggregator()).aggregateAdvisedResponse(advisedResponses, this::afterCall);
    }
}
