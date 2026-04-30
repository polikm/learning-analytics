import { get, post } from './request'
import type { PageParams, PageResult } from './request'

/** 学习行为信息 */
export interface LearningBehaviorItem {
  id: number
  studentId: number
  subjectId: number | null
  behaviorType: string
  behaviorDetail: string | null
  duration: number | null
  source: string | null
  ipAddress: string | null
  userAgent: string | null
  behaviorTime: string
  tenantId: number
  createdAt: string
}

/** 学习行为查询参数 */
export interface LearningBehaviorParams extends PageParams {
  studentId?: number
  behaviorType?: string
  startTime?: string
  endTime?: string
}

/** 学习行为统计 */
export interface LearningBehaviorStatistics {
  totalDuration: number
  totalDurationFormatted: string
  activeDays: number
  totalCount: number
  behaviorTypeDistribution: Record<string, number>
}

/** 学习行为趋势项 */
export interface LearningBehaviorTrendItem {
  date: string
  count: number
  duration: number
}

/** 记录学习行为参数 */
export interface LearningBehaviorFormData {
  studentId?: number
  subjectId?: number
  behaviorType: string
  behaviorDetail?: string
  duration?: number
  source?: string
  ipAddress?: string
  userAgent?: string
}

/** 分页查询学习行为 */
export function getLearningBehaviorList(params: LearningBehaviorParams) {
  return get<PageResult<LearningBehaviorItem>>('/learning-behaviors', params)
}

/** 获取学习行为统计 */
export function getLearningBehaviorStatistics(studentId?: number) {
  return get<LearningBehaviorStatistics>('/learning-behaviors/statistics', { studentId })
}

/** 获取学习行为趋势 */
export function getLearningBehaviorTrend(studentId?: number, days?: number) {
  return get<LearningBehaviorTrendItem[]>('/learning-behaviors/trend', { studentId, days })
}

/** 记录学习行为 */
export function recordLearningBehavior(data: LearningBehaviorFormData) {
  return post('/learning-behaviors', data)
}
