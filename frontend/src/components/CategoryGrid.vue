<template>
  <div class="cat-section">
    <h3 class="cat-title">药品分类</h3>
    <div class="cat-grid">
      <div v-for="cat in categories" :key="cat.id" class="cat-item" @click="$emit('select', cat)">
        <span class="cat-icon">{{ cat.iconUrl || '💊' }}</span>
        <span class="cat-name">{{ cat.name }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({ categories: { type: Array, default: () => [] } })
defineEmits(['select'])
</script>

<style scoped>
.cat-section { padding: 12px 16px; }
.cat-title { font-size: 16px; font-weight: 600; color: #1f2937; margin: 0 0 12px; }
.cat-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.cat-item { display: flex; flex-direction: column; align-items: center; padding: 12px 8px; background: #fff; border-radius: 12px; cursor: pointer; transition: transform .15s; }
.cat-item:active { transform: scale(.95); }
.cat-icon { font-size: 28px; margin-bottom: 6px; }
.cat-name { font-size: 12px; color: #666; text-align: center; }
</style>
