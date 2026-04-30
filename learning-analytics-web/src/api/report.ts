import { get, post } from './request'
import type { PageParams, PageResult } from './request'

/** 报告信息 */
export interface ReportItem {
  id: number
  name: string
  type: 'student' | 'class' | 'exam' | 'grade'
  targetType: string
  targetId: number
  targetName: string
  templateId: number
  templateName: string
  status: 'pending' | 'generating' | 'completed' | 'failed'
  fileUrl?: string
  generatedBy: number
  generatedByName: string
  createdAt: string
  completedAt?: string
}

/** 报告查询参数 */
export interface ReportParams extends PageParams {
  keyword?: string
  type?: string
  status?: string
  targetId?: number
}

/** 生成报告参数 */
export interface GenerateReportParams {
  templateId: number
  type: string
  targetId: number
  targetIds?: number[]
  name?: string
}

/** 报告模板 */
export interface ReportTemplate {
  id: number
  name: string
  type: 'student' | 'class' | 'exam' | 'grade'
  description: string
  preview?: string
}

/** 获取报告列表 */
export function getReportList(params: ReportParams) {
  return get<PageResult<ReportItem>>('/reports', params)
}

/** 生成报告 */
export function generateReport(params: GenerateReportParams) {
  return post<ReportItem>('/reports/generate', params)
}

/** 获取报告生成状态 */
export function getReportStatus(id: number) {
  return get<ReportItem>(`/reports/${id}/status`)
}

/** 下载报告 */
export function downloadReport(id: number) {
  return get<Blob>(`/reports/${id}/download`)
}

/** 批量生成报告 */
export function batchGenerateReport(params: GenerateReportParams) {
  return post('/reports/batch-generate', params)
}

/** 获取学生报告列表 */
export function getStudentReports(studentId: number) {
  return get<ReportItem[]>(`/reports/student/${studentId}`)
}

/** 获取报告模板列表 */
export function getReportTemplateList(templateType?: string) {
  return get<ReportTemplate[]>('/report-templates', { templateType })
}
