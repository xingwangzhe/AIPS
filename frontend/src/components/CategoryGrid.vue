<template>
  <div class="px-4 py-3">
    <h3 class="text-base font-semibold text-gray-800 mb-3">药品分类</h3>
    <div class="grid grid-cols-4 gap-3">
      <div
        v-for="cat in categories"
        :key="cat.id"
        class="flex flex-col items-center p-3 bg-white rounded-xl shadow-sm active:scale-95 transition"
        @click="$emit('select', cat)"
      >
        <span class="text-2xl mb-1.5">{{ cat.iconUrl || '💊' }}</span>
        <span class="text-xs text-gray-600 text-center">{{ cat.name }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  categories: { type: Array, default: () => [] },
})
defineEmits(['select'])
</script>
