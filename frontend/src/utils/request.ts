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
    const res = response.data
    if (res.code === 200) {
      return response
    } else {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
  },
  (error) => {
    if (error.response?.status === 401) {
      try {
        localStorage.removeItem('token')
        localStorage.removeItem('roles')
        localStorage.removeItem('permissions')
        localStorage.removeItem('menus')
      } catch {
        // ignore
      }
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default service
