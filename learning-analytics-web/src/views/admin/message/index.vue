<template>
  <div class="message-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">消息管理</span>
          <el-button type="primary" @click="handleSend">
            <el-icon><Promotion /></el-icon>
            发送消息
          </el-button>
        </div>
      </template>

      <!-- 类型筛选标签页 -->
      <el-tabs v-model="activeType" @tab-change="handleTypeChange">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane label="系统消息" name="system" />
        <el-tab-pane label="通知" name="notification" />
        <el-tab-pane label="警告" name="warning" />
        <el-tab-pane label="提醒" name="reminder" />
      </el-tabs>

      <!-- 消息列表表格 -->
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <span :class="{ 'unread-title': row.isRead === 0 }">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="receiverId" label="接收人ID" width="120" />
        <el-table-column prop="messageType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.messageType)" size="small">
              {{ getTypeLabel(row.messageType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isRead" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isRead === 1 ? 'info' : 'danger'" size="small">
              {{ row.isRead === 1 ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发送时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.isRead === 0"
              type="primary"
              link
              size="small"
              @click="handleMarkRead(row)"
            >
              标记已读
            </el-button>
            <el-popconfirm
              title="确定要删除该消息吗？"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- 发送消息弹窗 -->
    <el-dialog
      v-model="sendDialogVisible"
      title="发送消息"
      width="560px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="sendFormRef"
        :model="sendForm"
        :rules="sendRules"
        label-width="100px"
      >
        <el-form-item label="接收人ID" prop="receiverId">
          <el-input-number
            v-model="sendForm.receiverId"
            :min="1"
            placeholder="请输入接收人ID"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="消息标题" prop="title">
          <el-input v-model="sendForm.title" placeholder="请输入消息标题" maxlength="200" />
        </el-form-item>
        <el-form-item label="消息类型" prop="messageType">
          <el-select v-model="sendForm.messageType" placeholder="请选择消息类型" style="width: 100%">
            <el-option label="系统消息" value="system" />
            <el-option label="通知" value="notification" />
            <el-option label="警告" value="warning" />
            <el-option label="提醒" value="reminder" />
          </el-select>
        </el-form-item>
        <el-form-item label="消息内容" prop="content">
          <el-input
            v-model="sendForm.content"
            type="textarea"
            :rows="5"
            placeholder="请输入消息内容"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sendDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="sendLoading" @click="submitSend">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  getMessageList,
  sendMessage,
  markMessageRead,
  deleteMessage,
  type MessageItem,
  type SendMessageData,
} from '@/api/message'

const loading = ref(false)
const tableData = ref<MessageItem[]>([])
const total = ref(0)
const activeType = ref('')

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  messageType: '' as string,
})

/** 获取消息列表 */
async function fetchData() {
  loading.value = true
  try {
    queryParams.messageType = activeType.value
    const res = await getMessageList(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}

/** 类型筛选切换 */
function handleTypeChange() {
  queryParams.page = 1
  fetchData()
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

/** 标记已读 */
async function handleMarkRead(row: MessageItem) {
  try {
    await markMessageRead(row.id)
    ElMessage.success('已标记为已读')
    fetchData()
  } catch (error) {
    // 错误已在拦截器中处理
  }
}

/** 删除消息 */
async function handleDelete(row: MessageItem) {
  try {
    await deleteMessage(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    // 错误已在拦截器中处理
  }
}

// ========== 发送消息弹窗 ==========
const sendDialogVisible = ref(false)
const sendLoading = ref(false)
const sendFormRef = ref<FormInstance>()

const sendForm = reactive<SendMessageData>({
  senderId: 0,
  receiverId: undefined as unknown as number,
  title: '',
  content: '',
  messageType: 'notification',
})

const sendRules: FormRules = {
  receiverId: [{ required: true, message: '请输入接收人ID', trigger: 'blur' }],
  title: [{ required: true, message: '请输入消息标题', trigger: 'blur' }],
  messageType: [{ required: true, message: '请选择消息类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入消息内容', trigger: 'blur' }],
}

function handleSend() {
  sendForm.receiverId = undefined as unknown as number
  sendForm.title = ''
  sendForm.content = ''
  sendForm.messageType = 'notification'
  sendDialogVisible.value = true
}

async function submitSend() {
  if (!sendFormRef.value) return
  await sendFormRef.value.validate(async (valid) => {
    if (!valid) return
    sendLoading.value = true
    try {
      await sendMessage(sendForm)
      ElMessage.success('消息发送成功')
      sendDialogVisible.value = false
      fetchData()
    } catch (error) {
      // 错误已在拦截器中处理
    } finally {
      sendLoading.value = false
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.message-page {
  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .title {
      font-size: 16px;
      font-weight: 600;
    }
  }

  .unread-title {
    font-weight: 600;
    color: var(--el-color-primary);
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
