<template>
  <div class="page-container">
    <el-card shadow="never">
      <!-- Tab切换 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="进行中" name="in_progress" />
        <el-tab-pane label="已结束" name="ended" />
        <el-tab-pane label="草稿" name="draft" />
      </el-tabs>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input v-model="searchParams.keyword" placeholder="搜索测评名称" clearable prefix-icon="Search" style="width: 200px" @keyup.enter="handleSearch" />
        <el-select v-model="searchParams.subject" placeholder="学科" clearable style="width: 120px">
          <el-option label="语文" value="chinese" />
          <el-option label="数学" value="math" />
          <el-option label="英语" value="english" />
          <el-option label="物理" value="physics" />
          <el-option label="化学" value="chemistry" />
          <el-option label="生物" value="biology" />
        </el-select>
        <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
        <el-button icon="Refresh" @click="handleReset">重置</el-button>
        <div class="flex-1" />
        <el-button type="primary" icon="Plus" @click="handleCreate">发布测评</el-button>
      </div>

      <!-- 测评列表 -->
      <el-table :data="tableData" stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="测评名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="subject" label="学科" width="90" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ subjectText(row.subject) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="classNames" label="班级" width="140" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tag v-for="cn in (row.classNames || []).slice(0, 2)" :key="cn" size="small" class="mr-8">{{ cn }}</el-tag>
            <el-tag v-if="(row.classNames || []).length > 2" size="small" type="info">+{{ row.classNames.length - 2 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时间" width="180">
          <template #default="{ row }">
            <div class="time-cell">
              <div>{{ formatDate(row.startTime) }}</div>
              <div style="color: #909399; font-size: 12px">至 {{ formatDate(row.endTime) }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="examStatusType(row.status)" size="small">{{ examStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submittedCount" label="提交进度" width="120" align="center">
          <template #default="{ row }">
            <el-progress
              :percentage="row.studentCount ? Math.round(row.submittedCount / row.studentCount * 100) : 0"
              :stroke-width="6"
              :color="row.studentCount === row.submittedCount ? '#67C23A' : '#409EFF'"
            />
            <div style="font-size: 12px; color: #909399; margin-top: 2px">{{ row.submittedCount }}/{{ row.studentCount }}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="handleViewAnalysis(row)" v-if="row.status === 'ended' || row.status === 'grading'">成绩分析</el-button>
            <el-button text type="success" size="small" @click="handlePublish(row)" v-if="row.status === 'draft'">发布</el-button>
            <el-button text type="warning" size="small" @click="handleMonitor(row)" v-if="row.status === 'in_progress'">监考</el-button>
            <el-button text type="danger" size="small" @click="handleGrade(row)" v-if="row.status === 'grading' || row.status === 'ended'">阅卷</el-button>
            <el-popconfirm title="确定删除该测评吗?" @confirm="handleDelete(row)" v-if="row.status === 'draft'">
              <template #reference>
                <el-button text type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
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

    <!-- 创建测评弹窗 -->
    <el-dialog v-model="formVisible" title="发布测评" width="640px" destroy-on-close :close-on-click-modal="false">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="测评名称" prop="title">
          <el-input v-model="formData.title" placeholder="请输入测评名称" />
        </el-form-item>
        <el-form-item label="选择试卷" prop="paperId">
          <el-select v-model="formData.paperId" placeholder="请选择试卷" style="width: 100%" filterable>
            <el-option v-for="p in paperOptions" :key="p.id" :label="`${p.title} (${p.subject}) - ${p.totalScore}分`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择班级" prop="classIds">
          <el-select v-model="formData.classIds" placeholder="请选择班级" style="width: 100%" multiple>
            <el-option v-for="c in classOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试模式" prop="examMode">
          <el-radio-group v-model="formData.examMode">
            <el-radio-button value="normal">正式考试</el-radio-button>
            <el-radio-button value="practice">练习模式</el-radio-button>
            <el-radio-button value="test">模拟测试</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker v-model="formData.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker v-model="formData.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="考试时长">
          <el-input-number v-model="formData.duration" :min="10" :max="300" :step="5" />
          <span class="ml-8" style="color: #909399">分钟</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getExamList, createExam, deleteExam, publishExam, autoGradeExam } from '@/api/exam'
import { getPaperList } from '@/api/paper'
import { getClassList } from '@/api/class'
import type { ExamItem, ExamFormData } from '@/api/exam'
import type { PaperItem } from '@/api/paper'
import type { ClassItem } from '@/api/class'
import dayjs from 'dayjs'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<ExamItem[]>([])
const total = ref(0)
const activeTab = ref('all')
const formVisible = ref(false)

const searchParams = reactive({
  page: 1,
  pageSize: 10,
  keyword: '',
  subject: '',
  status: '',
})

const formData = reactive<ExamFormData>({
  title: '',
  paperId: 0,
  classIds: [],
  startTime: '',
  endTime: '',
  duration: 90,
  examMode: 'normal',
})

const formRules = {
  title: [{ required: true, message: '请输入测评名称', trigger: 'blur' }],
  paperId: [{ required: true, message: '请选择试卷', trigger: 'change' }],
  classIds: [{ required: true, message: '请选择班级', trigger: 'change' }],
  examMode: [{ required: true, message: '请选择考试模式', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
}

const paperOptions = ref<PaperItem[]>([])
const classOptions = ref<ClassItem[]>([])

function formatDate(date: string) {
  return dayjs(date).format('MM-DD HH:mm')
}

function subjectText(subject: string) {
  const map: Record<string, string> = {
    chinese: '语文', math: '数学', english: '英语', physics: '物理', chemistry: '化学', biology: '生物',
  }
  return map[subject] || subject
}

function examStatusText(status: string) {
  const map: Record<string, string> = {
    draft: '草稿', published: '已发布', in_progress: '进行中', ended: '已结束', grading: '阅卷中',
  }
  return map[status] || status
}

function examStatusType(status: string) {
  const map: Record<string, string> = {
    draft: 'info', published: 'primary', in_progress: 'success', ended: '', grading: 'warning',
  }
  return map[status] || 'info'
}

function handleTabChange(tab: string) {
  searchParams.status = tab === 'all' ? '' : tab
  searchParams.page = 1
  loadData()
}

function handleSearch() {
  searchParams.page = 1
  loadData()
}

function handleReset() {
  searchParams.keyword = ''
  searchParams.subject = ''
  searchParams.page = 1
  loadData()
}

async function handleCreate() {
  formData.title = ''
  formData.paperId = 0
  formData.classIds = []
  formData.startTime = ''
  formData.endTime = ''
  formData.duration = 90
  formData.examMode = 'normal'
  try {
    const [paperRes, classRes] = await Promise.all([
      getPaperList({ page: 1, pageSize: 100, status: 'published' }),
      getClassList(1),
    ])
    paperOptions.value = paperRes.data?.list || []
    classOptions.value = classRes.data || []
  } catch (e) {
    console.error(e)
  }
  formVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    await createExam({
      ...formData,
      startTime: dayjs(formData.startTime).format('YYYY-MM-DD HH:mm:ss'),
      endTime: dayjs(formData.endTime).format('YYYY-MM-DD HH:mm:ss'),
    })
    ElMessage.success('测评创建成功')
    formVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  } finally {
    submitLoading.value = false
  }
}

async function handlePublish(row: ExamItem) {
  try {
    await publishExam(row.id)
    ElMessage.success('发布成功')
    loadData()
  } catch (e) {
    console.error(e)
  }
}

function handleMonitor(row: ExamItem) {
  ElMessage.info('监考功能开发中')
}

async function handleGrade(row: ExamItem) {
  try {
    await autoGradeExam(row.id)
    ElMessage.success('自动阅卷完成')
    loadData()
  } catch (e) {
    console.error(e)
  }
}

function handleViewAnalysis(row: ExamItem) {
  router.push(`/teacher/exams/${row.id}/analysis`)
}

async function handleDelete(row: ExamItem) {
  try {
    await deleteExam(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    console.error(e)
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getExamList(searchParams)
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

.time-cell {
  line-height: 1.4;
}
</style>
