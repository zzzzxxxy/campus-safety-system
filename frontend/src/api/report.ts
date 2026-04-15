import request from '@/utils/request'

export function getDashboard() {
  return request.get('/report/dashboard')
}

export function getVisitorStats(params?: any) {
  return request.get('/report/visitor-stats', { params })
}

export function getDeviceStats() {
  return request.get('/report/device-stats')
}

export function getWarningStats(params?: any) {
  return request.get('/report/warning-stats', { params })
}

export function getAssetStats() {
  return request.get('/report/asset-stats')
}
