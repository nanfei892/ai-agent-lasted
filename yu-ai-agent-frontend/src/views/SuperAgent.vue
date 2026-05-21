<template>
  <div class="chat-page super-page">
    <div class="bg-layer bg-aurora super-aurora" aria-hidden="true"></div>
    <div class="bg-layer bg-grid" aria-hidden="true"></div>
    <div class="bg-layer bg-scanlines" aria-hidden="true"></div>
    <div class="bg-layer bg-vignette" aria-hidden="true"></div>

    <header class="page-header">
      <div class="back-button" @click="goBack">
        <span class="back-icon">←</span>
        <span>返回</span>
      </div>
      <div class="header-center">
        <h1 class="title">AI超级智能体</h1>
        <span class="header-tag mono">AGENT OS</span>
      </div>
      <div class="header-right mono">
        <span class="status-dot" :class="connectionStatus"></span>
        <span class="status-label">{{
          connectionStatus === 'connecting' ? 'PROCESSING' :
          connectionStatus === 'error' ? 'ERROR' : 'READY'
        }}</span>
      </div>
    </header>

    <div class="content-wrapper">
      <div class="chat-area">
        <ChatRoom
          :messages="messages"
          :connection-status="connectionStatus"
          ai-type="super"
          @send-message="sendMessage"
        />
      </div>
    </div>

    <div class="footer-container">
      <AppFooter variant="dark" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useHead } from '@vueuse/head'
import ChatRoom from '../components/ChatRoom.vue'
import AppFooter from '../components/AppFooter.vue'
import { chatWithManus } from '../api'
import { applyAgentEvent } from '../utils/agentStream'

// 设置页面标题和元数据
useHead({
  title: 'AI超级智能体 - 男妃AI超级智能体应用平台',
  meta: [
    {
      name: 'description',
      content: 'AI超级智能体是男妃AI超级智能体应用平台的全能助手，能解答各类专业问题，提供精准建议和解决方案'
    },
    {
      name: 'keywords',
      content: 'AI超级智能体,智能助手,专业问答,AI问答,专业建议,AI智能体'
    }
  ]
})

const router = useRouter()
const messages = ref([])
const connectionStatus = ref('disconnected')
let eventSource = null

const genId = () => `msg-${Date.now()}-${Math.random().toString(36).slice(2, 9)}`

const addMessage = (content, isUser, type = '', extra = {}) => {
  messages.value.push({
    id: genId(),
    content,
    isUser,
    type,
    time: Date.now(),
    ...extra
  })
}

/** 为本轮对话创建一条可增量更新的执行轨迹消息 */
const createTraceMessage = () => {
  addMessage('', false, 'agent-trace', {
    trace: {
      status: 'running',
      steps: [],
      error: null,
      doneReason: null
    }
  })
  return messages.value.length - 1
}

const finishStream = (traceIndex, streamCompletedRef) => {
  streamCompletedRef.value = true
  const trace = messages.value[traceIndex]?.trace
  if (trace && trace.status === 'running') {
    trace.status = 'done'
  }
  connectionStatus.value = 'disconnected'
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
}

// 发送消息
const sendMessage = (message) => {
  addMessage(message, true, 'user-question')

  if (eventSource) {
    eventSource.close()
    eventSource = null
  }

  connectionStatus.value = 'connecting'
  const traceIndex = createTraceMessage()
  const streamCompleted = { value: false }

  eventSource = chatWithManus(message)

  eventSource.onmessage = (event) => {
    const data = event.data

    if (data === '[DONE]') {
      finishStream(traceIndex, streamCompleted)
      return
    }

    if (!data) return

    try {
      const evt = JSON.parse(data)
      const trace = messages.value[traceIndex]?.trace
      if (trace) {
        applyAgentEvent(trace, evt)
      }
    } catch {
      console.warn('非 JSON SSE 数据，已忽略:', data)
    }
  }

  eventSource.onerror = () => {
    // 正常收到 [DONE] 后关闭连接也会触发 onerror，不应标红
    if (streamCompleted.value) {
      connectionStatus.value = 'disconnected'
      return
    }

    const trace = messages.value[traceIndex]?.trace
    if (trace?.status === 'done') {
      connectionStatus.value = 'disconnected'
      if (eventSource) {
        eventSource.close()
        eventSource = null
      }
      return
    }

    console.error('SSE 连接异常')
    if (trace) {
      trace.status = 'error'
      trace.error = trace.error || '连接中断，请重试'
    }
    connectionStatus.value = 'error'
    if (eventSource) {
      eventSource.close()
      eventSource = null
    }
  }
}

// 返回主页
const goBack = () => {
  router.push('/')
}

// 页面加载时添加欢迎消息
onMounted(() => {
  // 添加欢迎消息
  addMessage(
    '你好，我是AI超级智能体。我可以解答各类问题，提供专业建议，请问有什么可以帮助你的吗？',
    false,
    'ai-welcome'
  )
})

// 组件销毁前关闭SSE连接
onBeforeUnmount(() => {
  if (eventSource) {
    eventSource.close()
  }
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Orbitron:wght@500;700&family=JetBrains+Mono:wght@400;500&display=swap');

.chat-page {
  --neon-accent: #00f0ff;
  --neon-accent-soft: #7b2fff;
  --neon-glow: rgba(0, 240, 255, 0.45);
  --cyber-dark: #050810;

  display: flex;
  flex-direction: column;
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
  color: #e8f4ff;
}

.mono {
  font-family: 'JetBrains Mono', 'Consolas', monospace;
}

.bg-layer {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.super-aurora {
  background:
    radial-gradient(ellipse 70% 50% at 20% 15%, rgba(0, 240, 255, 0.14) 0%, transparent 55%),
    radial-gradient(ellipse 50% 40% at 80% 25%, rgba(123, 47, 255, 0.12) 0%, transparent 50%),
    linear-gradient(180deg, #030508 0%, #060a14 50%, #050810 100%);
}

.bg-grid {
  background-image:
    linear-gradient(rgba(0, 240, 255, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 240, 255, 0.04) 1px, transparent 1px);
  background-size: 40px 40px;
  mask-image: radial-gradient(ellipse 90% 80% at 50% 30%, black 10%, transparent 70%);
}

.bg-scanlines {
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 2px,
    rgba(0, 0, 0, 0.12) 2px,
    rgba(0, 0, 0, 0.12) 4px
  );
  opacity: 0.35;
}

.bg-vignette {
  background: radial-gradient(ellipse at center, transparent 35%, rgba(0, 0, 0, 0.55) 100%);
}

.page-header {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  padding: 14px 24px;
  background: rgba(5, 8, 16, 0.75);
  backdrop-filter: blur(14px);
  border-bottom: 1px solid rgba(0, 240, 255, 0.2);
  box-shadow: 0 4px 24px rgba(0, 240, 255, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
}

.back-button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.75);
  cursor: pointer;
  transition: color 0.2s, text-shadow 0.2s;
  justify-self: start;
}

.back-button:hover {
  color: var(--neon-accent);
  text-shadow: 0 0 12px var(--neon-glow);
}

.back-icon {
  font-size: 18px;
}

.header-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  justify-self: center;
}

.title {
  font-family: 'Orbitron', sans-serif;
  font-size: 1.15rem;
  font-weight: 700;
  margin: 0;
  letter-spacing: 0.08em;
  text-shadow: 0 0 16px var(--neon-glow);
}

.header-tag {
  font-size: 0.6rem;
  letter-spacing: 0.2em;
  color: var(--neon-accent);
  padding: 2px 8px;
  border: 1px solid rgba(0, 240, 255, 0.35);
  border-radius: 2px;
  background: rgba(0, 240, 255, 0.08);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-self: end;
  font-size: 0.72rem;
  color: rgba(255, 255, 255, 0.5);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #00ff88;
  box-shadow: 0 0 8px #00ff88;
}

.status-dot.connecting {
  background: #ffcc00;
  box-shadow: 0 0 10px #ffcc00;
  animation: pulse 1s ease-in-out infinite;
}

.status-dot.error {
  background: #ff4444;
  box-shadow: 0 0 10px #ff4444;
}

.status-dot.disconnected {
  background: rgba(255, 255, 255, 0.3);
  box-shadow: none;
}

.content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 2;
}

.chat-area {
  flex: 1;
  padding: 20px 24px;
  max-width: 960px;
  width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
  min-height: calc(100vh - 130px - 200px);
}

.footer-container {
  margin-top: auto;
  position: relative;
  z-index: 2;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

@media (max-width: 768px) {
  .page-header {
    padding: 12px 16px;
  }

  .title {
    font-size: 1rem;
  }

  .chat-area {
    padding: 12px 16px;
  }
}

@media (max-width: 480px) {
  .header-tag {
    display: none;
  }

  .status-label {
    display: none;
  }
}
</style>
