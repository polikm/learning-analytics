import { get, post, put, del } from './request'
import type { PageParams, PageResult } from './request'

/** 角色信息 */
export interface RoleItem {
  id: number
  roleName: string
  roleCode: string
  dataScope: number
  sortOrder: number
  status: number
  remark: string
  tenantId: number
  createdAt: string
  updatedAt: string
}

/** 角色查询参数 */
export interface RoleParams extends PageParams {
  keyword?: string
  status?: number
}

/** 创建/编辑角色参数 */
export interface RoleFormData {
  id?: number
  roleName: string
  roleCode: string
  dataScope?: number
  sortOrder?: number
  status?: number
  remark?: string
}

/** 权限树节点 */
export interface PermissionTreeNode {
  id: number
  parentId: number
  permissionName: string
  permissionCode: string
  permissionType: number
  path: string
  component: string
  icon: string
  sortOrder: number
  visible: number
  status: number
  children?: PermissionTreeNode[]
}

/** 获取角色列表（分页） */
export function getRoleList(params: RoleParams) {
  return get<PageResult<RoleItem>>('/roles', params)
}

/** 获取全部角色（下拉用） */
export function getAllRoles() {
  return get<RoleItem[]>('/roles/all')
}

/** 获取角色详情 */
export function getRoleDetail(id: number) {
  return get<RoleItem>(`/roles/${id}`)
}

/** 创建角色 */
export function createRole(data: RoleFormData) {
  return post('/roles', data)
}

/** 更新角色 */
export function updateRole(id: number, data: RoleFormData) {
  return put(`/roles/${id}`, data)
}

/** 删除角色 */
export function deleteRole(id: number) {
  return del(`/roles/${id}`)
}

/** 获取角色权限ID列表 */
export function getRolePermissionIds(id: number) {
  return get<number[]>(`/roles/${id}/permissions`)
}

/** 分配权限 */
export function assignPermissions(id: number, permissionIds: number[]) {
  return put(`/roles/${id}/permissions`, permissionIds)
}
