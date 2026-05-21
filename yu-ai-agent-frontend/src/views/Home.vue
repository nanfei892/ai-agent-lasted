<template>
  <div class="home-container">
    <!-- 多层背景 -->
    <div class="bg-layer bg-aurora" aria-hidden="true"></div>
    <div class="bg-layer bg-grid" aria-hidden="true"></div>
    <div class="bg-layer bg-particles" aria-hidden="true">
      <span
        v-for="p in particles"
        :key="p.id"
        class="particle"
        :style="{
          left: p.x + '%',
          top: p.y + '%',
          width: p.size + 'px',
          height: p.size + 'px',
          animationDuration: p.duration + 's',
          animationDelay: p.delay + 's',
          opacity: p.opacity
        }"
      ></span>
    </div>
    <div class="bg-layer bg-scanlines" aria-hidden="true"></div>
    <div class="bg-layer bg-vignette" aria-hidden="true"></div>

    <!-- 顶部状态栏 -->
    <header class="top-bar">
      <div class="status-left">
        <span class="status-dot pulse"></span>
        <span class="status-text mono">SYSTEM ONLINE</span>
      </div>
      <div class="status-center mono">
        <span class="blink">▸</span> NEURAL INTERFACE v3.0
      </div>
      <div class="status-right mono">
        <span class="time-label">{{ currentTime }}</span>
      </div>
    </header>

    <!-- Hero -->
    <section class="hero">
      <div class="hero-badge mono fade-in" style="--delay: 0.1s">
        <svg class="badge-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
        </svg>
        NEXT-GEN AI PLATFORM
      </div>

      <div class="glitch-wrapper fade-in" style="--delay: 0.2s">
        <h1 class="glitch-title" data-text="男妃AI超级智能体">AI超级智能体---男妃</h1>
      </div>

      <p class="typing-line mono fade-in" style="--delay: 0.35s">
        <span class="typing-prefix">&gt;</span>
        <span class="typing-text">{{ typedText }}</span>
        <span class="typing-cursor">|</span>
      </p>

      <div class="stats-row fade-in" style="--delay: 0.45s">
        <div v-for="stat in stats" :key="stat.label" class="stat-chip">
          <span class="stat-value mono">{{ stat.value }}</span>
          <span class="stat-label">{{ stat.label }}</span>
        </div>
      </div>

      <div class="cyber-line fade-in" style="--delay: 0.55s">
        <span class="line-node"></span>
        <span class="line-beam"></span>
        <span class="line-node"></span>
      </div>
    </section>

    <!-- 应用卡片 -->
    <section class="apps-container">
      <article
        class="app-card love-card fade-in-up"
        style="--delay: 0.6s"
        @click="navigateTo('/love-master')"
      >
        <div class="card-border-glow love-glow"></div>
        <div class="card-corner tl"></div>
        <div class="card-corner tr"></div>
        <div class="card-corner bl"></div>
        <div class="card-corner br"></div>
        <div class="card-scan"></div>

        <div class="card-header">
          <div class="app-icon love-icon">
            <svg viewBox="0 0 64 64" class="icon-svg">
              <defs>
                <linearGradient id="loveGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stop-color="#ff2d78"/>
                  <stop offset="100%" stop-color="#ff6b35"/>
                </linearGradient>
              </defs>
              <path fill="url(#loveGrad)" d="M32 54s-18-12-18-26c0-7 5.5-12 12-12 4 0 7.5 2 9 5 1.5-3 5-5 9-5 6.5 0 12 5 12 12 0 14-18 26-18 26z"/>
              <circle cx="32" cy="28" r="3" fill="rgba(255,255,255,0.6)"/>
            </svg>
            <span class="icon-ring"></span>
          </div>
          <span class="card-tag mono love-tag">EMOTION AI</span>
        </div>

        <div class="app-info">
          <h2 class="app-title">AI恋爱大师</h2>
          <p class="app-desc">深度情感神经网络 · 恋爱/已婚/单身全场景咨询 · 实时流式对话</p>
          <ul class="feature-list">
            <li><span class="feat-dot love-dot"></span>RAG 知识库增强</li>
            <li><span class="feat-dot love-dot"></span>多轮记忆上下文</li>
          </ul>
        </div>

        <div class="app-button love-btn">
          <span class="btn-text">启动恋爱大师</span>
          <span class="btn-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14M13 6l6 6-6 6"/>
            </svg>
          </span>
        </div>
      </article>

      <article
        class="app-card super-card fade-in-up"
        style="--delay: 0.75s"
        @click="navigateTo('/super-agent')"
      >
        <div class="card-border-glow super-glow"></div>
        <div class="card-corner tl"></div>
        <div class="card-corner tr"></div>
        <div class="card-corner bl"></div>
        <div class="card-corner br"></div>
        <div class="card-scan"></div>

        <div class="card-header">
          <div class="app-icon robot-icon">
            <svg viewBox="0 0 64 64" class="icon-svg">
              <defs>
                <linearGradient id="robotGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stop-color="#00d4ff"/>
                  <stop offset="100%" stop-color="#7b2fff"/>
                </linearGradient>
              </defs>
              <rect x="14" y="20" width="36" height="28" rx="6" fill="url(#robotGrad)"/>
              <rect x="22" y="10" width="20" height="14" rx="4" fill="url(#robotGrad)" opacity="0.85"/>
              <circle cx="24" cy="32" r="4" fill="#0a0e1a"/>
              <circle cx="40" cy="32" r="4" fill="#0a0e1a"/>
              <circle cx="25" cy="31" r="1.5" fill="#00f0ff"/>
              <circle cx="41" cy="31" r="1.5" fill="#00f0ff"/>
              <rect x="26" y="40" width="12" height="3" rx="1.5" fill="rgba(255,255,255,0.5)"/>
              <line x1="32" y1="48" x2="32" y2="56" stroke="url(#robotGrad)" stroke-width="3"/>
              <line x1="22" y1="56" x2="42" y2="56" stroke="url(#robotGrad)" stroke-width="3" stroke-linecap="round"/>
            </svg>
            <span class="icon-ring"></span>
          </div>
          <span class="card-tag mono super-tag">AGENT OS</span>
        </div>

        <div class="app-info">
          <h2 class="app-title">AI超级智能体</h2>
          <p class="app-desc">多工具链自主推理 · 网页搜索/文件/终端/PDF · 复杂任务分步执行</p>
          <ul class="feature-list">
            <li><span class="feat-dot super-dot"></span>20步 Agent 循环</li>
            <li><span class="feat-dot super-dot"></span>MCP 工具生态接入</li>
          </ul>
        </div>

        <div class="app-button super-btn">
          <span class="btn-text">唤醒超级智能体</span>
          <span class="btn-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14M13 6l6 6-6 6"/>
            </svg>
          </span>
        </div>
      </article>
    </section>

    <!-- 底部滚动科技条 -->
    <div class="tech-marquee fade-in" style="--delay: 0.9s" aria-hidden="true">
      <div class="marquee-track">
        <span v-for="(item, i) in marqueeItems" :key="i" class="marquee-item mono">{{ item }}</span>
        <span v-for="(item, i) in marqueeItems" :key="'dup-' + i" class="marquee-item mono">{{ item }}</span>
      </div>
    </div>

    <AppFooter variant="dark" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useHead } from '@vueuse/head'
import AppFooter from '../components/AppFooter.vue'

useHead({
  title: '男妃AI超级智能体应用平台 - 首页',
  meta: [
    {
      name: 'description',
      content: '男妃AI超级智能体应用平台提供AI恋爱大师和AI超级智能体服务，满足您的各种AI对话需求'
    },
    {
      name: 'keywords',
      content: 'AI智能体,AI应用,AI恋爱大师,AI助手,智能对话,AI超级智能体,首页'
    }
  ]
})

const router = useRouter()
const typedText = ref('')
const currentTime = ref('')

const fullTypedText = '探索 AI 无限可能 · 恋爱情感咨询 · 全能智能体协作'
const stats = [
  { value: '2', label: '核心 AI 应用' },
  { value: 'SSE', label: '实时流式响应' },
  { value: 'RAG+Agent', label: '双引擎架构' }
]

const marqueeItems = [
  '// NEURAL_NET: ACTIVE',
  '>> LOADING KNOWLEDGE BASE',
  '>> AGENT_TOOLS: ENABLED',
  '>> STREAM_PROTOCOL: SSE',
  '>> MODEL: DASHSCOPE',
  '>> STATUS: READY'
]

// 粒子配置（纯 CSS 动画，无 canvas 依赖）
const particles = Array.from({ length: 48 }, (_, i) => ({
  id: i,
  x: Math.random() * 100,
  y: Math.random() * 100,
  size: Math.random() * 2 + 1,
  duration: Math.random() * 4 + 3,
  delay: Math.random() * 5,
  opacity: Math.random() * 0.5 + 0.2
}))

let typeIndex = 0
let typeTimer = null
let clockTimer = null
let typingForward = true

const navigateTo = (path) => {
  router.push(path)
}

const updateClock = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour12: false })
}

const runTypewriter = () => {
  if (typingForward) {
    if (typeIndex <= fullTypedText.length) {
      typedText.value = fullTypedText.slice(0, typeIndex)
      typeIndex++
      typeTimer = setTimeout(runTypewriter, 80)
    } else {
      typingForward = false
      typeTimer = setTimeout(runTypewriter, 2200)
    }
  } else {
    if (typeIndex > 0) {
      typeIndex--
      typedText.value = fullTypedText.slice(0, typeIndex)
      typeTimer = setTimeout(runTypewriter, 40)
    } else {
      typingForward = true
      typeTimer = setTimeout(runTypewriter, 600)
    }
  }
}

onMounted(() => {
  updateClock()
  clockTimer = setInterval(updateClock, 1000)
  typeTimer = setTimeout(runTypewriter, 800)
})

onUnmounted(() => {
  if (typeTimer) clearTimeout(typeTimer)
  if (clockTimer) clearInterval(clockTimer)
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Orbitron:wght@400;500;700;900&family=JetBrains+Mono:wght@400;500&display=swap');

.home-container {
  --neon-blue: #00f0ff;
  --neon-purple: #a855f7;
  --neon-pink: #ff2d78;
  --neon-cyan: #22d3ee;
  --cyber-dark: #050810;
  --cyber-panel: rgba(8, 14, 32, 0.75);
  --cyber-light: #e8f4ff;
  --glass-border: rgba(0, 240, 255, 0.25);

  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: var(--cyber-dark);
  position: relative;
  overflow-x: hidden;
  color: var(--cyber-light);
}

.mono {
  font-family: 'JetBrains Mono', 'Consolas', monospace;
}

/* ========== 背景层 ========== */
.bg-layer {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.bg-aurora {
  background:
    radial-gradient(ellipse 80% 50% at 20% 20%, rgba(0, 240, 255, 0.12) 0%, transparent 50%),
    radial-gradient(ellipse 60% 40% at 80% 30%, rgba(168, 85, 247, 0.15) 0%, transparent 50%),
    radial-gradient(ellipse 70% 50% at 50% 90%, rgba(255, 45, 120, 0.08) 0%, transparent 50%),
    linear-gradient(180deg, #030508 0%, #0a0f1e 40%, #050810 100%);
  animation: aurora-shift 12s ease-in-out infinite alternate;
}

.bg-grid {
  background-image:
    linear-gradient(rgba(0, 240, 255, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 240, 255, 0.04) 1px, transparent 1px);
  background-size: 48px 48px;
  mask-image: radial-gradient(ellipse 80% 70% at 50% 40%, black 20%, transparent 75%);
}

.bg-particles .particle {
  position: absolute;
  border-radius: 50%;
  background: var(--neon-cyan);
  box-shadow: 0 0 6px var(--neon-cyan);
  animation: particle-float linear infinite;
}

.bg-scanlines {
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 2px,
    rgba(0, 0, 0, 0.15) 2px,
    rgba(0, 0, 0, 0.15) 4px
  );
  opacity: 0.4;
}

.bg-vignette {
  background: radial-gradient(ellipse at center, transparent 40%, rgba(0, 0, 0, 0.6) 100%);
}

/* ========== 顶栏 ========== */
.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 28px;
  border-bottom: 1px solid rgba(0, 240, 255, 0.12);
  background: rgba(5, 8, 16, 0.6);
  backdrop-filter: blur(12px);
  position: relative;
  z-index: 10;
}

.status-left,
.status-center,
.status-right {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.72rem;
  letter-spacing: 0.12em;
  color: rgba(0, 240, 255, 0.85);
}

.status-center {
  color: rgba(255, 255, 255, 0.5);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #00ff88;
  box-shadow: 0 0 8px #00ff88;
}

.pulse {
  animation: pulse-glow 2s ease-in-out infinite;
}

.blink {
  animation: blink 1s step-end infinite;
}

.time-label {
  color: var(--neon-cyan);
}

/* ========== Hero ========== */
.hero {
  text-align: center;
  padding: 48px 20px 32px;
  position: relative;
  z-index: 2;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  border: 1px solid rgba(0, 240, 255, 0.35);
  border-radius: 4px;
  font-size: 0.7rem;
  letter-spacing: 0.2em;
  color: var(--neon-cyan);
  background: rgba(0, 240, 255, 0.06);
  margin-bottom: 28px;
  box-shadow: 0 0 20px rgba(0, 240, 255, 0.15), inset 0 0 20px rgba(0, 240, 255, 0.05);
}

.badge-icon {
  width: 16px;
  height: 16px;
  color: var(--neon-cyan);
}

.glitch-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 20px;
}

.glitch-title {
  font-family: 'Orbitron', sans-serif;
  font-size: clamp(2rem, 6vw, 3.6rem);
  font-weight: 900;
  color: #fff;
  letter-spacing: 0.08em;
  position: relative;
  text-shadow:
    0 0 10px rgba(0, 240, 255, 0.8),
    0 0 30px rgba(0, 240, 255, 0.4),
    0 0 60px rgba(168, 85, 247, 0.3);
  animation: title-glow 4s ease-in-out infinite;
}

.glitch-title::before,
.glitch-title::after {
  content: attr(data-text);
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: transparent;
}

.glitch-title::before {
  color: var(--neon-pink);
  z-index: -1;
  clip-path: polygon(0 0, 100% 0, 100% 45%, 0 45%);
  animation: glitch-top 3s infinite linear alternate-reverse;
}

.glitch-title::after {
  color: var(--neon-blue);
  z-index: -2;
  clip-path: polygon(0 55%, 100% 55%, 100% 100%, 0 100%);
  animation: glitch-bottom 2.5s infinite linear alternate-reverse;
}

.typing-line {
  font-size: clamp(0.85rem, 2.5vw, 1rem);
  color: rgba(0, 240, 255, 0.9);
  margin-bottom: 28px;
  min-height: 1.6em;
  letter-spacing: 0.05em;
}

.typing-prefix {
  color: var(--neon-purple);
  margin-right: 8px;
}

.typing-cursor {
  color: var(--neon-cyan);
  animation: blink 0.8s step-end infinite;
  margin-left: 2px;
}

.stats-row {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 16px;
  margin-bottom: 28px;
}

.stat-chip {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 24px;
  background: var(--cyber-panel);
  border: 1px solid var(--glass-border);
  border-radius: 8px;
  backdrop-filter: blur(8px);
  min-width: 120px;
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-chip:hover {
  transform: translateY(-3px);
  box-shadow: 0 0 24px rgba(0, 240, 255, 0.2);
}

.stat-value {
  font-size: 1.4rem;
  font-weight: 700;
  color: var(--neon-cyan);
  text-shadow: 0 0 12px rgba(0, 240, 255, 0.6);
}

.stat-label {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.55);
  margin-top: 4px;
  letter-spacing: 0.05em;
}

.cyber-line {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0;
  width: min(90%, 640px);
  margin: 0 auto;
  height: 2px;
  position: relative;
}

.line-beam {
  flex: 1;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--neon-blue), var(--neon-purple), var(--neon-blue), transparent);
  background-size: 200% 100%;
  animation: beam-flow 3s linear infinite;
}

.line-node {
  width: 10px;
  height: 10px;
  border: 2px solid var(--neon-cyan);
  transform: rotate(45deg);
  box-shadow: 0 0 12px var(--neon-cyan);
  flex-shrink: 0;
}

/* ========== 卡片 ========== */
.apps-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 40px;
  max-width: 1100px;
  margin: 20px auto 40px;
  padding: 0 24px;
  flex: 1;
  position: relative;
  z-index: 2;
}

.app-card {
  width: min(100%, 380px);
  padding: 32px 28px 28px;
  background: var(--cyber-panel);
  backdrop-filter: blur(16px);
  border-radius: 4px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: transform 0.45s cubic-bezier(0.23, 1, 0.32, 1), box-shadow 0.45s;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.app-card:hover {
  transform: translateY(-12px) scale(1.02);
}

.love-card:hover {
  box-shadow:
    0 24px 60px rgba(255, 45, 120, 0.25),
    0 0 0 1px rgba(255, 45, 120, 0.4),
    inset 0 0 40px rgba(255, 45, 120, 0.05);
}

.super-card:hover {
  box-shadow:
    0 24px 60px rgba(0, 212, 255, 0.25),
    0 0 0 1px rgba(0, 240, 255, 0.4),
    inset 0 0 40px rgba(0, 240, 255, 0.05);
}

.card-border-glow {
  position: absolute;
  inset: -1px;
  border-radius: 4px;
  opacity: 0;
  transition: opacity 0.4s;
  pointer-events: none;
  z-index: 0;
}

.app-card:hover .card-border-glow {
  opacity: 1;
}

.love-glow {
  background: linear-gradient(135deg, rgba(255, 45, 120, 0.3), transparent 50%, rgba(255, 107, 53, 0.2));
}

.super-glow {
  background: linear-gradient(135deg, rgba(0, 240, 255, 0.3), transparent 50%, rgba(123, 47, 255, 0.2));
}

.card-corner {
  position: absolute;
  width: 16px;
  height: 16px;
  z-index: 2;
  opacity: 0.7;
  transition: opacity 0.3s, width 0.3s, height 0.3s;
}

.app-card:hover .card-corner {
  opacity: 1;
  width: 22px;
  height: 22px;
}

.card-corner.tl { top: 8px; left: 8px; border-top: 2px solid var(--neon-cyan); border-left: 2px solid var(--neon-cyan); }
.card-corner.tr { top: 8px; right: 8px; border-top: 2px solid var(--neon-cyan); border-right: 2px solid var(--neon-cyan); }
.card-corner.bl { bottom: 8px; left: 8px; border-bottom: 2px solid var(--neon-cyan); border-left: 2px solid var(--neon-cyan); }
.love-card .card-corner { border-color: var(--neon-pink); }
.super-card .card-corner { border-color: var(--neon-blue); }

.card-scan {
  position: absolute;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(0, 240, 255, 0.8), transparent);
  top: -2px;
  animation: card-scan 4s ease-in-out infinite;
  pointer-events: none;
  z-index: 3;
}

.love-card .card-scan {
  background: linear-gradient(90deg, transparent, rgba(255, 45, 120, 0.8), transparent);
}

.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
}

.app-icon {
  width: 72px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.icon-svg {
  width: 48px;
  height: 48px;
  position: relative;
  z-index: 1;
  filter: drop-shadow(0 0 8px rgba(0, 240, 255, 0.4));
}

.icon-ring {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  border: 1px dashed rgba(0, 240, 255, 0.4);
  animation: ring-spin 8s linear infinite;
}

.love-icon .icon-ring {
  border-color: rgba(255, 45, 120, 0.5);
  animation-direction: reverse;
}

.card-tag {
  font-size: 0.65rem;
  letter-spacing: 0.15em;
  padding: 4px 10px;
  border-radius: 2px;
}

.love-tag {
  color: var(--neon-pink);
  border: 1px solid rgba(255, 45, 120, 0.4);
  background: rgba(255, 45, 120, 0.1);
}

.super-tag {
  color: var(--neon-cyan);
  border: 1px solid rgba(0, 240, 255, 0.4);
  background: rgba(0, 240, 255, 0.08);
}

.app-info {
  flex: 1;
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
}

.app-title {
  font-family: 'Orbitron', sans-serif;
  font-size: 1.5rem;
  font-weight: 700;
  color: #fff;
  margin-bottom: 12px;
  letter-spacing: 0.05em;
}

.app-desc {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.7;
  margin-bottom: 16px;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-list li {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 6px;
  font-family: 'JetBrains Mono', monospace;
}

.feat-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}

.love-dot { background: var(--neon-pink); box-shadow: 0 0 6px var(--neon-pink); }
.super-dot { background: var(--neon-cyan); box-shadow: 0 0 6px var(--neon-cyan); }

.app-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 14px 24px;
  font-family: 'Orbitron', sans-serif;
  font-size: 0.85rem;
  letter-spacing: 0.1em;
  border: none;
  border-radius: 2px;
  color: #fff;
  position: relative;
  z-index: 1;
  overflow: hidden;
  transition: box-shadow 0.3s, transform 0.3s;
}

.app-button::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent);
  transform: translateX(-100%);
  transition: transform 0.6s;
}

.app-card:hover .app-button::before {
  transform: translateX(100%);
}

.love-btn {
  background: linear-gradient(135deg, #ff2d78, #ff5722);
  box-shadow: 0 4px 20px rgba(255, 45, 120, 0.4);
}

.super-btn {
  background: linear-gradient(135deg, #0088ff, #7b2fff);
  box-shadow: 0 4px 20px rgba(0, 136, 255, 0.4);
}

.app-card:hover .love-btn {
  box-shadow: 0 6px 30px rgba(255, 45, 120, 0.6);
  transform: scale(1.02);
}

.app-card:hover .super-btn {
  box-shadow: 0 6px 30px rgba(0, 212, 255, 0.5);
  transform: scale(1.02);
}

.btn-icon svg {
  width: 18px;
  height: 18px;
  transition: transform 0.3s;
}

.app-card:hover .btn-icon svg {
  transform: translateX(4px);
}

/* ========== 滚动条 ========== */
.tech-marquee {
  overflow: hidden;
  border-top: 1px solid rgba(0, 240, 255, 0.1);
  border-bottom: 1px solid rgba(0, 240, 255, 0.1);
  background: rgba(0, 10, 20, 0.5);
  padding: 10px 0;
  position: relative;
  z-index: 2;
  margin-bottom: 0;
}

.marquee-track {
  display: flex;
  gap: 48px;
  animation: marquee 25s linear infinite;
  width: max-content;
}

.marquee-item {
  font-size: 0.72rem;
  color: rgba(0, 240, 255, 0.5);
  letter-spacing: 0.1em;
  white-space: nowrap;
}

.marquee-item::before {
  content: '◆ ';
  color: var(--neon-purple);
}

/* ========== 入场动画 ========== */
.fade-in {
  animation: fade-in 0.8s ease forwards;
  animation-delay: var(--delay, 0s);
  opacity: 0;
}

.fade-in-up {
  animation: fade-in-up 0.9s cubic-bezier(0.23, 1, 0.32, 1) forwards;
  animation-delay: var(--delay, 0s);
  opacity: 0;
}

/* ========== Keyframes ========== */
@keyframes aurora-shift {
  0% { filter: hue-rotate(0deg); }
  100% { filter: hue-rotate(15deg); }
}

@keyframes particle-float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-20px) scale(1.2); }
}

@keyframes pulse-glow {
  0%, 100% { opacity: 1; box-shadow: 0 0 8px #00ff88; }
  50% { opacity: 0.6; box-shadow: 0 0 16px #00ff88; }
}

@keyframes blink {
  50% { opacity: 0; }
}

@keyframes title-glow {
  0%, 100% { filter: brightness(1); }
  50% { filter: brightness(1.15); }
}

@keyframes glitch-top {
  0%, 90%, 100% { transform: translate(0); }
  92% { transform: translate(-4px, 2px); }
  94% { transform: translate(4px, -2px); }
  96% { transform: translate(-2px, -2px); }
}

@keyframes glitch-bottom {
  0%, 88%, 100% { transform: translate(0); }
  90% { transform: translate(3px, 2px); }
  93% { transform: translate(-3px, -1px); }
}

@keyframes beam-flow {
  0% { background-position: 0% 50%; }
  100% { background-position: 200% 50%; }
}

@keyframes card-scan {
  0%, 100% { top: -2px; opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  50% { top: 100%; opacity: 0.6; }
}

@keyframes ring-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes marquee {
  0% { transform: translateX(0); }
  100% { transform: translateX(-50%); }
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fade-in-up {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .top-bar {
    padding: 12px 16px;
    flex-wrap: wrap;
    gap: 8px;
    justify-content: center;
  }

  .status-center {
    order: 3;
    width: 100%;
    justify-content: center;
  }

  .hero {
    padding: 32px 16px 24px;
  }

  .stats-row {
    gap: 10px;
  }

  .stat-chip {
    min-width: 100px;
    padding: 10px 16px;
  }

  .apps-container {
    gap: 28px;
    padding: 0 16px;
  }
}

@media (max-width: 480px) {
  .status-right {
    display: none;
  }

  .stat-chip {
    min-width: 90px;
    padding: 8px 12px;
  }

  .stat-value {
    font-size: 1.1rem;
  }
}

@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}
</style>
