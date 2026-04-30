import { get } from './request'
import type { PageParams, PageResult } from './request'

/** 审计日志信息 */
export interface AuditLogItem {
  id: number
  userId: number | null
  username: string | null
  operation: string
  method: string | null
  requestUrl: string | null
  requestMethod: string | null
  requestParams: string | null
  responseData: string | null
  ipAddress: string | null
  userAgent: string | null
  duration: number | null
  status: number
  errorMsg: string | null
  tenantId: number
  createdAt: string
}

/** 审计日志查询参数 */
export interface AuditLogParams extends PageParams {
  userId?: number
  operation?: string
  startTime?: string
  endTime?: string
}

/** 分页查询审计日志 */
export function getAuditLogList(params: AuditLogParams) {
  return get<PageResult<AuditLogItem>>('/audit-logs', params)
}

/** 获取审计日志详情 */
export function getAuditLogDetail(id: number) {
  return get<AuditLogItem>(`/audit-logs/${id}`)
}
