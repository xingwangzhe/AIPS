import { describe, it, expect } from 'vitest'
import { formatPrice, maskPhone, orderStatusText, prescriptionStatusText } from '@/utils/format.js'

describe('formatPrice', () => {
  it('formats a number to CNY string', () => {
    expect(formatPrice(19.9)).toBe('¥19.90')
  })
  it('handles null', () => {
    expect(formatPrice(null)).toBe('¥0.00')
  })
})

describe('maskPhone', () => {
  it('masks middle digits', () => {
    expect(maskPhone('13800138000')).toBe('138****8000')
  })
  it('handles empty string', () => {
    expect(maskPhone('')).toBe('')
  })
})

describe('orderStatusText', () => {
  it('maps 10 to 待付款', () => {
    expect(orderStatusText(10)).toBe('待付款')
  })
  it('maps 40 to 已完成', () => {
    expect(orderStatusText(40)).toBe('已完成')
  })
  it('maps unknown to 未知', () => {
    expect(orderStatusText(99)).toBe('未知')
  })
})

describe('prescriptionStatusText', () => {
  it('maps 4 to 已通过', () => {
    expect(prescriptionStatusText(4)).toBe('已通过')
  })
})
