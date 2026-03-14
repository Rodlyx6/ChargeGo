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
          <nav class="flex items-center space-x-6">
            <router-link to="/stations" class="text-gray-600 hover:text-gray-900">找充电桩</router-link>
            <router-link to="/my-orders" class="text-gray-900 font-semibold">我的订单</router-link>
          </nav>
        </div>
      </div>
    </header>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 class="text-3xl font-bold mb-8">我的订单</h1>

      <!-- Loading -->
      <div v-if="loading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
        <p class="text-gray-500 mt-4">加载中...</p>
      </div>

      <!-- Orders List -->
      <div v-else-if="orders.length > 0" class="space-y-4">
        <div v-for="order in orders" :key="order.orderNo" class="card p-6">
          <div class="flex justify-between items-start mb-4">
            <div class="flex-1">
              <div class="flex items-center space-x-3 mb-2">
                <h3 class="font-bold text-lg">订单号: {{ order.orderNo }}</h3>
                <span :class="getOrderStatusBadgeClass(order.status)">
                  {{ getOrderStatusText(order.status) }}
                </span>
              </div>
              <div class="space-y-1 text-sm text-gray-600">
                <p>充电桩ID: {{ order.stationId }}</p>
                <p>下单时间: {{ formatTime(order.createTime) }}</p>
                <p v-if="order.payTime">支付时间: {{ formatTime(order.payTime) }}</p>
                <p v-if="order.status === 0" class="text-orange-600 font-medium">
                  ⏰ 请在 {{ getRemainingTime(order.createTime) }} 内完成支付，否则订单将自动取消
                </p>
              </div>
            </div>
          </div>

          <div class="flex gap-3">
            <button
              v-if="order.status === 0"
              @click="handlePay(order)"
              class="btn-primary text-sm"
            >
              立即支付
            </button>
            <button
              v-if="order.status === 0"
              @click="handleCancelPayment(order)"
              class="btn-secondary text-sm"
            >
              取消订单
            </button>
            <button
              v-if="order.status === 1"
              @click="handleCancelOrder(order)"
              class="btn-secondary text-sm"
            >
              结束充电
            </button>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-12">
        <svg class="w-24 h-24 mx-auto text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
        <p class="text-gray-500 mb-4">暂无订单</p>
        <router-link to="/stations" class="btn-primary">
          去预约充电桩
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as orderApi from '@/api/order'

const orders = ref([])
const loading = ref(false)

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await orderApi.getMyOrders()
    orders.value = res.data || []
  } catch (error) {
    console.error('加载订单失败:', error)
    alert(error.message || '加载订单失败')
  } finally {
    loading.value = false
  }
}

const handlePay = async (order) => {
  try {
    await orderApi.payOrder(order.orderNo)
    alert('支付成功！')
    loadOrders()
  } catch (error) {
    alert(error.message || '支付失败')
  }
}

const handleCancelPayment = async (order) => {
  if (!confirm('确定要取消订单吗？')) return
  try {
    await orderApi.cancelPayment(order.orderNo)
    alert('订单已取消')
    loadOrders()
  } catch (error) {
    alert(error.message || '取消失败')
  }
}

const handleCancelOrder = async (order) => {
  if (!confirm('确定要结束充电吗？')) return
  try {
    await orderApi.cancelOrder(order.orderNo)
    alert('充电已结束')
    loadOrders()
  } catch (error) {
    alert(error.message || '操作失败')
  }
}

const getOrderStatusText = (status) => {
  const map = { 0: '待支付', 1: '充电中', 2: '已完成', 3: '已取消' }
  return map[status] || '未知'
}

const getOrderStatusBadgeClass = (status) => {
  const map = {
    0: 'badge-warning',
    1: 'badge-info',
    2: 'badge-success',
    3: 'badge-error'
  }
  return map[status] || 'badge'
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const getRemainingTime = (createTime) => {
  if (!createTime) return '0分钟'
  const created = new Date(createTime)
  const now = new Date()
  const diff = 15 * 60 * 1000 - (now - created)  // 15分钟
  if (diff <= 0) return '已超时'
  const minutes = Math.floor(diff / 60000)
  const seconds = Math.floor((diff % 60000) / 1000)
  return `${minutes}分${seconds}秒`
}

onMounted(() => {
  loadOrders()
  // 每30秒刷新一次订单列表
  setInterval(loadOrders, 30000)
})
</script>
