<template>
  <div class="pb-4">
    <AppHeader title="我的订单" />

    <!-- 状态筛选 -->
    <div class="px-4 py-2 flex gap-2 overflow-x-auto">
      <el-button
        v-for="tab in tabs" :key="tab.value"
        :type="activeTab === tab.value ? 'primary' : 'default'"
        size="small"
        @click="activeTab = tab.value; fetchOrders()"
      >{{ tab.label }}</el-button>
    </div>

    <!-- 订单列表 -->
    <div v-if="orders.length === 0" class="text-center pt-20 text-gray-400">
      <p class="text-5xl mb-4">📋</p>
      <p>暂无订单</p>
    </div>
    <div v-else class="px-4 flex flex-col gap-3">
      <div v-for="order in orders" :key="order.orderId"
           class="bg-white rounded-xl p-4 shadow-sm">
        <div class="flex justify-between items-center mb-2">
          <span class="text-xs text-gray-400">{{ order.orderNo }}</span>
          <span class="text-sm font-medium" :class="statusClass(order.status)">{{ order.statusText }}</span>
        </div>
        <div v-for="item in order.items" :key="item.medicineName"
             class="flex justify-between text-sm py-1">
          <span class="text-gray-600">{{ item.medicineName }}</span>
          <span class="text-gray-400">×{{ item.quantity }}</span>
        </div>
        <div class="flex justify-between items-center mt-2 pt-2 border-t border-gray-100">
          <span class="text-sm font-bold text-red-500">{{ formatPrice(order.totalAmount) }}</span>
        </div>
      </div>
    </div>

    <BottomNav />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderList } from '@/api/order.js'
import { formatPrice, orderStatusText } from '@/utils/format.js'
import AppHeader from '@/components/AppHeader.vue'
import BottomNav from '@/components/BottomNav.vue'

const tabs = [
  { label: '全部', value: null },
  { label: '待付款', value: 10 },
  { label: '待发货', value: 20 },
  { label: '配送中', value: 30 },
  { label: '已完成', value: 40 },
]

const activeTab = ref(null)
const orders = ref([])

function statusClass(s) {
  return s === 10 ? 'text-orange-500' : s === 40 ? 'text-green-500' : 'text-blue-500'
}

async function fetchOrders() {
  try {
    const result = await getOrderList({ status: activeTab.value, page: 1, pageSize: 10 })
    orders.value = (result.list || []).map(o => ({ ...o, statusText: orderStatusText(o.status) }))
  } catch {
    orders.value = []
  }
}

onMounted(() => fetchOrders())
</script>
