import request from '@/utils/request'

// ========== User ==========
export function getUserPage(params: any) {
  return request.get('/system/user/page', { params })
}

export function addUser(data: any) {
  return request.post('/system/user', data)
}

export function updateUser(data: any) {
  return request.put('/system/user', data)
}

export function deleteUser(id: number) {
  return request.delete(`/system/user/${id}`)
}

export function resetPassword(id: number, newPassword: string) {
  // backend expects request param: newPassword
  return request.put(`/system/user/${id}/resetPassword`, null, { params: { newPassword } })
}

// ========== Role ==========
export function getRolePage(params: any) {
  return request.get('/system/role/page', { params })
}

export function getRoleList() {
  return request.get('/system/role/list')
}

export function addRole(data: any) {
  return request.post('/system/role', data)
}

export function updateRole(data: any) {
  return request.put('/system/role', data)
}

export function deleteRole(id: number) {
  return request.delete(`/system/role/${id}`)
}

// ========== Menu ==========
export function getMenuList() {
  return request.get('/system/menu/list')
}

export function addMenu(data: any) {
  return request.post('/system/menu', data)
}

export function updateMenu(data: any) {
  return request.put('/system/menu', data)
}

export function deleteMenu(id: number) {
  return request.delete(`/system/menu/${id}`)
}

export function getMenuIdsByRoleId(roleId: number) {
  return request.get(`/system/menu/role/${roleId}`)
}
