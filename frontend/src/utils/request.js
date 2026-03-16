import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  },
  // 关键：使用自定义 JSON 序列化，确保大数字以字符串形式发送
  transformRequest: [(data) => {
    if (data && typeof data === 'object') {
      // 递归处理对象中的所有字段
      const processData = (obj) => {
        const result = {}
        for (const key in obj) {
          const value = obj[key]
          // 如果是数字且超过安全整数范围，转为字符串
          if (typeof value === 'number' && !Number.isSafeInteger(value)) {
            result[key] = String(value)
          } else if (typeof value === 'object' && value !== null) {
            result[key] = processData(value)
          } else {
            result[key] = value
          }
        }
        return result
      }
      return JSON.stringify(processData(data))
    }
    return JSON.stringify(data)
  }],
  // 关键：使用自定义 JSON 解析，保持大数字为字符串
  transformResponse: [(data) => {
    if (typeof data === 'string') {
      try {
        // 使用正则替换：将 JSON 中的大数字（17位以上）用引号包裹
        // 例如：{"id": 2032429259338866689} => {"id": "2032429259338866689"}
        const processedData = data.replace(
          /"(\w+)"\s*:\s*(\d{17,})/g,
          '"$1": "$2"'
        )
        return JSON.parse(processedData)
      } catch (e) {
        return data
      }
    }
    return data
  }]
})

// Request interceptor
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    console.log('🚀 发送请求:', config.url)
    console.log('🔑 当前 Token:', authStore.token)
    console.log('👤 当前用户:', authStore.user)
    
    if (authStore.token) {
      // 确保 token 格式正确：如果已经有 Bearer 前缀就不再添加
      const token = authStore.token.trim()
      if (token.startsWith('Bearer ')) {
        config.headers.Authorization = token
      } else {
        config.headers.Authorization = `Bearer ${token}`
      }
      console.log('✅ 已添加 Authorization 头:', config.headers.Authorization.substring(0, 30) + '...')
    } else {
      console.log('⚠️ 没有 Token，跳过 Authorization 头')
    }
    
    // 添加 X-User-Id 请求头（后端需要）
    if (authStore.user && authStore.user.userId) {
      config.headers['X-User-Id'] = authStore.user.userId
      console.log('✅ 已添加 X-User-Id 头:', authStore.user.userId)
    } else {
      console.log('⚠️ 没有用户ID，跳过 X-User-Id 头')
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
api.interceptors.response.use(
  (response) => {
    console.log('🌐 原始响应:', response)
    const res = response.data
    console.log('📥 响应数据:', res)
    
    if (res.code === 200) {
      console.log('✅ 响应成功，返回数据:', res)
      return res
    } else {
      console.error('❌ 响应失败:', res)
      return Promise.reject(new Error(res.msg || 'Request failed'))
    }
  },
  (error) => {
    const authStore = useAuthStore()
    
    if (error.response) {
      const { status, data } = error.response
      
      if (status === 401) {
        authStore.logout()
        router.push('/login')
        return Promise.reject(new Error('请先登录'))
      } else if (status === 403) {
        return Promise.reject(new Error(data.msg || '无权限访问'))
      } else {
        return Promise.reject(new Error(data.msg || '请求失败'))
      }
    } else if (error.request) {
      return Promise.reject(new Error('网络连接失败'))
    } else {
      return Promise.reject(error)
    }
  }
)

export default api
