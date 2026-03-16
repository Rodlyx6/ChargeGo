<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Header -->
    <header class="bg-white shadow-sm sticky top-0 z-40">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
        <div class="flex items-center justify-between">
          <h1 class="text-2xl font-bold text-gray-900">订单管理</h1>
          <router-link to="/admin" class="text-primary-600 hover:text-primary-700 font-medium">
            ← 返回管理概览
          </router-link>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- 统计卡片 -->
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-600 text-sm">总订单数</p>
              <p class="text-3xl font-bold text-gray-900 mt-2">{{ stats.totalOrders || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
              <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-600 text-sm">待支付</p>
              <p class="text-3xl font-bold text-yellow-600 mt-2">{{ stats.pendingPayment || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center">
              <svg class="w-6 h-6 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-600 text-sm">充电中</p>
              <p class="text-3xl font-bold text-green-600 mt-2">{{ stats.charging || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
              <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-600 text-sm">已完成</p>
              <p class="text-3xl font-bold text-blue-600 mt-2">{{ stats.completed || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
              <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- 筛选和搜索 -->
      <div class="bg-white rounded-lg shadow p-6 mb-8">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">订单状态</label>
            <select v-model="filters.status" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent">
              <option value="">全部状态</option>
              <option value="0">待支付</option>
              <option value="1">充电中</option>
              <option value="2">已完成</option>
              <option value="3">已取消</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">用户ID</label>
            <input v-model="filters.userId" type="text" placeholder="输入用户ID" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent" />
          </div>
          <div class="flex items-end gap-2">
            <button @click="handleSearch" class="flex-1 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors">
              🔍 搜索
            </button>
            <button @click="handleReset" class="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors">
              重置
            </button>
          </div>
        </div>
      </div>

      <!-- 订单列表 -->
      <div class="bg-white rounded-lg shadow overflow-hidden">
        <div v-if="loading" class="p-8 text-center">
          <div class="inline-block animate-spin">
            <svg class="w-8 h-8 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
          </div>
        </div>

        <table v-else class="w-full">
          <thead class="bg-gray-50 border-b border-gray-200">
            <tr>
              <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">订单号</th>
              <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">用户ID</th>
              <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">充电桩ID</th>
              <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">状态</th>
              <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">创建时间</th>
              <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <tr v-for="order in orders" :key="order.orderNo" class="hover:bg-gray-50">
              <td class="px-6 py-4 text-sm text-gray-900 font-mono">{{ order.orderNo }}</td>
              <td class="px-6 py-4 text-sm text-gray-600">{{ order.userId }}</td>
              <td class="px-6 py-4 text-sm text-gray-600">{{ order.stationId }}</td>
              <td class="px-6 py-4 text-sm">
                <span :class="getStatusBadgeClass(order.status)" class="px-3 py-1 rounded-full text-xs font-semibold">
                  {{ getStatusText(order.status) }}
                </span>
              </td>
              <td class="px-6 py-4 text-sm text-gray-600">{{ formatDate(order.createTime) }}</td>
              <td class="px-6 py-4 text-sm">
                <button @click="handleViewDetail(order.orderNo)" class="text-primary-600 hover:text-primary-700 font-medium">
                  查看详情
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <div v-if="!loading && orders.length === 0" class="p-8 text-center text-gray-500">
          暂无订单数据
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="pagination.total > 0" class="mt-8 flex items-center justify-between">
        <div class="text-sm text-gray-600">
          共 {{ pagination.total }} 条记录，第 {{ pagination.current }} / {{ pagination.pages }} 页
        </div>
        <div class="flex gap-2">
          <button 
            @click="handlePrevPage" 
            :disabled="pagination.current === 1"
            class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            上一页
          </button>
          <button 
            @click="handleNextPage" 
            :disabled="pagination.current >= pagination.pages"
            class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            下一页
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '@/api/admin'

const router = useRouter()

const loading = ref(false)
const orders = ref([])
const stats = ref({})

const filters = ref({
  status: '',
  userId: ''
})

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  pages: 0
})

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {}
    if (filters.value.status) {
      params.status = parseInt(filters.value.status)
    }
    if (filters.value.userId) {
      params.filterUserId = parseInt(filters.value.userId)
    }

    const res = await orderApi.getOrderList(pagination.value.current, pagination.value.pageSize, params)
    
    if (res.code === 200) {
      orders.value = res.data.records || []
      pagination.value.total = res.data.total
      pagination.value.pages = res.data.pages
      pagination.value.current = res.data.current
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取订单统计
const fetchStats = async () => {
  try {
    const res = await orderApi.getOrderStats()
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('获取订单统计失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.value.current = 1
  fetchOrders()
}

// 重置
const handleReset = () => {
  filters.value = { status: '', userId: '' }
  pagination.value.current = 1
  fetchOrders()
}

// 上一页
const handlePrevPage = () => {
  if (pagination.value.current > 1) {
    pagination.value.current--
    fetchOrders()
  }
}

// 下一页
const handleNextPage = () => {
  if (pagination.value.current < pagination.value.pages) {
    pagination.value.current++
    fetchOrders()
  }
}

// 查看详情
const handleViewDetail = (orderNo) => {
  // TODO: 跳转到订单详情页面
  console.log('查看订单详情:', orderNo)
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '充电中',
    2: '已完成',
    3: '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取状态徽章样式
const getStatusBadgeClass = (status) => {
  const classMap = {
    0: 'bg-yellow-100 text-yellow-800',
    1: 'bg-green-100 text-green-800',
    2: 'bg-blue-100 text-blue-800',
    3: 'bg-red-100 text-red-800'
  }
  return classMap[status] || 'bg-gray-100 text-gray-800'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchOrders()
  fetchStats()
})
</script>
