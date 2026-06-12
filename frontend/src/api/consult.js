import api from './index.js'

export function createSession(title) {
  return api.post('/consult/session', { title })
}

export function sendMessage(sessionId, content, msgType = 1) {
  return api.post(`/consult/session/${sessionId}/message`, { content, msgType })
}

export function transferToHuman(sessionId, reason) {
  return api.post(`/consult/session/${sessionId}/transfer`, { reason })
}
