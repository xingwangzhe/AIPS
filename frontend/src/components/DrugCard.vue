<template>
  <div
    class="bg-white rounded-xl shadow-sm p-3 flex gap-3 active:scale-[0.98] transition cursor-pointer"
    @click="$router.push(`/drug/${drug.id}`)"
  >
    <div class="w-20 h-20 bg-gray-100 rounded-lg flex items-center justify-center flex-shrink-0">
      <span class="text-3xl">💊</span>
    </div>
    <div class="flex-1 min-w-0">
      <div class="flex items-start gap-1">
        <span
          v-if="drug.isPrescription"
          class="text-xs bg-red-100 text-red-600 px-1 rounded font-medium flex-shrink-0"
        >RX</span>
        <span
          v-else
          class="text-xs bg-green-100 text-green-600 px-1 rounded font-medium flex-shrink-0"
        >OTC</span>
        <h4 class="text-sm font-medium text-gray-800 truncate">{{ drug.name }}</h4>
      </div>
      <p class="text-xs text-gray-400 mt-1 truncate">{{ drug.specification }}</p>
      <div class="flex items-center justify-between mt-2">
        <span class="text-base font-bold text-red-500">{{ formatPrice(drug.price) }}</span>
        <span v-if="drug.originalPrice" class="text-xs text-gray-400 line-through ml-1">{{ formatPrice(drug.originalPrice) }}</span>
        <span v-if="drug.stock <= 0" class="text-xs text-red-400 ml-auto">缺货</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { formatPrice } from '@/utils/format.js'

defineProps({
  drug: { type: Object, required: true },
})
</script>
