<template>
  <nav class="fixed bottom-0 left-0 right-0 z-50 bg-white border-t border-gray-200 h-14 flex items-center justify-around safe-area-bottom">
    <router-link
      v-for="item in tabs"
      :key="item.path"
      :to="item.path"
      class="flex flex-col items-center justify-center w-full h-full text-xs"
      :class="$route.path === item.path ? 'text-blue-500' : 'text-gray-400'"
    >
      <span class="text-xl mb-1">{{ item.icon }}</span>
      <span>{{ item.label }}</span>
    </router-link>
  </nav>
  <div class="h-14" />
</template>

<script setup>
const tabs = [
  { path: '/', label: '首页', icon: '🏠' },
  { path: '/consult', label: 'AI咨询', icon: '💊' },
  { path: '/cart', label: '购物车', icon: '🛒' },
  { path: '/orders', label: '订单', icon: '📋' },
  { path: '/profile', label: '我的', icon: '👤' },
]
</script>
