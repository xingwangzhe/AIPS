<template>
  <div class="flex mb-4" :class="msg.senderType === 1 ? 'justify-end' : 'justify-start'">
    <!-- AI/Avatar -->
    <div v-if="msg.senderType !== 1" class="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center mr-2 flex-shrink-0">
      <span class="text-sm">{{ msg.senderType === 2 ? '🤖' : '👨‍⚕️' }}</span>
    </div>

    <div class="max-w-[75%]">
      <!-- 消息气泡 -->
      <div
        class="rounded-2xl px-4 py-2.5 text-sm leading-relaxed"
        :class="msg.senderType === 1
          ? 'bg-blue-500 text-white rounded-br-md'
          : 'bg-white text-gray-700 rounded-bl-md shadow-sm'"
      >
        {{ msg.content }}
      </div>

      <!-- 风险标签 -->
      <div v-if="msg.riskLevel" class="mt-1 ml-1">
        <span
          class="text-xs px-2 py-0.5 rounded-full"
          :class="riskClass(msg.riskLevel)"
        >
          {{ riskText(msg.riskLevel) }}
        </span>
        <span v-if="msg.disclaimerShown" class="text-xs text-gray-400 ml-1">仅供参考</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  msg: { type: Object, required: true },
})

function riskClass(level) {
  return level === 3 ? 'bg-red-100 text-red-600' : level === 2 ? 'bg-yellow-100 text-yellow-700' : 'bg-green-100 text-green-600'
}

function riskText(level) {
  return level === 3 ? '高风险⚠' : level === 2 ? '中风险⚡' : '低风险✅'
}
</script>
