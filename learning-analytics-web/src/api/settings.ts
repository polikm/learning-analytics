import { get, put } from './request'

/** 系统设置项 */
export interface SettingItem {
  id: number
  settingKey: string
  settingValue: string
  settingGroup: string
  description: string
  createdAt: string
  updatedAt: string
}

/** 获取所有设置 */
export function getAllSettings() {
  return get<SettingItem[]>('/settings')
}

/** 根据键获取设置值 */
export function getSettingByKey(key: string) {
  return get<string>(`/settings/${key}`)
}

/** 批量更新设置 */
export function batchUpdateSettings(settings: Record<string, string>) {
  return put('/settings', settings)
}

/** 更新单个设置 */
export function updateSetting(key: string, value: string) {
  return put(`/settings/${key}`, { value })
}
