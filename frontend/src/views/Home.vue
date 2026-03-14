<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-white to-green-50">
    <!-- Navigation Header -->
    <header class="bg-white shadow-sm sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <router-link to="/" class="flex items-center space-x-3">
            <div class="w-10 h-10 bg-gradient-to-br from-primary-500 to-primary-700 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
            </div>
            <span class="text-2xl font-display font-bold bg-gradient-to-r from-primary-600 to-primary-800 bg-clip-text text-transparent">
              ChargeGo
            </span>
          </router-link>

          <!-- Navigation for Normal Users -->
          <nav v-if="isAuthenticated && !isAdmin" class="hidden md:flex items-center space-x-8">
            <router-link to="/" class="text-gray-700 hover:text-primary-600 font-medium transition-colors">首页</router-link>
            <router-link to="/stations" class="text-gray-700 hover:text-primary-600 font-medium transition-colors">找充电桩</router-link>
            <router-link to="/my-orders" class="text-gray-700 hover:text-primary-600 font-medium transition-colors">我的订单</router-link>
          </nav>

          <!-- Navigation for Admin -->
          <nav v-if="isAuthenticated && isAdmin" class="hidden md:flex items-center space-x-8">
            <router-link to="/admin" class="text-gray-700 hover:text-primary-600 font-medium transition-colors">管理概览</router-link>
            <router-link to="/admin/stations" class="text-gray-700 hover:text-primary-600 font-medium transition-colors">充电桩管理</router-link>
          </nav>

          <div class="flex items-center space-x-4">
            <template v-if="isAuthenticated">
              <div class="flex items-center space-x-3">
                <div class="hidden sm:block text-right">
                  <p class="text-sm font-semibold text-gray-900">{{ user?.nickname }}</p>
                  <p class="text-xs text-gray-500">{{ user?.phone }}</p>
                </div>
                <button @click="handleLogout" class="btn-ghost text-sm">退出登录</button>
              </div>
            </template>
            <template v-else>
              <router-link to="/login" class="btn-ghost text-sm">登录</router-link>
              <router-link to="/register" class="btn-primary text-sm">注册</router-link>
            </template>
          </div>
        </div>
      </div>
    </header>

    <!-- Hero Section -->
    <section class="relative overflow-hidden bg-gradient-to-br from-primary-600 via-primary-700 to-blue-900 text-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 relative">
        <div class="grid md:grid-cols-2 gap-12 items-center">
          <div class="space-y-8 animate-fade-in">
            <div class="inline-block">
              <span class="px-4 py-2 bg-white/20 backdrop-blur-sm rounded-full text-sm font-semibold">
                ⚡ 智能充电新体验
              </span>
            </div>
            <h1 class="text-5xl md:text-6xl font-display font-bold leading-tight">
              找桩快<br/>
              <span class="text-yellow-300">充电更快</span>
            </h1>
            <p class="text-xl text-blue-100 leading-relaxed">
              实时查询附近充电桩，一键预约锁桩，告别排队等待。让每一次充电都轻松便捷。
            </p>
            <div class="flex flex-wrap gap-4">
              <router-link to="/stations" class="px-8 py-4 bg-white text-primary-700 font-bold rounded-full hover:bg-yellow-300 hover:text-primary-900 transition-all duration-200 shadow-xl hover:shadow-2xl active:scale-95">
                立即找桩
              </router-link>
            </div>
            
            <div class="grid grid-cols-3 gap-6 pt-8">
              <div class="text-center">
                <div class="text-3xl font-bold text-yellow-300">1000+</div>
                <div class="text-sm text-blue-200 mt-1">充电桩</div>
              </div>
              <div class="text-center">
                <div class="text-3xl font-bold text-yellow-300">50K+</div>
                <div class="text-sm text-blue-200 mt-1">用户</div>
              </div>
              <div class="text-center">
                <div class="text-3xl font-bold text-yellow-300">98%</div>
                <div class="text-sm text-blue-200 mt-1">满意度</div>
              </div>
            </div>
          </div>

          <div class="relative animate-slide-up hidden md:block">
            <div class="relative z-10">
              <img src="https://images.unsplash.com/photo-1593941707882-a5bba14938c7?w=600&h=600&fit=crop" 
                   alt="Electric Car Charging" 
                   class="rounded-3xl shadow-2xl transform hover:scale-105 transition-transform duration-300" />
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="py-20 bg-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-16">
          <h2 class="text-4xl font-display font-bold text-gray-900 mb-4">为什么选择 ChargeGo</h2>
          <p class="text-xl text-gray-600">让充电变得简单高效</p>
        </div>

        <div class="grid md:grid-cols-3 gap-8">
          <div class="card p-8 text-center group hover:scale-105 transition-transform duration-200">
            <div class="w-16 h-16 bg-gradient-to-br from-blue-500 to-blue-600 rounded-2xl flex items-center justify-center mx-auto mb-6 group-hover:rotate-6 transition-transform">
              <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
            </div>
            <h3 class="text-xl font-bold text-gray-900 mb-3">附近搜索</h3>
            <p class="text-gray-600">基于地理位置，快速找到附近可用充电桩，实时显示距离和状态</p>
          </div>

          <div class="card p-8 text-center group hover:scale-105 transition-transform duration-200">
            <div class="w-16 h-16 bg-gradient-to-br from-green-500 to-green-600 rounded-2xl flex items-center justify-center mx-auto mb-6 group-hover:rotate-6 transition-transform">
              <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
              </svg>
            </div>
            <h3 class="text-xl font-bold text-gray-900 mb-3">一键锁桩</h3>
            <p class="text-gray-600">预约即锁定充电桩，15分钟内完成支付，避免到达后无桩可用</p>
          </div>

          <div class="card p-8 text-center group hover:scale-105 transition-transform duration-200">
            <div class="w-16 h-16 bg-gradient-to-br from-yellow-500 to-orange-500 rounded-2xl flex items-center justify-center mx-auto mb-6 group-hover:rotate-6 transition-transform">
              <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
            </div>
            <h3 class="text-xl font-bold text-gray-900 mb-3">快速充电</h3>
            <p class="text-gray-600">支付后立即开始充电，充电完成随时结束，灵活便捷</p>
          </div>
        </div>
      </div>
    </section>

    <!-- How It Works -->
    <section class="py-20 bg-gradient-to-br from-gray-50 to-blue-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-16">
          <h2 class="text-4xl font-display font-bold text-gray-900 mb-4">使用流程</h2>
          <p class="text-xl text-gray-600">三步完成充电预约</p>
        </div>

        <div class="grid md:grid-cols-4 gap-8">
          <div class="text-center">
            <div class="w-20 h-20 bg-primary-600 text-white rounded-full flex items-center justify-center text-3xl font-bold mx-auto mb-4">1</div>
            <h3 class="font-bold text-lg mb-2">搜索充电桩</h3>
            <p class="text-gray-600 text-sm">查看附近可用充电桩</p>
          </div>
          <div class="text-center">
            <div class="w-20 h-20 bg-primary-600 text-white rounded-full flex items-center justify-center text-3xl font-bold mx-auto mb-4">2</div>
            <h3 class="font-bold text-lg mb-2">预约锁桩</h3>
            <p class="text-gray-600 text-sm">选择充电桩并预约</p>
          </div>
          <div class="text-center">
            <div class="w-20 h-20 bg-primary-600 text-white rounded-full flex items-center justify-center text-3xl font-bold mx-auto mb-4">3</div>
            <h3 class="font-bold text-lg mb-2">完成支付</h3>
            <p class="text-gray-600 text-sm">15分钟内支付订单</p>
          </div>
          <div class="text-center">
            <div class="w-20 h-20 bg-primary-600 text-white rounded-full flex items-center justify-center text-3xl font-bold mx-auto mb-4">4</div>
            <h3 class="font-bold text-lg mb-2">开始充电</h3>
            <p class="text-gray-600 text-sm">到达后即可充电</p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="py-20 bg-gradient-to-r from-primary-600 to-blue-700 text-white">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 class="text-4xl font-display font-bold mb-6">准备好开始了吗？</h2>
        <p class="text-xl text-blue-100 mb-8">立即注册，享受便捷充电服务</p>
        <div class="flex justify-center gap-4">
          <router-link to="/register" class="px-8 py-4 bg-white text-primary-700 font-bold rounded-full hover:bg-yellow-300 hover:text-primary-900 transition-all duration-200 shadow-xl hover:shadow-2xl active:scale-95">
            免费注册
          </router-link>
          <router-link to="/stations" class="px-8 py-4 bg-white/10 backdrop-blur-sm text-white font-semibold rounded-full border-2 border-white/30 hover:bg-white/20 transition-all duration-200 active:scale-95">
            查看充电桩
          </router-link>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="bg-gray-900 text-gray-300 py-12">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid md:grid-cols-4 gap-8 mb-8">
          <div>
            <div class="flex items-center space-x-2 mb-4">
              <div class="w-8 h-8 bg-gradient-to-br from-primary-500 to-primary-700 rounded-lg flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
              </div>
              <span class="text-xl font-display font-bold text-white">ChargeGo</span>
            </div>
            <p class="text-sm text-gray-400">让充电变得简单高效</p>
          </div>
          <div>
            <h4 class="font-semibold text-white mb-4">产品</h4>
            <ul class="space-y-2 text-sm">
              <li><a href="#" class="hover:text-white transition-colors">找充电桩</a></li>
              <li><a href="#" class="hover:text-white transition-colors">预约服务</a></li>
            </ul>
          </div>
          <div>
            <h4 class="font-semibold text-white mb-4">支持</h4>
            <ul class="space-y-2 text-sm">
              <li><a href="#" class="hover:text-white transition-colors">帮助中心</a></li>
              <li><a href="#" class="hover:text-white transition-colors">联系我们</a></li>
            </ul>
          </div>
          <div>
            <h4 class="font-semibold text-white mb-4">关于</h4>
            <ul class="space-y-2 text-sm">
              <li><a href="#" class="hover:text-white transition-colors">关于我们</a></li>
              <li><a href="#" class="hover:text-white transition-colors">隐私政策</a></li>
            </ul>
          </div>
        </div>
        <div class="border-t border-gray-800 pt-8 text-center text-sm text-gray-400">
          <p>&copy; 2024 ChargeGo. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const isAuthenticated = computed(() => authStore.isAuthenticated)
const isAdmin = computed(() => authStore.isAdmin)
const user = computed(() => authStore.user)

const handleLogout = () => {
  authStore.logout()
  router.push('/')
}
</script>
