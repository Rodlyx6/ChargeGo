<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-900 via-primary-900 to-slate-900 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8 relative overflow-hidden">
    <!-- 背景装饰 -->
    <div class="absolute inset-0 overflow-hidden">
      <div class="absolute -top-40 -right-40 w-80 h-80 bg-primary-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob"></div>
      <div class="absolute -bottom-40 -left-40 w-80 h-80 bg-blue-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-2000"></div>
    </div>

    <div class="max-w-md w-full relative z-10">
      <!-- Logo -->
      <div class="text-center mb-12">
        <router-link to="/" class="inline-flex items-center space-x-3 group">
          <div class="w-14 h-14 bg-gradient-to-br from-primary-400 to-primary-600 rounded-2xl flex items-center justify-center shadow-lg group-hover:shadow-primary-500/50 transition-all duration-300">
            <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
            </svg>
          </div>
          <span class="text-4xl font-display font-bold bg-gradient-to-r from-primary-300 to-primary-100 bg-clip-text text-transparent">
            ChargeGo
          </span>
        </router-link>
        <h2 class="mt-8 text-3xl font-bold text-white">欢迎登录</h2>
        <p class="mt-3 text-sm text-slate-300">
          还没有账号？
          <router-link to="/register" class="font-semibold text-primary-400 hover:text-primary-300 transition-colors">
            立即注册
          </router-link>
        </p>
      </div>

      <!-- Login Form -->
      <div class="backdrop-blur-xl bg-white/10 border border-white/20 rounded-2xl p-8 shadow-2xl">
        <form @submit.prevent="handleLogin" class="space-y-6">
          <div>
            <label class="block text-sm font-semibold text-white mb-3">手机号</label>
            <input
              v-model="form.phone"
              type="tel"
              required
              placeholder="请输入手机号"
              class="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:border-primary-400 focus:ring-2 focus:ring-primary-400/20 transition-all duration-300"
            />
          </div>

          <div>
            <label class="block text-sm font-semibold text-white mb-3">密码</label>
            <input
              v-model="form.password"
              type="password"
              required
              placeholder="请输入密码"
              class="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:border-primary-400 focus:ring-2 focus:ring-primary-400/20 transition-all duration-300"
            />
          </div>

          <div class="flex items-center space-x-3 pt-2">
            <input
              v-model="isAdminLogin"
              type="checkbox"
              id="admin-login"
              class="w-5 h-5 rounded border-white/20 bg-white/5 text-primary-500 focus:ring-primary-400 cursor-pointer"
            />
            <label for="admin-login" class="text-sm font-medium text-slate-200 cursor-pointer">
              管理员登录
            </label>
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="w-full mt-8 py-3 px-4 bg-gradient-to-r from-primary-500 to-primary-600 hover:from-primary-600 hover:to-primary-700 text-white font-semibold rounded-lg shadow-lg hover:shadow-primary-500/50 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center space-x-2"
          >
            <svg v-if="loading" class="w-5 h-5 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            <span>{{ loading ? '登录中...' : '登录' }}</span>
          </button>
        </form>

        <div class="mt-8">
          <div class="relative">
            <div class="absolute inset-0 flex items-center">
              <div class="w-full border-t border-white/10"></div>
            </div>
            <div class="relative flex justify-center text-sm">
              <span class="px-3 bg-gradient-to-br from-slate-900 via-primary-900 to-slate-900 text-slate-400">测试账号</span>
            </div>
          </div>

          <div class="mt-6 space-y-2 text-center text-sm text-slate-300">
            <p>👤 普通用户: 13800000001 / 123456</p>
            <p>👨‍💼 管理员: 13800000000 / 123456</p>
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
      // 显示成功提示
      showSuccessNotification('管理员登录成功')
      // 跳转到首页
      setTimeout(() => {
        router.push('/')
      }, 800)
    } else {
      await authStore.login(form.value.phone, form.value.password)
      // 显示成功提示
      showSuccessNotification('登录成功')
      // 跳转到重定向页面或首页
      setTimeout(() => {
        const redirect = route.query.redirect || '/'
        router.push(redirect)
      }, 800)
    }
  } catch (error) {
    showErrorNotification(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

const showSuccessNotification = (message) => {
  // 这里可以集成 toast 通知库，暂时用 alert
  console.log('✅', message)
}

const showErrorNotification = (message) => {
  console.error('❌', message)
  alert(message)
}
</script>

<style scoped>
@keyframes blob {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -50px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

.animate-blob {
  animation: blob 7s infinite;
}

.animation-delay-2000 {
  animation-delay: 2s;
}
</style>

