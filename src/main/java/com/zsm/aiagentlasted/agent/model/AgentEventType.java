package com.zsm.aiagentlasted.agent.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Agent 流式推送给前端的事件类型
 * 使用 @JsonValue：序列化成 JSON 时，输出的是 value 字段（如“think”）
 * 而不是枚举名 THINK，这样前端更好读。
 *
 * @author zsm
 */
public enum AgentEventType {

    // 智能体处理是处理用户请求（整个任务只发一次）
    AGENT_START("agent_start"),

    // 某一步开始执行（每步都发送一次）
    STEP_START("step_start"),

    // 模型思考内容（对应日志里的 [xxx的思考]
    THINK("think"),

    // 模型决定调用某个工具（工具名 + 参数）
    TOOL_CALL("tool_call"),

    // 工具执行完毕后的返回结果
    TOOL_RESULT("tool_result"),

    // 面向用户的回答（无工具时，或你认为应展示给用户的正文）
    ANSWER("answer"),

    // 发生错误
    ERROR("error"),

    // 整个流程结束
    DONE("done");


    // 与前端 JSON 中 type 字段对应的字符串
    private final String value;

    AgentEventType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
