<template>
  <div class="flex flex-col h-screen bg-gray-50">
    <AppHeader title="AI 药师咨询" show-back />

    <!-- 消息列表 -->
    <div ref="msgList" class="flex-1 overflow-y-auto px-4 py-3">
      <div v-if="messages.length === 0" class="text-center pt-20 text-gray-400">
        <p class="text-4xl mb-4">💊</p>
        <p class="text-base font-medium mb-2">AI 智能药师</p>
        <p class="text-sm">描述你的症状，获取用药建议</p>
        <p class="text-xs mt-4 text-gray-300">高风险话题将自动转接人工药师</p>
      </div>
      <ChatMessage v-for="msg in messages" :key="msg.id" :msg="msg" />
      <div v-if="waiting" class="flex items-center gap-2 text-gray-400 text-sm pl-12 py-2">
        <span class="animate-pulse">●</span> AI 正在分析...
      </div>
    </div>

    <!-- 输入栏 -->
    <div class="bg-white border-t border-gray-200 p-3 flex gap-2">
      <el-input
        v-model="input"
        placeholder="描述症状或用药问题..."
        size="large"
        :disabled="sessionId === null"
        @keyup.enter="handleSend"
      />
      <el-button type="primary" size="large" :disabled="!input.trim() || waiting" @click="handleSend">
        发送
      </el-button>
      <el-button size="large" :disabled="sessionId === null" @click="handleTransfer">
        转人工
      </el-button>
    </div>

    <BottomNav />
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { createSession, sendMessage } from '@/api/consult.js'
import { ElMessage } from 'element-plus'
import AppHeader from '@/components/AppHeader.vue'
import BottomNav from '@/components/BottomNav.vue'
import ChatMessage from '@/components/ChatMessage.vue'

const sessionId = ref(null)
const messages = ref([])
const input = ref('')
const waiting = ref(false)
const msgList = ref(null)

function scrollBottom() {
  nextTick(() => {
    if (msgList.value) msgList.value.scrollTop = msgList.value.scrollHeight
  })
}

async function handleSend() {
  if (!input.value.trim() || waiting.value) return
  waiting.value = true
  const text = input.value
  input.value = ''

  // 先显示用户消息
  messages.value.push({ id: Date.now(), senderType: 1, content: text })
  scrollBottom()

  try {
    const resp = await sendMessage(sessionId.value, text)
    const ai = {
      id: Date.now() + 1,
      senderType: 2,
      content: resp.aiReply.content,
      riskLevel: resp.aiReply.riskLevel,
      disclaimerShown: true,
    }
    messages.value.push(ai)

    // 高风险自动提示转人工
    if (resp.aiReply.riskLevel === 3) {
      ElMessage.warning('该问题涉及高风险用药，建议转接人工药师')
    }
  } catch (e) {
    messages.value.push({
      id: Date.now() + 1,
      senderType: 2,
      content: '抱歉，AI 服务暂时不可用，请稍后重试或转接人工药师。',
    })
  } finally {
    waiting.value = false
    scrollBottom()
  }
}

async function handleTransfer() {
  try {
    const { transferToHuman } = await import('@/api/consult.js')
    await transferToHuman(sessionId.value, '用户请求转人工')
    ElMessage.success('已加入转接队列，预计等待 60 秒')
  } catch (e) {
    ElMessage.error('转接失败')
  }
}

onMounted(async () => {
  try {
    const session = await createSession('用药咨询')
    sessionId.value = session.sessionId
  } catch {
    ElMessage.error('创建会话失败')
  }
})
</script>
