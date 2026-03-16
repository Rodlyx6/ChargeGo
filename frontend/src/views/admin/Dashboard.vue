<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Admin Header -->
    <header class="bg-white shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <div class="flex items-center space-x-3">
            <div class="w-10 h-10 bg-gradient-to-br from-primary-500 to-primary-700 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
            </div>
            <span class="text-2xl font-display font-bold text-gray-900">管理后台</span>
          </div>
          <nav class="flex items-center space-x-6">
            <router-link to="/admin" class="text-gray-900 font-semibold">概览</router-link>
            <router-link to="/admin/stations" class="text-gray-600 hover:text-gray-900">充电桩管理</router-link>
            <router-link to="/admin/orders" class="text-gray-600 hover:text-gray-900">订单管理</router-link>
            <router-link to="/admin/users" class="text-gray-600 hover:text-gray-900">用户管理</router-link>
            <router-link to="/" class="text-gray-600 hover:text-gray-900">返回首页</router-link>
          </nav>
        </div>
      </div>
    </header>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 class="text-3xl font-bold mb-8">管理概览</h1>

      <!-- Stats Grid -->
      <div class="grid md:grid-cols-4 gap-6 mb-8">
        <div class="card p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-600 mb-1">总充电桩</p>
              <p class="text-3xl font-bold text-gray-900">{{ stats.totalStations }}</p>
            </div>
            <div class="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
            </div>
          </div>
        </div>

        <div class="card p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-600 mb-1">空闲</p>
              <p class="text-3xl font-bold text-green-600">{{ stats.idleStations }}</p>
            </div>
            <div class="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
            </div>
          </div>
        </div>

        <div class="card p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-600 mb-1">使用中</p>
              <p class="text-3xl font-bold text-yellow-600">{{ stats.busyStations }}</p>
            </div>
            <div class="w-12 h-12 bg-yellow-100 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
          </div>
        </div>

        <div class="card p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-600 mb-1">故障</p>
              <p class="text-3xl font-bold text-red-600">{{ stats.faultStations }}</p>
            </div>
            <div class="w-12 h-12 bg-red-100 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="card p-6">
        <h2 class="text-xl font-bold mb-4">快捷操作</h2>
        <div class="grid md:grid-cols-3 gap-4">
          <router-link to="/admin/stations" class="p-4 border-2 border-gray-200 rounded-xl hover:border-primary-500 hover:bg-primary-50 transition-all">
            <h3 class="font-semibold mb-2">充电桩管理</h3>
            <p class="text-sm text-gray-600">查看、添加、编辑充电桩</p>
          </router-link>
          <router-link to="/admin/orders" class="p-4 border-2 border-gray-200 rounded-xl hover:border-primary-500 hover:bg-primary-50 transition-all">
            <h3 class="font-semibold mb-2">订单管理</h3>
            <p class="text-sm text-gray-600">查看所有订单和统计数据</p>
          </router-link>
          <router-link to="/admin/users" class="p-4 border-2 border-gray-200 rounded-xl hover:border-primary-500 hover:bg-primary-50 transition-all">
            <h3 class="font-semibold mb-2">用户管理</h3>
            <p class="text-sm text-gray-600">管理用户信息和权限</p>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as stationApi from '@/api/station'

const stats = ref({
  totalStations: 0,
  idleStations: 0,
  busyStations: 0,
  faultStations: 0
})

const loadStats = async () => {
  try {
    const res = await stationApi.getAllStations()
    const stations = res.data || []
    stats.value.totalStations = stations.length
    stats.value.idleStations = stations.filter(s => s.status === 0).length
    stats.value.busyStations = stations.filter(s => s.status === 1 || s.status === 2).length
    stats.value.faultStations = stations.filter(s => s.status === 3).length
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadStats()
})
</script>
