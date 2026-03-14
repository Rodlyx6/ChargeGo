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
        <h2 class="mt-6 text-3xl font-bold text-gray-900">注册账号</h2>
        <p class="mt-2 text-sm text-gray-600">
          已有账号？
          <router-link to="/login" class="font-medium text-primary-600 hover:text-primary-500">
            立即登录
          </router-link>
        </p>
      </div>

      <!-- Register Form -->
      <div class="card p-8">
        <form @submit.prevent="handleRegister" class="space-y-6">
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
              placeholder="请输入密码（至少6位）"
              minlength="6"
              class="input-field"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">昵称（可选）</label>
            <input
              v-model="form.nickname"
              type="text"
              placeholder="请输入昵称"
              class="input-field"
            />
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="w-full btn-primary"
          >
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  phone: '',
  password: '',
  nickname: ''
})

const loading = ref(false)

const handleRegister = async () => {
  loading.value = true
  try {
    await authStore.register(form.value.phone, form.value.password, form.value.nickname)
    alert('注册成功！请登录')
    router.push('/login')
  } catch (error) {
    alert(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>
