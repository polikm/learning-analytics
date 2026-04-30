import { get, post, put, del } from './request'

/** 学科信息 */
export interface SubjectItem {
  id: number
  tenantId: number
  subjectName: string
  subjectCode: string
  sortOrder: number
  status: number
  createdAt: string
}

/** 创建/编辑学科参数 */
export interface SubjectFormData {
  id?: number
  subjectName: string
  subjectCode: string
  sortOrder?: number
  status?: number
}

/** 获取学科列表 */
export function getSubjectList(tenantId?: number) {
  return get<SubjectItem[]>('/subjects', tenantId ? { tenantId } : undefined)
}

/** 创建学科 */
export function createSubject(data: SubjectFormData) {
  return post<SubjectItem>('/subjects', data)
}

/** 更新学科 */
export function updateSubject(id: number, data: SubjectFormData) {
  return put<SubjectItem>(`/subjects/${id}`, data)
}

/** 删除学科 */
export function deleteSubject(id: number) {
  return del(`/subjects/${id}`)
}
