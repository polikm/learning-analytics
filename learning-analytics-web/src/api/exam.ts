import { get, post, put, del } from './request'
import type { PageParams, PageResult } from './request'

/** 测评信息 */
export interface ExamItem {
  id: number
  title: string
  paperId: number
  paperName: string
  subject: string
  classIds: number[]
  classNames: string[]
  startTime: string
  endTime: string
  duration: number
  examMode: 'normal' | 'practice' | 'test'
  status: 'draft' | 'published' | 'in_progress' | 'ended' | 'grading'
  studentCount: number
  submittedCount: number
  createdBy: number
  createdByName: string
  createdAt: string
  updatedAt: string
}

/** 测评查询参数 */
export interface ExamParams extends PageParams {
  keyword?: string
  subject?: string
  classId?: number
  status?: string
}

/** 创建/编辑测评参数 */
export interface ExamFormData {
  id?: number
  title: string
  paperId: number
  classIds: number[]
  startTime: string
  endTime: string
  duration: number
  examMode: string
}

/** 答题数据 */
export interface AnswerData {
  examId: number
  studentId: number
  questionId: number
  answer: string
  isCorrect?: boolean
  score?: number
}

/** 获取测评列表 */
export function getExamList(params: ExamParams) {
  return get<PageResult<ExamItem>>('/exams', params)
}

/** 获取测评详情 */
export function getExamDetail(id: number) {
  return get<ExamItem>(`/exams/${id}`)
}

/** 创建测评 */
export function createExam(data: ExamFormData) {
  return post('/exams', data)
}

/** 更新测评 */
export function updateExam(id: number, data: ExamFormData) {
  return put(`/exams/${id}`, data)
}

/** 删除测评 */
export function deleteExam(id: number) {
  return del(`/exams/${id}`)
}

/** 发布测评 */
export function publishExam(id: number) {
  return post(`/exams/${id}/publish`)
}

/** 开始测评 */
export function startExam(id: number) {
  return post(`/exams/${id}/start`)
}

/** 提交测评 */
export function submitExam(id: number) {
  return post(`/exams/${id}/submit`)
}

/** 保存答案 */
export function saveAnswer(data: AnswerData) {
  return post('/exams/save-answer', data)
}

/** 自动阅卷 */
export function autoGradeExam(id: number) {
  return post(`/exams/${id}/auto-grade`)
}
