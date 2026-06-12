<template>
  <div class="pb-4">
    <AppHeader title="搜索药品" show-back />

    <!-- 搜索输入栏 -->
    <div class="px-4 py-2">
      <div class="flex gap-2">
        <el-input
          v-model="keyword"
          placeholder="输入药品名、症状、拼音..."
          size="large"
          clearable
          @clear="doSearch"
          @keyup.enter="doSearch"
        />
        <el-button type="primary" size="large" @click="doSearch">搜索</el-button>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="px-4 pb-2 flex gap-2 flex-wrap">
      <el-select v-model="categoryId" placeholder="分类" size="small" clearable style="width: 120px">
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-select v-model="isRx" placeholder="药品类型" size="small" clearable style="width: 120px">
        <el-option label="全部" :value="null" />
        <el-option label="OTC" :value="0" />
        <el-option label="RX" :value="1" />
      </el-select>
      <el-select v-model="sort" placeholder="排序" size="small" style="width: 120px">
        <el-option label="默认" value="" />
        <el-option label="价格↑" value="price_asc" />
        <el-option label="价格↓" value="price_desc" />
      </el-select>
    </div>

    <!-- 结果列表 -->
    <div v-if="loading" class="text-center py-10 text-gray-400">搜索中...</div>
    <div v-else-if="list.length === 0" class="text-center py-10 text-gray-400">
      {{ keyword ? '未找到相关药品' : '请输入关键词搜索' }}
    </div>
    <div v-else class="px-4 flex flex-col gap-3">
      <DrugCard v-for="drug in list" :key="drug.id" :drug="drug" />
    </div>

    <!-- 分页 -->
    <div v-if="total > pageSize" class="flex justify-center py-4">
      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="doSearch"
      />
    </div>

    <BottomNav />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDrugStore } from '@/stores/drug.js'
import { searchDrugs } from '@/api/drug.js'
import AppHeader from '@/components/AppHeader.vue'
import BottomNav from '@/components/BottomNav.vue'
import DrugCard from '@/components/DrugCard.vue'

const route = useRoute()
const router = useRouter()
const drugStore = useDrugStore()

const keyword = ref(route.query.keyword || '')
const categoryId = ref(route.query.categoryId ? Number(route.query.categoryId) : null)
const isRx = ref(null)
const sort = ref('')
const page = ref(1)
const pageSize = ref(20)
const loading = ref(false)
const list = ref([])
const total = ref(0)
const categories = ref([])

async function doSearch() {
  loading.value = true
  try {
    const result = await searchDrugs({
      keyword: keyword.value,
      categoryId: categoryId.value,
      isRx: isRx.value,
      sort: sort.value,
      page: page.value,
      pageSize: pageSize.value,
    })
    list.value = result.list || []
    total.value = result.total || 0
  } catch {
    list.value = []
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await drugStore.fetchCategories()
  categories.value = drugStore.categories
  if (keyword.value) doSearch()
})
</script>
