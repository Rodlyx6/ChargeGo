import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as authApi from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 1)

  const setAuth = (authData) => {
    console.log('🔐 setAuth 接收到的数据:', authData)
    
    if (!authData || !authData.token) {
      console.error('❌ authData 或 token 为空:', authData)
      return
    }
    
    token.value = authData.token
    user.value = {
      userId: authData.userId,
      phone: authData.phone,
      nickname: authData.nickname,
      role: authData.role
    }
    localStorage.setItem('token', authData.token)
    localStorage.setItem('user', JSON.stringify(user.value))
    
    console.log('✅ Token 已保存:', token.value)
    console.log('✅ User 已保存:', user.value)
  }

  const clearAuth = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  const login = async (phone, password) => {
    console.log('📞 开始登录:', phone)
    const res = await authApi.login(phone, password)
    console.log('📦 登录响应:', res)
    console.log('📦 响应数据 res.data:', res.data)
    setAuth(res.data)
    return res
  }

  const adminLogin = async (phone, password) => {
    const res = await authApi.adminLogin(phone, password)
    setAuth(res.data)
    return res
  }

  const register = async (phone, password, nickname) => {
    return await authApi.register(phone, password, nickname)
  }

  const logout = () => {
    clearAuth()
  }

  const checkAuth = () => {
    if (!token.value || !user.value) {
      clearAuth()
    }
  }

  return {
    token,
    user,
    isAuthenticated,
    isAdmin,
    login,
    adminLogin,
    register,
    logout,
    checkAuth
  }
})
