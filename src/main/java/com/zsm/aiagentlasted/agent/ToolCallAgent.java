package com.zsm.aiagentlasted.agent;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.zsm.aiagentlasted.agent.model.AgentState;
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
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 支持工具调用的 Agent：实现 ReAct 的 think（想）和 act（做）。
 * 流式模式下通过 BaseAgent.emitXxx 把过程推送给前端
 *
 * @author zsm
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReAcAgent {

    // 可用的工具
    private final ToolCallback[] availableTools;

    // 保存了工具调用信息的响应
    private ChatResponse toolCallChatResponse;

    // 工具调用管理者
    private final ToolCallingManager toolCallingManager;

    // 禁用内置的工具调用机制，自己维护上下文
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
        this.chatOptions = DashScopeChatOptions.builder()
                .internalToolExecutionEnabled(false)
                .build();
    }

    /**
     * 思考阶段：调用 LLM，判断要不要使用工具
     *
     * @return true=需要执行 act()；false=本步不调用工具
     */
    @Override
    public boolean think() {
        if (getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()) {
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }
        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList, chatOptions);
        try {
            // 获取带工具选项的响应
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .toolCallbacks(availableTools)
                    .call()
                    .chatResponse();
            // 记录响应，用于 Act
            this.toolCallChatResponse = chatResponse;
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            // 输出提示信息 拼 Prompt，调用 LLM
            String result = assistantMessage.getText();
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            // 保留日志，便于本地调试
            log.info(getName() + "的思考：" + result);
            log.info(getName() + "选择了 " + toolCallList.size() + " 个工具来使用");

            // ===== 1.推送思考内容到前端 =========
            if (result != null && !result.isEmpty()) {
                if (isStreamMode()) {
                    emitThink(result);
                }
            }

            //====== 2.有工具：推送每个 tool_call，然后进入 act ======
            if (!toolCallList.isEmpty()) {
                for (AssistantMessage.ToolCall toolCall : toolCallList) {
                    log.info("工具名称：{}，参数：{}", toolCall.name(), toolCall.arguments());
                    if (isStreamMode()) {
                        emitToolCall(toolCall.name(), toolCall.arguments());
                    }
                }
                return true;
            }

            // ====== 3. 无工具：模型直接给文字回答结束本轮 Agent ========
            getMessageList().add(assistantMessage);
            if (result != null && !result.isBlank()) {
                if (isStreamMode()) {
                    emitAnswer(result);
                    setState(AgentState.FINISHED);
                }
            }
            return false;
        } catch (Exception e) {
            log.error("{} 思考失败：{}", getName(), e.getMessage(), e);
            getMessageList().add(
                    new AssistantMessage("处理时遇到错误：" + e.getMessage())
            );
            if (isStreamMode()) {
                emitError("思考过程出错：" + e.getMessage());
            }
            return false;
        }
    }

    /**
     * 行动阶段：执行 LLM 选中的工具
     */
    @Override
    public String act() {
        if (toolCallChatResponse == null || !toolCallChatResponse.hasToolCalls()) {
            return "没有工具调用";
        }
        // 调用工具
        Prompt prompt = new Prompt(getMessageList(), chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        // 记录消息上下文，conversationHistory 已经包含了助手消息和工具调用返回的结果
        setMessageList(toolExecutionResult.conversationHistory());
        // 当前工具调用的结果
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());

        // ======= 逐个推送工具结果 ========
        toolResponseMessage.getResponses().forEach(response -> {
            String line = "工具 " + response.name() + "完成，结果：" + response.responseData();
            log.info(line);
            if (isStreamMode()) {
                emitToolResult(response.name(), String.valueOf(response.responseData()));
            }
        });

        // 判断是否调用了终止工具
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> "doTerminate".equals(response.name()));

        if (terminateToolCalled) {
            setState(AgentState.FINISHED);
            if (isStreamMode()) {
                emitAnswer("任务已结束。");
            }
        }

        return toolResponseMessage.getResponses().stream()
                .map(response -> "工具" + response.name() + "完成了它的任务！结果：" + response.responseData())
                .collect(Collectors.joining("\n"));

    }
}
