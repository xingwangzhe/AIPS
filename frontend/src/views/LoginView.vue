<template>
  <div class="login-page">
    <div class="login-hero">
      <h1 class="login-logo">AIPS</h1>
      <p class="login-subtitle">AI 智能线上购药系统</p>
    </div>
    <div class="login-card">
      <h2 class="login-title">手机号登录</h2>
      <el-form :model="form" label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" size="large" />
        </el-form-item>
        <el-form-item label="验证码">
          <div style="display:flex;gap:8px;width:100%">
            <el-input v-model="form.verifyCode" placeholder="请输入验证码" maxlength="6" size="large" style="flex:1" />
            <el-button :disabled="countdown > 0" size="large" @click="sendCode">{{ countdown > 0 ? countdown + 's' : '发送验证码' }}</el-button>
          </div>
        </el-form-item>
        <p style="font-size:12px;color:#9ca3af;margin-bottom:16px">开发阶段验证码固定为 123456</p>
        <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="handleLogin">登录</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'
import { ElMessage } from 'element-plus'

const router = useRouter(); const route = useRoute(); const auth = useAuthStore()
const form = reactive({ phone: '', verifyCode: '' })
const loading = ref(false); const countdown = ref(0)

function sendCode() {
  if (!form.phone || form.phone.length < 11) return ElMessage.warning('请输入正确的手机号')
  countdown.value = 60; const t = setInterval(() => { countdown.value--; if (countdown.value <= 0) clearInterval(t) }, 1000)
  ElMessage.success('验证码已发送（开发验证码：123456）')
}
async function handleLogin() {
  if (!form.phone || !form.verifyCode) return ElMessage.warning('请填写手机号和验证码')
  loading.value = true
  try { await auth.login(form.phone, form.verifyCode); ElMessage.success('登录成功'); router.push(route.query.redirect || '/') }
  catch (e) { ElMessage.error(e.message || '登录失败') }
  finally { loading.value = false }
}
</script>

<style scoped>
.login-page { min-height: 100vh; background: linear-gradient(180deg, #60a5fa, #2563eb); display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 24px; }
.login-hero { text-align: center; margin-bottom: 40px; }
.login-logo { font-size: 40px; font-weight: 700; color: #fff; margin: 0; }
.login-subtitle { color: rgba(255,255,255,.8); margin-top: 8px; }
.login-card { width: 100%; max-width: 360px; background: #fff; border-radius: 16px; padding: 32px; }
.login-title { font-size: 20px; font-weight: 600; color: #1f2937; text-align: center; margin: 0 0 24px; }
</style>
