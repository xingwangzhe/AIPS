<template>
  <div class="pb-4">
    <AppHeader title="AIPS 线上购药" />

    <!-- Banner -->
    <div class="px-4 py-2">
      <div class="bg-gradient-to-r from-blue-500 to-cyan-400 rounded-xl p-5 text-white shadow-md">
        <h2 class="text-lg font-bold">AI 智能药师</h2>
        <p class="text-sm text-blue-100 mt-1">7×24 小时用药咨询 · 症状自查 · 处方审核</p>
        <el-button
          size="small"
          class="mt-3 !bg-white !text-blue-500 !border-0"
          @click="$router.push('/consult')"
        >
          立即咨询 →
        </el-button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <SearchBar />

    <!-- 分类 -->
    <CategoryGrid :categories="categories" @select="handleCategorySelect" />

    <!-- 热门搜索 -->
    <div class="px-4 py-2">
      <h3 class="text-base font-semibold text-gray-800 mb-3">热门搜索</h3>
      <div class="flex flex-wrap gap-2">
        <span
          v-for="kw in hotSearches"
          :key="kw"
          class="px-3 py-1.5 bg-white rounded-full text-sm text-gray-600 shadow-sm active:bg-blue-50 active:text-blue-500 cursor-pointer"
          @click="handleSearch(kw)"
        >{{ kw }}</span>
      </div>
    </div>

    <!-- 推荐药品占位 -->
    <div class="px-4 pt-2">
      <h3 class="text-base font-semibold text-gray-800 mb-3">为您推荐</h3>
      <p class="text-sm text-gray-400 text-center py-8">连接后端获取推荐数据...</p>
    </div>

    <BottomNav />
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useDrugStore } from '@/stores/drug.js'
import AppHeader from '@/components/AppHeader.vue'
import BottomNav from '@/components/BottomNav.vue'
import SearchBar from '@/components/SearchBar.vue'
import CategoryGrid from '@/components/CategoryGrid.vue'

const router = useRouter()
const drugStore = useDrugStore()

const categories = computed(() => drugStore.categories)
const hotSearches = computed(() => drugStore.hotSearches)

function handleCategorySelect(cat) {
  router.push(`/search?categoryId=${cat.id}`)
}

function handleSearch(kw) {
  router.push(`/search?keyword=${kw}`)
}

onMounted(() => {
  drugStore.fetchCategories()
})
</script>
