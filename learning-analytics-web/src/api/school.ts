import { get, post, put, del } from './request'
import type { PageParams, PageResult } from './request'

/** 学校信息 */
export interface SchoolItem {
  id: number
  name: string
  code: string
  address: string
  phone: string
  principal: string
  tenantId: number
  tenantName: string
  status: number
  createdAt: string
  updatedAt: string
}

/** 学校查询参数 */
export interface SchoolParams extends PageParams {
  keyword?: string
  status?: number
  tenantId?: number
}

/** 创建/编辑学校参数 */
export interface SchoolFormData {
  id?: number
  name: string
  code: string
  address?: string
  phone?: string
  principal?: string
  tenantId?: number
  status?: number
}

/** 获取学校列表 */
export function getSchoolList(params: SchoolParams) {
  return get<PageResult<SchoolItem>>('/schools', params)
}

/** 获取学校详情 */
export function getSchoolDetail(id: number) {
  return get<SchoolItem>(`/schools/${id}`)
}

/** 创建学校 */
export function createSchool(data: SchoolFormData) {
  return post('/schools', data)
}

/** 更新学校 */
export function updateSchool(id: number, data: SchoolFormData) {
  return put(`/schools/${id}`, data)
}

/** 删除学校 */
export function deleteSchool(id: number) {
  return del(`/schools/${id}`)
}
