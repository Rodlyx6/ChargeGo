import api from '@/utils/request'

// 创建预约订单
export const createOrder = (stationId, chargeOptions = {}) => {
  // 确保参数正确传递（与后端 API 文档一致）
  const chargeType = chargeOptions.chargeType !== undefined ? chargeOptions.chargeType : 1
  const chargeTime = chargeOptions.chargeTime !== undefined ? chargeOptions.chargeTime : 1
  
  const payload = {
    stationId: String(stationId),
    chargeType: Number(chargeType),
    chargeTime: Number(chargeTime)
  }
  
  console.log('📤 createOrder 发送数据:', payload)
  console.log('📤 chargeType:', payload.chargeType, '类型:', typeof payload.chargeType)
  console.log('📤 chargeTime:', payload.chargeTime, '类型:', typeof payload.chargeTime)
  
  return api.post('/order/create', payload)
}

// 支付订单
export const payOrder = (orderNo) => {
  return api.post('/order/pay', { orderNo })
}

// 取消支付（待支付时取消）
export const cancelPayment = (orderNo) => {
  return api.post('/order/cancelPayment', { orderNo })
}

// 取消订单（充电中时取消）
export const cancelOrder = (orderNo) => {
  return api.post('/order/cancelOrder', { orderNo })
}

// 查询我的订单列表
export const getMyOrders = () => {
  return api.get('/order/list')
}

// 查询订单详情
export const getOrderDetail = (orderNo) => {
  return api.get(`/order/detail/${orderNo}`)
}
