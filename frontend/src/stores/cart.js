import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  const count = ref(0)

  function setCount(n) { count.value = n }
  function increment() { count.value++ }

  return { count, setCount, increment }
})
