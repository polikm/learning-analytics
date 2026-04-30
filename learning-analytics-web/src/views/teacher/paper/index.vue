<template>
  <div class="page-container">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input v-model="searchParams.keyword" placeholder="搜索试卷名称" clearable prefix-icon="Search" style="width: 200px" @keyup.enter="handleSearch" />
        <el-select v-model="searchParams.subject" placeholder="学科" clearable style="width: 120px">
          <el-option label="语文" value="chinese" />
          <el-option label="数学" value="math" />
          <el-option label="英语" value="english" />
          <el-option label="物理" value="physics" />
          <el-option label="化学" value="chemistry" />
          <el-option label="生物" value="biology" />
        </el-select>
        <el-select v-model="searchParams.status" placeholder="状态" clearable style="width: 120px">
          <el-option label="草稿" value="draft" />
          <el-option label="已发布" value="published" />
        </el-select>
        <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
        <el-button icon="Refresh" @click="handleReset">重置</el-button>
        <div class="flex-1" />
        <el-button type="success" icon="MagicStick" @click="handleAutoGenerate">智能组卷</el-button>
        <el-button type="primary" icon="Plus" @click="handleCreate">新增试卷</el-button>
      </div>

      <!-- 试卷列表 -->
      <el-table :data="tableData" stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="试卷名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="subject" label="学科" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ subjectText(row.subject) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="questionCount" label="题目数" width="90" align="center" />
        <el-table-column prop="totalScore" label="总分" width="80" align="center">
          <template #default="{ row }">
            <span style="font-weight: 600; color: #409EFF">{{ row.totalScore }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="110" align="center">
          <template #default="{ row }">{{ row.duration }}分钟</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'published' ? 'success' : 'info'" size="small">{{ row.status === 'published' ? '已发布' : '草稿' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button text type="success" size="small" @click="handlePreview(row)">预览</el-button>
            <el-popconfirm title="确定删除该试卷吗?" @confirm="handleDelete(row)">
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

    <!-- 手动组卷弹窗 -->
    <el-dialog v-model="formVisible" :title="isEdit ? '编辑试卷' : '新增试卷'" width="800px" destroy-on-close :close-on-click-modal="false">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="16">
            <el-form-item label="试卷名称" prop="title">
              <el-input v-model="formData.title" placeholder="请输入试卷名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="学科" prop="subject">
              <el-select v-model="formData.subject" placeholder="请选择学科" style="width: 100%">
                <el-option label="语文" value="chinese" />
                <el-option label="数学" value="math" />
                <el-option label="英语" value="english" />
                <el-option label="物理" value="physics" />
                <el-option label="化学" value="chemistry" />
                <el-option label="生物" value="biology" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="考试时长" prop="duration">
          <el-input-number v-model="formData.duration" :min="10" :max="300" :step="5" />
          <span class="ml-8" style="color: #909399">分钟</span>
        </el-form-item>

        <!-- 题目选择区域 -->
        <el-form-item label="试卷题目">
          <div class="question-selector">
            <div class="selector-toolbar">
              <el-input v-model="questionKeyword" placeholder="搜索题目" clearable prefix-icon="Search" style="width: 240px" size="small" />
              <el-select v-model="questionType" placeholder="题型" clearable style="width: 120px" size="small">
                <el-option label="单选题" value="single_choice" />
                <el-option label="多选题" value="multi_choice" />
                <el-option label="判断题" value="true_false" />
                <el-option label="填空题" value="fill_blank" />
                <el-option label="简答题" value="short_answer" />
              </el-select>
              <el-button type="primary" size="small" @click="searchQuestions">搜索题目</el-button>
            </div>
            <el-table :data="availableQuestions" size="small" max-height="260" @selection-change="handleQuestionSelect">
              <el-table-column type="selection" width="40" />
              <el-table-column prop="content" label="题目内容" min-width="200" show-overflow-tooltip>
                <template #default="{ row }">{{ stripHtml(row.content) }}</template>
              </el-table-column>
              <el-table-column prop="type" label="题型" width="80" align="center">
                <template #default="{ row }">
                  <el-tag size="small">{{ questionTypeText(row.type) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="difficulty" label="难度" width="70" align="center">
                <template #default="{ row }">
                  <span v-for="i in row.difficulty" :key="i" style="color: #E6A23C">*</span>
                </template>
              </el-table-column>
            </el-table>
            <el-button type="primary" text size="small" @click="addSelectedQuestions" :disabled="!selectedQuestions.length">
              添加选中题目 ({{ selectedQuestions.length }})
            </el-button>
          </div>
        </el-form-item>

        <!-- 已选题目列表 -->
        <el-form-item label="已选题目">
          <el-table :data="formData.questions" size="small" border max-height="240">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="content" label="题目内容" min-width="200" show-overflow-tooltip>
              <template #default="{ row }">{{ stripHtml(row.content) }}</template>
            </el-table-column>
            <el-table-column prop="type" label="题型" width="80" align="center">
              <template #default="{ row }">
                <el-tag size="small">{{ questionTypeText(row.type) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="分值" width="100" align="center">
              <template #default="{ row }">
                <el-input-number v-model="row.score" :min="1" :max="50" size="small" style="width: 80px" />
              </template>
            </el-table-column>
            <el-table-column label="排序" width="100" align="center">
              <template #default="{ row }">
                <el-input-number v-model="row.orderNum" :min="1" size="small" style="width: 80px" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="70" align="center">
              <template #default="{ $index }">
                <el-button text type="danger" size="small" @click="removeQuestion($index)">移除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="total-score-info">
            <span>已选 {{ formData.questions.length }} 题，总分：<strong style="color: #409EFF">{{ calcTotalScore }}</strong> 分</span>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存试卷</el-button>
      </template>
    </el-dialog>

    <!-- 智能组卷弹窗 -->
    <el-dialog v-model="autoVisible" title="智能组卷" width="600px" destroy-on-close :close-on-click-modal="false">
      <el-form ref="autoFormRef" :model="autoFormData" :rules="autoFormRules" label-width="100px">
        <el-form-item label="学科" prop="subject">
          <el-select v-model="autoFormData.subject" placeholder="请选择学科" style="width: 100%">
            <el-option label="语文" value="chinese" />
            <el-option label="数学" value="math" />
            <el-option label="英语" value="english" />
            <el-option label="物理" value="physics" />
            <el-option label="化学" value="chemistry" />
            <el-option label="生物" value="biology" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度分布">
          <el-row :gutter="8">
            <el-col :span="8">
              <el-input-number v-model="autoFormData.easyCount" :min="0" :max="50" size="small" style="width: 100%" />
              <div class="difficulty-label">简单</div>
            </el-col>
            <el-col :span="8">
              <el-input-number v-model="autoFormData.mediumCount" :min="0" :max="50" size="small" style="width: 100%" />
              <div class="difficulty-label">中等</div>
            </el-col>
            <el-col :span="8">
              <el-input-number v-model="autoFormData.hardCount" :min="0" :max="50" size="small" style="width: 100%" />
              <div class="difficulty-label">困难</div>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="题型比例">
          <el-row :gutter="8">
            <el-col :span="8">
              <el-input-number v-model="autoFormData.singleChoiceCount" :min="0" :max="30" size="small" style="width: 100%" />
              <div class="difficulty-label">单选题</div>
            </el-col>
            <el-col :span="8">
              <el-input-number v-model="autoFormData.multiChoiceCount" :min="0" :max="20" size="small" style="width: 100%" />
              <div class="difficulty-label">多选题</div>
            </el-col>
            <el-col :span="8">
              <el-input-number v-model="autoFormData.fillBlankCount" :min="0" :max="20" size="small" style="width: 100%" />
              <div class="difficulty-label">填空题</div>
            </el-col>
          </el-row>
          <el-row :gutter="8" class="mt-8">
            <el-col :span="8">
              <el-input-number v-model="autoFormData.shortAnswerCount" :min="0" :max="10" size="small" style="width: 100%" />
              <div class="difficulty-label">简答题</div>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="总分" prop="totalScore">
          <el-input-number v-model="autoFormData.totalScore" :min="10" :max="200" :step="10" />
        </el-form-item>
        <el-form-item label="考试时长">
          <el-input-number v-model="autoFormData.duration" :min="10" :max="300" :step="5" />
          <span class="ml-8" style="color: #909399">分钟</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="autoVisible = false">取消</el-button>
        <el-button type="primary" :loading="autoLoading" @click="handleAutoSubmit">一键生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getPaperList, createPaper, updatePaper, deletePaper, autoGeneratePaper } from '@/api/paper'
import { getQuestionList } from '@/api/question'
import type { PaperItem, PaperFormData } from '@/api/paper'
import type { QuestionItem } from '@/api/question'
import dayjs from 'dayjs'

const formRef = ref<FormInstance>()
const autoFormRef = ref<FormInstance>()
const loading = ref(false)
const submitLoading = ref(false)
const autoLoading = ref(false)
const tableData = ref<PaperItem[]>([])
const total = ref(0)
const formVisible = ref(false)
const autoVisible = ref(false)
const isEdit = ref(false)

const searchParams = reactive({
  page: 1,
  pageSize: 10,
  keyword: '',
  subject: '',
  status: '',
})

const formData = reactive<PaperFormData & { questions: any[] }>({
  title: '',
  subject: '',
  gradeId: 1,
  duration: 90,
  questions: [],
})

const autoFormData = reactive({
  subject: '',
  easyCount: 5,
  mediumCount: 10,
  hardCount: 5,
  singleChoiceCount: 10,
  multiChoiceCount: 5,
  fillBlankCount: 5,
  shortAnswerCount: 3,
  totalScore: 100,
  duration: 90,
})

const formRules = {
  title: [{ required: true, message: '请输入试卷名称', trigger: 'blur' }],
  subject: [{ required: true, message: '请选择学科', trigger: 'change' }],
  duration: [{ required: true, message: '请设置考试时长', trigger: 'change' }],
}

const autoFormRules = {
  subject: [{ required: true, message: '请选择学科', trigger: 'change' }],
  totalScore: [{ required: true, message: '请设置总分', trigger: 'change' }],
}

const questionKeyword = ref('')
const questionType = ref('')
const availableQuestions = ref<QuestionItem[]>([])
const selectedQuestions = ref<QuestionItem[]>([])

const calcTotalScore = computed(() => {
  return formData.questions.reduce((sum, q) => sum + (q.score || 0), 0)
})

function formatDate(date: string) {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

function subjectText(subject: string) {
  const map: Record<string, string> = {
    chinese: '语文', math: '数学', english: '英语', physics: '物理', chemistry: '化学', biology: '生物',
  }
  return map[subject] || subject
}

function questionTypeText(type: string) {
  const map: Record<string, string> = {
    single_choice: '单选', multi_choice: '多选', true_false: '判断', fill_blank: '填空', short_answer: '简答',
  }
  return map[type] || type
}

function stripHtml(html: string) {
  return html?.replace(/<[^>]+>/g, '') || ''
}

function handleSearch() {
  searchParams.page = 1
  loadData()
}

function handleReset() {
  searchParams.keyword = ''
  searchParams.subject = ''
  searchParams.status = ''
  searchParams.page = 1
  loadData()
}

function handleCreate() {
  isEdit.value = false
  formData.title = ''
  formData.subject = ''
  formData.duration = 90
  formData.questions = []
  availableQuestions.value = []
  selectedQuestions.value = []
  formVisible.value = true
}

function handleEdit(row: PaperItem) {
  isEdit.value = true
  Object.assign(formData, {
    id: row.id,
    title: row.title,
    subject: row.subject,
    gradeId: row.gradeId,
    duration: row.duration,
    questions: (row.questions || []).map(q => ({
      questionId: q.questionId,
      content: q.content,
      type: q.type,
      score: q.score,
      orderNum: q.orderNum,
    })),
  })
  formVisible.value = true
}

function handlePreview(row: PaperItem) {
  ElMessage.info('预览功能开发中')
}

async function searchQuestions() {
  try {
    const res = await getQuestionList({
      page: 1,
      pageSize: 50,
      keyword: questionKeyword.value,
      type: questionType.value,
      subject: formData.subject,
      reviewStatus: 'approved',
    })
    availableQuestions.value = res.data?.list || []
  } catch (e) {
    console.error(e)
  }
}

function handleQuestionSelect(rows: QuestionItem[]) {
  selectedQuestions.value = rows
}

function addSelectedQuestions() {
  const existIds = new Set(formData.questions.map(q => q.questionId))
  selectedQuestions.value.forEach(q => {
    if (!existIds.has(q.id)) {
      formData.questions.push({
        questionId: q.id,
        content: q.content,
        type: q.type,
        score: 5,
        orderNum: formData.questions.length + 1,
      })
    }
  })
  ElMessage.success('已添加题目')
}

function removeQuestion(idx: number) {
  formData.questions.splice(idx, 1)
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (!formData.questions.length) {
    ElMessage.warning('请至少添加一道题目')
    return
  }
  submitLoading.value = true
  try {
    const data: PaperFormData = {
      title: formData.title,
      subject: formData.subject,
      gradeId: formData.gradeId,
      duration: formData.duration,
      questions: formData.questions.map(q => ({
        questionId: q.questionId,
        score: q.score,
        orderNum: q.orderNum,
      })),
    }
    if (isEdit.value && formData.id) {
      await updatePaper(formData.id, data)
      ElMessage.success('更新成功')
    } else {
      await createPaper(data)
      ElMessage.success('创建成功')
    }
    formVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row: PaperItem) {
  try {
    await deletePaper(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    console.error(e)
  }
}

function handleAutoGenerate() {
  autoFormData.subject = ''
  autoFormData.easyCount = 5
  autoFormData.mediumCount = 10
  autoFormData.hardCount = 5
  autoFormData.singleChoiceCount = 10
  autoFormData.multiChoiceCount = 5
  autoFormData.fillBlankCount = 5
  autoFormData.shortAnswerCount = 3
  autoFormData.totalScore = 100
  autoFormData.duration = 90
  autoVisible.value = true
}

async function handleAutoSubmit() {
  const valid = await autoFormRef.value?.validate().catch(() => false)
  if (!valid) return
  autoLoading.value = true
  try {
    await autoGeneratePaper({
      subject: autoFormData.subject,
      gradeId: 1,
      difficulty: 2,
      questionTypeRatios: [
        { type: 'single_choice', count: autoFormData.singleChoiceCount },
        { type: 'multi_choice', count: autoFormData.multiChoiceCount },
        { type: 'fill_blank', count: autoFormData.fillBlankCount },
        { type: 'short_answer', count: autoFormData.shortAnswerCount },
      ],
      totalScore: autoFormData.totalScore,
      duration: autoFormData.duration,
    })
    ElMessage.success('智能组卷成功')
    autoVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  } finally {
    autoLoading.value = false
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getPaperList(searchParams)
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

.question-selector {
  width: 100%;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
  padding: 12px;
}

.selector-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.total-score-info {
  margin-top: 8px;
  font-size: 13px;
  color: #606266;
}

.difficulty-label {
  font-size: 12px;
  color: #909399;
  text-align: center;
  margin-top: 4px;
}
</style>
