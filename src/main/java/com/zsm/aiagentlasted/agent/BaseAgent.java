package com.zsm.aiagentlasted.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zsm.aiagentlasted.agent.model.AgentEventType;
import com.zsm.aiagentlasted.agent.model.AgentState;
import com.zsm.aiagentlasted.agent.model.AgentStreamEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 抽象基础代理类，用于管理代理状态、多步循环、同步/流式两种运行方式。
 * 子类海鲜 step()；流式时子类在 think/act 里调用 emitXXX() 推送结构化事件
 *
 * @author zsm
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // 核心属性
    private String name;

    // 提示
    private String systemPrompt;
    private String nextStepPrompt;

    // 状态
    private AgentState state = AgentState.IDLE;

    // 执行控制
    private int maxSteps = 10;
    private int currentStep = 0;

    // LLM
    private ChatClient chatClient;

    // Memory（需要自主维护会话上下文）
    private List<Message> messageList = new ArrayList<>();

    // Jackson：Java 对象 <-> JSON字符串
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // 仅在 runStream 执行期间非 null。transient：若以后序列化 Agent 对象，不会把这个字段写进磁盘。
    private transient SseEmitter sseEmitter;

    //========================流式推送核心方法=================================

    /**
     * runStream 入口绑定 emitter，子类才能推送
     */
    protected void bindEmitter(SseEmitter emitter) {
        this.sseEmitter = emitter;
    }

    /**
     * 任务结束解绑，防止误用已关闭的连接。
     */
    protected void unbindEmitter() {
        this.sseEmitter = null;
    }

    /**
     * 是否处于 SSE 流式模式（think/act 里可用来判断是否 emit）
     */
    protected boolean isStreamMode() {
        return this.sseEmitter != null;
    }

    /**
     * 推送一条结构化事件到前端
     * 自动补全 timestamp，发送失败只打日志，不中断 Agent 主流程
     *
     * @param event 事件对象
     */
    protected void emitEvent(AgentStreamEvent event) {
        if (!isStreamMode()) {
            return;
        }
        if (event.getTimestamp() == null) {
            event.setTimestamp(System.currentTimeMillis());
        }
        if (event.getStep() == null) {
            event.setStep(currentStep);
        }
        if (event.getMaxSteps() == null) {
            event.setMaxSteps(maxSteps);
        }
        try {
            String json = OBJECT_MAPPER.writeValueAsString(event);
            sseEmitter.send(json);
        } catch (Exception e) {
            log.error("SSE 推送失败，event={}", event, e);
        }
    }


    // --------------便捷方法：子类直接调用，不用每次 new Builder--------------
    protected void emitAgentStart(String userPrompt) {
        emitEvent(AgentStreamEvent.builder()
                .type(AgentEventType.AGENT_START)
                .content("开始处理用户请求")
                .build());
        log.info("{}: 开始执行，用户输入：{}", name, userPrompt);
    }

    protected void emitStepStart(int stepNumber) {
        emitEvent(AgentStreamEvent.builder()
                .type(AgentEventType.STEP_START)
                .step(stepNumber)
                .content("开始执行第 " + stepNumber + " 步")
                .build());
        log.info("Executing step {}/{}", stepNumber, maxSteps);
    }

    protected void emitThink(String content) {
        emitEvent(AgentStreamEvent.builder()
                .type(AgentEventType.THINK)
                .content(content)
                .build());
    }

    protected void emitToolCall(String toolName, String arguments) {
        emitEvent(AgentStreamEvent.builder()
                .type(AgentEventType.TOOL_CALL)
                .toolName(toolName)
                .arguments(arguments)
                .build());
    }

    protected void emitToolResult(String toolName, String toolResult) {
        emitEvent(AgentStreamEvent.builder()
                .type(AgentEventType.TOOL_RESULT)
                .toolName(toolName)
                .toolResult(toolResult)
                .build());
    }

    protected void emitAnswer(String content) {
        emitEvent(AgentStreamEvent.builder()
                .type(AgentEventType.ANSWER)
                .content(content)
                .build());
    }

    protected void emitError(String message) {
        emitEvent(AgentStreamEvent.builder()
                .type(AgentEventType.ERROR)
                .content(message)
                .build());
    }

    protected void emitDone(String reason) {
        emitEvent(AgentStreamEvent.builder()
                .type(AgentEventType.DONE)
                .reason(reason)
                .content("执行结束")
                .build());
    }

    //=======================同步运行（非 SSE）=======================

    /**
     * 运行代理
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt) {
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent from state: " + this.state);
        }
        if (StringUtil.isBlank(userPrompt)) {
            throw new RuntimeException("Cannot run agent with empty user prompt");
        }
        // 更改状态
        state = AgentState.RUNNING;
        // 记录消息上下文
        messageList.add(new UserMessage(userPrompt));
        // 保存结果列表
        List<String> results = new ArrayList<>();
        try {
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step " + stepNumber + "/" + maxSteps);
                // 单步执行
                String stepResult = step();
                String result = "Step " + stepNumber + ": " + stepResult;
                results.add(result);
            }
            // 检查是否超出最大步骤
            if (currentStep >= maxSteps) {
                state = AgentState.FINISHED;
                results.add("Terminated: Reached max steps(" + maxSteps + ")");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("Error executing agent", e);
            return "执行错误" + e.getMessage();
        } finally {
            // 清理资源
            this.cleanup();
        }
    }


    // ========================= 流式运行（SSE）========================

    /**
     * 运行代理（流失输出）
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public SseEmitter runStream(String userPrompt) {
        // 创建SseEmitter，设置较长的超时时间
        SseEmitter emitter = new SseEmitter(300000L);   // 5分钟超时

        // 使用线程异步处理，避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            bindEmitter(emitter);
            try {
                if (this.state != AgentState.IDLE) {
                    emitter.send("无法从状态运行代理：" + this.state);
                    emitter.send("[DONE]");
                    emitter.complete();
                    return;
                }
                if (StringUtil.isBlank(userPrompt)) {
                    emitter.send("不能使用空提示词运行代理");
                    emitter.send("[DONE]");
                    emitter.complete();
                    return;
                }

                // 更改状态
                state = AgentState.RUNNING;
                // 记录消息上下文
                messageList.add(new UserMessage(userPrompt));
                emitAgentStart(userPrompt);

                for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                    int stepNumber = i + 1;
                    currentStep = stepNumber;
                    emitStepStart(stepNumber);

                    step();
                }

                String doneReason = "finished";
                // 检查是否超出最大步骤
                if (currentStep >= maxSteps && state != AgentState.FINISHED) {
                    state = AgentState.FINISHED;
                    doneReason = "max_steps";
                    emitAnswer("已达最大步数限制(" + maxSteps + "), 自动结束。");
                }

                // 正常完成
                emitDone(doneReason);
                emitter.send("[DONE]");
                emitter.complete();

            } catch (Exception e) {
                state = AgentState.ERROR;
                log.error("执行智能体失败", e);
                try {
                    emitError("执行错误：" + e.getMessage());
                    emitter.send("[DONE]");
                    emitter.complete();
                } catch (Exception ex) {
                    emitter.completeWithError(ex);
                }
            } finally {
                cleanup();
                unbindEmitter();
            }
        });

        // 设置超时和完成回调
        emitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.cleanup();
            log.warn("SSE connection timed out");
        });
        emitter.onCompletion(() -> {
            if (this.state == AgentState.RUNNING) {
                this.state = AgentState.FINISHED;
            }
            this.cleanup();
            log.info("SSE connection completed");
        });

        return emitter;
    }

    /**
     * 执行单个步骤
     *
     * @return 步骤执行结果
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup() {
        // 子类可以重写此方法来清理资源
    }
}
