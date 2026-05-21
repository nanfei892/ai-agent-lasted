<template>
  <div class="chat-container" :class="'theme-' + aiType">
    <div class="chat-frame-corner tl" aria-hidden="true"></div>
    <div class="chat-frame-corner tr" aria-hidden="true"></div>
    <div class="chat-frame-corner bl" aria-hidden="true"></div>
    <div class="chat-frame-corner br" aria-hidden="true"></div>

    <!-- 聊天记录区域 -->
    <div class="chat-messages" ref="messagesContainer">
      <div
        v-for="(msg, index) in messages"
        :key="msg.id || index"
        class="message-wrapper"
      >
        <!-- 超级智能体：结构化执行轨迹 -->
        <div
          v-if="!msg.isUser && msg.type === 'agent-trace'"
          class="message ai-message agent-trace-message"
        >
          <div class="avatar ai-avatar">
            <AiAvatarFallback :type="aiType" />
          </div>
          <div class="message-bubble trace-bubble">
            <AgentTraceCard
              :trace="msg.trace"
              :is-streaming="connectionStatus === 'connecting' && index === messages.length - 1"
            />
            <div class="message-time mono">{{ formatTime(msg.time) }}</div>
          </div>
        </div>

        <!-- 普通 AI 消息 -->
        <div
          v-else-if="!msg.isUser"
          class="message ai-message"
          :class="[msg.type]"
        >
          <div class="avatar ai-avatar">
            <AiAvatarFallback :type="aiType" />
          </div>
          <div class="message-bubble">
            <div class="message-content">
              {{ msg.content }}
              <span
                v-if="connectionStatus === 'connecting' && index === messages.length - 1"
                class="typing-indicator"
              >▋</span>
            </div>
            <div class="message-time mono">{{ formatTime(msg.time) }}</div>
          </div>
        </div>

        <!-- 用户消息 -->
        <div v-else class="message user-message" :class="[msg.type]">
          <div class="message-bubble">
            <div class="message-content">{{ msg.content }}</div>
            <div class="message-time mono">{{ formatTime(msg.time) }}</div>
          </div>
          <div class="avatar user-avatar">
            <div class="avatar-placeholder">我</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-input-container">
      <div
        v-if="connectionStatus === 'connecting'"
        class="stream-status mono"
      >
        <span class="stream-dot"></span> STREAMING...
      </div>
      <div class="chat-input">
        <textarea
          v-model="inputMessage"
          @keydown.enter.prevent="sendMessage"
          :placeholder="aiType === 'love' ? '> 倾诉你的情感困惑...' : '> 输入指令，唤醒智能体...'"
          class="input-box mono"
          :disabled="connectionStatus === 'connecting'"
        ></textarea>
        <button
          @click="sendMessage"
          class="send-button"
          :disabled="connectionStatus === 'connecting' || !inputMessage.trim()"
        >
          <span class="send-text">发送</span>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="send-icon">
            <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z"/>
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch, computed } from 'vue'
import AiAvatarFallback from './AiAvatarFallback.vue'
import AgentTraceCard from './AgentTraceCard.vue'

const props = defineProps({
  messages: {
    type: Array,
    default: () => []
  },
  connectionStatus: {
    type: String,
    default: 'disconnected'
  },
  aiType: {
    type: String,
    default: 'default'  // 'love' 或 'super'
  }
})

const emit = defineEmits(['send-message'])

const inputMessage = ref('')
const messagesContainer = ref(null)

// 根据AI类型选择不同头像
const aiAvatar = computed(() => {
  return props.aiType === 'love'
    ? '/ai-love-avatar.png'  // 恋爱大师头像
    : '/ai-super-avatar.png' // 超级智能体头像
})

// 发送消息
const sendMessage = () => {
  if (!inputMessage.value.trim()) return

  emit('send-message', inputMessage.value)
  inputMessage.value = ''
}

// 格式化时间
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

// 自动滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 监听消息变化与内容变化，自动滚动
watch(() => props.messages.length, () => {
  scrollToBottom()
})

watch(() => props.messages.map(m => m.content).join(''), () => {
  scrollToBottom()
})

watch(
  () => props.messages.map(m => (m.trace ? JSON.stringify(m.trace) : '')).join(''),
  () => scrollToBottom()
)

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Orbitron:wght@500;700&family=JetBrains+Mono:wght@400;500&display=swap');

.mono {
  font-family: 'JetBrains Mono', 'Consolas', monospace;
}

/* ========== 主题变量 ========== */
.chat-container {
  --accent: #00f0ff;
  --accent-soft: #7b2fff;
  --accent-glow: rgba(0, 240, 255, 0.4);
  --panel-bg: rgba(8, 14, 28, 0.88);
  --bubble-ai: rgba(12, 20, 40, 0.95);
  --bubble-user: linear-gradient(135deg, #0088ff, #7b2fff);
  --input-bg: rgba(5, 10, 22, 0.9);
  --border-color: rgba(0, 240, 255, 0.2);

  display: flex;
  flex-direction: column;
  height: calc(100vh - 220px);
  min-height: 480px;
  max-height: 720px;
  background: var(--panel-bg);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  overflow: hidden;
  position: relative;
  backdrop-filter: blur(12px);
  box-shadow:
    0 0 40px rgba(0, 0, 0, 0.4),
    inset 0 0 60px rgba(0, 240, 255, 0.03);
}

.chat-container.theme-love {
  --accent: #ff2d78;
  --accent-soft: #ff6b35;
  --accent-glow: rgba(255, 45, 120, 0.45);
  --bubble-user: linear-gradient(135deg, #ff2d78, #ff5722);
  --border-color: rgba(255, 45, 120, 0.25);
  box-shadow:
    0 0 40px rgba(255, 45, 120, 0.12),
    inset 0 0 60px rgba(255, 45, 120, 0.03);
}

/* 四角装饰 */
.chat-frame-corner {
  position: absolute;
  width: 14px;
  height: 14px;
  z-index: 5;
  pointer-events: none;
}

.chat-frame-corner.tl { top: 6px; left: 6px; border-top: 2px solid var(--accent); border-left: 2px solid var(--accent); }
.chat-frame-corner.tr { top: 6px; right: 6px; border-top: 2px solid var(--accent); border-right: 2px solid var(--accent); }
.chat-frame-corner.bl { bottom: 6px; left: 6px; border-bottom: 2px solid var(--accent); border-left: 2px solid var(--accent); }
.chat-frame-corner.br { bottom: 6px; right: 6px; border-bottom: 2px solid var(--accent); border-right: 2px solid var(--accent); }

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px 18px;
  padding-bottom: 88px;
  display: flex;
  flex-direction: column;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 80px;
  scrollbar-width: thin;
  scrollbar-color: var(--accent) transparent;
}

.chat-messages::-webkit-scrollbar {
  width: 4px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: var(--accent);
  border-radius: 2px;
  opacity: 0.5;
}

.message-wrapper {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  width: 100%;
  animation: msg-in 0.35s ease-out;
}

.message {
  display: flex;
  align-items: flex-start;
  max-width: 88%;
  margin-bottom: 6px;
}

.user-message {
  margin-left: auto;
  flex-direction: row;
}

.ai-message {
  margin-right: auto;
}

.avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-avatar {
  margin-left: 10px;
}

.ai-avatar {
  margin-right: 10px;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.15), rgba(255, 255, 255, 0.05));
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: var(--accent);
  font-weight: bold;
  font-size: 14px;
  font-family: 'Orbitron', sans-serif;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 4px;
  position: relative;
  word-wrap: break-word;
  min-width: 80px;
}

.user-message .message-bubble {
  background: var(--bubble-user);
  color: #fff;
  border-bottom-right-radius: 0;
  box-shadow: 0 4px 16px var(--accent-glow);
}

.ai-message .message-bubble {
  background: var(--bubble-ai);
  color: rgba(232, 244, 255, 0.95);
  border: 1px solid var(--border-color);
  border-bottom-left-radius: 0;
  box-shadow: inset 0 0 20px rgba(0, 0, 0, 0.2);
}

.ai-message.ai-final .message-bubble {
  border-color: var(--accent);
  box-shadow: 0 0 16px var(--accent-glow);
}

.ai-message.ai-error .message-bubble {
  border-color: rgba(255, 68, 68, 0.5);
  opacity: 0.85;
}

.message-content {
  font-size: 15px;
  line-height: 1.65;
  white-space: pre-wrap;
}

.message-time {
  font-size: 0.65rem;
  opacity: 0.45;
  margin-top: 6px;
  text-align: right;
  letter-spacing: 0.05em;
}

.chat-input-container {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--input-bg);
  border-top: 1px solid var(--border-color);
  z-index: 100;
  min-height: 80px;
  box-shadow: 0 -8px 24px rgba(0, 0, 0, 0.3);
}

.stream-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px 0;
  font-size: 0.65rem;
  letter-spacing: 0.15em;
  color: var(--accent);
}

.stream-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--accent);
  animation: pulse 1s ease-in-out infinite;
}

.chat-input {
  display: flex;
  padding: 12px 16px 16px;
  gap: 12px;
  align-items: flex-end;
}

.input-box {
  flex: 1;
  border: 1px solid var(--border-color);
  border-radius: 2px;
  padding: 12px 14px;
  font-size: 14px;
  resize: none;
  min-height: 44px;
  max-height: 80px;
  outline: none;
  transition: border-color 0.3s, box-shadow 0.3s;
  overflow-y: auto;
  background: rgba(0, 0, 0, 0.35);
  color: #e8f4ff;
  scrollbar-width: none;
}

.input-box::placeholder {
  color: rgba(255, 255, 255, 0.3);
}

.input-box:focus {
  border-color: var(--accent);
  box-shadow: 0 0 16px var(--accent-glow);
}

.send-button {
  display: flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, var(--accent), var(--accent-soft));
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 2px;
  padding: 0 18px;
  font-family: 'Orbitron', sans-serif;
  font-size: 0.8rem;
  letter-spacing: 0.08em;
  cursor: pointer;
  transition: box-shadow 0.3s, transform 0.2s;
  height: 44px;
  flex-shrink: 0;
}

.send-button:hover:not(:disabled) {
  box-shadow: 0 0 20px var(--accent-glow);
  transform: translateY(-1px);
}

.send-icon {
  width: 16px;
  height: 16px;
  display: none;
}

.typing-indicator {
  display: inline-block;
  color: var(--accent);
  animation: blink 0.7s infinite;
  margin-left: 2px;
  text-shadow: 0 0 8px var(--accent);
}

.input-box:disabled,
.send-button:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

/* 连续 AI 气泡 */
.ai-message + .ai-message {
  margin-top: 2px;
}

.ai-message + .ai-message .avatar {
  visibility: hidden;
}

.ai-message + .ai-message .message-bubble {
  border-top-left-radius: 2px;
}

.ai-answer {
  animation: fadeIn 0.3s ease-in-out;
}

/* 超级智能体执行轨迹：拉宽气泡 */
.agent-trace-message {
  max-width: 96%;
}

.agent-trace-message .trace-bubble {
  padding: 10px 12px;
  min-width: min(100%, 520px);
  max-width: 100%;
}

.agent-trace-message + .ai-message .avatar,
.agent-trace-message + .agent-trace-message .avatar {
  visibility: hidden;
}

@keyframes blink {
  0% { opacity: 0; }
  50% { opacity: 1; }
  100% { opacity: 0; }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

@keyframes msg-in {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@media (max-width: 768px) {
  .chat-container {
    height: calc(100vh - 200px);
    min-height: 420px;
  }

  .message {
    max-width: 92%;
  }

  .send-text {
    display: none;
  }

  .send-icon {
    display: block;
  }

  .send-button {
    padding: 0 14px;
  }
}

@media (max-width: 480px) {
  .avatar {
    width: 32px;
    height: 32px;
  }

  .message-content {
    font-size: 14px;
  }

  .chat-messages {
    bottom: 76px;
    padding: 14px 12px;
  }
}
</style>
