<template>
  <div>
    <AppHeader title="购物车" />
    <div v-if="items.length === 0" style="text-align:center;padding-top:80px;color:#9ca3af">
      <p style="font-size:48px;margin:0">🛒</p>
      <p style="font-size:16px;margin:16px 0">购物车是空的</p>
      <el-button type="primary" @click="$router.push('/')">去逛逛</el-button>
    </div>
    <div v-else style="padding:12px 16px;display:flex;flex-direction:column;gap:12px">
      <div v-for="item in items" :key="item.id" class="cart-item">
        <el-checkbox v-model="item.checked" />
        <div class="cart-img"><span>💊</span></div>
        <div class="cart-info">
          <h4 class="cart-name">{{ item.name }}</h4>
          <p style="font-size:12px;color:#9ca3af">{{ item.spec }}</p>
          <span class="cart-price">{{ formatPrice(item.price) }}</span>
        </div>
        <div style="display:flex;align-items:center;gap:4px">
          <el-button size="small" circle @click="item.qty > 1 && item.qty--">−</el-button>
          <span style="width:28px;text-align:center;font-size:14px">{{ item.qty }}</span>
          <el-button size="small" circle @click="item.qty++">+</el-button>
        </div>
      </div>
    </div>
    <div v-if="items.length" class="cart-bottom">
      <span style="font-size:14px;color:#666">合计 <span class="cart-total">{{ formatPrice(total) }}</span></span>
      <el-button type="primary" size="large" @click="$router.push('/order/confirm')">去结算</el-button>
    </div>
    <BottomNav />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { formatPrice } from '@/utils/format.js'
import AppHeader from '@/components/AppHeader.vue'
import BottomNav from '@/components/BottomNav.vue'

const items = ref([
  { id: 1, name: '布洛芬缓释胶囊', spec: '0.3g*20粒', price: 19.90, qty: 2, checked: true },
  { id: 2, name: '维生素C泡腾片', spec: '1g*20片', price: 45.00, qty: 1, checked: true },
])
const total = computed(() => items.value.filter(i => i.checked).reduce((s,i) => s + i.price * i.qty, 0))
</script>

<style scoped>
.cart-item { background:#fff;border-radius:12px;padding:12px;display:flex;align-items:center;gap:12px; }
.cart-img { width:60px;height:60px;background:#f3f4f6;border-radius:8px;display:flex;align-items:center;justify-content:center;font-size:28px;flex-shrink:0; }
.cart-info { flex:1;min-width:0; }
.cart-name { font-size:14px;font-weight:500;color:#1f2937;margin:0;white-space:nowrap;overflow:hidden;text-overflow:ellipsis; }
.cart-price { font-size:15px;font-weight:700;color:#ef4444; }
.cart-bottom { position:fixed;bottom:0;left:0;right:0;background:#fff;border-top:1px solid #e5e7eb;padding:12px 16px;display:flex;justify-content:space-between;align-items:center; }
.cart-total { font-size:20px;font-weight:700;color:#ef4444; }
</style>
