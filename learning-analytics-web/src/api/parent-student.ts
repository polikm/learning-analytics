import { get, post, put, del } from './request'

/** 家长学生关联信息 */
export interface ParentStudentItem {
  id: number
  parentId: number
  studentId: number
  relation: string
  status: number
  remark: string
  tenantId: number
  createdAt: string
  updatedAt: string
}

/** 创建/编辑关联参数 */
export interface ParentStudentFormData {
  id?: number
  parentId: number
  studentId: number
  relation?: string
  status?: number
  remark?: string
  tenantId?: number
}

/** 获取关联列表 */
export function getParentStudentList(params?: {
  parentId?: number
  studentId?: number
}) {
  return get<ParentStudentItem[]>('/parent-students', params)
}

/** 获取家长关联的学生列表 */
export function getParentStudentsByParent(parentId: number) {
  return get<ParentStudentItem[]>(`/parent-students/parent/${parentId}`)
}

/** 获取学生关联的家长列表 */
export function getParentStudentsByStudent(studentId: number) {
  return get<ParentStudentItem[]>(`/parent-students/student/${studentId}`)
}

/** 新增关联 */
export function createParentStudent(data: ParentStudentFormData) {
  return post('/parent-students', data)
}

/** 更新关联 */
export function updateParentStudent(id: number, data: ParentStudentFormData) {
  return put(`/parent-students/${id}`, data)
}

/** 删除关联 */
export function deleteParentStudent(id: number) {
  return del(`/parent-students/${id}`)
}
