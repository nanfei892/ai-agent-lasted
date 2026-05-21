<template>
  <div class="chat-page love-page">
    <div class="bg-layer bg-aurora love-aurora" aria-hidden="true"></div>
    <div class="bg-layer bg-grid" aria-hidden="true"></div>
    <div class="bg-layer bg-scanlines" aria-hidden="true"></div>
    <div class="bg-layer bg-vignette" aria-hidden="true"></div>

    <header class="page-header">
      <div class="back-button" @click="goBack">
        <span class="back-icon">←</span>
        <span>返回</span>
      </div>
      <div class="header-center">
        <h1 class="title">AI恋爱大师</h1>
        <span class="header-tag mono">EMOTION AI</span>
      </div>
      <div class="header-right mono">
        <span class="status-dot" :class="connectionStatus"></span>
        <span class="chat-id">ID: {{ chatId }}</span>
      </div>
    </header>

    <div class="content-wrapper">
      <div class="chat-area">
        <ChatRoom
          :messages="messages"
          :connection-status="connectionStatus"
          ai-type="love"
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
import { chatWithLoveApp } from '../api'

// 设置页面标题和元数据
useHead({
  title: 'AI恋爱大师 - 男妃AI超级智能体应用平台',
  meta: [
    {
      name: 'description',
      content: 'AI恋爱大师是男妃AI超级智能体应用平台的专业情感顾问，帮你解答各种恋爱问题，提供情感建议'
    },
    {
      name: 'keywords',
      content: 'AI恋爱大师,情感顾问,恋爱咨询,AI聊天,情感问题,AI智能体'
    }
  ]
})

const router = useRouter()
const messages = ref([])
const chatId = ref('')
const connectionStatus = ref('disconnected')
let eventSource = null

// 生成随机会话ID
const generateChatId = () => {
  return 'love_' + Math.random().toString(36).substring(2, 10)
}

// 添加消息到列表
const addMessage = (content, isUser) => {
  messages.value.push({
    content,
    isUser,
    time: new Date().getTime()
  })
}

// 发送消息
const sendMessage = (message) => {
  addMessage(message, true)

  // 连接SSE
  if (eventSource) {
    eventSource.close()
  }

  // 创建一个空的AI回复消息
  const aiMessageIndex = messages.value.length
  addMessage('', false)

  connectionStatus.value = 'connecting'
  eventSource = chatWithLoveApp(message, chatId.value)

  // 监听SSE消息
  eventSource.onmessage = (event) => {
    const data = event.data
    if (data && data !== '[DONE]') {
      // 更新最新的AI消息内容，而不是创建新消息
      if (aiMessageIndex < messages.value.length) {
        messages.value[aiMessageIndex].content += data
      }
    }

    if (data === '[DONE]') {
      connectionStatus.value = 'disconnected'
      eventSource.close()
    }
  }

  // 监听SSE错误
  eventSource.onerror = (error) => {
    console.error('SSE Error:', error)
    connectionStatus.value = 'error'
    eventSource.close()
  }
}

// 返回主页
const goBack = () => {
  router.push('/')
}

// 页面加载时添加欢迎消息
onMounted(() => {
  // 生成聊天ID
  chatId.value = generateChatId()

  // 添加欢迎消息
  addMessage('欢迎来到AI恋爱大师，请告诉我你的恋爱问题，我会尽力给予帮助和建议。', false)
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
  --neon-accent: #ff2d78;
  --neon-accent-soft: #ff6b35;
  --neon-glow: rgba(255, 45, 120, 0.45);
  --cyber-dark: #050810;
  --cyber-panel: rgba(12, 8, 18, 0.85);

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

.love-aurora {
  background:
    radial-gradient(ellipse 70% 50% at 15% 10%, rgba(255, 45, 120, 0.18) 0%, transparent 55%),
    radial-gradient(ellipse 50% 40% at 85% 20%, rgba(255, 107, 53, 0.1) 0%, transparent 50%),
    linear-gradient(180deg, #030508 0%, #0c0610 50%, #050810 100%);
}

.bg-grid {
  background-image:
    linear-gradient(rgba(255, 45, 120, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 45, 120, 0.04) 1px, transparent 1px);
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
  background: rgba(8, 5, 12, 0.75);
  backdrop-filter: blur(14px);
  border-bottom: 1px solid rgba(255, 45, 120, 0.25);
  box-shadow: 0 4px 24px rgba(255, 45, 120, 0.12);
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
  border: 1px solid rgba(255, 45, 120, 0.4);
  border-radius: 2px;
  background: rgba(255, 45, 120, 0.1);
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

.chat-id {
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
  .header-right .chat-id {
    display: none;
  }

  .header-tag {
    display: none;
  }
}
</style>
