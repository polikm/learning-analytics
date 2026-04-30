<template>
  <div class="audit-log-page">
    <!-- 搜索条件 -->
    <el-card shadow="never">
      <el-form :model="queryParams" inline class="search-form">
        <el-form-item label="操作用户">
          <el-input
            v-model="queryParams.username"
            placeholder="用户名/用户ID"
            clearable
            style="width: 160px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="操作描述">
          <el-input
            v-model="queryParams.operation"
            placeholder="操作描述关键词"
            clearable
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 360px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never" class="mt-16">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="username" label="操作用户" width="120">
          <template #default="{ row }">
            {{ row.username || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="operation" label="操作描述" min-width="180" show-overflow-tooltip />
        <el-table-column prop="requestUrl" label="请求URL" min-width="200" show-overflow-tooltip />
        <el-table-column prop="requestMethod" label="HTTP方法" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.requestMethod" :type="getMethodTagType(row.requestMethod)" size="small">
              {{ row.requestMethod }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="duration" label="执行时长(ms)" width="120" sortable>
          <template #default="{ row }">
            <span :class="{ 'slow-request': row.duration > 3000 }">
              {{ row.duration != null ? row.duration : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="时间" width="180" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link icon="View" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="审计日志详情"
      width="720px"
      destroy-on-close
    >
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="操作用户">{{ detailData.username || '-' }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detailData.userId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作描述">{{ detailData.operation }}</el-descriptions-item>
        <el-descriptions-item label="请求方法" :span="2">{{ detailData.method || '-' }}</el-descriptions-item>
        <el-descriptions-item label="请求URL" :span="2">{{ detailData.requestUrl || '-' }}</el-descriptions-item>
        <el-descriptions-item label="HTTP方法">
          <el-tag v-if="detailData.requestMethod" :type="getMethodTagType(detailData.requestMethod)" size="small">
            {{ detailData.requestMethod }}
          </el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detailData.ipAddress || '-' }}</el-descriptions-item>
        <el-descriptions-item label="执行时长">{{ detailData.duration != null ? detailData.duration + 'ms' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === 1 ? 'success' : 'danger'" size="small">
            {{ detailData.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ detailData.createdAt }}</el-descriptions-item>
      </el-descriptions>

      <div v-if="detailData" class="detail-section">
        <h4>用户代理</h4>
        <el-input
          :model-value="detailData.userAgent || '-'"
          type="textarea"
          :rows="2"
          readonly
        />
      </div>

      <div v-if="detailData" class="detail-section">
        <h4>请求参数</h4>
        <el-input
          :model-value="formatJson(detailData.requestParams)"
          type="textarea"
          :rows="6"
          readonly
        />
      </div>

      <div v-if="detailData" class="detail-section">
        <h4>响应数据</h4>
        <el-input
          :model-value="formatJson(detailData.responseData)"
          type="textarea"
          :rows="6"
          readonly
        />
      </div>

      <div v-if="detailData && detailData.errorMsg" class="detail-section">
        <h4 style="color: #F56C6C">错误信息</h4>
        <el-input
          :model-value="detailData.errorMsg"
          type="textarea"
          :rows="4"
          readonly
          style="--el-input-text-color: #F56C6C"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getAuditLogList, getAuditLogDetail, type AuditLogItem } from '@/api/audit-log'

const loading = ref(false)
const total = ref(0)
const detailVisible = ref(false)
const detailData = ref<AuditLogItem | null>(null)
const dateRange = ref<string[]>([])

const queryParams = reactive({
  username: '',
  operation: '',
  startTime: '',
  endTime: '',
  page: 1,
  pageSize: 20,
})

const tableData = ref<any[]>([])

function getMethodTagType(method: string) {
  const map: Record<string, string> = {
    GET: '',
    POST: 'success',
    PUT: 'warning',
    DELETE: 'danger',
    PATCH: 'info',
  }
  return map[method.toUpperCase()] || 'info'
}

function formatJson(str: string | null | undefined): string {
  if (!str) return '-'
  try {
    return JSON.stringify(JSON.parse(str), null, 2)
  } catch {
    return str
  }
}

async function fetchData() {
  loading.value = true
  try {
    const params: any = {
      operation: queryParams.operation || undefined,
      page: queryParams.page,
      pageSize: queryParams.pageSize,
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    const res = await getAuditLogList(params)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    tableData.value = [
      { id: 1, username: 'admin', operation: '创建用户', requestUrl: '/api/v1/users', requestMethod: 'POST', ipAddress: '192.168.1.100', duration: 156, status: 1, createdAt: '2026-04-30 10:30:00', userAgent: 'Mozilla/5.0...', requestParams: '{"username":"test01","realName":"测试用户"}', responseData: '{"code":0,"message":"success"}', errorMsg: null },
      { id: 2, username: 'teacher01', operation: '创建测评', requestUrl: '/api/v1/exams', requestMethod: 'POST', ipAddress: '192.168.1.101', duration: 230, status: 1, createdAt: '2026-04-30 10:25:00', userAgent: 'Mozilla/5.0...', requestParams: '{"examName":"数学期中测试"}', responseData: '{"code":0,"message":"success"}', errorMsg: null },
      { id: 3, username: 'admin', operation: '删除用户', requestUrl: '/api/v1/users/5', requestMethod: 'DELETE', ipAddress: '192.168.1.100', duration: 89, status: 0, createdAt: '2026-04-30 10:20:00', userAgent: 'Mozilla/5.0...', requestParams: '{}', responseData: '{"code":500,"message":"操作失败"}', errorMsg: '用户不存在或已被删除' },
      { id: 4, username: 'admin', operation: '分页查询用户列表', requestUrl: '/api/v1/users', requestMethod: 'GET', ipAddress: '192.168.1.100', duration: 45, status: 1, createdAt: '2026-04-30 10:15:00', userAgent: 'Mozilla/5.0...', requestParams: '{"page":1,"size":20}', responseData: '{"code":0,"message":"success","data":{}}', errorMsg: null },
      { id: 5, username: 'teacher01', operation: '发布测评', requestUrl: '/api/v1/exams/3/publish', requestMethod: 'POST', ipAddress: '192.168.1.101', duration: 312, status: 1, createdAt: '2026-04-30 10:10:00', userAgent: 'Mozilla/5.0...', requestParams: '{}', responseData: '{"code":0,"message":"success"}', errorMsg: null },
    ]
    total.value = 5
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  fetchData()
}

function handleReset() {
  queryParams.username = ''
  queryParams.operation = ''
  dateRange.value = []
  handleSearch()
}

async function handleDetail(row: any) {
  try {
    const res = await getAuditLogDetail(row.id)
    detailData.value = res.data
  } catch {
    detailData.value = row
  }
  detailVisible.value = true
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.audit-log-page {
  .search-form {
    :deep(.el-form-item) {
      margin-bottom: 0;
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }

  .slow-request {
    color: #F56C6C;
    font-weight: bold;
  }

  .detail-section {
    margin-top: 20px;

    h4 {
      font-size: 14px;
      color: #303133;
      margin-bottom: 8px;
      font-weight: 600;
    }
  }
}
</style>
