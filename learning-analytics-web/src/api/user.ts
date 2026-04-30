import { get, post, put, del } from './request'
import type { PageParams, PageResult } from './request'

/** 用户信息 */
export interface UserItem {
  id: number
  username: string
  realName: string
  phone: string
  email: string
  avatar: string
  role: string
  roleId: number
  status: number
  tenantId: number
  tenantName: string
  schoolId?: number
  schoolName?: string
  createdAt: string
  updatedAt: string
}

/** 用户查询参数 */
export interface UserParams extends PageParams {
  keyword?: string
  role?: string
  status?: number
  tenantId?: number
  schoolId?: number
}

/** 创建/编辑用户参数 */
export interface UserFormData {
  id?: number
  username: string
  password?: string
  realName: string
  phone?: string
  email?: string
  role: string
  roleId: number
  status?: number
  tenantId?: number
  schoolId?: number
}

/** 获取用户列表 */
export function getUserList(params: UserParams) {
  return get<PageResult<UserItem>>('/users', params)
}

/** 获取用户详情 */
export function getUserDetail(id: number) {
  return get<UserItem>(`/users/${id}`)
}

/** 创建用户 */
export function createUser(data: UserFormData) {
  return post('/users', data)
}

/** 更新用户 */
export function updateUser(id: number, data: UserFormData) {
  return put(`/users/${id}`, data)
}

/** 删除用户 */
export function deleteUser(id: number) {
  return del(`/users/${id}`)
}
