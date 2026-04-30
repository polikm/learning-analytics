import { get, post, put, del } from './request'
import type { PageParams, PageResult } from './request'

/** 消息信息 */
export interface MessageItem {
  id: number
  senderId: number
  receiverId: number
  title: string
  content: string
  messageType: string
  isRead: number
  readTime?: string
  extraData?: string
  status: number
  remark?: string
  tenantId: number
  createdAt: string
  updatedAt: string
}

/** 消息查询参数 */
export interface MessageParams extends PageParams {
  receiverId?: number
  messageType?: string
  isRead?: number
}

/** 发送消息参数 */
export interface SendMessageData {
  senderId: number
  receiverId: number
  title: string
  content: string
  messageType: string
  extraData?: string
  tenantId?: number
}

/** 获取消息列表 */
export function getMessageList(params: MessageParams) {
  return get<PageResult<MessageItem>>('/messages', params)
}

/** 获取未读消息数量 */
export function getUnreadCount(receiverId: number) {
  return get<number>('/messages/unread-count', { receiverId })
}

/** 发送消息 */
export function sendMessage(data: SendMessageData) {
  return post('/messages', data)
}

/** 标记消息已读 */
export function markMessageRead(id: number) {
  return put(`/messages/${id}/read`)
}

/** 全部标记已读 */
export function markAllMessageRead(receiverId: number) {
  return put('/messages/read-all', { receiverId })
}

/** 删除消息 */
export function deleteMessage(id: number) {
  return del(`/messages/${id}`)
}
