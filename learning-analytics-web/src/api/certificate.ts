import { get, post, put, del } from './request'
import type { PageParams, PageResult } from './request'

/** 证书信息 */
export interface CertificateItem {
  id: number
  studentId: number
  studentName: string
  studentNo: string
  classId: number
  className: string
  name: string
  type: string
  level: string
  issuingAuthority: string
  issueDate: string
  certificateNo: string
  scanFile?: string
  reviewStatus: 'pending' | 'approved' | 'rejected'
  reviewRemark?: string
  createdBy: number
  createdByName: string
  createdAt: string
  updatedAt: string
}

/** 证书查询参数 */
export interface CertificateParams extends PageParams {
  keyword?: string
  studentId?: number
  classId?: number
  type?: string
  reviewStatus?: string
}

/** 创建/编辑证书参数 */
export interface CertificateFormData {
  id?: number
  studentId: number
  name: string
  type: string
  level: string
  issuingAuthority: string
  issueDate: string
  certificateNo?: string
  scanFile?: string
}

/** 证书统计 */
export interface CertificateStatistics {
  total: number
  approved: number
  pending: number
  rejected: number
  byType: { type: string; count: number }[]
  byLevel: { level: string; count: number }[]
}

/** 获取证书列表 */
export function getCertificateList(params: CertificateParams) {
  return get<PageResult<CertificateItem>>('/certificates', params)
}

/** 获取证书详情 */
export function getCertificateDetail(id: number) {
  return get<CertificateItem>(`/certificates/${id}`)
}

/** 创建证书 */
export function createCertificate(data: CertificateFormData) {
  return post('/certificates', data)
}

/** 更新证书 */
export function updateCertificate(id: number, data: CertificateFormData) {
  return put(`/certificates/${id}`, data)
}

/** 删除证书 */
export function deleteCertificate(id: number) {
  return del(`/certificates/${id}`)
}

/** 审核证书 */
export function reviewCertificate(id: number, status: 'approved' | 'rejected', remark?: string) {
  return post(`/certificates/${id}/review`, { status, remark })
}

/** 获取证书统计 */
export function getCertificateStatistics() {
  return get<CertificateStatistics>('/certificates/statistics')
}
