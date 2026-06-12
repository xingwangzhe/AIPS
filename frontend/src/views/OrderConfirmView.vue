<template>
  <div>
    <AppHeader title="确认订单" show-back />
    <div style="padding:12px 16px;display:flex;flex-direction:column;gap:12px">
      <div class="confirm-card">
        <h3 style="font-size:14px;color:#9ca3af;margin:0 0 8px">收货地址</h3>
        <p style="font-weight:500;margin:0">{{ form.receiver }} {{ form.phone }}</p>
        <p style="font-size:14px;color:#9ca3af;margin:4px 0 0">{{ form.fullAddress }}</p>
      </div>
      <div class="confirm-card">
        <h3 style="font-size:14px;color:#9ca3af;margin:0 0 8px">商品信息</h3>
        <div v-for="item in items" :key="item.id" style="display:flex;justify-content:space-between;font-size:14px;padding:4px 0">
          <span style="color:#666">{{ item.name }} ×{{ item.qty }}</span>
          <span>{{ formatPrice(item.price * item.qty) }}</span>
        </div>
      </div>
      <div class="confirm-card">
        <div style="margin-bottom:12px"><span style="font-size:14px;color:#9ca3af;margin-right:16px">配送方式</span><el-radio-group v-model="form.deliveryType" size="small"><el-radio-button :value="1" label="标准快递 ¥8" /><el-radio-button :value="2" label="当日达 ¥15" /></el-radio-group></div>
        <div style="margin-bottom:12px"><span style="font-size:14px;color:#9ca3af;margin-right:16px">支付方式</span><el-radio-group v-model="form.paymentMethod" size="small"><el-radio-button :value="1" label="微信" /><el-radio-button :value="2" label="支付宝" /></el-radio-group></div>
        <el-checkbox v-model="form.isPrivacyPack"><span style="font-size:14px;color:#9ca3af">隐私包装</span></el-checkbox>
      </div>
      <div class="confirm-card" style="font-size:14px">
        <div style="display:flex;justify-content:space-between"><span style="color:#9ca3af">商品金额</span><span>{{ formatPrice(subtotal) }}</span></div>
        <div style="display:flex;justify-content:space-between;margin-top:4px"><span style="color:#9ca3af">运费</span><span>{{ formatPrice(deliveryFee) }}</span></div>
        <div style="display:flex;justify-content:space-between;font-size:16px;font-weight:700;border-top:1px solid #f3f4f6;margin-top:8px;padding-top:8px"><span>应付总额</span><span style="color:#ef4444">{{ formatPrice(subtotal + deliveryFee) }}</span></div>
      </div>
      <el-button type="primary" size="large" style="width:100%" :loading="submitting" @click="handleSubmit">提交订单</el-button>
    </div>
    <div style="height:80px"></div>
    <BottomNav />
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createOrder } from '@/api/order.js'
import { formatPrice } from '@/utils/format.js'
import { ElMessage } from 'element-plus'
import AppHeader from '@/components/AppHeader.vue'
import BottomNav from '@/components/BottomNav.vue'

const router = useRouter(); const submitting = ref(false)
const form = reactive({ receiver: '张三', phone: '138****8000', fullAddress: '北京市朝阳区 xx路 100号', deliveryType: 1, paymentMethod: 1, isPrivacyPack: false })
const items = [{ id: 1, name: '布洛芬缓释胶囊', price: 19.90, qty: 2 }]
const subtotal = items.reduce((s,i) => s + i.price * i.qty, 0)
const deliveryFee = form.deliveryType === 1 ? 8 : 15

async function handleSubmit() {
  submitting.value = true
  try { await createOrder({ addressId: 1, deliveryType: form.deliveryType, paymentMethod: form.paymentMethod, isPrivacyPack: form.isPrivacyPack ? 1 : 0, cartItemIds: [1] }); ElMessage.success('订单创建成功'); router.push('/orders') }
  catch(e) { ElMessage.error(e.message || '下单失败') }
  finally { submitting.value = false }
}
</script>

<style scoped>
.confirm-card { background: #fff; border-radius: 12px; padding: 16px; }
</style>
