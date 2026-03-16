<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Header -->
    <header class="bg-white shadow-sm sticky top-0 z-40">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
        <div class="flex items-center justify-between">
          <h1 class="text-2xl font-bold text-gray-900">用户管理</h1>
          <router-link to="/admin" class="text-primary-600 hover:text-primary-700 font-medium">
            ← 返回管理概览
          </router-link>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- 统计卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-600 text-sm">总用户数</p>
              <p class="text-3xl font-bold text-gray-900 mt-2">{{ stats.totalUsers || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
              <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 12H9m6 0a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-600 text-sm">普通用户</p>
              <p class="text-3xl font-bold text-green-600 mt-2">{{ stats.normalUsers || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
              <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-600 text-sm">管理员</p>
              <p class="text-3xl font-bold text-purple-600 mt-2">{{ stats.adminUsers || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
              <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- 筛选和搜索 -->
      <div class="bg-white rounded-lg shadow p-6 mb-8">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">用户角色</label>
            <select v-model="filters.role" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent">
              <option value="">全部角色</option>
              <option value="0">普通用户</option>
              <option value="1">管理员</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">手机号</label>
            <input v-model="filters.phone" type="text" placeholder="输入手机号" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent" />
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

      <!-- 用户列表 -->
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
              <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">用户ID</th>
              <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">昵称</th>
              <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">手机号</th>
              <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">角色</th>
              <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">注册时间</th>
              <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <tr v-for="user in users" :key="user.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 text-sm text-gray-900 font-mono">{{ user.id }}</td>
              <td class="px-6 py-4 text-sm text-gray-900">{{ user.nickname }}</td>
              <td class="px-6 py-4 text-sm text-gray-600">{{ user.phone }}</td>
              <td class="px-6 py-4 text-sm">
                <span :class="getRoleBadgeClass(user.role)" class="px-3 py-1 rounded-full text-xs font-semibold">
                  {{ getRoleText(user.role) }}
                </span>
              </td>
              <td class="px-6 py-4 text-sm text-gray-600">{{ formatDate(user.createTime) }}</td>
              <td class="px-6 py-4 text-sm">
                <button @click="handleViewDetail(user.id)" class="text-primary-600 hover:text-primary-700 font-medium">
                  查看详情
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <div v-if="!loading && users.length === 0" class="p-8 text-center text-gray-500">
          暂无用户数据
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
import { userApi } from '@/api/admin'

const router = useRouter()

const loading = ref(false)
const users = ref([])
const stats = ref({})

const filters = ref({
  role: '',
  phone: ''
})

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  pages: 0
})

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    const params = {}
    if (filters.value.role) {
      params.role = parseInt(filters.value.role)
    }
    if (filters.value.phone) {
      params.phone = filters.value.phone
    }

    const res = await userApi.getUserList(pagination.value.current, pagination.value.pageSize, params)
    
    if (res.code === 200) {
      users.value = res.data.records || []
      pagination.value.total = res.data.total
      pagination.value.pages = res.data.pages
      pagination.value.current = res.data.current
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取用户统计
const fetchStats = async () => {
  try {
    const res = await userApi.getUserStats()
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('获取用户统计失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.value.current = 1
  fetchUsers()
}

// 重置
const handleReset = () => {
  filters.value = { role: '', phone: '' }
  pagination.value.current = 1
  fetchUsers()
}

// 上一页
const handlePrevPage = () => {
  if (pagination.value.current > 1) {
    pagination.value.current--
    fetchUsers()
  }
}

// 下一页
const handleNextPage = () => {
  if (pagination.value.current < pagination.value.pages) {
    pagination.value.current++
    fetchUsers()
  }
}

// 查看详情
const handleViewDetail = (userId) => {
  // TODO: 跳转到用户详情页面
  console.log('查看用户详情:', userId)
}

// 获取角色文本
const getRoleText = (role) => {
  const roleMap = {
    0: '普通用户',
    1: '管理员'
  }
  return roleMap[role] || '未知'
}

// 获取角色徽章样式
const getRoleBadgeClass = (role) => {
  const classMap = {
    0: 'bg-green-100 text-green-800',
    1: 'bg-purple-100 text-purple-800'
  }
  return classMap[role] || 'bg-gray-100 text-gray-800'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchUsers()
  fetchStats()
})
</script>
