import axios from 'axios'
import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// Backend response structure
interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
}

const clearAuthStorage = () => {
  try {
    localStorage.removeItem('token')
    localStorage.removeItem('roles')
    localStorage.removeItem('permissions')
    localStorage.removeItem('menus')
  } catch {
    // ignore
  }
}

const redirectToLogin = () => {
  const current = router.currentRoute.value
  if (current.path === '/login') return
  const redirect = current.fullPath || '/'
  router.push(`/login?redirect=${encodeURIComponent(redirect)}`)
}

const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// Request interceptor
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    // For file downloads (blob/arraybuffer), bypass standard {code,msg,data} handling
    const rt = (response.config as any)?.responseType
    const data: any = response.data as any
    if (rt === 'blob' || rt === 'arraybuffer' || (typeof Blob !== 'undefined' && data instanceof Blob)) {
      return response as any
    }

    const res = response.data
    if (res.code === 200) {
      return response
    }

    // 兼容后端历史行为：部分未认证/无权限响应曾以 HTTP 200 + 业务 code 返回。
    // 这里统一按认证错误处理，避免刷新或直接打开首页时只停留在“未登录或token已过期”弹窗。
    if (res.code === 401) {
      clearAuthStorage()
      redirectToLogin()
      ElMessage.error(res.msg || '登录已过期，请重新登录')
      return Promise.reject(new Error(res.msg || '登录已过期，请重新登录'))
    }

    if (res.code === 403) {
      router.push('/403')
      ElMessage.error(res.msg || '没有相关权限')
      return Promise.reject(new Error(res.msg || '没有相关权限'))
    }

    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  (error) => {
    if (error.response?.status === 401) {
      clearAuthStorage()
      redirectToLogin()
      ElMessage.error('登录已过期，请重新登录')
    } else if (error.response?.status === 403) {
      router.push('/403')
      ElMessage.error('没有相关权限')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default service
