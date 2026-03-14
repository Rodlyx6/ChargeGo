<template>
  <div class="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full">
      <!-- Logo -->
      <div class="text-center mb-8">
        <router-link to="/" class="inline-flex items-center space-x-3">
          <div class="w-12 h-12 bg-gradient-to-br from-primary-500 to-primary-700 rounded-xl flex items-center justify-center">
            <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
            </svg>
          </div>
          <span class="text-3xl font-display font-bold bg-gradient-to-r from-primary-600 to-primary-800 bg-clip-text text-transparent">
            ChargeGo
          </span>
        </router-link>
        <h2 class="mt-6 text-3xl font-bold text-gray-900">登录账号</h2>
        <p class="mt-2 text-sm text-gray-600">
          还没有账号？
          <router-link to="/register" class="font-medium text-primary-600 hover:text-primary-500">
            立即注册
          </router-link>
        </p>
      </div>

      <!-- Login Form -->
      <div class="card p-8">
        <form @submit.prevent="handleLogin" class="space-y-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">手机号</label>
            <input
              v-model="form.phone"
              type="tel"
              required
              placeholder="请输入手机号"
              class="input-field"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">密码</label>
            <input
              v-model="form.password"
              type="password"
              required
              placeholder="请输入密码"
              class="input-field"
            />
          </div>

          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <input
                v-model="isAdminLogin"
                type="checkbox"
                class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
              />
              <label class="ml-2 block text-sm text-gray-900">
                管理员登录
              </label>
            </div>
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="w-full btn-primary"
          >
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </form>

        <div class="mt-6">
          <div class="relative">
            <div class="absolute inset-0 flex items-center">
              <div class="w-full border-t border-gray-300"></div>
            </div>
            <div class="relative flex justify-center text-sm">
              <span class="px-2 bg-white text-gray-500">快速登录</span>
            </div>
          </div>

          <div class="mt-6 text-center">
            <p class="text-sm text-gray-600">
              测试账号: 13800000000 / 123456 (管理员)
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const form = ref({
  phone: '',
  password: ''
})

const isAdminLogin = ref(false)
const loading = ref(false)

const handleLogin = async () => {
  loading.value = true
  try {
    if (isAdminLogin.value) {
      await authStore.adminLogin(form.value.phone, form.value.password)
      // 管理员直接跳转到管理后台
      router.push('/admin')
    } else {
      await authStore.login(form.value.phone, form.value.password)
      // 普通用户跳转到重定向页面或首页
      const redirect = route.query.redirect || '/'
      router.push(redirect)
    }
  } catch (error) {
    alert(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>
