import { get, post, put, del } from './request'
import type { PageParams, PageResult } from './request'

/** 试卷题目项 */
export interface PaperQuestionItem {
  questionId: number
  content: string
  type: string
  score: number
  orderNum: number
  difficulty: number
  knowledgePoints: string[]
}

/** 试卷信息 */
export interface PaperItem {
  id: number
  title: string
  subject: string
  gradeId: number
  gradeName: string
  totalScore: number
  questionCount: number
  duration: number
  questions?: PaperQuestionItem[]
  status: 'draft' | 'published'
  createdBy: number
  createdByName: string
  createdAt: string
  updatedAt: string
}

/** 试卷查询参数 */
export interface PaperParams extends PageParams {
  keyword?: string
  subject?: string
  gradeId?: number
  status?: string
}

/** 创建/编辑试卷参数 */
export interface PaperFormData {
  id?: number
  title: string
  subject: string
  gradeId: number
  duration: number
  questions?: { questionId: number; score: number; orderNum: number }[]
}

/** 智能组卷参数 */
export interface AutoGeneratePaperParams {
  subject: string
  gradeId: number
  knowledgePointIds?: number[]
  difficulty?: number
  questionTypeRatios?: { type: string; count: number }[]
  totalScore?: number
  duration?: number
}

/** 获取试卷列表 */
export function getPaperList(params: PaperParams) {
  return get<PageResult<PaperItem>>('/papers', params)
}

/** 获取试卷详情 */
export function getPaperDetail(id: number) {
  return get<PaperItem>(`/papers/${id}`)
}

/** 创建试卷 */
export function createPaper(data: PaperFormData) {
  return post('/papers', data)
}

/** 更新试卷 */
export function updatePaper(id: number, data: PaperFormData) {
  return put(`/papers/${id}`, data)
}

/** 删除试卷 */
export function deletePaper(id: number) {
  return del(`/papers/${id}`)
}

/** 智能组卷 */
export function autoGeneratePaper(params: AutoGeneratePaperParams) {
  return post<PaperItem>('/papers/auto-generate', params)
}
