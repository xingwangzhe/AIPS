import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { guest: true },
  },
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/HomeView.vue'),
  },
  {
    path: '/search',
    name: 'search',
    component: () => import('@/views/DrugSearchView.vue'),
  },
  {
    path: '/drug/:id',
    name: 'drug-detail',
    component: () => import('@/views/DrugDetailView.vue'),
  },
  {
    path: '/consult',
    name: 'consult',
    component: () => import('@/views/ConsultView.vue'),
    meta: { auth: true },
  },
  {
    path: '/prescription',
    name: 'prescription',
    component: () => import('@/views/PrescriptionView.vue'),
    meta: { auth: true },
  },
  {
    path: '/cart',
    name: 'cart',
    component: () => import('@/views/CartView.vue'),
    meta: { auth: true },
  },
  {
    path: '/order/confirm',
    name: 'order-confirm',
    component: () => import('@/views/OrderConfirmView.vue'),
    meta: { auth: true },
  },
  {
    path: '/orders',
    name: 'orders',
    component: () => import('@/views/OrderListView.vue'),
    meta: { auth: true },
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('@/views/ProfileView.vue'),
    meta: { auth: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  },
})

// 路由守卫：未登录跳登录页
router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.auth && !auth.isLoggedIn) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
})

export default router
