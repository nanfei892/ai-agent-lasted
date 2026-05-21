package com.zsm.aiagentlasted.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ReAcAgent（Reasoning and Action）模式的代理抽象类
 * 实现了 思考-行动 的循环模式
 * 每一步：先 think() 再按序 act()
 *
 * @author zsm
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ReAcAgent extends BaseAgent {

    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否需要执行行动，true表示需要执行，false表示不需要执行
     */
    public abstract boolean think();

    /**
     * 执行决定的行动
     *
     * @return 行动执行结果
     */
    public abstract String act();

    /**
     * 执行单个步骤：思考和行动
     *
     * @return 执行步骤的结果
     */
    @Override
    public String step() {
        try {
            boolean shouldAct = think();
            if (!shouldAct) {
                if (isStreamMode()) {
                    // 流式：事件已经在 think() 里推送：返回空串即可
                    return "";
                }
                return "思考完成 - 无需行动";
            }
            return act();
        } catch (Exception e) {
            // 记录日常日志
            e.printStackTrace();
            String msg = "步骤执行失败：" + e.getMessage();
            if (isStreamMode()) {
                emitError(msg);
            }
            return msg;
        }
    }
}
