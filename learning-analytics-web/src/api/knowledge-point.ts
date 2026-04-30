import { get, post, put, del } from './request'

/** 知识点信息 */
export interface KnowledgePointItem {
  id: number
  tenantId: number
  parentId: number
  subjectId: number
  kpCode: string
  kpName: string
  level: number
  sortOrder: number
  status: number
  createdAt: string
  children?: KnowledgePointItem[]
}

/** 创建/编辑知识点参数 */
export interface KnowledgePointFormData {
  id?: number
  parentId?: number
  subjectId: number
  kpCode: string
  kpName: string
  level?: number
  sortOrder?: number
  status?: number
}

/** 获取知识点树形列表 */
export function getKnowledgePointTree(subjectId: number) {
  return get<KnowledgePointItem[]>('/knowledge-points', { subjectId })
}

/** 创建知识点 */
export function createKnowledgePoint(data: KnowledgePointFormData) {
  return post<KnowledgePointItem>('/knowledge-points', data)
}

/** 更新知识点 */
export function updateKnowledgePoint(id: number, data: KnowledgePointFormData) {
  return put<KnowledgePointItem>(`/knowledge-points/${id}`, data)
}

/** 删除知识点 */
export function deleteKnowledgePoint(id: number) {
  return del(`/knowledge-points/${id}`)
}
