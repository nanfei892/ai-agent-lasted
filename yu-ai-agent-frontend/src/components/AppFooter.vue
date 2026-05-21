<template>
  <footer class="app-footer" :class="variant">
    <div class="footer-content">
      <div class="footer-section">
        <div class="footer-logo">
          <h3>AI超级智能体应用平台</h3>
        </div>
        <div class="footer-links">
          <a href="#">《用户协议》</a>
          <a href="#">《隐私政策》</a>
        </div>
      </div>
      
      <div class="footer-section">
        <h4>友情链接</h4>
        <div class="footer-links">
          <a href="https://www.mianshiya.com" target="_blank">AI零代码应用生成平台</a>
        </div>
      </div>
      
      <div class="footer-section">
        <h4>联系我们</h4>
        <div class="footer-links">
          <a href="#">作者邮箱：nanfei892@gmail.com</a>
        </div>
      </div>
    </div>
    
    <div class="footer-bottom">
      <p>© {{ currentYear }} 男妃AI超级智能体应用平台 - 让AI为你服务</p>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'

defineProps({
  variant: {
    type: String,
    default: 'light',
    validator: (v) => ['light', 'dark'].includes(v)
  }
})

const currentYear = computed(() => new Date().getFullYear())
</script>

<style scoped>
.app-footer {
  background-color: #fff;
  padding: 40px 0 20px;
  color: #666;
  border-top: 1px solid #eee;
  width: 100%;
  margin-top: auto;
  position: relative;
  z-index: 2;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  padding: 0 20px;
}

.footer-section {
  flex: 1;
  min-width: 200px;
  margin-bottom: 30px;
  padding: 0 15px;
}

.footer-logo h3 {
  font-size: 1.25rem;
  margin-bottom: 15px;
  color: #333;
}

.footer-section h4 {
  font-size: 1rem;
  margin-bottom: 15px;
  color: #333;
}

.footer-links {
  display: flex;
  flex-direction: column;
}

.footer-links a {
  margin-bottom: 10px;
  color: #666;
  text-decoration: none;
  transition: color 0.2s;
}

.footer-links a:hover {
  color: #007bff;
}

.qrcode {
  display: flex;
  align-items: center;
}

.qrcode-container {
  text-align: center;
}

.qrcode-placeholder {
  width: 90px;
  height: 90px;
  background-color: #f5f5f5;
  margin: 0 auto 10px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
}

.qrcode-placeholder:after {
  content: '🤖';
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  margin-top: 20px;
  border-top: 1px solid #eee;
  color: #999;
  font-size: 0.9rem;
}

/* 暗色主题（首页极客风） */
.app-footer.dark {
  background: linear-gradient(180deg, rgba(5, 8, 16, 0.95) 0%, #030508 100%);
  border-top: 1px solid rgba(0, 240, 255, 0.15);
  color: rgba(255, 255, 255, 0.55);
}

.app-footer.dark .footer-logo h3 {
  color: rgba(232, 244, 255, 0.9);
  text-shadow: 0 0 20px rgba(0, 240, 255, 0.2);
}

.app-footer.dark .footer-section h4 {
  color: rgba(0, 240, 255, 0.85);
  font-family: 'JetBrains Mono', 'Consolas', monospace;
  font-size: 0.85rem;
  letter-spacing: 0.1em;
}

.app-footer.dark .footer-links a {
  color: rgba(255, 255, 255, 0.5);
}

.app-footer.dark .footer-links a:hover {
  color: #00f0ff;
  text-shadow: 0 0 8px rgba(0, 240, 255, 0.5);
}

.app-footer.dark .footer-bottom {
  border-top-color: rgba(0, 240, 255, 0.1);
  color: rgba(255, 255, 255, 0.35);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
  }
  
  .footer-section {
    width: 100%;
    margin-bottom: 20px;
    padding: 0;
  }
}

@media (max-width: 480px) {
  .app-footer {
    padding: 30px 0 15px;
  }
  
  .footer-section h4 {
    font-size: 0.95rem;
  }
  
  .footer-links a {
    font-size: 0.9rem;
  }
  
  .qrcode-placeholder {
    width: 80px;
    height: 80px;
  }
}
</style> 