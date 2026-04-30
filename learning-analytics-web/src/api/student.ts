import { get, post, put, del } from './request'
import type { PageParams, PageResult } from './request'

/** 学生信息 */
export interface StudentItem {
  id: number
  name: string
  studentNo: string
  gender: number
  classId: number
  className: string
  gradeId: number
  gradeName: string
  schoolId: number
  schoolName: string
  phone?: string
  parentName?: string
  parentPhone?: string
  status: number
  createdAt: string
  updatedAt: string
}

/** 学生查询参数 */
export interface StudentParams extends PageParams {
  keyword?: string
  classId?: number
  gradeId?: number
  schoolId?: number
  gender?: number
  status?: number
}

/** 创建/编辑学生参数 */
export interface StudentFormData {
  id?: number
  name: string
  studentNo: string
  gender: number
  classId: number
  phone?: string
  parentName?: string
  parentPhone?: string
  status?: number
}

/** 获取学生列表 */
export function getStudentList(params: StudentParams) {
  return get<PageResult<StudentItem>>('/students', params)
}

/** 获取学生详情 */
export function getStudentDetail(id: number) {
  return get<StudentItem>(`/students/${id}`)
}

/** 创建学生 */
export function createStudent(data: StudentFormData) {
  return post('/students', data)
}

/** 更新学生 */
export function updateStudent(id: number, data: StudentFormData) {
  return put(`/students/${id}`, data)
}

/** 删除学生 */
export function deleteStudent(id: number) {
  return del(`/students/${id}`)
}
