<template>
  <div class="agent-trace-card" :class="[trace.status, { streaming: isStreaming }]">
    <div class="trace-header">
      <span class="trace-icon" aria-hidden="true">⬡</span>
      <span class="trace-title">智能体执行轨迹</span>
      <span v-if="isStreaming" class="trace-badge mono streaming-badge">RUNNING</span>
      <span v-else-if="trace.status === 'done'" class="trace-badge mono done-badge">DONE</span>
      <span v-else-if="trace.status === 'error'" class="trace-badge mono error-badge">ERROR</span>
    </div>

    <div v-if="trace.error" class="trace-error mono">{{ trace.error }}</div>

    <div
      v-for="(step, idx) in trace.steps"
      :key="'step-' + step.step + '-' + idx"
      class="trace-step"
      :class="step.status"
    >
      <div class="step-head">
        <span class="step-num mono">STEP {{ step.step }}</span>
        <span class="step-line" aria-hidden="true"></span>
        <span v-if="step.status === 'running' && isStreaming" class="step-status mono">执行中</span>
        <span v-else-if="step.status === 'done'" class="step-status mono done">完成</span>
      </div>

      <div v-if="step.think" class="think-block">
        <button
          type="button"
          class="think-toggle mono"
          @click="step.thinkExpanded = !step.thinkExpanded"
        >
          <span class="toggle-icon">{{ step.thinkExpanded ? '▼' : '▶' }}</span>
          <span>思考过程</span>
          <span v-if="isThinkSameAsAnswer(step)" class="think-hint">（与回答相同，默认折叠）</span>
        </button>
        <div v-show="step.thinkExpanded" class="think-body" v-html="formatContent(step.think)"></div>
      </div>

      <div v-for="(tool, tIdx) in step.tools" :key="'tool-' + tIdx" class="tool-block">
        <div class="tool-head mono">
          <span class="tool-icon">⚙</span>
          <span class="tool-name">{{ tool.name }}</span>
          <span class="tool-status" :class="tool.status">{{ tool.status === 'done' ? '完成' : '执行中' }}</span>
        </div>
        <div v-if="tool.arguments" class="tool-args mono">
          <span class="label">参数</span>
          <code>{{ formatArgs(tool.arguments) }}</code>
        </div>
        <div v-if="tool.result != null" class="tool-result">
          <span class="label mono">结果</span>
          <div class="result-text">{{ tool.result }}</div>
        </div>
      </div>

      <div v-if="step.answer" class="answer-block">
        <div class="answer-label mono">回答</div>
        <div class="answer-body" v-html="formatContent(step.answer)"></div>
      </div>
    </div>

    <div v-if="isStreaming && trace.steps.length === 0" class="trace-loading mono">
      <span class="pulse-dot"></span> 正在连接智能体...
    </div>
  </div>
</template>

<script setup>
defineProps({
  trace: {
    type: Object,
    required: true
  },
  isStreaming: {
    type: Boolean,
    default: false
  }
})

const isThinkSameAsAnswer = (step) => {
  const t = (step.think || '').trim()
  const a = (step.answer || '').trim()
  return t && a && t === a
}

const formatContent = (text) => {
  if (!text) return ''
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\n/g, '<br>')
}

const formatArgs = (args) => {
  if (!args) return '{}'
  try {
    return JSON.stringify(JSON.parse(args), null, 2)
  } catch {
    return args
  }
}
</script>

<style scoped>
.mono {
  font-family: 'JetBrains Mono', 'Consolas', monospace;
}

.agent-trace-card {
  width: 100%;
  border: 1px solid rgba(0, 240, 255, 0.25);
  border-radius: 4px;
  background: rgba(6, 12, 24, 0.92);
  padding: 14px 16px;
  box-shadow: inset 0 0 30px rgba(0, 240, 255, 0.04);
}

.agent-trace-card.streaming {
  border-color: rgba(255, 204, 0, 0.35);
  box-shadow: 0 0 20px rgba(255, 204, 0, 0.08);
}

.agent-trace-card.done {
  border-color: rgba(0, 255, 136, 0.3);
}

.agent-trace-card.error {
  border-color: rgba(255, 68, 68, 0.45);
}

.trace-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(0, 240, 255, 0.12);
}

.trace-icon {
  color: #00f0ff;
  font-size: 14px;
  text-shadow: 0 0 8px rgba(0, 240, 255, 0.6);
}

.trace-title {
  font-family: 'Orbitron', sans-serif;
  font-size: 0.82rem;
  letter-spacing: 0.06em;
  color: rgba(232, 244, 255, 0.95);
  flex: 1;
}

.trace-badge {
  font-size: 0.6rem;
  letter-spacing: 0.12em;
  padding: 2px 8px;
  border-radius: 2px;
}

.streaming-badge {
  color: #ffcc00;
  border: 1px solid rgba(255, 204, 0, 0.4);
  background: rgba(255, 204, 0, 0.1);
  animation: badge-pulse 1.2s ease-in-out infinite;
}

.done-badge {
  color: #00ff88;
  border: 1px solid rgba(0, 255, 136, 0.35);
  background: rgba(0, 255, 136, 0.08);
}

.error-badge {
  color: #ff6666;
  border: 1px solid rgba(255, 68, 68, 0.4);
}

.trace-error {
  color: #ff8888;
  font-size: 13px;
  margin-bottom: 12px;
  padding: 8px 10px;
  background: rgba(255, 68, 68, 0.1);
  border-left: 3px solid #ff4444;
}

.trace-step {
  position: relative;
  padding-left: 12px;
  margin-bottom: 16px;
  border-left: 2px solid rgba(0, 240, 255, 0.2);
}

.trace-step:last-child {
  margin-bottom: 0;
}

.trace-step.running {
  border-left-color: rgba(255, 204, 0, 0.5);
}

.trace-step.done {
  border-left-color: rgba(0, 255, 136, 0.4);
}

.step-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.step-num {
  font-size: 0.68rem;
  color: #00f0ff;
  letter-spacing: 0.1em;
}

.step-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, rgba(0, 240, 255, 0.3), transparent);
}

.step-status {
  font-size: 0.62rem;
  color: #ffcc00;
}

.step-status.done {
  color: #00ff88;
}

.think-block {
  margin-bottom: 10px;
}

.think-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
  width: 100%;
  padding: 6px 10px;
  background: rgba(123, 47, 255, 0.12);
  border: 1px solid rgba(123, 47, 255, 0.3);
  border-radius: 2px;
  color: rgba(200, 180, 255, 0.95);
  font-size: 0.7rem;
  letter-spacing: 0.06em;
  cursor: pointer;
  text-align: left;
  transition: background 0.2s, border-color 0.2s;
}

.think-toggle:hover {
  background: rgba(123, 47, 255, 0.2);
  border-color: rgba(123, 47, 255, 0.5);
}

.toggle-icon {
  font-size: 0.55rem;
  opacity: 0.8;
}

.think-hint {
  opacity: 0.55;
  font-size: 0.62rem;
}

.think-body {
  margin-top: 8px;
  padding: 10px 12px;
  font-size: 13px;
  line-height: 1.6;
  color: rgba(200, 210, 230, 0.85);
  background: rgba(0, 0, 0, 0.25);
  border-radius: 2px;
  border: 1px dashed rgba(123, 47, 255, 0.25);
  max-height: 280px;
  overflow-y: auto;
}

.tool-block {
  margin-bottom: 10px;
  padding: 10px 12px;
  background: rgba(0, 240, 255, 0.05);
  border: 1px solid rgba(0, 240, 255, 0.2);
  border-radius: 2px;
}

.tool-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.tool-icon {
  color: #00f0ff;
  font-size: 12px;
}

.tool-name {
  font-size: 0.75rem;
  color: #00f0ff;
  font-weight: 600;
}

.tool-status {
  margin-left: auto;
  font-size: 0.62rem;
  color: #ffcc00;
}

.tool-status.done {
  color: #00ff88;
}

.tool-args {
  font-size: 0.68rem;
  margin-bottom: 6px;
}

.tool-args .label,
.tool-result .label {
  display: block;
  opacity: 0.5;
  margin-bottom: 4px;
  font-size: 0.62rem;
}

.tool-args code {
  display: block;
  white-space: pre-wrap;
  word-break: break-all;
  color: rgba(180, 220, 255, 0.9);
  background: rgba(0, 0, 0, 0.3);
  padding: 6px 8px;
  border-radius: 2px;
}

.tool-result .result-text {
  font-size: 13px;
  color: rgba(232, 244, 255, 0.9);
  white-space: pre-wrap;
}

.answer-block {
  margin-top: 10px;
  padding: 12px 14px;
  background: rgba(0, 240, 255, 0.06);
  border: 1px solid rgba(0, 240, 255, 0.35);
  border-radius: 2px;
  box-shadow: 0 0 16px rgba(0, 240, 255, 0.08);
}

.answer-label {
  font-size: 0.62rem;
  letter-spacing: 0.15em;
  color: #00f0ff;
  margin-bottom: 8px;
  opacity: 0.85;
}

.answer-body {
  font-size: 15px;
  line-height: 1.65;
  color: rgba(232, 244, 255, 0.98);
}

.answer-body :deep(strong) {
  color: #fff;
  font-weight: 600;
}

.trace-loading {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.5);
  padding: 8px 0;
}

.pulse-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #00f0ff;
  animation: badge-pulse 1s ease-in-out infinite;
}

@keyframes badge-pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.4; transform: scale(0.85); }
}
</style>
