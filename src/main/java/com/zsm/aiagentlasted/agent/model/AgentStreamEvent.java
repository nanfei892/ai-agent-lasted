package com.zsm.aiagentlasted.agent.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**通过 SSE 流式推送给前端的单条事件
 * 不同 type 使用不同的字段，例如：
 * think -> content
 * tool_call -> toolName + arguments
 * too_result -> toolName + toolResult
 * @author zsm
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)   // 为 null 的字段不写入 JSON，减少体积
public class AgentStreamEvent {

    // 时间类型，序列化后为 agent_state / think / done 等
    private AgentEventType type;

    // 当前第几步（从 1 开始）
    private Integer step;

    // 最大步数（20）
    private Integer maxSteps;

    // 通用文本：思考内容、回答、错误信息等
    private String content;

    // 工具名称
    private String toolName;

    // 工具调用参数
    private String arguments;

    // 工具执行结果
    private String toolResult;

    // 时间时间戳（毫秒），前端可用来排序或显示时间
    private Long timestamp;

    // 结束原因 （仅 done 时可选）
    private String reason;
}
