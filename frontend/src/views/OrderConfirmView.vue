<template>
  <div class="pb-4">
    <AppHeader title="确认订单" show-back />

    <div class="px-4 pt-3 space-y-3">
      <!-- 收货地址 -->
      <div class="bg-white rounded-xl p-4 shadow-sm">
        <h3 class="text-sm font-medium text-gray-500 mb-2">收货地址</h3>
        <div class="flex items-start justify-between">
          <div>
            <p class="font-medium">{{ form.receiver }} {{ form.phone }}</p>
            <p class="text-sm text-gray-400 mt-1">{{ form.fullAddress }}</p>
          </div>
          <span class="text-blue-500 text-sm">切换</span>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="bg-white rounded-xl p-4 shadow-sm">
        <h3 class="text-sm font-medium text-gray-500 mb-2">商品信息</h3>
        <div class="space-y-2">
          <div v-for="item in items" :key="item.id" class="flex justify-between text-sm">
            <span class="text-gray-600">{{ item.name }} × {{ item.qty }}</span>
            <span class="text-gray-800">{{ formatPrice(item.price * item.qty) }}</span>
          </div>
        </div>
      </div>

      <!-- 配送 & 支付 -->
      <div class="bg-white rounded-xl p-4 shadow-sm space-y-3">
        <div>
          <span class="text-sm text-gray-500 mr-4">配送方式</span>
          <el-radio-group v-model="form.deliveryType" size="small">
            <el-radio-button :value="1" label="标准快递 ¥8" />
            <el-radio-button :value="2" label="当日达 ¥15" />
          </el-radio-group>
        </div>
        <div>
          <span class="text-sm text-gray-500 mr-4">支付方式</span>
          <el-radio-group v-model="form.paymentMethod" size="small">
            <el-radio-button :value="1" label="微信" />
            <el-radio-button :value="2" label="支付宝" />
          </el-radio-group>
        </div>
        <div class="flex items-center gap-2">
          <el-checkbox v-model="form.isPrivacyPack" />
          <span class="text-sm text-gray-500">隐私包装（隐藏药品信息）</span>
        </div>
      </div>

      <!-- 金额明细 -->
      <div class="bg-white rounded-xl p-4 shadow-sm text-sm space-y-1">
        <div class="flex justify-between"><span class="text-gray-500">商品金额</span><span>{{ formatPrice(subtotal) }}</span></div>
        <div class="flex justify-between"><span class="text-gray-500">运费</span><span>{{ formatPrice(deliveryFee) }}</span></div>
        <div class="flex justify-between font-bold text-base pt-2 border-t border-gray-100 mt-2">
          <span>应付总额</span><span class="text-red-500">{{ formatPrice(subtotal + deliveryFee) }}</span>
        </div>
      </div>
    </div>

    <!-- 提交按钮 -->
    <div class="px-4 pt-4 pb-20">
      <el-button type="primary" size="large" class="w-full" @click="handleSubmit" :loading="submitting">
        提交订单
      </el-button>
    </div>

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

const router = useRouter()
const submitting = ref(false)

const form = reactive({
  receiver: '张三',
  phone: '138****8000',
  fullAddress: '北京市朝阳区 xx路 100号',
  deliveryType: 1,
  paymentMethod: 1,
  isPrivacyPack: false,
})

const items = [{ id: 1, name: '布洛芬缓释胶囊', price: 19.90, qty: 2 }]
const subtotal = items.reduce((s, i) => s + i.price * i.qty, 0)
const deliveryFee = form.deliveryType === 1 ? 8 : 15

async function handleSubmit() {
  submitting.value = true
  try {
    const order = await createOrder({
      addressId: 1,
      deliveryType: form.deliveryType,
      paymentMethod: form.paymentMethod,
      isPrivacyPack: form.isPrivacyPack ? 1 : 0,
      cartItemIds: [1],
    })
    ElMessage.success('订单创建成功')
    router.push('/orders')
  } catch (e) {
    ElMessage.error(e.message || '下单失败')
  } finally {
    submitting.value = false
  }
}
</script>
