<template>
  <nav class="bottom-nav">
    <router-link v-for="tab in tabs" :key="tab.path" :to="tab.path"
      class="nav-item" :class="{ active: $route.path === tab.path }">
      <span class="nav-icon">{{ tab.icon }}</span>
      <span class="nav-label">{{ tab.label }}</span>
    </router-link>
  </nav>
  <div class="nav-spacer" />
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

<style scoped>
.bottom-nav {
  position: fixed; bottom: 0; left: 0; right: 0; z-index: 100;
  height: 56px; display: flex; background: #fff; border-top: 1px solid #e5e7eb;
}
.nav-item { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; text-decoration: none; color: #9ca3af; font-size: 11px; }
.nav-item.active { color: #3b82f6; }
.nav-icon { font-size: 22px; }
.nav-label { margin-top: 2px; }
.nav-spacer { height: 56px; }
</style>
