import request from '@/utils/request'

// ========== Warning Record ==========
export function getWarningRecordPage(params: any) {
  return request.get('/warning/record/page', { params })
}

export function addWarningRecord(data: any) {
  return request.post('/warning/record', data)
}

export function updateWarningRecord(data: any) {
  return request.put('/warning/record', data)
}

export function deleteWarningRecord(id: number) {
  return request.delete(`/warning/record/${id}`)
}

export function handleWarningRecord(data: { id: number; status: number; handleResult?: string }) {
  return request.put('/warning/record/handle', data)
}

export function getWarningStatsByType() {
  return request.get('/warning/record/stats/type')
}

export function getWarningStatsByLevel() {
  return request.get('/warning/record/stats/level')
}

export function getUnhandledCount() {
  return request.get('/warning/record/unhandled/count')
}

// ========== Warning Rule ==========
export function getWarningRulePage(params: any) {
  return request.get('/warning/rule/page', { params })
}

export function addWarningRule(data: any) {
  return request.post('/warning/rule', data)
}

export function updateWarningRule(data: any) {
  return request.put('/warning/rule', data)
}

export function deleteWarningRule(id: number) {
  return request.delete(`/warning/rule/${id}`)
}

export function changeWarningRuleStatus(id: number, status: number) {
  return request.put(`/warning/rule/${id}/status`, { status })
}
