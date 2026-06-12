<template>
  <div class="pb-20">
    <AppHeader title="药品详情" show-back />

    <div v-if="loading" class="text-center py-20 text-gray-400">加载中...</div>
    <div v-else-if="!drug" class="text-center py-20 text-gray-400">药品不存在</div>
    <template v-else>
      <!-- 药品图片 -->
      <div class="bg-white p-6 flex justify-center">
        <div class="w-40 h-40 bg-gray-100 rounded-xl flex items-center justify-center">
          <span class="text-6xl">💊</span>
        </div>
      </div>

      <!-- 基本信息 -->
      <div class="bg-white px-4 pb-4">
        <div class="flex items-start gap-2 mb-2">
          <span :class="drug.isPrescription ? 'bg-red-100 text-red-600' : 'bg-green-100 text-green-600'"
                class="text-xs px-2 py-0.5 rounded font-medium mt-1">
            {{ drug.isPrescription ? 'RX 处方药' : 'OTC 非处方药' }}
          </span>
          <h1 class="text-lg font-bold text-gray-800">{{ drug.name }}</h1>
        </div>
        <p class="text-sm text-gray-400">{{ drug.genericName }}</p>
        <p class="text-xs text-gray-400 mt-1">{{ drug.specification }} · {{ drug.manufacturer }}</p>

        <div class="flex items-baseline gap-2 mt-4">
          <span class="text-2xl font-bold text-red-500">{{ formatPrice(drug.price) }}</span>
          <span v-if="drug.originalPrice" class="text-sm text-gray-400 line-through">{{ formatPrice(drug.originalPrice) }}</span>
        </div>
        <p class="text-xs mt-1" :class="drug.stock > 0 ? 'text-green-500' : 'text-red-500'">
          {{ drug.stock > 0 ? `库存 ${drug.stock} 件` : '暂时缺货' }}
        </p>
      </div>

      <!-- 详细信息 Tab -->
      <div class="mt-3 bg-white">
        <el-tabs v-model="activeTab" class="px-4">
          <el-tab-pane label="适应症" name="indications">
            <p class="text-sm text-gray-600 leading-relaxed whitespace-pre-line">{{ drug.indications || '暂无' }}</p>
          </el-tab-pane>
          <el-tab-pane label="用法用量" name="dosage">
            <p class="text-sm text-gray-600 leading-relaxed whitespace-pre-line">{{ drug.dosage || '暂无' }}</p>
          </el-tab-pane>
          <el-tab-pane label="禁忌" name="contraindications">
            <p class="text-sm text-red-500 leading-relaxed whitespace-pre-line">{{ drug.contraindications || '暂无' }}</p>
          </el-tab-pane>
          <el-tab-pane label="评价" name="reviews">
            <div v-if="drug.reviews?.length" class="flex flex-col gap-3">
              <div v-for="(r, i) in drug.reviews" :key="i" class="border-b border-gray-100 pb-3">
                <div class="flex items-center gap-2 mb-1">
                  <span class="text-sm font-medium">{{ r.userName }}</span>
                  <span class="text-yellow-500 text-xs">{{ '★'.repeat(r.rating) }}{{ '☆'.repeat(5 - r.rating) }}</span>
                </div>
                <p class="text-sm text-gray-600">{{ r.content }}</p>
              </div>
            </div>
            <p v-else class="text-sm text-gray-400 py-4">暂无评价</p>
          </el-tab-pane>
        </el-tabs>
      </div>
    </template>

    <!-- 底部操作栏 -->
    <div v-if="drug" class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 p-3 flex gap-3 safe-area-bottom">
      <el-button size="large" class="flex-1" @click="$router.push('/consult')">咨询药师</el-button>
      <el-button size="large" type="primary" class="flex-1" :disabled="drug.stock <= 0" @click="handleAddToCart">
        加入购物车
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getDrugDetail } from '@/api/drug.js'
import { addToCart } from '@/api/order.js'
import { formatPrice } from '@/utils/format.js'
import { ElMessage } from 'element-plus'
import AppHeader from '@/components/AppHeader.vue'

const route = useRoute()
const drug = ref(null)
const loading = ref(true)
const activeTab = ref('indications')

async function handleAddToCart() {
  try {
    await addToCart(drug.value.id, 1)
    ElMessage.success('已加入购物车')
  } catch (e) {
    ElMessage.error(e.message || '添加失败')
  }
}

onMounted(async () => {
  try {
    drug.value = await getDrugDetail(route.params.id)
  } catch {
    drug.value = null
  } finally {
    loading.value = false
  }
})
</script>
