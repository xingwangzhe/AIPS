import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as userApi from '@/api/user.js'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('aips_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('aips_user') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const userId = computed(() => userInfo.value?.userId)
  const nickname = computed(() => userInfo.value?.nickname || '未登录')

  async function login(phone, verifyCode) {
    const data = await userApi.login(phone, verifyCode)
    token.value = data.token
    userInfo.value = data.userInfo
    localStorage.setItem('aips_token', data.token)
    localStorage.setItem('aips_user', JSON.stringify(data.userInfo))
    return data
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('aips_token')
    localStorage.removeItem('aips_user')
  }

  return { token, userInfo, isLoggedIn, userId, nickname, login, logout }
})
