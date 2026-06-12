<template>
  <div>
    <AppHeader title="AIPS 线上购药" />
    <div class="hero-banner">
      <h2>AI 智能药师</h2>
      <p>7×24 小时用药咨询 · 症状自查 · 处方审核</p>
      <el-button style="margin-top:12px;background:#fff;color:#2563eb;border:none" @click="$router.push('/consult')">立即咨询 →</el-button>
    </div>
    <SearchBar />
    <CategoryGrid :categories="categories" @select="c => $router.push(`/search?categoryId=${c.id}`)" />
    <div class="hot-section">
      <h3>热门搜索</h3>
      <div class="hot-tags">
        <span v-for="kw in hotSearches" :key="kw" class="hot-tag" @click="$router.push(`/search?keyword=${kw}`)">{{ kw }}</span>
      </div>
    </div>
    <div class="rec-section">
      <h3>为您推荐</h3>
      <p style="text-align:center;color:#9ca3af;padding:32px 0">连接后端获取推荐数据...</p>
    </div>
    <BottomNav />
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useDrugStore } from '@/stores/drug.js'
import AppHeader from '@/components/AppHeader.vue'
import BottomNav from '@/components/BottomNav.vue'
import SearchBar from '@/components/SearchBar.vue'
import CategoryGrid from '@/components/CategoryGrid.vue'

const drugStore = useDrugStore()
const categories = computed(() => drugStore.categories)
const hotSearches = computed(() => drugStore.hotSearches)
onMounted(() => drugStore.fetchCategories())
</script>

<style scoped>
.hero-banner { margin: 8px 16px; padding: 24px; background: linear-gradient(135deg, #3b82f6, #06b6d4); border-radius: 12px; color: #fff; }
.hero-banner h2 { font-size: 20px; font-weight: 700; margin: 0; }
.hero-banner p { font-size: 14px; opacity: .85; margin: 4px 0 0; }
.hot-section, .rec-section { padding: 8px 16px; }
.hot-section h3, .rec-section h3 { font-size: 16px; font-weight: 600; color: #1f2937; margin-bottom: 12px; }
.hot-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.hot-tag { padding: 6px 16px; background: #fff; border-radius: 20px; font-size: 14px; color: #666; cursor: pointer; box-shadow: 0 1px 2px rgba(0,0,0,.05); }
.hot-tag:active { background: #eff6ff; color: #3b82f6; }
</style>
