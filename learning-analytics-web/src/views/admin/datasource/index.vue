<template>
  <div class="datasource-management">
    <!-- 表格区域 -->
    <el-card shadow="never">
      <template #header>
        <div class="flex-between">
          <span>数据源列表</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">新增数据源</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="sourceName" label="数据源名称" min-width="140" show-overflow-tooltip />
        <el-table-column prop="sourceType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ sourceTypeMap[row.sourceType] || row.sourceType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="apiEndpoint" label="API地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="syncStatus" label="同步状态" width="100">
          <template #default="{ row }">
            <el-tag :type="syncStatusType(row.syncStatus)">
              {{ syncStatusLabel(row.syncStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastSyncTime" label="最后同步时间" width="180">
          <template #default="{ row }">
            {{ row.lastSyncTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link icon="Refresh" @click="handleSync(row)">同步</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="560px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="数据源名称" prop="sourceName">
          <el-input v-model="formData.sourceName" placeholder="请输入数据源名称" />
        </el-form-item>
        <el-form-item label="数据源类型" prop="sourceType">
          <el-select v-model="formData.sourceType" placeholder="请选择数据源类型" style="width: 100%">
            <el-option label="REST API" value="rest_api" />
            <el-option label="数据库" value="database" />
            <el-option label="文件导入" value="file" />
            <el-option label="WebSocket" value="websocket" />
          </el-select>
        </el-form-item>
        <el-form-item label="API地址" prop="apiEndpoint">
          <el-input v-model="formData.apiEndpoint" placeholder="请输入API地址" />
        </el-form-item>
        <el-form-item label="认证配置" prop="authConfig">
          <el-input v-model="formData.authConfig" type="textarea" :rows="3" placeholder="请输入认证配置（JSON格式）" />
        </el-form-item>
        <el-form-item label="同步配置" prop="syncConfig">
          <el-input v-model="formData.syncConfig" type="textarea" :rows="3" placeholder="请输入同步配置（JSON格式）" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  getDataSourceList,
  createDataSource,
  updateDataSource,
  deleteDataSource,
  triggerDataSourceSync,
  type DataSourceFormData,
  type DataSourceItem,
} from '@/api/datasource'

const sourceTypeMap: Record<string, string> = {
  rest_api: 'REST API',
  database: '数据库',
  file: '文件导入',
  websocket: 'WebSocket',
}

/** 同步状态标签 */
function syncStatusLabel(status: number): string {
  const map: Record<number, string> = {
    0: '未同步',
    1: '同步中',
    2: '同步成功',
    3: '同步失败',
  }
  return map[status] ?? '未知'
}

/** 同步状态标签颜色 */
function syncStatusType(status: number): '' | 'success' | 'warning' | 'danger' | 'info' {
  const map: Record<number, '' | 'success' | 'warning' | 'danger' | 'info'> = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger',
  }
  return map[status] ?? 'info'
}

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增数据源')
const formRef = ref<FormInstance>()

const tableData = ref<DataSourceItem[]>([])

const formData = reactive<DataSourceFormData>({
  sourceName: '',
  sourceType: '',
  apiEndpoint: '',
  authConfig: '',
  syncConfig: '',
  status: 1,
})

const formRules: FormRules = {
  sourceName: [{ required: true, message: '请输入数据源名称', trigger: 'blur' }],
  sourceType: [{ required: true, message: '请选择数据源类型', trigger: 'change' }],
  apiEndpoint: [{ required: true, message: '请输入API地址', trigger: 'blur' }],
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getDataSourceList()
    tableData.value = res.data
  } catch {
    // 使用模拟数据
    tableData.value = [
      {
        id: 1,
        tenantId: 1,
        sourceName: '学生信息系统',
        sourceType: 'rest_api',
        apiEndpoint: 'https://api.school.edu.cn/v1/students',
        authConfig: '{"type":"bearer","token":"xxx"}',
        syncConfig: '{"cron":"0 2 * * *"}',
        lastSyncTime: '2026-04-29 02:00:00',
        syncStatus: 2,
        status: 1,
        createdAt: '2026-01-01 00:00:00',
        updatedAt: '2026-04-29 02:00:00',
      },
      {
        id: 2,
        tenantId: 1,
        sourceName: '成绩数据库',
        sourceType: 'database',
        apiEndpoint: 'jdbc:mysql://db.school.edu.cn:3306/scores',
        authConfig: '{"username":"reader","password":"***"}',
        syncConfig: '{"cron":"0 3 * * *"}',
        lastSyncTime: '2026-04-30 03:00:00',
        syncStatus: 2,
        status: 1,
        createdAt: '2026-01-15 00:00:00',
        updatedAt: '2026-04-30 03:00:00',
      },
      {
        id: 3,
        tenantId: 1,
        sourceName: '考勤数据文件',
        sourceType: 'file',
        apiEndpoint: '/data/uploads/attendance/',
        authConfig: '',
        syncConfig: '{"cron":"0 6 * * 1-5"}',
        lastSyncTime: null,
        syncStatus: 0,
        status: 1,
        createdAt: '2026-02-01 00:00:00',
        updatedAt: '2026-02-01 00:00:00',
      },
      {
        id: 4,
        tenantId: 1,
        sourceName: '实时课堂数据',
        sourceType: 'websocket',
        apiEndpoint: 'wss://live.school.edu.cn/classroom',
        authConfig: '{"type":"token"}',
        syncConfig: '',
        lastSyncTime: '2026-04-30 08:30:00',
        syncStatus: 3,
        status: 0,
        createdAt: '2026-03-01 00:00:00',
        updatedAt: '2026-04-30 08:30:00',
      },
    ]
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  dialogTitle.value = '新增数据源'
  Object.assign(formData, {
    id: undefined,
    sourceName: '',
    sourceType: '',
    apiEndpoint: '',
    authConfig: '',
    syncConfig: '',
    status: 1,
  })
  dialogVisible.value = true
}

function handleEdit(row: DataSourceItem) {
  dialogTitle.value = '编辑数据源'
  Object.assign(formData, {
    id: row.id,
    sourceName: row.sourceName,
    sourceType: row.sourceType,
    apiEndpoint: row.apiEndpoint,
    authConfig: row.authConfig,
    syncConfig: row.syncConfig,
    status: row.status,
  })
  dialogVisible.value = true
}

async function handleDelete(row: DataSourceItem) {
  await ElMessageBox.confirm(`确认删除数据源 "${row.sourceName}" 吗？`, '提示', { type: 'warning' })
  try {
    await deleteDataSource(row.id)
    ElMessage.success('删除成功')
  } catch {
    ElMessage.success('删除成功（模拟）')
  }
  fetchData()
}

async function handleSync(row: DataSourceItem) {
  try {
    await ElMessageBox.confirm(`确认手动同步数据源 "${row.sourceName}" 吗？`, '手动同步', { type: 'info' })
  } catch {
    return
  }
  try {
    await triggerDataSourceSync(row.id)
    ElMessage.success('同步任务已触发')
    fetchData()
  } catch {
    // 模拟同步
    ElMessage.success('同步任务已触发（模拟）')
    fetchData()
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (formData.id) {
        await updateDataSource(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createDataSource(formData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      fetchData()
    } catch {
      ElMessage.success(formData.id ? '更新成功（模拟）' : '创建成功（模拟）')
      dialogVisible.value = false
      fetchData()
    } finally {
      submitLoading.value = false
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.datasource-management {
  min-height: 400px;
}
</style>
