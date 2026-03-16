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
              <div class="flex items-center space-x-4">
                <div class="hidden sm:block text-right">
                  <p class="text-sm font-semibold text-gray-900">{{ user?.nickname }}</p>
                  <p class="text-xs text-gray-500">{{ isAdmin ? '👨‍💼 管理员' : '👤 普通用户' }}</p>
                </div>
                <button 
                  @click="showLogoutModal = true"
                  class="px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700 rounded-lg transition-all duration-200 shadow-md hover:shadow-lg active:scale-95"
                >
                  退出登录
                </button>
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

    <!-- 退出登录确认弹窗 -->
    <Teleport to="body">
      <transition name="fade">
        <div v-if="showLogoutModal" class="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div class="bg-white rounded-2xl shadow-2xl max-w-sm w-full animate-scale-in">
            <div class="p-8">
              <div class="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-6">
                <svg class="w-8 h-8 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                </svg>
              </div>
              <h3 class="text-2xl font-bold text-center text-gray-900 mb-2">确认退出登录？</h3>
              <p class="text-center text-gray-600 mb-8">退出后需要重新登录才能使用服务</p>
              <div class="flex gap-4">
                <button
                  @click="showLogoutModal = false"
                  class="flex-1 px-4 py-3 bg-gray-100 hover:bg-gray-200 text-gray-900 font-semibold rounded-lg transition-colors duration-200"
                >
                  取消
                </button>
                <button
                  @click="confirmLogout"
                  class="flex-1 px-4 py-3 bg-gradient-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700 text-white font-semibold rounded-lg transition-all duration-200 shadow-md hover:shadow-lg active:scale-95"
                >
                  确认退出
                </button>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>

    <!-- 管理员首页 -->
    <template v-if="isAuthenticated && isAdmin">
      <section class="min-h-screen bg-gradient-to-br from-slate-900 via-primary-900 to-slate-900 text-white">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
          <div class="space-y-12">
            <!-- 欢迎信息 -->
            <div class="text-center space-y-4">
              <h1 class="text-5xl md:text-6xl font-display font-bold">
                欢迎回来，<span class="text-transparent bg-clip-text bg-gradient-to-r from-primary-300 to-primary-100">{{ user?.nickname }}</span>
              </h1>
              <p class="text-xl text-slate-300">👨‍💼 管理员面板</p>
            </div>

            <!-- 管理功能卡片 -->
            <div class="grid md:grid-cols-2 gap-8">
              <!-- 充电桩管理 -->
              <router-link to="/admin/stations" class="group">
                <div class="backdrop-blur-xl bg-white/10 border border-white/20 rounded-2xl p-8 hover:bg-white/15 transition-all duration-300 cursor-pointer">
                  <div class="flex items-start justify-between mb-6">
                    <div class="w-16 h-16 bg-gradient-to-br from-blue-400 to-blue-600 rounded-xl flex items-center justify-center group-hover:scale-110 transition-transform">
                      <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                      </svg>
                    </div>
                    <svg class="w-6 h-6 text-white/50 group-hover:text-white/100 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                    </svg>
                  </div>
                  <h3 class="text-2xl font-bold mb-2">充电桩管理</h3>
                  <p class="text-slate-300">新增、编辑、删除充电桩信息</p>
                </div>
              </router-link>

              <!-- 管理概览 -->
              <router-link to="/admin" class="group">
                <div class="backdrop-blur-xl bg-white/10 border border-white/20 rounded-2xl p-8 hover:bg-white/15 transition-all duration-300 cursor-pointer">
                  <div class="flex items-start justify-between mb-6">
                    <div class="w-16 h-16 bg-gradient-to-br from-green-400 to-green-600 rounded-xl flex items-center justify-center group-hover:scale-110 transition-transform">
                      <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                      </svg>
                    </div>
                    <svg class="w-6 h-6 text-white/50 group-hover:text-white/100 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                    </svg>
                  </div>
                  <h3 class="text-2xl font-bold mb-2">管理概览</h3>
                  <p class="text-slate-300">查看系统统计数据和运营情况</p>
                </div>
              </router-link>
            </div>

            <!-- 快速操作 -->
            <div class="backdrop-blur-xl bg-white/10 border border-white/20 rounded-2xl p-8">
              <h3 class="text-2xl font-bold mb-6">快速操作</h3>
              <div class="grid md:grid-cols-3 gap-4">
                <router-link to="/admin/stations" class="px-6 py-3 bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 text-white font-semibold rounded-lg transition-all duration-200 text-center">
                  ➕ 新增充电桩
                </router-link>
                <router-link to="/admin/stations" class="px-6 py-3 bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 text-white font-semibold rounded-lg transition-all duration-200 text-center">
                  📋 查看所有充电桩
                </router-link>
                <router-link to="/admin" class="px-6 py-3 bg-gradient-to-r from-purple-500 to-purple-600 hover:from-purple-600 hover:to-purple-700 text-white font-semibold rounded-lg transition-all duration-200 text-center">
                  📊 查看统计数据
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </section>
    </template>

    <!-- 普通用户首页 -->
    <template v-else-if="isAuthenticated && !isAdmin">
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
    </template>

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
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const isAuthenticated = computed(() => authStore.isAuthenticated)
const isAdmin = computed(() => authStore.isAdmin)
const user = computed(() => authStore.user)

// 退出登录弹窗状态
const showLogoutModal = ref(false)

// 确认退出登录
const confirmLogout = async () => {
  showLogoutModal.value = false
  // 添加退出动画
  await new Promise(resolve => setTimeout(resolve, 300))
  authStore.logout()
  router.push('/')
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.animate-scale-in {
  animation: scale-in 0.3s ease-out;
}

@keyframes scale-in {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
