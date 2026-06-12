<template>
  <div>
    <AppHeader title="我的订单" />
    <div style="padding:8px 16px;display:flex;gap:8px;overflow-x:auto">
      <el-button v-for="tab in tabs" :key="tab.value" :type="activeTab === tab.value ? 'primary' : 'default'" size="small" @click="activeTab = tab.value; fetchOrders()">{{ tab.label }}</el-button>
    </div>
    <div v-if="orders.length === 0" style="text-align:center;padding-top:80px;color:#9ca3af">
      <p style="font-size:48px;margin:0">📋</p><p>暂无订单</p>
    </div>
    <div v-else style="padding:0 16px;display:flex;flex-direction:column;gap:12px">
      <div v-for="order in orders" :key="order.orderId" class="order-card">
        <div style="display:flex;justify-content:space-between;margin-bottom:8px">
          <span style="font-size:12px;color:#9ca3af">{{ order.orderNo }}</span>
          <span class="order-status" :style="{color: order.status===10?'#f59e0b':order.status===40?'#16a34a':'#3b82f6'}">{{ order.statusText }}</span>
        </div>
        <div v-for="item in order.items" :key="item.medicineName" style="display:flex;justify-content:space-between;font-size:14px;padding:4px 0">
          <span style="color:#666">{{ item.medicineName }}</span><span style="color:#9ca3af">×{{ item.quantity }}</span>
        </div>
        <div style="display:flex;justify-content:space-between;border-top:1px solid #f3f4f6;margin-top:8px;padding-top:8px">
          <span style="font-size:15px;font-weight:700;color:#ef4444">{{ formatPrice(order.totalAmount) }}</span>
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

const tabs = [{ label:'全部',value:null },{ label:'待付款',value:10 },{ label:'待发货',value:20 },{ label:'配送中',value:30 },{ label:'已完成',value:40 }]
const activeTab = ref(null); const orders = ref([])
async function fetchOrders() {
  try { const r = await getOrderList({ status: activeTab.value, page: 1, pageSize: 10 }); orders.value = (r.list||[]).map(o => ({ ...o, statusText: orderStatusText(o.status) })) }
  catch { orders.value = [] }
}
onMounted(() => fetchOrders())
</script>

<style scoped>
.order-card { background:#fff;border-radius:12px;padding:16px; }
.order-status { font-size:14px;font-weight:500; }
</style>
