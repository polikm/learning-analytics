import { get, post, put, del } from './request'
import type { PageResult } from './request'

/** 年级信息 */
export interface GradeItem {
  id: number
  name: string
  schoolId: number
  schoolName: string
  gradeLevel: number
  studentCount: number
  classCount: number
  status: number
  createdAt: string
  updatedAt: string
}

/** 创建/编辑年级参数 */
export interface GradeFormData {
  id?: number
  name: string
  schoolId: number
  gradeLevel?: number
  status?: number
}

/** 获取年级列表 */
export function getGradeList(schoolId: number) {
  return get<GradeItem[]>('/grades', { schoolId })
}

/** 创建年级 */
export function createGrade(data: GradeFormData) {
  return post('/grades', data)
}

/** 更新年级 */
export function updateGrade(id: number, data: GradeFormData) {
  return put(`/grades/${id}`, data)
}

/** 删除年级 */
export function deleteGrade(id: number) {
  return del(`/grades/${id}`)
}
