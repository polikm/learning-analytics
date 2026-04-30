<template>
  <el-popover
    placement="bottom-end"
    :width="380"
    trigger="click"
    popper-class="message-center-popover"
  >
    <template #reference>
      <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99">
        <el-icon class="bell-icon" :size="20"><Bell /></el-icon>
      </el-badge>
    </template>

    <div class="message-center">
      <div class="message-center-header">
        <span class="header-title">消息通知</span>
        <el-button
          type="primary"
          link
          size="small"
          :disabled="unreadCount === 0"
          @click="handleMarkAllRead"
        >
          全部已读
        </el-button>
      </div>

      <div v-loading="loading" class="message-center-body">
        <template v-if="messageList.length > 0">
          <div
            v-for="item in messageList"
            :key="item.id"
            class="message-item"
            :class="{ unread: item.isRead === 0 }"
            @click="handleRead(item)"
          >
            <div class="message-item-header">
              <el-tag :type="getTypeTagType(item.messageType)" size="small">
                {{ getTypeLabel(item.messageType) }}
              </el-tag>
              <span class="message-time">{{ formatTime(item.createdAt) }}</span>
            </div>
            <div class="message-title">{{ item.title }}</div>
            <div class="message-content">{{ item.content }}</div>
          </div>
        </template>
        <el-empty v-else description="暂无未读消息" :image-size="60" />
      </div>

      <div class="message-center-footer">
        <el-button type="primary" link @click="goToMessagePage">
          查看全部
        </el-button>
      </div>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getMessageList,
  getUnreadCount,
  markMessageRead,
  markAllMessageRead,
  type MessageItem,
} from '@/api/message'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const unreadCount = ref(0)
const messageList = ref<MessageItem[]>([])

let pollingTimer: ReturnType<typeof setInterval> | null = null

/** 获取未读数量 */
async function fetchUnreadCount() {
  try {
    const userId = authStore.userInfo?.id
    if (!userId) return
    const res = await getUnreadCount(userId)
    unreadCount.value = res.data
  } catch (error) {
    // 静默处理
  }
}

/** 获取最近未读消息 */
async function fetchRecentMessages() {
  loading.value = true
  try {
    const userId = authStore.userInfo?.id
    if (!userId) return
    const res = await getMessageList({
      receiverId: userId,
      isRead: 0,
      page: 1,
      pageSize: 5,
    })
    messageList.value = res.data.list
  } catch (error) {
    // 静默处理
  } finally {
    loading.value = false
  }
}

/** 标记单条已读 */
async function handleRead(item: MessageItem) {
  if (item.isRead === 1) return
  try {
    await markMessageRead(item.id)
    item.isRead = 1
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  } catch (error) {
    // 静默处理
  }
}

/** 全部标记已读 */
async function handleMarkAllRead() {
  try {
    const userId = authStore.userInfo?.id
    if (!userId) return
    await markAllMessageRead(userId)
    unreadCount.value = 0
    messageList.value = []
    ElMessage.success('已全部标记为已读')
  } catch (error) {
    // 错误已在拦截器中处理
  }
}

/** 跳转到消息管理页面 */
function goToMessagePage() {
  router.push('/admin/messages')
}

/** 获取类型标签样式 */
function getTypeTagType(type: string) {
  const map: Record<string, string> = {
    system: '',
    notification: 'success',
    warning: 'warning',
    reminder: 'info',
  }
  return map[type] || 'info'
}

/** 获取类型标签文本 */
function getTypeLabel(type: string) {
  const map: Record<string, string> = {
    system: '系统消息',
    notification: '通知',
    warning: '警告',
    reminder: '提醒',
  }
  return map[type] || type
}

/** 格式化时间 */
function formatTime(time: string) {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  if (diff < 60 * 1000) return '刚刚'
  if (diff < 60 * 60 * 1000) return `${Math.floor(diff / (60 * 1000))}分钟前`
  if (diff < 24 * 60 * 60 * 1000) return `${Math.floor(diff / (60 * 60 * 1000))}小时前`
  if (diff < 7 * 24 * 60 * 60 * 1000) return `${Math.floor(diff / (24 * 60 * 60 * 1000))}天前`
  return time.slice(0, 10)
}

onMounted(() => {
  fetchUnreadCount()
  fetchRecentMessages()
  // 每60秒轮询未读数量
  pollingTimer = setInterval(() => {
    fetchUnreadCount()
  }, 60000)
})

onUnmounted(() => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
})
</script>

<style scoped lang="scss">
.bell-icon {
  cursor: pointer;
  color: var(--color-text-regular);
  transition: color 0.3s;

  &:hover {
    color: var(--color-primary);
  }
}

.message-center {
  .message-center-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--el-border-color-lighter);

    .header-title {
      font-size: 16px;
      font-weight: 600;
    }
  }

  .message-center-body {
    max-height: 360px;
    overflow-y: auto;
    padding: 8px 0;
  }

  .message-item {
    padding: 10px 4px;
    border-bottom: 1px solid var(--el-border-color-extra-light);
    cursor: pointer;
    transition: background-color 0.2s;

    &:hover {
      background-color: var(--el-fill-color-light);
    }

    &.unread {
      .message-title {
        font-weight: 600;
        color: var(--el-color-primary);
      }
    }

    .message-item-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 4px;

      .message-time {
        font-size: 12px;
        color: var(--el-text-color-placeholder);
      }
    }

    .message-title {
      font-size: 14px;
      margin-bottom: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .message-content {
      font-size: 12px;
      color: var(--el-text-color-secondary);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .message-center-footer {
    display: flex;
    justify-content: center;
    padding-top: 12px;
    border-top: 1px solid var(--el-border-color-lighter);
  }
}
</style>
