import api from '@/utils/request'

// 查询附近充电桩
export const getNearbyStations = (longitude, latitude, radiusMeters = 5000) => {
  return api.post('/station/nearby', { longitude, latitude, radiusMeters })
}

// 管理员 - 查询所有充电桩
export const getAllStations = () => {
  return api.get('/admin/station/list')
}

// 管理员 - 新增充电桩
export const addStation = (data) => {
  return api.post('/admin/station/add', data)
}

// 管理员 - 修改充电桩
export const updateStation = (id, data) => {
  return api.put(`/admin/station/update/${id}`, data)
}

// 管理员 - 删除充电桩
export const deleteStation = (id) => {
  return api.delete(`/admin/station/delete/${id}`)
}

// 管理员 - 查询单个充电桩
export const getStationById = (id) => {
  return api.get(`/admin/station/${id}`)
}
