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
          <router-link to="/" class="text-gray-600 hover:text-gray-900">
            返回首页
          </router-link>
        </div>
      </div>
    </header>

    <!-- Hero Section -->
    <section class="relative overflow-hidden bg-gradient-to-br from-primary-600 via-primary-700 to-blue-900 text-white py-12">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h1 class="text-4xl font-display font-bold mb-4">找到附近充电桩</h1>
        <p class="text-xl text-blue-100">实时查看充电桩状态，一键预约</p>
      </div>
    </section>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Search Section -->
      <div class="card p-6 mb-8">
        <h2 class="text-xl font-bold mb-4">搜索附近充电桩</h2>
        <div class="grid md:grid-cols-4 gap-4">
          <input v-model="searchForm.longitude" type="number" step="0.000001" placeholder="经度" class="input-field" />
          <input v-model="searchForm.latitude" type="number" step="0.000001" placeholder="纬度" class="input-field" />
          <input v-model="searchForm.radiusMeters" type="number" placeholder="搜索半径(米)" class="input-field" />
          <button @click="searchStations" :disabled="loading" class="btn-primary">
            {{ loading ? '搜索中...' : '搜索' }}
          </button>
        </div>
        <button @click="getCurrentLocation" class="mt-4 text-primary-600 hover:text-primary-700 text-sm font-medium">
          📍 使用当前位置
        </button>
      </div>

      <!-- Results -->
      <div v-if="stations.length > 0" class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div v-for="station in stations" :key="station.id" class="card p-6 hover:shadow-lg transition-shadow">
          <div class="flex justify-between items-start mb-4">
            <div>
              <h3 class="font-bold text-lg">{{ station.snCode }}</h3>
              <p class="text-sm text-gray-600 mt-1">{{ station.address }}</p>
            </div>
            <span :class="getStatusBadgeClass(station.status)">
              {{ getStatusText(station.status) }}
            </span>
          </div>
          
          <div class="flex items-center text-gray-600 text-sm mb-4">
            <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
            </svg>
            距离: {{ (station.distance / 1000).toFixed(2) }} km
          </div>

          <button 
            @click="reserveStation(station)" 
            :disabled="station.status !== 0"
            class="w-full btn-primary disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ station.status === 0 ? '立即预约' : '不可预约' }}
          </button>
        </div>
      </div>

      <div v-else-if="!loading && searched" class="text-center py-12">
        <p class="text-gray-500">未找到附近的充电桩</p>
      </div>
    </div>

    <!-- Reserve Modal -->
    <div v-if="showReserveModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-2xl p-8 max-w-md w-full">
        <h3 class="text-2xl font-bold mb-4">确认预约</h3>
        <div class="space-y-3 mb-6">
          <p><span class="font-semibold">充电桩:</span> {{ selectedStation?.snCode }}</p>
          <p><span class="font-semibold">地址:</span> {{ selectedStation?.address }}</p>
          <p class="text-sm text-gray-600">预约后请在15分钟内完成支付，否则订单将自动取消</p>
        </div>
        <div class="flex gap-4">
          <button @click="confirmReserve" :disabled="reserving" class="flex-1 btn-primary">
            {{ reserving ? '预约中...' : '确认预约' }}
          </button>
          <button @click="showReserveModal = false" class="flex-1 btn-secondary">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import * as stationApi from '@/api/station'
import * as orderApi from '@/api/order'

const router = useRouter()

const searchForm = ref({
  longitude: 113.9431,
  latitude: 22.5411,
  radiusMeters: 5000
})

const stations = ref([])
const loading = ref(false)
const searched = ref(false)
const showReserveModal = ref(false)
const selectedStation = ref(null)
const reserving = ref(false)

const searchStations = async () => {
  loading.value = true
  searched.value = false
  try {
    const res = await stationApi.getNearbyStations(
      searchForm.value.longitude,
      searchForm.value.latitude,
      searchForm.value.radiusMeters
    )
    stations.value = res.data || []
    searched.value = true
  } catch (error) {
    alert(error.message || '搜索失败')
  } finally {
    loading.value = false
  }
}

const getCurrentLocation = () => {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        searchForm.value.longitude = position.coords.longitude
        searchForm.value.latitude = position.coords.latitude
        searchStations()
      },
      (error) => {
        alert('无法获取位置信息')
      }
    )
  } else {
    alert('浏览器不支持地理定位')
  }
}

const getStatusText = (status) => {
  const map = { 0: '空闲', 1: '预约中', 2: '充电中', 3: '故障' }
  return map[status] || '未知'
}

const getStatusBadgeClass = (status) => {
  const map = {
    0: 'badge-success',
    1: 'badge-warning',
    2: 'badge-info',
    3: 'badge-error'
  }
  return map[status] || 'badge'
}

const reserveStation = (station) => {
  selectedStation.value = station
  showReserveModal.value = true
}

const confirmReserve = async () => {
  reserving.value = true
  try {
    // 注意：stationId 可能是字符串格式（后端返回的 Long 类型）
    // 需要确保发送时也是字符串或数字
    const stationId = typeof selectedStation.value.id === 'string' 
      ? selectedStation.value.id 
      : String(selectedStation.value.id)
    
    console.log('📤 发送预约请求，stationId:', stationId, '类型:', typeof stationId)
    
    const res = await orderApi.createOrder(stationId)
    alert(`预约成功！订单号: ${res.data}`)
    showReserveModal.value = false
    router.push('/my-orders')
  } catch (error) {
    alert(error.message || '预约失败')
  } finally {
    reserving.value = false
  }
}
</script>
