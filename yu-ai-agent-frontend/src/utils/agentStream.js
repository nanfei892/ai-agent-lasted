/**
 * 解析并应用 Agent SSE 事件到 trace 对象（响应式对象，直接修改即可触发 UI 更新）
 */

/** @param {object} trace - 消息上的 trace 对象 */
/** @param {object} evt - 后端 AgentStreamEvent JSON */
export function applyAgentEvent(trace, evt) {
  const type = evt.type
  const stepNum = evt.step > 0 ? evt.step : (trace.steps.length || 1)

  const getOrCreateStep = (n) => {
    let step = trace.steps.find((s) => s.step === n)
    if (!step) {
      step = {
        step: n,
        think: '',
        thinkExpanded: false,
        tools: [],
        answer: '',
        status: 'running'
      }
      trace.steps.push(step)
    }
    return step
  }

  switch (type) {
    case 'agent_start':
      // 兼容后端误把 step_start 写成 agent_start 的情况
      if (evt.step > 0 && evt.content && evt.content.includes('步')) {
        getOrCreateStep(evt.step)
      }
      break

    case 'step_start':
      getOrCreateStep(evt.step || stepNum)
      break

    case 'think': {
      const step = getOrCreateStep(evt.step || stepNum)
      step.think = evt.content || ''
      step.thinkExpanded = true
      break
    }

    case 'tool_call': {
      const step = getOrCreateStep(evt.step || stepNum)
      step.tools.push({
        name: evt.toolName,
        arguments: evt.arguments,
        result: null,
        status: 'running'
      })
      break
    }

    case 'tool_result': {
      const step = getOrCreateStep(evt.step || stepNum)
      const tool =
        [...step.tools].reverse().find((t) => t.name === evt.toolName && t.result == null) ||
        step.tools[step.tools.length - 1]
      if (tool) {
        tool.result = evt.toolResult
        tool.status = 'done'
      }
      break
    }

    case 'answer': {
      const step = getOrCreateStep(evt.step || stepNum)
      step.answer = evt.content || ''
      step.status = 'done'
      step.thinkExpanded = false
      break
    }

    case 'error':
      trace.status = 'error'
      trace.error = evt.content || '未知错误'
      break

    case 'done':
      trace.status = 'done'
      trace.doneReason = evt.reason
      trace.steps.forEach((s) => {
        if (s.status === 'running') s.status = 'done'
      })
      break

    default:
      console.warn('未知 Agent 事件类型:', type, evt)
  }
}
