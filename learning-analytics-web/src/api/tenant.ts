import { get, post, put, del } from './request'
import type { PageParams, PageResult } from './request'

/** 租户信息 */
export interface TenantItem {
  id: number
  name: string
  code: string
  contactName: string
  contactPhone: string
  contactEmail: string
  domain?: string
  logo?: string
  expireDate: string
  schoolCount: number
  userCount: number
  status: number
  createdAt: string
  updatedAt: string
}

/** 租户查询参数 */
export interface TenantParams extends PageParams {
  keyword?: string
  status?: number
}

/** 创建/编辑租户参数 */
export interface TenantFormData {
  id?: number
  name: string
  code: string
  contactName: string
  contactPhone: string
  contactEmail: string
  domain?: string
  logo?: string
  expireDate: string
  status?: number
}

/** 获取租户列表 */
export function getTenantList(params: TenantParams) {
  return get<PageResult<TenantItem>>('/tenants', params)
}

/** 创建租户 */
export function createTenant(data: TenantFormData) {
  return post('/tenants', data)
}

/** 更新租户 */
export function updateTenant(id: number, data: TenantFormData) {
  return put(`/tenants/${id}`, data)
}

/** 删除租户 */
export function deleteTenant(id: number) {
  return del(`/tenants/${id}`)
}

/** 获取租户详情 */
export function getTenantDetail(id: number) {
  return get<TenantItem>(`/tenants/${id}`)
}
