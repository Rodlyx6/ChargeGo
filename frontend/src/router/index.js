import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/stations',
    name: 'Stations',
    component: () => import('@/views/Stations.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/my-orders',
    name: 'MyOrders',
    component: () => import('@/views/MyOrders.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/Dashboard.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/stations',
    name: 'AdminStations',
    component: () => import('@/views/admin/Stations.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // 如果已登录且访问登录/注册页，重定向
  if ((to.name === 'Login' || to.name === 'Register') && authStore.isAuthenticated) {
    // 管理员跳转到管理后台，普通用户跳转到首页
    if (authStore.isAdmin) {
      next({ name: 'Admin' })
    } else {
      next({ name: 'Home' })
    }
    return
  }
  
  // 需要登录的页面
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }
  
  // 需要管理员权限的页面
  if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next({ name: 'Home' })
    return
  }
  
  // 管理员访问普通用户页面，重定向到管理后台
  if (authStore.isAdmin && (to.name === 'Stations' || to.name === 'MyOrders')) {
    next({ name: 'Admin' })
    return
  }
  
  // 普通用户访问首页，如果是管理员则跳转到管理后台
  if (to.name === 'Home' && authStore.isAuthenticated && authStore.isAdmin) {
    next({ name: 'Admin' })
    return
  }
  
  next()
})

export default router
