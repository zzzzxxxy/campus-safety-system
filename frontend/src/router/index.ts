import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/404.vue'),
    meta: { title: '404', hidden: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    meta: { title: '首页', hidden: false },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      }
    ]
  }
]

// Placeholder for dynamically loaded routes from backend menus
export const asyncRoutes: RouteRecordRaw[] = []

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior: () => ({ top: 0 })
})

// Ensure dynamic routes are nested under Layout for correct sidebar/breadcrumb.
const LAYOUT_NAME = 'Layout'


// White list - routes that don't require authentication
const whiteList = ['/login', '/404']

// Flag to track if dynamic routes have been added
let hasAddedRoutes = false

function loadPersistedMenus(): any[] {
  try {
    const raw = localStorage.getItem('menus')
    if (!raw) return []
    const parsed = JSON.parse(raw)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

export function resetRouter() {
  hasAddedRoutes = false
  // Remove all dynamically added routes
  const newRouter = createRouter({
    history: createWebHistory(),
    routes: constantRoutes,
    scrollBehavior: () => ({ top: 0 })
  })
  // Replace the matcher
  ;(router as any).matcher = (newRouter as any).matcher
}

// Navigation guard
router.beforeEach(async (to, _from, next) => {
  // Dynamic import inside guard to avoid circular dependency
  const { useUserStore } = await import('@/stores/user')
  const { usePermissionStore } = await import('@/stores/permission')

  const userStore = useUserStore()
  const permissionStore = usePermissionStore()

  const hasToken = userStore.token

  if (hasToken) {
    if (to.path === '/login') {
      // Already logged in, redirect to home
      next({ path: '/' })
    } else {
      // Check if user info has been loaded
      const hasUserInfo = (userStore.roles && userStore.roles.length > 0)
      const hasRoutes = (permissionStore.routes && permissionStore.routes.length > 0)
      if (hasUserInfo && hasRoutes) {
        next()
      } else {
        try {
          // Get user info
          await userStore.getInfo()
          const menus = userStore.menus || []
          const accessRoutes = permissionStore.generateRoutes(menus)

          // Add dynamic routes to router (nest under Layout)
          accessRoutes.forEach((route: RouteRecordRaw) => {
            router.addRoute(LAYOUT_NAME, route)
          })

          // Add catch-all 404 route last
          if (!hasAddedRoutes) {
            router.addRoute({
              path: '/:pathMatch(.*)*',
              redirect: '/404',
              meta: { hidden: true }
            })
            hasAddedRoutes = true
          }

          // Use replace to navigate to ensure the route is found
          next({ ...to, replace: true })
        } catch (error) {
          // If refresh happens while backend is temporarily unavailable, try using cached menus
          const cachedMenus = loadPersistedMenus()
          if (cachedMenus.length) {
            const accessRoutes = permissionStore.generateRoutes(cachedMenus)
            accessRoutes.forEach((route: RouteRecordRaw) => {
              router.addRoute(LAYOUT_NAME, route)
            })
            if (!hasAddedRoutes) {
              router.addRoute({
                path: '/:pathMatch(.*)*',
                redirect: '/404',
                meta: { hidden: true }
              })
              hasAddedRoutes = true
            }
            next({ ...to, replace: true })
            return
          }

          // Failed to get user info, reset token and redirect to login
          userStore.resetToken()
          next(`/login?redirect=${to.path}`)
        }
      }
    }
  } else {
    // No token
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

// Set page title
router.afterEach((to) => {
  const title = to.meta?.title as string
  document.title = title ? `${title} - 校园安全管理系统` : '校园安全管理系统'
})

export default router
