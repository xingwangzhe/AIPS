/** 金额格式化：保留两位小数，千分位 */
export function formatPrice(price) {
  if (price == null) return '¥0.00'
  return `¥${Number(price).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')}`
}

/** 手机号脱敏 */
export function maskPhone(phone) {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

/** 时间格式化 */
export function formatTime(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

/** 订单状态映射 */
export function orderStatusText(status) {
  const map = { 10: '待付款', 20: '待发货', 30: '配送中', 40: '已完成', 50: '已取消' }
  return map[status] || '未知'
}

/** 处方状态映射 */
export function prescriptionStatusText(status) {
  const map = { 0: '待上传', 1: '识别中', 2: 'AI检查中', 3: '待药师审核', 4: '已通过', 5: '已驳回', 6: '需重传' }
  return map[status] || '未知'
}
