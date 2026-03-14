import api from '@/utils/request'

// 用户注册
export const register = (phone, password, nickname) => {
  return api.post('/user/register', { phone, password, nickname })
}

// 用户登录
export const login = (phone, password) => {
  return api.post('/user/login', { phone, password })
}

// 管理员登录
export const adminLogin = (phone, password) => {
  return api.post('/admin/login', { phone, password })
}
