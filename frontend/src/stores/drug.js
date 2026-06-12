import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as drugApi from '@/api/drug.js'

export const useDrugStore = defineStore('drug', () => {
  const categories = ref([])
  const hotSearches = ref(['感冒', '布洛芬', '高血压', '维生素C', '蒙脱石散'])

  async function fetchCategories() {
    try {
      categories.value = await drugApi.getCategories()
    } catch {
      categories.value = []
    }
  }

  return { categories, hotSearches, fetchCategories }
})
