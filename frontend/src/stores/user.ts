import { defineStore } from 'pinia'
import { login as loginApi, getInfo as getInfoApi, logout as logoutApi } from '@/api/auth'
import router, { resetRouter } from '@/router'

interface UserState {
  token: string
  userInfo: Record<string, any>
  roles: string[]
  permissions: string[]
  menus: any[]
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: localStorage.getItem('token') || '',
    userInfo: {},
    roles: [],
    permissions: [],
    menus: []
  }),

  actions: {
    async login(loginForm: { username: string; password: string }) {
      const res = await loginApi(loginForm)
      const { token } = res.data.data
      this.token = token
      localStorage.setItem('token', token)
      return res
    },

    async getInfo() {
      const res = await getInfoApi()
      const { user, roles, permissions, menus } = res.data.data as any
      this.userInfo = user
      this.roles = roles || []
      this.permissions = permissions || []
      this.menus = menus || []

      // Persist to localStorage for non-store consumers (e.g., directives)
      try {
        localStorage.setItem('roles', JSON.stringify(this.roles || []))
        localStorage.setItem('permissions', JSON.stringify(this.permissions || []))
        localStorage.setItem('menus', JSON.stringify(this.menus || []))
      } catch {
        // ignore
      }

      return res
    },

    async logout() {
      try {
        await logoutApi()
      } finally {
        this.resetToken()
        router.push('/login')
      }
    },

    resetToken() {
      this.token = ''
      this.userInfo = {}
      this.roles = []
      this.permissions = []
      this.menus = []

      // Clear persistent auth state
      try {
        localStorage.removeItem('token')
        localStorage.removeItem('roles')
        localStorage.removeItem('permissions')
        localStorage.removeItem('menus')
      } catch {
        // ignore
      }

      // Reset dynamic routes + permission store
      try {
        resetRouter()
      } catch {
        // ignore
      }
      import('@/stores/permission')
        .then(({ usePermissionStore }) => {
          try {
            const permissionStore = usePermissionStore()
            permissionStore.resetRoutes()
          } catch {
            // ignore
          }
        })
        .catch(() => {})
    }
  }
})
