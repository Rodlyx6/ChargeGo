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
            <router-link to="/admin" class="text-gray-600 hover:text-gray-900">概览</router-link>
            <router-link to="/admin/stations" class="text-gray-900 font-semibold">充电桩管理</router-link>
            <router-link to="/" class="text-gray-600 hover:text-gray-900">返回首页</router-link>
          </nav>
        </div>
      </div>
    </header>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="flex justify-between items-center mb-8">
        <h1 class="text-3xl font-bold">充电桩管理</h1>
        <button @click="showAddModal = true" class="btn-primary">
          + 新增充电桩
        </button>
      </div>

      <!-- Stations Table -->
      <div class="card overflow-hidden">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">设备编号</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">地址</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">状态</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="station in stations" :key="station.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ station.snCode }}</td>
              <td class="px-6 py-4 text-sm text-gray-600">{{ station.address }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusBadgeClass(station.status)">
                  {{ getStatusText(station.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm space-x-2">
                <button @click="editStation(station)" class="text-primary-600 hover:text-primary-900">编辑</button>
                <button @click="deleteStationConfirm(station)" class="text-red-600 hover:text-red-900">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Add/Edit Modal -->
    <div v-if="showAddModal || showEditModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-2xl p-8 max-w-md w-full max-h-[90vh] overflow-y-auto">
        <h3 class="text-2xl font-bold mb-6">{{ showEditModal ? '编辑充电桩' : '新增充电桩' }}</h3>
        <form @submit.prevent="handleSubmit" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">设备编号</label>
            <input v-model="form.snCode" required class="input-field" placeholder="例: SN-A001" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">详细地址</label>
            <input v-model="form.address" required class="input-field" placeholder="例: 科技园A栋地下车库" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">经度</label>
            <input v-model="form.longitude" type="number" step="0.000001" required class="input-field" placeholder="例: 113.9431" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">纬度</label>
            <input v-model="form.latitude" type="number" step="0.000001" required class="input-field" placeholder="例: 22.5411" />
          </div>
          <div class="flex gap-4 pt-4">
            <button type="submit" :disabled="submitting" class="flex-1 btn-primary">
              {{ submitting ? '提交中...' : '确认' }}
            </button>
            <button type="button" @click="closeModal" class="flex-1 btn-secondary">取消</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as stationApi from '@/api/station'

const stations = ref([])
const showAddModal = ref(false)
const showEditModal = ref(false)
const submitting = ref(false)
const editingStation = ref(null)

const form = ref({
  snCode: '',
  address: '',
  longitude: null,
  latitude: null
})

const loadStations = async () => {
  try {
    const res = await stationApi.getAllStations()
    stations.value = res.data || []
  } catch (error) {
    alert(error.message || '加载失败')
  }
}

const editStation = (station) => {
  editingStation.value = station
  form.value = {
    snCode: station.snCode,
    address: station.address,
    longitude: null,
    latitude: null
  }
  showEditModal.value = true
}

const deleteStationConfirm = async (station) => {
  if (!confirm(`确定要删除充电桩 ${station.snCode} 吗？`)) return
  try {
    await stationApi.deleteStation(station.id)
    alert('删除成功')
    loadStations()
  } catch (error) {
    alert(error.message || '删除失败')
  }
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    if (showEditModal.value) {
      await stationApi.updateStation(editingStation.value.id, form.value)
      alert('修改成功')
    } else {
      await stationApi.addStation(form.value)
      alert('新增成功')
    }
    closeModal()
    loadStations()
  } catch (error) {
    alert(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
  editingStation.value = null
  form.value = {
    snCode: '',
    address: '',
    longitude: null,
    latitude: null
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

onMounted(() => {
  loadStations()
})
</script>
