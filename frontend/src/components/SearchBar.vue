<template>
  <div class="search-bar" @click="$router.push('/search')">
    <span class="search-icon">🔍</span>
    <span class="search-placeholder">搜索药品、症状...</span>
  </div>
</template>

<style scoped>
.search-bar { margin: 8px 16px; display: flex; align-items: center; background: #fff; border-radius: 24px; padding: 10px 16px; box-shadow: 0 1px 2px rgba(0,0,0,.05); cursor: pointer; }
.search-icon { margin-right: 8px; }
.search-placeholder { color: #9ca3af; font-size: 14px; }
</style>
