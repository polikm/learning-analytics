import { get, post, put, del } from './request'
import type { PageParams, PageResult } from './request'

/** 题目选项 */
export interface QuestionOption {
  label: string
  content: string
  isCorrect: boolean
}

/** 题目信息 */
export interface QuestionItem {
  id: number
  content: string
  type: 'single_choice' | 'multi_choice' | 'true_false' | 'fill_blank' | 'short_answer'
  subject: string
  knowledgePoints: string[]
  difficulty: number
  options?: QuestionOption[]
  answer: string
  analysis: string
  reviewStatus: 'pending' | 'approved' | 'rejected'
  reviewRemark?: string
  createdBy: number
  createdByName: string
  createdAt: string
  updatedAt: string
}

/** 题目查询参数 */
export interface QuestionParams extends PageParams {
  keyword?: string
  type?: string
  subject?: string
  difficulty?: number
  knowledgePointId?: number
  reviewStatus?: string
}

/** 创建/编辑题目参数 */
export interface QuestionFormData {
  id?: number
  content: string
  type: string
  subject: string
  knowledgePoints?: string[]
  difficulty: number
  options?: QuestionOption[]
  answer: string
  analysis: string
}

/** 获取题目列表 */
export function getQuestionList(params: QuestionParams) {
  return get<PageResult<QuestionItem>>('/questions', params)
}

/** 获取题目详情 */
export function getQuestionDetail(id: number) {
  return get<QuestionItem>(`/questions/${id}`)
}

/** 创建题目 */
export function createQuestion(data: QuestionFormData) {
  return post('/questions', data)
}

/** 更新题目 */
export function updateQuestion(id: number, data: QuestionFormData) {
  return put(`/questions/${id}`, data)
}

/** 删除题目 */
export function deleteQuestion(id: number) {
  return del(`/questions/${id}`)
}

/** 审核题目 */
export function reviewQuestion(id: number, status: 'approved' | 'rejected', remark?: string) {
  return post(`/questions/${id}/review`, { status, remark })
}
