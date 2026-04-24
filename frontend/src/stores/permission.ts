import { defineStore } from 'pinia'
import { constantRoutes } from '@/router'
import type { RouteRecordRaw } from 'vue-router'

// A simple router-view wrapper for directory menus
import ParentView from '@/layout/components/ParentView.vue'

// Dynamic import map: resolves component path strings to actual component imports
const viewModules = import.meta.glob('../views/**/*.vue')

function resolveComponent(componentPath: string) {
  const path = `../views/${componentPath}.vue`
  if (viewModules[path]) {
    return viewModules[path]
  }
  console.warn(`Component not found: ${path}`)
  return () => import('@/views/404.vue')
}

export interface MenuItem {
  id?: number
  name?: string
  path: string
  component?: string
  redirect?: string
  meta?: {
    title?: string
    icon?: string
    hidden?: boolean
    keepAlive?: boolean
  }
  children?: MenuItem[]

  // backend SysMenu compatibility
  menuName?: string
  icon?: string
  visible?: number
  menuType?: string
}

function normalizeBackendMenus(menus: any[]): MenuItem[] {
  const walk = (list: any[]): MenuItem[] => {
    return (list || [])
      // filter out button permissions
      .filter((m) => m && m.menuType !== 'F')
      .map((m) => {
        const children = walk(m.children || [])
        // backend visible: 0=显示, 1=隐藏
        const hidden = m.visible === 1

        return {
          id: m.id,
          name: m.name || m.path || m.menuName,
          // IMPORTANT: keep backend path as-is (usually relative). We'll compose full paths in buildRoutes
          path: m.path,
          component: m.component || undefined,
          redirect: m.redirect,
          meta: {
            title: (m.meta && m.meta.title) || m.menuName,
            icon: (m.meta && m.meta.icon) || m.icon,
            hidden: (m.meta && m.meta.hidden) ?? hidden,
            keepAlive: (m.meta && m.meta.keepAlive) || false
          },
          children,

          // keep raw fields for downstream
          menuType: m.menuType
        } as MenuItem
      })
  }
  return walk(menus)
}

function joinPath(parent: string, child: string) {
  const p = (parent || '').replace(/\/+$/, '')
  const c = (child || '').replace(/^\/+/, '')
  if (!p) return `/${c}`
  if (!c) return p || '/'
  return `${p}/${c}`
}

function buildRoutes(menus: MenuItem[], parentPath = ''): RouteRecordRaw[] {
  return menus.map((menu) => {
    const rawPath = menu.path || ''

    // For nested routes, child path must be RELATIVE, otherwise it becomes root and breaks matched/breadcrumb/opened.
    const isRoot = !parentPath
    const routePath = isRoot ? (rawPath.startsWith('/') ? rawPath : `/${rawPath}`) : rawPath.replace(/^\//, '')
    const fullPath = isRoot ? routePath : joinPath(parentPath, routePath)
    const children = menu.children && menu.children.length ? buildRoutes(menu.children, fullPath) : undefined

    const route: RouteRecordRaw = {
      path: routePath,
      name: (menu.name || fullPath).replace(/\//g, '_'),
      meta: { ...(menu.meta || {}), fullPath },
      redirect: menu.redirect,
      component: menu.component
        ? resolveComponent(menu.component)
        : (menu.menuType === 'M' ? ParentView : undefined),
      children
    } as RouteRecordRaw

    // Directory/menu nodes from backend often have no explicit redirect. Without this,
    // clicking /device lands on an empty ParentView; redirect to first visible child so
    // single-child modules such as “设备管理/设备列表” never appear as a blank/404 page.
    if (!route.redirect && children && children.length > 0) {
      route.redirect = joinPath(fullPath, String(children[0].path || ''))
    }

    return route
  })
}
interface PermissionState {
  routes: RouteRecordRaw[]
  addRoutes: RouteRecordRaw[]
}

export const usePermissionStore = defineStore('permission', {
  state: (): PermissionState => ({
    routes: [],
    addRoutes: []
  }),

  actions: {
    resetRoutes() {
      this.routes = []
      this.addRoutes = []
    },

    generateRoutes(menus: any[]) {
      const normalizedMenus = normalizeBackendMenus(menus)
      const accessedRoutes = buildRoutes(normalizedMenus)
      this.addRoutes = accessedRoutes
      this.routes = constantRoutes.concat(accessedRoutes)
      return accessedRoutes
    }
  }
})
