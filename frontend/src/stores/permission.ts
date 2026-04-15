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
        const fullPath = m.path?.startsWith('/') ? m.path : `/${m.path}`
        return {
          id: m.id,
          name: m.name || m.path || m.menuName,
          path: fullPath,
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

function buildRoutes(menus: MenuItem[]): RouteRecordRaw[] {
  return menus.map((menu) => {
    // backend menu path is often relative like 'system/user'
    const fullPath = menu.path?.startsWith('/') ? menu.path : `/${menu.path}`

    const route: RouteRecordRaw = {
      path: fullPath,
      name: menu.name || fullPath,
      meta: menu.meta || {},
      redirect: menu.redirect,
      component: menu.component
        ? resolveComponent(menu.component)
        : (menu.menuType === 'M' ? ParentView : undefined),
      children: menu.children ? buildRoutes(menu.children) : undefined
    } as RouteRecordRaw
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
    generateRoutes(menus: any[]) {
      const normalizedMenus = normalizeBackendMenus(menus)
      const accessedRoutes = buildRoutes(normalizedMenus)
      this.addRoutes = accessedRoutes
      this.routes = constantRoutes.concat(accessedRoutes)
      return accessedRoutes
    }
  }
})
