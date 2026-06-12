<template>
  <header class="fixed top-0 left-0 right-0 z-50 bg-white shadow-sm h-12 flex items-center px-4">
    <slot name="left">
      <button v-if="showBack" class="mr-3 text-gray-600" @click="$router.back()">
        <span class="text-lg">←</span>
      </button>
    </slot>
    <h1 class="text-lg font-semibold text-gray-800 flex-1 truncate">{{ title }}</h1>
    <slot name="right" />
  </header>
  <div class="h-12" />
</template>

<script setup>
defineProps({
  title: { type: String, default: 'AIPS' },
  showBack: { type: Boolean, default: false },
})
</script>
