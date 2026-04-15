import request from '@/utils/request'

// ========== Asset Info ==========
export function getAssetInfoPage(params: any) {
  return request.get('/asset/info/page', { params })
}

export function addAssetInfo(data: any) {
  return request.post('/asset/info', data)
}

export function updateAssetInfo(data: any) {
  return request.put('/asset/info', data)
}

export function deleteAssetInfo(id: number) {
  return request.delete(`/asset/info/${id}`)
}

export function getAssetStatsByStatus() {
  return request.get('/asset/info/stats/status')
}

// ========== Device Info ==========
export function getDeviceInfoPage(params: any) {
  return request.get('/asset/device/page', { params })
}

export function addDeviceInfo(data: any) {
  return request.post('/asset/device', data)
}

export function updateDeviceInfo(data: any) {
  return request.put('/asset/device', data)
}

export function deleteDeviceInfo(id: number) {
  return request.delete(`/asset/device/${id}`)
}

export function getDeviceStatsByType() {
  return request.get('/asset/device/stats/type')
}

export function getDeviceStatsByStatus() {
  return request.get('/asset/device/stats/status')
}
