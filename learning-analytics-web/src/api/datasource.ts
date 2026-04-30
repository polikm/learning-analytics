import { get, post, put, del } from './request'

/** 数据源信息 */
export interface DataSourceItem {
  id: number
  tenantId: number
  sourceName: string
  sourceType: string
  apiEndpoint: string
  authConfig: string
  syncConfig: string
  lastSyncTime: string
  syncStatus: number
  status: number
  createdAt: string
  updatedAt: string
}

/** 创建/编辑数据源参数 */
export interface DataSourceFormData {
  id?: number
  sourceName: string
  sourceType: string
  apiEndpoint: string
  authConfig?: string
  syncConfig?: string
  status?: number
}

/** 同步状态信息 */
export interface SyncStatusInfo {
  syncStatus: number
  lastSyncTime: string
  nextSyncTime: string
  message?: string
}

/** 获取数据源列表 */
export function getDataSourceList() {
  return get<DataSourceItem[]>('/data-sources')
}

/** 获取数据源详情 */
export function getDataSourceDetail(id: number) {
  return get<DataSourceItem>(`/data-sources/${id}`)
}

/** 创建数据源 */
export function createDataSource(data: DataSourceFormData) {
  return post<DataSourceItem>('/data-sources', data)
}

/** 更新数据源 */
export function updateDataSource(id: number, data: DataSourceFormData) {
  return put<DataSourceItem>(`/data-sources/${id}`, data)
}

/** 删除数据源 */
export function deleteDataSource(id: number) {
  return del(`/data-sources/${id}`)
}

/** 手动触发同步 */
export function triggerDataSourceSync(id: number) {
  return post(`/data-sources/${id}/sync`)
}

/** 获取同步状态 */
export function getDataSourceSyncStatus(id: number) {
  return get<SyncStatusInfo>(`/data-sources/${id}/sync-status`)
}
