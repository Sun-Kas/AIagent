package com.mak.aiagent.agentmodel;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象基础代理类，用于管理代理状态和执行流程。
 *
 * 提供状态转换、内存管理和基于步骤的执行循环的基础功能。
 * 子类必须实现step方法。
 */
@Data
@Slf4j
public abstract class BaseModel {
    /**
     * agent名字
     */
    private String name;

    /**
     * prompt：
     * systemPrompt
     * nextStepPrompt： 主要用于给每一步添加提示，主要用于引导模型进行thinkign
     */
    private String systemPrompt;
    private String nextStepPrompt;

    /**
     * agent运行状态
     */
    private AgentState state = AgentState.IDLE;

    /**
     * 控制流程
     */
    private int maxSteps=10;

    /**
     * LLM
     */
    private ChatClient chatClient;

    /**
     * 手动维护的Message 列表i
     */
    private List<Message> messageList = new ArrayList<>();  ;

    /**
     * 运行代理
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt){
        //1.如果agent状态不为空闲，则报错
        if(this.state!=AgentState.IDLE){
            throw new RuntimeException("Agent is not idle");
        }
        //2. 如果用户提示词为空，则agent也无法运行
        if(userPrompt.isBlank()){
            throw new RuntimeException("User prompt cannot be blank");
        }
        //3. 更新agent状态
        this.state=AgentState.RUNNING;
        //4. 记录上下问
        this.messageList.add(new UserMessage(userPrompt));
        // 5. 建立每一步的返回结果
        List<String> results=new ArrayList<>();
        try{
            for(int i=0;i<this.maxSteps&&this.state!=AgentState.FINISHED;i++){
                log.info("Executing step " + i+1 + "/" + maxSteps);
                //6. 单步运行
                String result=this.step();
                log.info("Step {} result: {}", i+1, result);
                results.add(result);
                //7. 检查是否达最大步数
                if(i==this.maxSteps-1){
                    this.state=AgentState.FINISHED;
                    results.add("Terminated: Reached max steps (" + maxSteps + ")");
                }
            }
            return String.join("\n", results);
        }catch(Exception e){
            //8. 异常处理
            this.state=AgentState.ERROR;
            log.error("Error executing agent", e);
            return e.getMessage();
        }finally{
            //p. 资源释放
            this.cleanup();
        }
    }

    public abstract String step();

    public abstract void cleanup();

}
