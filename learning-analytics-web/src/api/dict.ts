import { get, post, put, del } from './request'
import type { PageParams, PageResult } from './request'

/** 字典信息 */
export interface DictItem {
  id: number
  tenantId: number
  dictCode: string
  dictName: string
  description: string
  status: number
  createdAt: string
}

/** 字典项信息 */
export interface DictEntryItem {
  id: number
  dictId: number
  itemCode: string
  itemValue: string
  itemLabel: string
  sortOrder: number
  status: number
  createdAt: string
}

/** 字典查询参数 */
export interface DictParams extends PageParams {
  keyword?: string
}

/** 创建/编辑字典参数 */
export interface DictFormData {
  id?: number
  dictCode: string
  dictName: string
  description?: string
  status?: number
}

/** 创建/编辑字典项参数 */
export interface DictItemFormData {
  id?: number
  dictId: number
  itemCode: string
  itemValue: string
  itemLabel: string
  sortOrder?: number
  status?: number
}

/** 字典详情（含字典项列表） */
export interface DictDetail {
  dict: DictItem
  items: DictEntryItem[]
}

/** 获取字典列表（分页） */
export function getDictList(params: DictParams) {
  return get<PageResult<DictItem>>('/dicts', params)
}

/** 获取字典详情（含字典项列表） */
export function getDictDetail(id: number) {
  return get<DictDetail>(`/dicts/${id}`)
}

/** 按字典编码查询字典项列表 */
export function getDictItemsByCode(dictCode: string) {
  return get<DictEntryItem[]>('/dicts/items', { dictCode })
}

/** 创建字典 */
export function createDict(data: DictFormData) {
  return post<DictItem>('/dicts', data)
}

/** 更新字典 */
export function updateDict(id: number, data: DictFormData) {
  return put<DictItem>(`/dicts/${id}`, data)
}

/** 删除字典 */
export function deleteDict(id: number) {
  return del(`/dicts/${id}`)
}

/** 创建字典项 */
export function createDictItem(data: DictItemFormData) {
  return post<DictEntryItem>('/dicts/item', data)
}

/** 更新字典项 */
export function updateDictItem(id: number, data: DictItemFormData) {
  return put<DictEntryItem>(`/dicts/item/${id}`, data)
}

/** 删除字典项 */
export function deleteDictItem(id: number) {
  return del(`/dicts/item/${id}`)
}
