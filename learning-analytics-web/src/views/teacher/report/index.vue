<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="flex-between">
          <span class="card-title">学情报告</span>
          <div>
            <el-button type="success" icon="FolderAdd" @click="handleBatchGenerate">批量生成</el-button>
            <el-button type="primary" icon="Plus" @click="handleGenerate">生成报告</el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input v-model="searchParams.keyword" placeholder="搜索报告名称" clearable prefix-icon="Search" style="width: 200px" @keyup.enter="handleSearch" />
        <el-select v-model="searchParams.type" placeholder="报告类型" clearable style="width: 120px">
          <el-option label="学生报告" value="student" />
          <el-option label="班级报告" value="class" />
          <el-option label="测评报告" value="exam" />
          <el-option label="年级报告" value="grade" />
        </el-select>
        <el-select v-model="searchParams.status" placeholder="生成状态" clearable style="width: 120px">
          <el-option label="已完成" value="completed" />
          <el-option label="生成中" value="generating" />
          <el-option label="失败" value="failed" />
        </el-select>
        <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
        <el-button icon="Refresh" @click="handleReset">重置</el-button>
      </div>

      <!-- 报告列表 -->
      <el-table :data="tableData" stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="报告名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="reportTypeTag(row.type)">{{ reportTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetName" label="目标" width="120" show-overflow-tooltip />
        <el-table-column prop="templateName" label="模板" width="140" show-overflow-tooltip />
        <el-table-column prop="status" label="生成状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="reportStatusTag(row.status)" size="small">
              <el-icon v-if="row.status === 'generating'" class="is-loading"><Loading /></el-icon>
              {{ reportStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="生成时间" width="170">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column prop="generatedByName" label="生成人" width="100" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="handlePreview(row)" v-if="row.status === 'completed'">预览</el-button>
            <el-button text type="success" size="small" @click="handleDownload(row)" v-if="row.status === 'completed'">
              <el-icon><Download /></el-icon>下载
            </el-button>
            <el-button text type="warning" size="small" @click="handleRetry(row)" v-if="row.status === 'failed'">重试</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="searchParams.page"
          v-model:page-size="searchParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 生成报告弹窗 -->
    <el-dialog v-model="generateVisible" title="生成报告" width="560px" destroy-on-close :close-on-click-modal="false">
      <el-form ref="genFormRef" :model="genFormData" :rules="genFormRules" label-width="100px">
        <el-form-item label="报告类型" prop="type">
          <el-select v-model="genFormData.type" placeholder="请选择报告类型" style="width: 100%" @change="handleTypeChange">
            <el-option label="学生报告" value="student" />
            <el-option label="班级报告" value="class" />
            <el-option label="测评报告" value="exam" />
            <el-option label="年级报告" value="grade" />
          </el-select>
        </el-form-item>
        <el-form-item label="报告模板" prop="templateId">
          <el-select v-model="genFormData.templateId" placeholder="请选择模板" style="width: 100%">
            <el-option v-for="t in templateOptions" :key="t.id" :label="t.name" :value="t.id">
              <div>
                <span>{{ t.name }}</span>
                <span style="color: #909399; font-size: 12px; margin-left: 8px">{{ t.description }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="目标" prop="targetId">
          <el-select
            v-model="genFormData.targetId"
            placeholder="请选择目标"
            style="width: 100%"
            filterable
            multiple
          >
            <el-option v-for="t in targetOptions" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="报告名称">
          <el-input v-model="genFormData.name" placeholder="留空将自动生成名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateVisible = false">取消</el-button>
        <el-button type="primary" :loading="genLoading" @click="handleGenSubmit">生成</el-button>
      </template>
    </el-dialog>

    <!-- 批量生成弹窗 -->
    <el-dialog v-model="batchVisible" title="批量生成报告" width="560px" destroy-on-close :close-on-click-modal="false">
      <el-form ref="batchFormRef" :model="batchFormData" :rules="batchFormRules" label-width="100px">
        <el-form-item label="报告类型" prop="type">
          <el-select v-model="batchFormData.type" placeholder="请选择报告类型" style="width: 100%" @change="handleBatchTypeChange">
            <el-option label="学生报告" value="student" />
            <el-option label="班级报告" value="class" />
          </el-select>
        </el-form-item>
        <el-form-item label="报告模板" prop="templateId">
          <el-select v-model="batchFormData.templateId" placeholder="请选择模板" style="width: 100%">
            <el-option v-for="t in templateOptions" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标范围">
          <el-select v-model="batchFormData.targetIds" placeholder="请选择目标" style="width: 100%" filterable multiple>
            <el-option v-for="t in targetOptions" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-alert type="info" :closable="false" show-icon>
          <template #title>将为选中的所有目标生成报告，请耐心等待</template>
        </el-alert>
      </el-form>
      <template #footer>
        <el-button @click="batchVisible = false">取消</el-button>
        <el-button type="primary" :loading="batchLoading" @click="handleBatchSubmit">开始批量生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import {
  getReportList, generateReport, downloadReport, batchGenerateReport, getReportTemplateList,
} from '@/api/report'
import { getStudentList } from '@/api/student'
import { getClassList } from '@/api/class'
import type { ReportItem } from '@/api/report'
import type { ReportTemplate } from '@/api/report'
import dayjs from 'dayjs'

const genFormRef = ref<FormInstance>()
const batchFormRef = ref<FormInstance>()
const loading = ref(false)
const genLoading = ref(false)
const batchLoading = ref(false)
const tableData = ref<ReportItem[]>([])
const total = ref(0)
const generateVisible = ref(false)
const batchVisible = ref(false)
const templateOptions = ref<ReportTemplate[]>([])
const targetOptions = ref<{ id: number; name: string }[]>([])

const searchParams = reactive({
  page: 1,
  pageSize: 10,
  keyword: '',
  type: '',
  status: '',
})

const genFormData = reactive({
  type: 'student',
  templateId: 0,
  targetId: [] as number[],
  name: '',
})

const batchFormData = reactive({
  type: 'student',
  templateId: 0,
  targetIds: [] as number[],
})

const genFormRules = {
  type: [{ required: true, message: '请选择报告类型', trigger: 'change' }],
  templateId: [{ required: true, message: '请选择模板', trigger: 'change' }],
  targetId: [{ required: true, message: '请选择目标', trigger: 'change' }],
}

const batchFormRules = {
  type: [{ required: true, message: '请选择报告类型', trigger: 'change' }],
  templateId: [{ required: true, message: '请选择模板', trigger: 'change' }],
}

function formatDate(date: string) {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

function reportTypeText(type: string) {
  const map: Record<string, string> = { student: '学生报告', class: '班级报告', exam: '测评报告', grade: '年级报告' }
  return map[type] || type
}

function reportTypeTag(type: string) {
  const map: Record<string, string> = { student: '', class: 'success', exam: 'warning', grade: 'danger' }
  return map[type] || 'info'
}

function reportStatusText(status: string) {
  const map: Record<string, string> = { pending: '待生成', generating: '生成中', completed: '已完成', failed: '失败' }
  return map[status] || status
}

function reportStatusTag(status: string) {
  const map: Record<string, string> = { pending: 'info', generating: 'warning', completed: 'success', failed: 'danger' }
  return map[status] || 'info'
}

function handleSearch() {
  searchParams.page = 1
  loadData()
}

function handleReset() {
  searchParams.keyword = ''
  searchParams.type = ''
  searchParams.status = ''
  searchParams.page = 1
  loadData()
}

async function loadTemplates(type?: string) {
  try {
    const res = await getReportTemplateList(type)
    templateOptions.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

async function loadTargets(type: string) {
  targetOptions.value = []
  try {
    if (type === 'student') {
      const res = await getStudentList({ page: 1, pageSize: 100 })
      targetOptions.value = (res.data?.list || []).map(s => ({ id: s.id, name: `${s.name} (${s.studentNo})` }))
    } else if (type === 'class') {
      const res = await getClassList(1)
      targetOptions.value = (res.data || []).map(c => ({ id: c.id, name: c.name }))
    }
  } catch (e) {
    console.error(e)
  }
}

async function handleTypeChange(type: string) {
  genFormData.templateId = 0
  genFormData.targetId = []
  await Promise.all([loadTemplates(type), loadTargets(type)])
}

async function handleBatchTypeChange(type: string) {
  batchFormData.templateId = 0
  batchFormData.targetIds = []
  await Promise.all([loadTemplates(type), loadTargets(type)])
}

async function handleGenerate() {
  genFormData.type = 'student'
  genFormData.templateId = 0
  genFormData.targetId = []
  genFormData.name = ''
  await loadTemplates()
  await loadTargets('student')
  generateVisible.value = true
}

async function handleGenSubmit() {
  const valid = await genFormRef.value?.validate().catch(() => false)
  if (!valid) return
  genLoading.value = true
  try {
    await generateReport({
      templateId: genFormData.templateId,
      type: genFormData.type,
      targetId: genFormData.targetId[0],
      name: genFormData.name || undefined,
    })
    ElMessage.success('报告生成任务已提交')
    generateVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  } finally {
    genLoading.value = false
  }
}

async function handleBatchGenerate() {
  batchFormData.type = 'student'
  batchFormData.templateId = 0
  batchFormData.targetIds = []
  await loadTemplates()
  await loadTargets('student')
  batchVisible.value = true
}

async function handleBatchSubmit() {
  const valid = await batchFormRef.value?.validate().catch(() => false)
  if (!valid) return
  if (!batchFormData.targetIds.length) {
    ElMessage.warning('请选择目标')
    return
  }
  batchLoading.value = true
  try {
    await batchGenerateReport({
      templateId: batchFormData.templateId,
      type: batchFormData.type,
      targetId: batchFormData.targetIds[0],
      targetIds: batchFormData.targetIds,
    })
    ElMessage.success(`已提交 ${batchFormData.targetIds.length} 个报告生成任务`)
    batchVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  } finally {
    batchLoading.value = false
  }
}

function handlePreview(row: ReportItem) {
  ElMessage.info('预览功能开发中')
}

async function handleDownload(row: ReportItem) {
  try {
    const res = await downloadReport(row.id)
    const url = URL.createObjectURL(res.data as Blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `${row.name}.pdf`
    a.click()
    URL.revokeObjectURL(url)
  } catch (e) {
    console.error(e)
  }
}

function handleRetry(row: ReportItem) {
  ElMessage.info('重试功能开发中')
}

async function loadData() {
  loading.value = true
  try {
    const res = await getReportList(searchParams)
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-title {
  font-size: 16px;
  font-weight: 600;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
