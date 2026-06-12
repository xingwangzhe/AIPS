<template>
  <div>
    <AppHeader title="搜索药品" show-back />
    <div class="search-input-row">
      <el-input v-model="keyword" placeholder="输入药品名、症状、拼音..." size="large" clearable @clear="doSearch" @keyup.enter="doSearch" />
      <el-button type="primary" size="large" @click="doSearch">搜索</el-button>
    </div>
    <div class="filter-row">
      <el-select v-model="categoryId" placeholder="分类" size="small" clearable style="width:120px">
        <el-option v-for="c in cats" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-select v-model="isRx" placeholder="药品类型" size="small" clearable style="width:120px">
        <el-option label="OTC" :value="0" /><el-option label="RX" :value="1" />
      </el-select>
      <el-select v-model="sort" placeholder="排序" size="small" style="width:120px">
        <el-option label="默认" value="" /><el-option label="价格↑" value="price_asc" /><el-option label="价格↓" value="price_desc" />
      </el-select>
    </div>
    <div v-if="loading" style="text-align:center;padding:40px;color:#9ca3af">搜索中...</div>
    <div v-else-if="list.length === 0" style="text-align:center;padding:40px;color:#9ca3af">{{ keyword ? '未找到相关药品' : '请输入关键词搜索' }}</div>
    <div v-else style="padding:0 16px;display:flex;flex-direction:column;gap:12px">
      <DrugCard v-for="drug in list" :key="drug.id" :drug="drug" />
    </div>
    <div v-if="total > pageSize" style="display:flex;justify-content:center;padding:16px">
      <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="doSearch" />
    </div>
    <BottomNav />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useDrugStore } from '@/stores/drug.js'
import { searchDrugs } from '@/api/drug.js'
import AppHeader from '@/components/AppHeader.vue'
import BottomNav from '@/components/BottomNav.vue'
import DrugCard from '@/components/DrugCard.vue'

const route = useRoute(); const drugStore = useDrugStore()
const keyword = ref(route.query.keyword || '')
const categoryId = ref(route.query.categoryId ? Number(route.query.categoryId) : null)
const isRx = ref(null); const sort = ref(''); const page = ref(1); const pageSize = ref(20)
const loading = ref(false); const list = ref([]); const total = ref(0); const cats = ref([])

async function doSearch() {
  loading.value = true
  try {
    const r = await searchDrugs({ keyword: keyword.value, categoryId: categoryId.value, isRx: isRx.value, sort: sort.value, page: page.value, pageSize: pageSize.value })
    list.value = r.list || []; total.value = r.total || 0
  } catch { list.value = [] } finally { loading.value = false }
}
onMounted(async () => { await drugStore.fetchCategories(); cats.value = drugStore.categories; if (keyword.value) doSearch() })
</script>

<style scoped>
.search-input-row { display: flex; gap: 8px; padding: 8px 16px; }
.filter-row { display: flex; gap: 8px; padding: 0 16px 8px; flex-wrap: wrap; }
</style>
