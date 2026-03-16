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
      <div class="bg-white rounded-2xl p-8 max-w-md w-full max-h-[90vh] overflow-y-auto">
        <h3 class="text-2xl font-bold mb-6">预约充电</h3>
        
        <!-- 充电桩信息 -->
        <div class="space-y-3 mb-6 p-4 bg-blue-50 rounded-lg">
          <p><span class="font-semibold">充电桩:</span> {{ selectedStation?.snCode }}</p>
          <p><span class="font-semibold">地址:</span> {{ selectedStation?.address }}</p>
        </div>

        <!-- 充电类型选择 -->
        <div class="mb-6">
          <label class="block text-sm font-semibold text-gray-700 mb-3">选择充电类型</label>
          <div class="grid grid-cols-3 gap-3">
            <button
              v-for="type in chargeTypes"
              :key="type.code"
              @click="reserveForm.chargeType = type.code"
              :class="[
                'p-3 rounded-lg border-2 transition-all duration-200',
                reserveForm.chargeType === type.code
                  ? 'border-primary-500 bg-primary-50 text-primary-700 font-semibold'
                  : 'border-gray-200 bg-white text-gray-700 hover:border-gray-300'
              ]"
            >
              <div class="text-2xl mb-1">{{ type.icon }}</div>
              <div class="text-xs font-medium">{{ type.name }}</div>
              <div class="text-xs text-gray-500 mt-1">{{ type.desc }}</div>
            </button>
          </div>
        </div>

        <!-- 充电时长选择 -->
        <div class="mb-6">
          <label class="block text-sm font-semibold text-gray-700 mb-3">选择充电时长</label>
          <div class="grid grid-cols-4 gap-2">
            <button
              v-for="hours in [1, 2, 3, 4]"
              :key="hours"
              @click="reserveForm.chargeTime = hours"
              :class="[
                'p-2 rounded-lg border-2 transition-all duration-200 text-sm font-medium',
                reserveForm.chargeTime === hours
                  ? 'border-primary-500 bg-primary-50 text-primary-700'
                  : 'border-gray-200 bg-white text-gray-700 hover:border-gray-300'
              ]"
            >
              {{ hours }}小时
            </button>
          </div>
          <div class="mt-3 p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
            <p class="text-sm text-yellow-800">
              <span class="font-semibold">预期费用:</span>
              <span class="text-lg font-bold text-yellow-600">¥{{ calculateExpectedAmount() }}</span>
            </p>
          </div>
        </div>

        <!-- 提示信息 -->
        <div class="mb-6 p-3 bg-blue-50 border border-blue-200 rounded-lg">
          <p class="text-sm text-blue-800">
            ⏰ 预约后请在15分钟内完成支付，否则订单将自动取消
          </p>
        </div>

        <!-- 操作按钮 -->
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

const reserveForm = ref({
  chargeType: 1,  // 1: 快充, 2: 普通充电, 3: 慢充
  chargeTime: 1   // 充电时长（小时）
})

const stations = ref([])
const loading = ref(false)
const searched = ref(false)
const showReserveModal = ref(false)
const selectedStation = ref(null)
const reserving = ref(false)

// 充电类型配置（与后端 API 文档一致）
const chargeTypes = [
  { code: 1, name: '快充', icon: '⚡', desc: '10元/小时' },
  { code: 2, name: '普通充电', icon: '🔌', desc: '5元/小时' },
  { code: 3, name: '慢充', icon: '🚀', desc: '2元/小时' }
]

// 充电价格配置（元/小时）- 与后端 API 文档一致
const chargePrices = {
  1: 10.0,  // 快充 10元/小时
  2: 5.0,   // 普通充电 5元/小时
  3: 2.0    // 慢充 2元/小时
}

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

const calculateExpectedAmount = () => {
  const price = chargePrices[reserveForm.value.chargeType] || 0
  const amount = price * reserveForm.value.chargeTime
  return amount.toFixed(2)
}

const reserveStation = (station) => {
  selectedStation.value = station
  // 重置预约表单（默认快充1小时）
  reserveForm.value = {
    chargeType: 1,  // 默认快充
    chargeTime: 1   // 默认1小时
  }
  showReserveModal.value = true
}

const confirmReserve = async () => {
  reserving.value = true
  try {
    const stationId = typeof selectedStation.value.id === 'string' 
      ? selectedStation.value.id 
      : String(selectedStation.value.id)
    
    console.log('📤 发送预约请求', {
      stationId,
      chargeType: reserveForm.value.chargeType,
      chargeTime: reserveForm.value.chargeTime,
      expectedAmount: calculateExpectedAmount()
    })
    
    const res = await orderApi.createOrder(stationId, {
      chargeType: reserveForm.value.chargeType,
      chargeTime: reserveForm.value.chargeTime
    })
    
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
