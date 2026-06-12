<template>
  <div class="msg-row" :class="msg.senderType === 1 ? 'msg-right' : 'msg-left'">
    <div v-if="msg.senderType !== 1" class="msg-avatar">{{ msg.senderType === 2 ? '🤖' : '👨‍⚕️' }}</div>
    <div class="msg-body">
      <div class="msg-bubble" :class="msg.senderType === 1 ? 'bubble-user' : 'bubble-ai'">{{ msg.content }}</div>
      <div v-if="msg.riskLevel" class="msg-risk">
        <span class="risk-badge" :class="'risk-' + msg.riskLevel">{{ riskLevelText(msg.riskLevel) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({ msg: { type: Object, required: true } })
function riskLevelText(l) { return l === 3 ? '高风险⚠' : l === 2 ? '中风险⚡' : '低风险✅' }
</script>

<style scoped>
.msg-row { display: flex; margin-bottom: 16px; }
.msg-right { justify-content: flex-end; }
.msg-left { justify-content: flex-start; }
.msg-avatar { width: 32px; height: 32px; border-radius: 50%; background: #dbeafe; display: flex; align-items: center; justify-content: center; margin-right: 8px; flex-shrink: 0; font-size: 14px; }
.msg-body { max-width: 75%; }
.msg-bubble { padding: 10px 16px; border-radius: 16px; font-size: 14px; line-height: 1.5; }
.bubble-user { background: #3b82f6; color: #fff; border-bottom-right-radius: 4px; }
.bubble-ai { background: #fff; color: #374151; border-bottom-left-radius: 4px; box-shadow: 0 1px 2px rgba(0,0,0,.05); }
.msg-risk { margin-top: 4px; }
.risk-badge { font-size: 11px; padding: 2px 8px; border-radius: 10px; }
.risk-1 { background: #dcfce7; color: #16a34a; }
.risk-2 { background: #fef9c3; color: #ca8a04; }
.risk-3 { background: #fee2e2; color: #dc2626; }
</style>
