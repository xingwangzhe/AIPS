<template>
  <div class="pb-4">
    <AppHeader title="购物车" />

    <div v-if="items.length === 0" class="text-center pt-20 text-gray-400">
      <p class="text-5xl mb-4">🛒</p>
      <p class="text-base">购物车是空的</p>
      <el-button class="mt-4" type="primary" @click="$router.push('/')">去逛逛</el-button>
    </div>

    <div v-else class="px-4 pt-3 flex flex-col gap-3">
      <div v-for="item in items" :key="item.id"
           class="bg-white rounded-xl p-3 flex items-center gap-3 shadow-sm">
        <el-checkbox v-model="item.checked" />
        <div class="w-16 h-16 bg-gray-100 rounded-lg flex items-center justify-center">
          <span class="text-2xl">💊</span>
        </div>
        <div class="flex-1 min-w-0">
          <h4 class="text-sm font-medium truncate">{{ item.name }}</h4>
          <p class="text-xs text-gray-400">{{ item.spec }}</p>
          <span class="text-sm font-bold text-red-500">{{ formatPrice(item.price) }}</span>
        </div>
        <div class="flex items-center gap-1">
          <el-button size="small" circle @click="item.qty > 1 && item.qty--">−</el-button>
          <span class="w-8 text-center text-sm">{{ item.qty }}</span>
          <el-button size="small" circle @click="item.qty++">+</el-button>
        </div>
      </div>
    </div>

    <!-- 底部结算 -->
    <div v-if="items.length" class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 p-3 flex items-center justify-between">
      <div>
        <span class="text-sm text-gray-500">合计 </span>
        <span class="text-lg font-bold text-red-500">{{ formatPrice(total) }}</span>
      </div>
      <el-button type="primary" size="large" @click="$router.push('/order/confirm')">
        去结算
      </el-button>
    </div>

    <BottomNav />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { formatPrice } from '@/utils/format.js'
import AppHeader from '@/components/AppHeader.vue'
import BottomNav from '@/components/BottomNav.vue'

// 模拟购物车数据（后续接入真实 API）
const items = ref([
  { id: 1, name: '布洛芬缓释胶囊', spec: '0.3g*20粒', price: 19.90, qty: 2, checked: true },
  { id: 2, name: '维生素C泡腾片', spec: '1g*20片', price: 45.00, qty: 1, checked: true },
])

const total = computed(() =>
  items.value.filter(i => i.checked).reduce((s, i) => s + i.price * i.qty, 0)
)
</script>
