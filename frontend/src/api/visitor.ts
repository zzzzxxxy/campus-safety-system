import request from '@/utils/request'

export function getVisitorPage(params: any) {
  return request.get('/visitor/page', { params })
}

export function addVisitor(data: any) {
  return request.post('/visitor', data)
}

export function updateVisitor(data: any) {
  return request.put('/visitor', data)
}

export function deleteVisitor(id: number) {
  return request.delete(`/visitor/${id}`)
}

export function auditVisitor(data: { id: number; status: number; remark?: string }) {
  return request.put('/visitor/audit', data)
}

export function checkInVisitor(id: number) {
  return request.put(`/visitor/check-in/${id}`)
}

export function checkOutVisitor(id: number) {
  return request.put(`/visitor/check-out/${id}`)
}

export function getTodayStats() {
  return request.get('/visitor/today-stats')
}
