import type { App, DirectiveBinding } from 'vue'

/**
 * v-permission 按钮级权限控制
 * 用法：
 *   <el-button v-permission="'system:user:add'">新增</el-button>
 *   <el-button v-permission="['system:user:add','system:user:edit']">新增/编辑</el-button>
 */

function hasAnyPermission(userPerms: string[], required: string | string[]): boolean {
  const req = Array.isArray(required) ? required : [required]
  if (!req.length) return true
  return req.some((p) => userPerms.includes(p))
}

function getUserPermissions(): string[] {
  // Avoid importing pinia store here to prevent circular deps in directive
  try {
    const raw = localStorage.getItem('permissions')
    if (raw) {
      const arr = JSON.parse(raw)
      return Array.isArray(arr) ? arr : []
    }
  } catch {
    // ignore
  }
  return []
}

export const permissionDirective = {
  mounted(el: HTMLElement, binding: DirectiveBinding<string | string[]>) {
    const required = binding.value
    if (!required) return

    // admin bypass: if roles contains 'admin'
    let roles: string[] = []
    try {
      const raw = localStorage.getItem('roles')
      if (raw) {
        const arr = JSON.parse(raw)
        roles = Array.isArray(arr) ? arr : []
      }
    } catch {
      roles = []
    }
    if (roles.includes('admin')) return

    const perms = getUserPermissions()
    const ok = hasAnyPermission(perms, required)
    if (!ok) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
}

export function setupPermissionDirective(app: App) {
  app.directive('permission', permissionDirective)
}
