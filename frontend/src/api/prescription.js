import api from './index.js'

export function uploadPrescription(file) {
  const formData = new FormData()
  formData.append('file', file)
  return api.post('/prescription/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function getPrescriptionStatus(id) {
  return api.get(`/prescription/${id}`)
}
