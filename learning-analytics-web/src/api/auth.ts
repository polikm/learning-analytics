import { post, get } from './request'

/** 登录参数 */
export interface LoginData {
  username: string
  password: string
  tenantId?: string
}

/** 登录响应 */
export interface LoginResult {
  token: string
  refreshToken: string
}

/** 用户信息 */
export interface UserInfo {
  id: number
  username: string
  realName: string
  avatar: string
  role: string
  roleId: number
  tenantId: number
  tenantName: string
  schoolId?: number
  schoolName?: string
}

/** 登录 */
export function login(data: LoginData) {
  return post<LoginResult>('/auth/login', data)
}

/** 登出 */
export function logout() {
  return post('/auth/logout')
}

/** 获取当前用户信息 */
export function getUserInfo() {
  return get<UserInfo>('/auth/user-info')
}

/** 刷新令牌 */
export function refreshToken(refreshToken: string) {
  return post<LoginResult>('/auth/refresh-token', { refreshToken })
}
