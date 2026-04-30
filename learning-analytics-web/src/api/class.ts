import { get, post, put, del } from './request'
import type { PageResult } from './request'

/** 班级信息 */
export interface ClassItem {
  id: number
  name: string
  gradeId: number
  gradeName: string
  schoolId: number
  schoolName: string
  teacherId: number
  teacherName: string
  studentCount: number
  status: number
  createdAt: string
  updatedAt: string
}

/** 创建/编辑班级参数 */
export interface ClassFormData {
  id?: number
  name: string
  gradeId: number
  teacherId?: number
  status?: number
}

/** 学生简要信息 */
export interface ClassStudent {
  id: number
  name: string
  studentNo: string
  gender: number
  status: number
}

/** 获取班级列表 */
export function getClassList(gradeId: number) {
  return get<ClassItem[]>('/classes', { gradeId })
}

/** 创建班级 */
export function createClass(data: ClassFormData) {
  return post('/classes', data)
}

/** 更新班级 */
export function updateClass(id: number, data: ClassFormData) {
  return put(`/classes/${id}`, data)
}

/** 删除班级 */
export function deleteClass(id: number) {
  return del(`/classes/${id}`)
}

/** 获取班级学生列表 */
export function getClassStudents(classId: number) {
  return get<ClassStudent[]>(`/classes/${classId}/students`)
}
