<template>
  <header class="app-header">
    <slot name="left">
      <button v-if="showBack" class="back-btn" @click="$router.back()">&larr;</button>
    </slot>
    <h1 class="header-title">{{ title }}</h1>
    <slot name="right" />
  </header>
  <div class="header-spacer" />
</template>

<script setup>
defineProps({ title: { type: String, default: 'AIPS' }, showBack: { type: Boolean, default: false } })
</script>

<style scoped>
.app-header {
  position: fixed; top: 0; left: 0; right: 0; z-index: 100;
  height: 48px; display: flex; align-items: center; padding: 0 16px;
  background: #fff; box-shadow: 0 1px 3px rgba(0,0,0,.08);
}
.header-title { font-size: 17px; font-weight: 600; color: #1f2937; flex: 1; margin: 0; }
.back-btn { border: none; background: none; font-size: 20px; color: #666; margin-right: 12px; cursor: pointer; }
.header-spacer { height: 48px; }
</style>
