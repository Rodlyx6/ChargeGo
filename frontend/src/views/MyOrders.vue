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
        <div v-for="order in orders" :key="order.orderNo" class="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow p-6">
          <!-- 订单头部 -->
          <div class="flex justify-between items-start mb-6 pb-4 border-b border-gray-200">
            <div class="flex-1">
              <div class="flex items-center space-x-3 mb-3">
                <h3 class="font-bold text-lg text-gray-900">订单号: {{ order.orderNo }}</h3>
                <span :class="getOrderStatusBadgeClass(order.status)" class="px-3 py-1 rounded-full text-sm font-semibold">
                  {{ getOrderStatusText(order.status) }}
                </span>
              </div>
              <p class="text-sm text-gray-500">充电桩ID: {{ order.stationId }}</p>
            </div>
          </div>

          <!-- 充电详情 -->
          <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6 p-4 bg-gradient-to-r from-blue-50 to-green-50 rounded-lg">
            <!-- 充电类型 -->
            <div class="text-center">
              <div class="text-3xl mb-2">{{ getChargeTypeIcon(order.chargeType) }}</div>
              <p class="text-xs text-gray-600 mb-1">充电类型</p>
              <p class="font-semibold text-gray-900">{{ getChargeTypeText(order.chargeType) }}</p>
            </div>

            <!-- 预约时长 -->
            <div class="text-center">
              <div class="text-3xl mb-2">⏱️</div>
              <p class="text-xs text-gray-600 mb-1">预约时长</p>
              <p class="font-semibold text-gray-900">{{ order.chargeTime }}小时</p>
            </div>

            <!-- 预期费用 -->
            <div class="text-center">
              <div class="text-3xl mb-2">💰</div>
              <p class="text-xs text-gray-600 mb-1">预期费用</p>
              <p class="font-semibold text-primary-600">¥{{ order.expectedAmount?.toFixed(2) || '0.00' }}</p>
            </div>

            <!-- 实际费用（仅已完成时显示） -->
            <div v-if="order.status === 2" class="text-center">
              <div class="text-3xl mb-2">✅</div>
              <p class="text-xs text-gray-600 mb-1">实际费用</p>
              <p class="font-semibold text-green-600">¥{{ order.actualAmount?.toFixed(2) || '0.00' }}</p>
            </div>
          </div>

          <!-- 时间信息 -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6 text-sm">
            <div class="flex items-center space-x-2 text-gray-700 p-3 bg-gray-50 rounded-lg">
              <span class="text-gray-500">📅</span>
              <div>
                <p class="text-xs text-gray-600">下单时间</p>
                <p class="font-medium">{{ formatTime(order.createTime) }}</p>
              </div>
            </div>
            <div v-if="order.payTime" class="flex items-center space-x-2 text-gray-700 p-3 bg-gray-50 rounded-lg">
              <span class="text-gray-500">💳</span>
              <div>
                <p class="text-xs text-gray-600">支付时间</p>
                <p class="font-medium">{{ formatTime(order.payTime) }}</p>
              </div>
            </div>
            <div v-if="order.status === 0" class="flex items-center space-x-2 text-orange-600 font-medium p-3 bg-orange-50 rounded-lg border border-orange-200">
              <span>⏰</span>
              <div>
                <p class="text-xs text-orange-600">剩余支付时间</p>
                <p class="text-lg font-bold animate-pulse">{{ getRemainingTime(order.createTime) }}</p>
              </div>
            </div>
            <div v-else-if="order.status === 1" class="flex items-center space-x-2 text-green-600 font-medium p-3 bg-green-50 rounded-lg border border-green-200">
              <span>⚡</span>
              <div>
                <p class="text-xs text-green-600">充电中</p>
                <p class="text-lg font-bold animate-pulse">{{ getChargingTime(order.payTime) }}</p>
              </div>
            </div>
            <div v-else-if="order.status === 2" class="flex items-center space-x-2 text-blue-600 font-medium p-3 bg-blue-50 rounded-lg border border-blue-200">
              <span>✅</span>
              <div>
                <p class="text-xs text-blue-600">完单时间</p>
                <p class="font-medium">{{ formatTime(order.updateTime) }}</p>
              </div>
            </div>
          </div>

          <!-- 退款信息 -->
          <div v-if="order.refundAmount && order.refundAmount > 0" class="mb-6 p-3 bg-green-50 border border-green-200 rounded-lg">
            <p class="text-sm text-green-800">
              <span class="font-semibold">✅ 退款金额:</span>
              <span class="text-lg font-bold text-green-600">¥{{ order.refundAmount.toFixed(2) }}</span>
            </p>
          </div>

          <!-- 操作按钮 -->
          <div class="flex gap-3 flex-wrap">
            <button
              v-if="order.status === 0"
              @click="handlePay(order)"
              class="flex-1 min-w-[120px] px-4 py-2 bg-gradient-to-r from-primary-500 to-primary-600 hover:from-primary-600 hover:to-primary-700 text-white font-semibold rounded-lg transition-all duration-200 shadow-md hover:shadow-lg active:scale-95"
            >
              💳 立即支付
            </button>
            <button
              v-if="order.status === 0"
              @click="handleCancelPayment(order)"
              class="flex-1 min-w-[120px] px-4 py-2 bg-gray-200 hover:bg-gray-300 text-gray-900 font-semibold rounded-lg transition-colors duration-200"
            >
              ❌ 取消订单
            </button>
            <button
              v-if="order.status === 1"
              @click="handleCancelOrder(order)"
              class="flex-1 min-w-[120px] px-4 py-2 bg-gradient-to-r from-orange-500 to-orange-600 hover:from-orange-600 hover:to-orange-700 text-white font-semibold rounded-lg transition-all duration-200 shadow-md hover:shadow-lg active:scale-95"
            >
              ⏹️ 结束充电
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
import { ref, onMounted, onUnmounted } from 'vue'
import * as orderApi from '@/api/order'

const orders = ref([])
const loading = ref(false)
const currentTime = ref(new Date())

// 充电类型映射（与后端 API 文档一致）
const chargeTypeMap = {
  1: '快充',
  2: '普通充电',
  3: '慢充'
}

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
    0: 'bg-yellow-100 text-yellow-800',
    1: 'bg-green-100 text-green-800',
    2: 'bg-blue-100 text-blue-800',
    3: 'bg-red-100 text-red-800'
  }
  return map[status] || 'bg-gray-100 text-gray-800'
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const getRemainingTime = (createTime) => {
  if (!createTime) return '0分0秒'
  const created = new Date(createTime)
  const diff = 15 * 60 * 1000 - (currentTime.value - created)  // 15分钟
  if (diff <= 0) return '已超时'
  const minutes = Math.floor(diff / 60000)
  const seconds = Math.floor((diff % 60000) / 1000)
  return `${minutes}分${seconds}秒`
}

const getChargingTime = (payTime) => {
  if (!payTime) return '0小时0分0秒'
  const paid = new Date(payTime)
  const diff = currentTime.value - paid
  const hours = Math.floor(diff / 3600000)
  const minutes = Math.floor((diff % 3600000) / 60000)
  const seconds = Math.floor((diff % 60000) / 1000)
  return `${hours}小时${minutes}分${seconds}秒`
}

const getChargeTypeText = (chargeType) => {
  return chargeTypeMap[chargeType] || '未知'
}

const getChargeTypeIcon = (chargeType) => {
  const iconMap = {
    1: '⚡',  // 快充
    2: '🔌',  // 普通充电
    3: '🚀'   // 慢充
  }
  return iconMap[chargeType] || '🔋'
}

let timeInterval = null
let refreshInterval = null

onMounted(() => {
  loadOrders()
  
  // 每秒更新当前时间（用于动态倒计时和充电时间）
  timeInterval = setInterval(() => {
    currentTime.value = new Date()
  }, 1000)
  
  // 每30秒刷新一次订单列表
  refreshInterval = setInterval(loadOrders, 30000)
})

onUnmounted(() => {
  // 清理定时器
  if (timeInterval) clearInterval(timeInterval)
  if (refreshInterval) clearInterval(refreshInterval)
})
</script>
