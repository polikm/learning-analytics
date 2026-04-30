import { get, post, put, del } from './request'

/** 报告模板信息 */
export interface ReportTemplateItem {
  id: number
  tenantId: number
  templateName: string
  templateType: string
  templateConfig: string
  isDefault: number
  status: number
  createdAt: string
  updatedAt: string
}

/** 创建/编辑报告模板参数 */
export interface ReportTemplateFormData {
  id?: number
  templateName: string
  templateType: string
  templateConfig?: string
  isDefault?: number
  status?: number
}

/** 获取模板列表 */
export function getReportTemplateList(templateType?: string) {
  return get<ReportTemplateItem[]>('/report-templates', templateType ? { templateType } : undefined)
}

/** 获取模板详情 */
export function getReportTemplateDetail(id: number) {
  return get<ReportTemplateItem>(`/report-templates/${id}`)
}

/** 创建报告模板 */
export function createReportTemplate(data: ReportTemplateFormData) {
  return post<ReportTemplateItem>('/report-templates', data)
}

/** 更新报告模板 */
export function updateReportTemplate(id: number, data: ReportTemplateFormData) {
  return put<ReportTemplateItem>(`/report-templates/${id}`, data)
}

/** 删除报告模板 */
export function deleteReportTemplate(id: number) {
  return del(`/report-templates/${id}`)
}
