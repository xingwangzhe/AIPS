import api from './index.js'

export function getCategories(parentId) {
  const params = parentId ? { parentId } : {}
  return api.get('/drug/categories', { params })
}

export function searchDrugs(params) {
  return api.get('/drug/search', { params })
}

export function getDrugDetail(id) {
  return api.get(`/drug/${id}`)
}
