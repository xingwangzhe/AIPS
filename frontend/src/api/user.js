import api from './index.js'

export function login(phone, verifyCode) {
  return api.post('/user/login', { phone, verifyCode })
}

export function getHealthProfile() {
  return api.get('/user/health')
}

export function setReminder(data) {
  return api.post('/user/reminder', data)
}
