<template>
  <div class="countdown-timer">
    <div class="timer-icon">
      <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="#2563eb" stroke-width="2">
        <circle cx="12" cy="12" r="10"/>
        <polyline points="12 6 12 12 16 14"/>
      </svg>
    </div>
    <span class="timer-label">距{{ promotion.tag }}结束</span>
    <div class="time-blocks">
      <span class="time-block" v-if="days !== '00'">
        <span class="time-num">{{ days }}</span>
      </span>
      <span class="time-sep" v-if="days !== '00'">天</span>
      <span class="time-block">
        <span class="time-num">{{ hours }}</span>
      </span>
      <span class="time-sep">:</span>
      <span class="time-block">
        <span class="time-num">{{ minutes }}</span>
      </span>
      <span class="time-sep">:</span>
      <span class="time-block">
        <span class="time-num">{{ seconds }}</span>
      </span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const { promotion } = defineProps({
  promotion: {
    type: Object,
    required: true
  }
})

const remainingTime = ref(0)
let timer = null

const days = computed(() => {
  return String(Math.floor(remainingTime.value / 86400)).padStart(2, '0')
})

const hours = computed(() => {
  return String(Math.floor((remainingTime.value % 86400) / 3600)).padStart(2, '0')
})

const minutes = computed(() => {
  return String(Math.floor((remainingTime.value % 3600) / 60)).padStart(2, '0')
})

const seconds = computed(() => {
  return String(remainingTime.value % 60).padStart(2, '0')
})

function updateTimer() {
  const end = new Date(promotion.endTime).getTime()
  const now = Date.now()
  const diff = Math.max(0, Math.floor((end - now) / 1000))
  remainingTime.value = diff

  if (diff <= 0) {
    clearInterval(timer)
  }
}

onMounted(() => {
  updateTimer()
  timer = setInterval(updateTimer, 1000)
})

onUnmounted(() => {
  clearInterval(timer)
})
</script>

<style scoped>
.countdown-timer {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  border-radius: 8px;
  border: 1px solid rgba(37,99,235,0.15);
}

.timer-icon {
  display: flex;
  align-items: center;
}

.timer-label {
  font-size: 13px;
  color: #2563eb;
  font-weight: 600;
  white-space: nowrap;
}

.time-blocks {
  display: flex;
  align-items: center;
  gap: 3px;
}

.time-block {
  background: #2f3542;
  border-radius: 4px;
  padding: 2px 6px;
  min-width: 28px;
  text-align: center;
}

.time-num {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  font-weight: 800;
  color: #fff;
  letter-spacing: 1px;
}

.time-sep {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  font-weight: 800;
  color: #2563eb;
  animation: blink 1s step-end infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}
</style>
