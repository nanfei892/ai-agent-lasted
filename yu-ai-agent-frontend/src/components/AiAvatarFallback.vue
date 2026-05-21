<template>
  <div class="ai-avatar-fallback" :class="type">
    <svg v-if="type === 'love'" viewBox="0 0 24 24" class="avatar-icon">
      <path
        fill="currentColor"
        d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"
      />
    </svg>
    <svg v-else viewBox="0 0 24 24" class="avatar-icon">
      <rect x="5" y="9" width="14" height="10" rx="2" fill="currentColor"/>
      <rect x="8" y="4" width="8" height="6" rx="1.5" fill="currentColor" opacity="0.85"/>
      <circle cx="9.5" cy="13" r="1.2" fill="#0a0e1a"/>
      <circle cx="14.5" cy="13" r="1.2" fill="#0a0e1a"/>
    </svg>
  </div>
</template>

<script setup>
defineProps({
  type: {
    type: String,
    default: 'default'
  }
})
</script>

<style scoped>
.ai-avatar-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.avatar-icon {
  width: 20px;
  height: 20px;
}

.love {
  background: linear-gradient(135deg, #ff2d78, #ff6b35);
  color: #fff;
  box-shadow: 0 0 14px rgba(255, 45, 120, 0.5);
}

.default,
.super {
  background: linear-gradient(135deg, #00d4ff, #7b2fff);
  color: #fff;
  box-shadow: 0 0 14px rgba(0, 240, 255, 0.45);
}
</style>
