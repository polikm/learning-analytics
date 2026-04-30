import { get, post, put, del } from './request'

/** 教师任课分配信息 */
export interface TeacherAssignmentItem {
  id: number
  teacherId: number
  schoolId: number
  gradeId: number
  classId: number
  subjectId: number
  academicYear: string
  semester: string
  status: number
  remark: string
  tenantId: number
  createdAt: string
  updatedAt: string
}

/** 创建/编辑任课分配参数 */
export interface TeacherAssignmentFormData {
  id?: number
  teacherId: number
  schoolId: number
  gradeId?: number
  classId?: number
  subjectId: number
  academicYear?: string
  semester?: string
  status?: number
  remark?: string
  tenantId?: number
}

/** 获取任课分配列表 */
export function getTeacherAssignmentList(params?: {
  teacherId?: number
  schoolId?: number
  subjectId?: number
  academicYear?: string
}) {
  return get<TeacherAssignmentItem[]>('/teacher-assignments', params)
}

/** 获取教师的任课信息 */
export function getTeacherAssignmentsByTeacher(teacherId: number) {
  return get<TeacherAssignmentItem[]>(`/teacher-assignments/teacher/${teacherId}`)
}

/** 新增任课分配 */
export function createTeacherAssignment(data: TeacherAssignmentFormData) {
  return post('/teacher-assignments', data)
}

/** 更新任课分配 */
export function updateTeacherAssignment(id: number, data: TeacherAssignmentFormData) {
  return put(`/teacher-assignments/${id}`, data)
}

/** 删除任课分配 */
export function deleteTeacherAssignment(id: number) {
  return del(`/teacher-assignments/${id}`)
}
