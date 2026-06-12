<template>
  <div class="min-h-screen bg-gradient-to-b from-blue-400 to-blue-600 flex flex-col items-center justify-center px-6">
    <div class="text-center mb-10">
      <h1 class="text-4xl font-bold text-white mb-2">AIPS</h1>
      <p class="text-blue-100">AI 智能线上购药系统</p>
    </div>

    <div class="w-full max-w-sm bg-white rounded-2xl shadow-xl p-8">
      <h2 class="text-xl font-semibold text-gray-800 mb-6 text-center">手机号登录</h2>

      <el-form :model="form" label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="手机号">
          <el-input
            v-model="form.phone"
            placeholder="请输入手机号"
            maxlength="11"
            size="large"
          />
        </el-form-item>

        <el-form-item label="验证码">
          <div class="flex gap-2 w-full">
            <el-input
              v-model="form.verifyCode"
              placeholder="请输入验证码"
              maxlength="6"
              size="large"
              class="flex-1"
            />
            <el-button
              :disabled="countdown > 0"
              size="large"
              @click="sendCode"
            >
              {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
            </el-button>
          </div>
        </el-form-item>

        <p class="text-xs text-gray-400 mb-4">开发阶段验证码固定为 123456</p>

        <el-button
          type="primary"
          size="large"
          class="w-full"
          :loading="loading"
          @click="handleLogin"
        >
          登录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const form = reactive({ phone: '', verifyCode: '' })
const loading = ref(false)
const countdown = ref(0)

function sendCode() {
  if (!form.phone || form.phone.length < 11) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
  ElMessage.success('验证码已发送（开发验证码：123456）')
}

async function handleLogin() {
  if (!form.phone || !form.verifyCode) {
    ElMessage.warning('请填写手机号和验证码')
    return
  }
  loading.value = true
  try {
    await auth.login(form.phone, form.verifyCode)
    ElMessage.success('登录成功')
    router.push(route.query.redirect || '/')
  } catch (e) {
    ElMessage.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>
