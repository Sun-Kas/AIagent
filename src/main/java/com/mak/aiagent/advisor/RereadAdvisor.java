package com.mak.aiagent.advisor;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * 自定义 Re2 Advisor
 * 可提高大型语言模型的推理能力
 */
public class RereadAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    private final String NAME="MyLoggerAdvisor";
    private final int ORDER=0;


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    private AdvisedRequest beforeCall(AdvisedRequest advisedRequest) {
        //这个应该指示提示词模板中的变量
        Map<String, Object> stringObjectMap = advisedRequest.userParams();
        stringObjectMap.put("re2_input_query", advisedRequest.userText());
        return AdvisedRequest.from(advisedRequest)
                .userText("""
                        {re2_input_query}
                        Read the question again: {re2_input_query}
                        """)
                .userParams(stringObjectMap)
                .build();
    }


    @NotNull
    @Override
    public AdvisedResponse aroundCall(@NotNull AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        AdvisedRequest modifiedRequest = beforeCall(advisedRequest);
        return chain.nextAroundCall(modifiedRequest);
    }


    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        AdvisedRequest modifiedRequest = beforeCall(advisedRequest);
        return chain.nextAroundStream(modifiedRequest);
    }
}
