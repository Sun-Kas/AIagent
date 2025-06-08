package com.mak.aiagent.agentmodel;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理工具调用的基础代理类，具体实现了 think 和 act 方法，可以用作创建实例的父类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActModel {
    // 可用的工具
    private final ToolCallback[] availableTools;

    // 保存了工具调用信息的响应
    private ChatResponse toolCallChatResponse;

    // 工具调用管理者
    private final ToolCallingManager toolCallingManager;

    // 禁用内置的工具调用机制，自己维护上下文
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools){
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(this.availableTools)
                .internalToolExecutionEnabled(false)
                .build();
    }

    @Override
    public boolean think() {
        if(getNextStepPrompt()!=null&&!getNextStepPrompt().isBlank()){
            getMessageList().add(new UserMessage(getNextStepPrompt()));
        }
        List<Message> messages = getMessageList();
        Prompt prompt = new Prompt(messages,this.chatOptions);
        try{
            ChatResponse  chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .call()
                    .chatResponse();
            this.toolCallChatResponse=chatResponse;
            AssistantMessage output = chatResponse.getResult().getOutput();
            String result = output.getText();
            List<AssistantMessage.ToolCall> toolCalls = output.getToolCalls();
            if(toolCalls.isEmpty()){
                // 只有不调用工具时，才记录助手消息
                log.info(getName()+"思考了："+result);
                getMessageList().add(new AssistantMessage(result));
                return false;
            }else{
                // 需要调用工具时，无需记录助手消息，因为调用工具时会自动记录
                log.info(getName() + "的思考: " + result);
                log.info(getName() + "选择了 " + toolCalls.size() + " 个工具来使用");
                String toolCallInfo = toolCalls.stream()
                        .map(toolCall -> String.format("工具名称：%s，参数：%s",
                                toolCall.name(),
                                toolCall.arguments())
                        )
                        .collect(Collectors.joining("\n"));
                log.info(toolCallInfo);
                return true;
            }

        }catch( Exception e){
            log.info(getName()+"运行时出现错误：{}",e.getMessage());
            AssistantMessage  assistantMessage = new AssistantMessage(e.getMessage());
            getMessageList().add(assistantMessage);
        }
        return false;
    }

    @Override
    public String act() {
        if(!this.toolCallChatResponse.hasToolCalls()){
            return "没有工具被调用";
        }
        // 调用工具
        Prompt prompt = new Prompt(getMessageList(), chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        // 记录消息上下文，conversationHistory 已经包含了助手消息和工具调用返回的结果
        setMessageList(toolExecutionResult.conversationHistory());
        // 当前工具调用的结果
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "工具 " + response.name() + " 完成了它的任务！结果: " + response.responseData())
                .collect(Collectors.joining("\n"));
        log.info(results);
        // 判断是否调用了终止工具
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> "doTerminate".equals(response.name()));
        if (terminateToolCalled) {
            setState(AgentState.FINISHED);
        }
        return results;
    }

    @Override
    public void cleanup() {

    }
}
