import request from '@/utils/request'

/**
 * 订单管理 API
 */
export const orderApi = {
  // 获取订单列表
  getOrderList(pageNum = 1, pageSize = 10, params = {}) {
    return request.get('/admin/order/list', {
      params: {
        pageNum,
        pageSize,
        ...params
      }
    })
  },

  // 获取订单详情
  getOrderDetail(orderNo) {
    return request.get(`/admin/order/${orderNo}`)
  },

  // 获取订单统计
  getOrderStats() {
    return request.get('/admin/order/stats')
  }
}

/**
 * 用户管理 API
 */
export const userApi = {
  // 获取用户列表
  getUserList(pageNum = 1, pageSize = 10, params = {}) {
    return request.get('/admin/user/list', {
      params: {
        pageNum,
        pageSize,
        ...params
      }
    })
  },

  // 获取用户详情
  getUserDetail(userId) {
    return request.get(`/admin/user/${userId}`)
  },

  // 获取用户统计
  getUserStats() {
    return request.get('/admin/user/stats')
  },

  // 获取用户订单数
  getUserOrderCount(userId) {
    return request.get(`/admin/user/${userId}/orders/count`)
  }
}
