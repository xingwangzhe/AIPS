<template>
  <div class="drug-card" @click="$router.push(`/drug/${drug.id}`)">
    <div class="drug-img"><span>💊</span></div>
    <div class="drug-info">
      <div class="drug-tags">
        <span :class="drug.isPrescription ? 'tag-rx' : 'tag-otc'">{{ drug.isPrescription ? 'RX' : 'OTC' }}</span>
        <h4 class="drug-name">{{ drug.name }}</h4>
      </div>
      <p class="drug-spec">{{ drug.specification }}</p>
      <div class="drug-price-row">
        <span class="drug-price">{{ formatPrice(drug.price) }}</span>
        <span v-if="drug.originalPrice" class="drug-original">{{ formatPrice(drug.originalPrice) }}</span>
        <span v-if="drug.stock <= 0" class="drug-oos">缺货</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { formatPrice } from '@/utils/format.js'
defineProps({ drug: { type: Object, required: true } })
</script>

<style scoped>
.drug-card { display: flex; gap: 12px; background: #fff; border-radius: 12px; padding: 12px; cursor: pointer; transition: transform .15s; }
.drug-card:active { transform: scale(.98); }
.drug-img { width: 80px; height: 80px; background: #f3f4f6; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 36px; flex-shrink: 0; }
.drug-info { flex: 1; min-width: 0; }
.drug-tags { display: flex; align-items: flex-start; gap: 6px; }
.tag-rx, .tag-otc { font-size: 11px; padding: 1px 4px; border-radius: 3px; font-weight: 500; }
.tag-rx { background: #fee2e2; color: #dc2626; }
.tag-otc { background: #dcfce7; color: #16a34a; }
.drug-name { font-size: 14px; font-weight: 500; color: #1f2937; margin: 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.drug-spec { font-size: 12px; color: #9ca3af; margin: 4px 0; }
.drug-price-row { display: flex; align-items: baseline; gap: 6px; margin-top: 4px; }
.drug-price { font-size: 17px; font-weight: 700; color: #ef4444; }
.drug-original { font-size: 12px; color: #9ca3af; text-decoration: line-through; }
.drug-oos { font-size: 12px; color: #ef4444; margin-left: auto; }
</style>
