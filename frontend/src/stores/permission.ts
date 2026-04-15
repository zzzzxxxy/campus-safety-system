import { defineStore } from 'pinia'
import { constantRoutes } from '@/router'
import type { RouteRecordRaw } from 'vue-router'

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
}

function buildRoutes(menus: MenuItem[]): RouteRecordRaw[] {
  return menus.map((menu) => {
    const route: RouteRecordRaw = {
      path: menu.path,
      name: menu.name || menu.path,
      meta: menu.meta || {},
      redirect: menu.redirect,
      component: menu.component ? resolveComponent(menu.component) : undefined,
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
    generateRoutes(menus: MenuItem[]) {
      const accessedRoutes = buildRoutes(menus)
      this.addRoutes = accessedRoutes
      this.routes = constantRoutes.concat(accessedRoutes)
      return accessedRoutes
    }
  }
})
