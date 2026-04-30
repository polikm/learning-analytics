import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

/** 统一响应结构 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

/** 分页请求参数 */
export interface PageParams {
  page: number
  pageSize: number
}

/** 分页响应结构 */
export interface PageResult<T = any> {
  list: T[]
  total: number
  page: number
  pageSize: number
}

// 创建 axios 实例
const service: AxiosInstance = axios.create({
  baseURL: '/api/v1',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('token')
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const res = response.data
    if (res.code !== 0 && res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      // 401 未授权，跳转登录
      if (res.code === 401) {
        localStorage.removeItem('token')
        router.push('/login')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return response
  },
  (error) => {
    const status = error.response?.status
    let message = '网络错误，请稍后重试'
    if (status === 401) {
      message = '登录已过期，请重新登录'
      localStorage.removeItem('token')
      router.push('/login')
    } else if (status === 403) {
      message = '没有权限访问'
    } else if (status === 404) {
      message = '请求的资源不存在'
    } else if (status === 500) {
      message = '服务器内部错误'
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

/** 封装 GET 请求 */
export function get<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.get(url, { params, ...config }).then((res) => res.data)
}

/** 封装 POST 请求 */
export function post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.post(url, data, config).then((res) => res.data)
}

/** 封装 PUT 请求 */
export function put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.put(url, data, config).then((res) => res.data)
}

/** 封装 DELETE 请求 */
export function del<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.delete(url, { params, ...config }).then((res) => res.data)
}

export default service
