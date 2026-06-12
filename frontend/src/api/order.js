import api from './index.js'

export function addToCart(medicineId, quantity) {
  return api.post('/cart/item', { medicineId, quantity })
}

export function createOrder(data) {
  return api.post('/order', data)
}

export function getOrderList(params) {
  return api.get('/order/list', { params })
}
