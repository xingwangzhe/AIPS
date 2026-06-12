<template>
  <div>
    <AppHeader title="药品详情" show-back />
    <div v-if="loading" style="text-align:center;padding:80px;color:#9ca3af">加载中...</div>
    <div v-else-if="!drug" style="text-align:center;padding:80px;color:#9ca3af">药品不存在</div>
    <template v-else>
      <div class="detail-img"><span>💊</span></div>
      <div class="detail-info">
        <div style="display:flex;align-items:flex-start;gap:8px;margin-bottom:8px">
          <span :class="drug.isPrescription ? 'tag-rx' : 'tag-otc'">{{ drug.isPrescription ? 'RX 处方药' : 'OTC 非处方药' }}</span>
          <h1 class="detail-name">{{ drug.name }}</h1>
        </div>
        <p style="font-size:14px;color:#9ca3af">{{ drug.genericName }}</p>
        <p style="font-size:12px;color:#9ca3af;margin-top:4px">{{ drug.specification }} · {{ drug.manufacturer }}</p>
        <div style="display:flex;align-items:baseline;gap:8px;margin-top:16px">
          <span style="font-size:24px;font-weight:700;color:#ef4444">{{ formatPrice(drug.price) }}</span>
          <span v-if="drug.originalPrice" style="font-size:14px;color:#9ca3af;text-decoration:line-through">{{ formatPrice(drug.originalPrice) }}</span>
        </div>
        <p :style="{fontSize:'12px',marginTop:'4px',color:drug.stock>0?'#16a34a':'#ef4444'}">{{ drug.stock > 0 ? '库存 '+drug.stock+' 件' : '暂时缺货' }}</p>
      </div>
      <div style="margin-top:12px;background:#fff">
        <el-tabs v-model="activeTab" style="padding:0 16px">
          <el-tab-pane label="适应症" name="indications"><p style="font-size:14px;color:#666;line-height:1.6;white-space:pre-line">{{ drug.indications || '暂无' }}</p></el-tab-pane>
          <el-tab-pane label="用法用量" name="dosage"><p style="font-size:14px;color:#666;line-height:1.6;white-space:pre-line">{{ drug.dosage || '暂无' }}</p></el-tab-pane>
          <el-tab-pane label="禁忌" name="contraindications"><p style="font-size:14px;color:#ef4444;line-height:1.6;white-space:pre-line">{{ drug.contraindications || '暂无' }}</p></el-tab-pane>
          <el-tab-pane label="评价" name="reviews">
            <div v-if="drug.reviews?.length" style="display:flex;flex-direction:column;gap:12px">
              <div v-for="(r,i) in drug.reviews" :key="i" style="border-bottom:1px solid #f3f4f6;padding-bottom:12px">
                <div style="display:flex;align-items:center;gap:8px;margin-bottom:4px">
                  <span style="font-size:14px;font-weight:500">{{ r.userName }}</span>
                  <span style="color:#f59e0b;font-size:12px">{{ '★'.repeat(r.rating) }}{{ '☆'.repeat(5-r.rating) }}</span>
                </div>
                <p style="font-size:14px;color:#666">{{ r.content }}</p>
              </div>
            </div>
            <p v-else style="font-size:14px;color:#9ca3af;padding:16px 0">暂无评价</p>
          </el-tab-pane>
        </el-tabs>
      </div>
      <div v-if="drug" class="detail-bottom-bar">
        <el-button size="large" style="flex:1" @click="$router.push('/consult')">咨询药师</el-button>
        <el-button size="large" type="primary" style="flex:1" :disabled="drug.stock <= 0" @click="handleAddToCart">加入购物车</el-button>
      </div>
    </template>
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
const drug = ref(null); const loading = ref(true); const activeTab = ref('indications')
async function handleAddToCart() { try { await addToCart(drug.value.id, 1); ElMessage.success('已加入购物车') } catch(e) { ElMessage.error(e.message) } }
onMounted(async () => { try { drug.value = await getDrugDetail(route.params.id) } catch { drug.value = null } finally { loading.value = false } })
</script>

<style scoped>
.detail-img { background:#fff;padding:24px;display:flex;justify-content:center; }
.detail-img span { font-size:80px; }
.detail-info { background:#fff;padding:0 16px 16px; }
.detail-name { font-size:18px;font-weight:700;color:#1f2937;margin:0; }
.tag-rx, .tag-otc { font-size:11px;padding:2px 6px;border-radius:3px;font-weight:500; }
.tag-rx { background:#fee2e2;color:#dc2626; }
.tag-otc { background:#dcfce7;color:#16a34a; }
.detail-bottom-bar { position:fixed;bottom:0;left:0;right:0;background:#fff;border-top:1px solid #e5e7eb;padding:12px 16px;display:flex;gap:12px;padding-bottom:max(12px, env(safe-area-inset-bottom)); }
</style>
