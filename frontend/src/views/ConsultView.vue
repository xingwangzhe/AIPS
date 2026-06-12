<template>
  <div class="consult-page">
    <AppHeader title="AI 药师咨询" show-back />
    <div ref="msgList" class="msg-list">
      <div v-if="messages.length === 0" class="empty-state">
        <p style="font-size:48px;margin:0">💊</p>
        <p style="font-size:16px;font-weight:500;margin:12px 0 0">AI 智能药师</p>
        <p style="font-size:14px;color:#9ca3af;margin:4px 0 0">描述你的症状，获取用药建议</p>
        <p style="font-size:12px;color:#d1d5db;margin:12px 0 0">高风险话题将自动转接人工药师</p>
      </div>
      <ChatMessage v-for="msg in messages" :key="msg.id" :msg="msg" />
      <div v-if="waiting" style="display:flex;align-items:center;gap:8px;color:#9ca3af;font-size:14px;padding-left:44px;padding-bottom:8px">
        <span class="dot-pulse"></span> AI 正在分析...
      </div>
    </div>
    <div class="input-bar">
      <el-input v-model="input" placeholder="描述症状或用药问题..." size="large" :disabled="!sessionId" @keyup.enter="handleSend" style="flex:1" />
      <el-button type="primary" size="large" :disabled="!input.trim() || waiting" @click="handleSend">发送</el-button>
      <el-button size="large" :disabled="!sessionId" @click="handleTransfer">转人工</el-button>
    </div>
    <BottomNav />
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { createSession, sendMessage, transferToHuman } from '@/api/consult.js'
import { ElMessage } from 'element-plus'
import AppHeader from '@/components/AppHeader.vue'
import BottomNav from '@/components/BottomNav.vue'
import ChatMessage from '@/components/ChatMessage.vue'

const sessionId = ref(null); const messages = ref([]); const input = ref(''); const waiting = ref(false); const msgList = ref(null)
function scrollBottom() { nextTick(() => { if (msgList.value) msgList.value.scrollTop = msgList.value.scrollHeight }) }
async function handleSend() {
  if (!input.value.trim() || waiting.value) return
  waiting.value = true; const text = input.value; input.value = ''
  messages.value.push({ id: Date.now(), senderType: 1, content: text }); scrollBottom()
  try {
    const resp = await sendMessage(sessionId.value, text)
    messages.value.push({ id: Date.now()+1, senderType: 2, content: resp.aiReply.content, riskLevel: resp.aiReply.riskLevel, disclaimerShown: true })
    if (resp.aiReply.riskLevel === 3) ElMessage.warning('该问题涉及高风险用药，建议转接人工药师')
  } catch {
    messages.value.push({ id: Date.now()+1, senderType: 2, content: 'AI 服务暂时不可用，请稍后重试或转接人工药师。' })
  } finally { waiting.value = false; scrollBottom() }
}
async function handleTransfer() { try { await transferToHuman(sessionId.value, '用户请求转人工'); ElMessage.success('已加入转接队列') } catch { ElMessage.error('转接失败') } }
onMounted(async () => { try { const s = await createSession('用药咨询'); sessionId.value = s.sessionId } catch { ElMessage.error('创建会话失败') } })
</script>

<style scoped>
.consult-page { display: flex; flex-direction: column; height: 100vh; background: #f5f7fa; }
.msg-list { flex: 1; overflow-y: auto; padding: 16px; }
.empty-state { text-align: center; padding-top: 80px; }
.input-bar { background: #fff; border-top: 1px solid #e5e7eb; padding: 12px; display: flex; gap: 8px; }
.dot-pulse { width: 8px; height: 8px; background: #3b82f6; border-radius: 50%; animation: pulse 1s infinite; }
@keyframes pulse { 0%,100% { opacity: .2 } 50% { opacity: 1 } }
</style>
