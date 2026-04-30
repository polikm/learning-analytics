<template>
  <div class="page-container question-page">
    <el-row :gutter="16" style="height: 100%">
      <!-- 左侧知识点树 -->
      <el-col :span="5">
        <el-card shadow="never" class="tree-card">
          <template #header>
            <span class="card-title">知识点分类</span>
          </template>
          <el-input
            v-model="treeFilter"
            placeholder="搜索知识点"
            clearable
            prefix-icon="Search"
            class="mb-8"
          />
          <el-tree
            ref="treeRef"
            :data="knowledgeTree"
            :props="{ label: 'name', children: 'children' }"
            :filter-node-method="filterNode"
            node-key="id"
            highlight-current
            default-expand-all
            @node-click="handleNodeClick"
          >
            <template #default="{ node, data }">
              <span class="tree-node">
                <span>{{ node.label }}</span>
                <span class="tree-node-count">{{ data.count || 0 }}</span>
              </span>
            </template>
          </el-tree>
        </el-card>
      </el-col>

      <!-- 右侧主内容 -->
      <el-col :span="19">
        <el-card shadow="never">
          <!-- 搜索栏 -->
          <div class="search-bar">
            <el-select v-model="searchParams.subject" placeholder="学科" clearable style="width: 120px">
              <el-option label="语文" value="chinese" />
              <el-option label="数学" value="math" />
              <el-option label="英语" value="english" />
              <el-option label="物理" value="physics" />
              <el-option label="化学" value="chemistry" />
              <el-option label="生物" value="biology" />
              <el-option label="历史" value="history" />
              <el-option label="地理" value="geography" />
              <el-option label="政治" value="politics" />
            </el-select>
            <el-select v-model="searchParams.type" placeholder="题型" clearable style="width: 120px">
              <el-option label="单选题" value="single_choice" />
              <el-option label="多选题" value="multi_choice" />
              <el-option label="判断题" value="true_false" />
              <el-option label="填空题" value="fill_blank" />
              <el-option label="简答题" value="short_answer" />
            </el-select>
            <el-select v-model="searchParams.difficulty" placeholder="难度" clearable style="width: 120px">
              <el-option label="简单" :value="1" />
              <el-option label="中等" :value="2" />
              <el-option label="困难" :value="3" />
            </el-select>
            <el-input
              v-model="searchParams.keyword"
              placeholder="搜索题目内容"
              clearable
              prefix-icon="Search"
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
            <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
            <el-button icon="Refresh" @click="handleReset">重置</el-button>
            <div class="flex-1" />
            <el-button type="primary" icon="Plus" @click="handleCreate">新增题目</el-button>
          </div>

          <!-- 题目列表 -->
          <el-table :data="tableData" stripe v-loading="loading" style="width: 100%">
            <el-table-column prop="type" label="题型" width="100" align="center">
              <template #default="{ row }">
                <el-tag size="small" :type="questionTypeTag(row.type)">{{ questionTypeText(row.type) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="内容预览" min-width="240" show-overflow-tooltip>
              <template #default="{ row }">
                <span class="content-preview" v-html="stripHtml(row.content)"></span>
              </template>
            </el-table-column>
            <el-table-column prop="difficulty" label="难度" width="100" align="center">
              <template #default="{ row }">
                <el-rate v-model="row.difficulty" disabled :max="3" :colors="['#67C23A', '#E6A23C', '#F56C6C']" />
              </template>
            </el-table-column>
            <el-table-column prop="knowledgePoints" label="知识点" width="140" show-overflow-tooltip>
              <template #default="{ row }">
                <el-tag v-for="kp in (row.knowledgePoints || []).slice(0, 2)" :key="kp" size="small" class="mr-8">{{ kp }}</el-tag>
                <el-tag v-if="(row.knowledgePoints || []).length > 2" size="small" type="info">+{{ row.knowledgePoints.length - 2 }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="reviewStatus" label="审核状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="reviewStatusTag(row.reviewStatus)" size="small">{{ reviewStatusText(row.reviewStatus) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" align="center" fixed="right">
              <template #default="{ row }">
                <el-button text type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
                <el-button text type="warning" size="small" @click="handleReview(row)" v-if="row.reviewStatus === 'pending'">审核</el-button>
                <el-popconfirm title="确定删除该题目吗?" @confirm="handleDelete(row)">
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
      </el-col>
    </el-row>

    <!-- 创建/编辑题目弹窗 -->
    <el-dialog
      v-model="formVisible"
      :title="isEdit ? '编辑题目' : '新增题目'"
      width="720px"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="题型" prop="type">
              <el-select v-model="formData.type" placeholder="请选择题型" style="width: 100%" @change="handleTypeChange">
                <el-option label="单选题" value="single_choice" />
                <el-option label="多选题" value="multi_choice" />
                <el-option label="判断题" value="true_false" />
                <el-option label="填空题" value="fill_blank" />
                <el-option label="简答题" value="short_answer" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学科" prop="subject">
              <el-select v-model="formData.subject" placeholder="请选择学科" style="width: 100%">
                <el-option label="语文" value="chinese" />
                <el-option label="数学" value="math" />
                <el-option label="英语" value="english" />
                <el-option label="物理" value="physics" />
                <el-option label="化学" value="chemistry" />
                <el-option label="生物" value="biology" />
                <el-option label="历史" value="history" />
                <el-option label="地理" value="geography" />
                <el-option label="政治" value="politics" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="难度" prop="difficulty">
          <el-rate v-model="formData.difficulty" :max="3" :texts="['简单', '中等', '困难']" show-text />
        </el-form-item>
        <el-form-item label="题目内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="4"
            placeholder="请输入题目内容，支持HTML格式"
          />
        </el-form-item>

        <!-- 选项编辑（选择题） -->
        <el-form-item v-if="isChoiceType" label="选项">
          <div class="options-editor">
            <div v-for="(opt, idx) in formData.options" :key="idx" class="option-item">
              <el-input
                v-model="opt.label"
                style="width: 60px"
                placeholder="标号"
              />
              <el-input
                v-model="opt.content"
                style="flex: 1"
                :placeholder="'选项' + (idx + 1) + '内容'"
              />
              <el-checkbox v-model="opt.isCorrect" style="margin-left: 8px">正确</el-checkbox>
              <el-button text type="danger" icon="Delete" @click="removeOption(idx)" />
            </div>
            <el-button type="primary" text icon="Plus" @click="addOption">添加选项</el-button>
          </div>
        </el-form-item>

        <el-form-item label="答案" prop="answer">
          <el-input
            v-model="formData.answer"
            type="textarea"
            :rows="2"
            placeholder="请输入答案"
          />
        </el-form-item>
        <el-form-item label="解析" prop="analysis">
          <el-input
            v-model="formData.analysis"
            type="textarea"
            :rows="3"
            placeholder="请输入题目解析"
          />
        </el-form-item>
        <el-form-item label="知识点">
          <el-select v-model="formData.knowledgePoints" multiple filterable allow-create placeholder="请选择或输入知识点" style="width: 100%">
            <el-option v-for="kp in flatKnowledgePoints" :key="kp" :label="kp" :value="kp" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 审核弹窗 -->
    <el-dialog v-model="reviewVisible" title="审核题目" width="480px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="题目内容">
          <div class="review-content" v-html="reviewData.content"></div>
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="reviewForm.status">
            <el-radio value="approved">通过</el-radio>
            <el-radio value="rejected">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="reviewForm.status === 'rejected'" label="驳回原因">
          <el-input v-model="reviewForm.remark" type="textarea" :rows="3" placeholder="请输入驳回原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button type="primary" :loading="reviewLoading" @click="handleReviewSubmit">提交审核</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import {
  getQuestionList, createQuestion, updateQuestion, deleteQuestion, reviewQuestion,
} from '@/api/question'
import type { QuestionItem, QuestionFormData } from '@/api/question'

const treeRef = ref()
const formRef = ref<FormInstance>()
const loading = ref(false)
const submitLoading = ref(false)
const reviewLoading = ref(false)
const tableData = ref<QuestionItem[]>([])
const total = ref(0)
const treeFilter = ref('')
const formVisible = ref(false)
const reviewVisible = ref(false)
const isEdit = ref(false)

const searchParams = reactive({
  page: 1,
  pageSize: 10,
  keyword: '',
  type: '',
  subject: '',
  difficulty: undefined as number | undefined,
  knowledgePointId: undefined as number | undefined,
})

const formData = reactive<QuestionFormData>({
  type: 'single_choice',
  subject: '',
  content: '',
  difficulty: 2,
  options: [
    { label: 'A', content: '', isCorrect: false },
    { label: 'B', content: '', isCorrect: false },
    { label: 'C', content: '', isCorrect: false },
    { label: 'D', content: '', isCorrect: false },
  ],
  answer: '',
  analysis: '',
  knowledgePoints: [],
})

const reviewData = reactive<Partial<QuestionItem>>({})
const reviewForm = reactive({
  status: 'approved' as 'approved' | 'rejected',
  remark: '',
})

const formRules = {
  type: [{ required: true, message: '请选择题型', trigger: 'change' }],
  subject: [{ required: true, message: '请选择学科', trigger: 'change' }],
  content: [{ required: true, message: '请输入题目内容', trigger: 'blur' }],
  difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }],
  answer: [{ required: true, message: '请输入答案', trigger: 'blur' }],
}

// 知识点树模拟数据
const knowledgeTree = ref([
  {
    id: 1, name: '数学', count: 120, children: [
      { id: 11, name: '代数', count: 45, children: [
        { id: 111, name: '方程', count: 20 },
        { id: 112, name: '不等式', count: 15 },
        { id: 113, name: '函数', count: 10 },
      ]},
      { id: 12, name: '几何', count: 40, children: [
        { id: 121, name: '平面几何', count: 25 },
        { id: 122, name: '立体几何', count: 15 },
      ]},
      { id: 13, name: '概率统计', count: 35 },
    ],
  },
  {
    id: 2, name: '语文', count: 80, children: [
      { id: 21, name: '阅读理解', count: 40 },
      { id: 22, name: '古诗文', count: 25 },
      { id: 23, name: '写作', count: 15 },
    ],
  },
  {
    id: 3, name: '英语', count: 90, children: [
      { id: 31, name: '语法', count: 35 },
      { id: 32, name: '阅读', count: 30 },
      { id: 33, name: '写作', count: 25 },
    ],
  },
])

const flatKnowledgePoints = computed(() => {
  const result: string[] = []
  function walk(nodes: any[]) {
    nodes.forEach(n => {
      result.push(n.name)
      if (n.children) walk(n.children)
    })
  }
  walk(knowledgeTree.value)
  return result
})

const isChoiceType = computed(() => {
  return ['single_choice', 'multi_choice'].includes(formData.type)
})

watch(treeFilter, (val) => {
  treeRef.value?.filter(val)
})

function filterNode(value: string, data: any) {
  if (!value) return true
  return data.name.includes(value)
}

function stripHtml(html: string) {
  return html?.replace(/<[^>]+>/g, '') || ''
}

function questionTypeText(type: string) {
  const map: Record<string, string> = {
    single_choice: '单选题', multi_choice: '多选题', true_false: '判断题', fill_blank: '填空题', short_answer: '简答题',
  }
  return map[type] || type
}

function questionTypeTag(type: string) {
  const map: Record<string, string> = {
    single_choice: '', multi_choice: 'success', true_false: 'warning', fill_blank: 'info', short_answer: 'danger',
  }
  return map[type] || 'info'
}

function reviewStatusText(status: string) {
  const map: Record<string, string> = { pending: '待审核', approved: '已通过', rejected: '已驳回' }
  return map[status] || status
}

function reviewStatusTag(status: string) {
  const map: Record<string, string> = { pending: 'warning', approved: 'success', rejected: 'danger' }
  return map[status] || 'info'
}

function handleNodeClick(data: any) {
  searchParams.knowledgePointId = data.id
  searchParams.page = 1
  loadData()
}

function handleSearch() {
  searchParams.page = 1
  loadData()
}

function handleReset() {
  searchParams.keyword = ''
  searchParams.type = ''
  searchParams.subject = ''
  searchParams.difficulty = undefined
  searchParams.knowledgePointId = undefined
  searchParams.page = 1
  treeRef.value?.setCurrentKey(null)
  loadData()
}

function handleTypeChange(type: string) {
  if (['single_choice', 'multi_choice'].includes(type)) {
    if (!formData.options || formData.options.length === 0) {
      formData.options = [
        { label: 'A', content: '', isCorrect: false },
        { label: 'B', content: '', isCorrect: false },
        { label: 'C', content: '', isCorrect: false },
        { label: 'D', content: '', isCorrect: false },
      ]
    }
  } else if (type === 'true_false') {
    formData.options = [
      { label: 'T', content: '正确', isCorrect: false },
      { label: 'F', content: '错误', isCorrect: false },
    ]
  } else {
    formData.options = []
  }
}

function addOption() {
  const labels = 'ABCDEFGHIJKLMNOP'
  const idx = formData.options?.length || 0
  formData.options = formData.options || []
  formData.options.push({ label: labels[idx] || String(idx + 1), content: '', isCorrect: false })
}

function removeOption(idx: number) {
  formData.options?.splice(idx, 1)
}

function resetForm() {
  formData.type = 'single_choice'
  formData.subject = ''
  formData.content = ''
  formData.difficulty = 2
  formData.options = [
    { label: 'A', content: '', isCorrect: false },
    { label: 'B', content: '', isCorrect: false },
    { label: 'C', content: '', isCorrect: false },
    { label: 'D', content: '', isCorrect: false },
  ]
  formData.answer = ''
  formData.analysis = ''
  formData.knowledgePoints = []
}

function handleCreate() {
  isEdit.value = false
  resetForm()
  formVisible.value = true
}

function handleEdit(row: QuestionItem) {
  isEdit.value = true
  Object.assign(formData, {
    id: row.id,
    type: row.type,
    subject: row.subject,
    content: row.content,
    difficulty: row.difficulty,
    options: row.options ? JSON.parse(JSON.stringify(row.options)) : [],
    answer: row.answer,
    analysis: row.analysis,
    knowledgePoints: row.knowledgePoints || [],
  })
  formVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value && formData.id) {
      await updateQuestion(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createQuestion(formData)
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

async function handleDelete(row: QuestionItem) {
  try {
    await deleteQuestion(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    console.error(e)
  }
}

function handleReview(row: QuestionItem) {
  Object.assign(reviewData, row)
  reviewForm.status = 'approved'
  reviewForm.remark = ''
  reviewVisible.value = true
}

async function handleReviewSubmit() {
  if (reviewForm.status === 'rejected' && !reviewForm.remark) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  reviewLoading.value = true
  try {
    await reviewQuestion(reviewData.id!, reviewForm.status, reviewForm.remark)
    ElMessage.success('审核完成')
    reviewVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  } finally {
    reviewLoading.value = false
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getQuestionList(searchParams)
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
.question-page {
  height: calc(100vh - 120px);
}

.tree-card {
  height: 100%;
  overflow-y: auto;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
}

.tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex: 1;
  font-size: 13px;
}

.tree-node-count {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.content-preview {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.options-editor {
  width: 100%;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.review-content {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
  line-height: 1.6;
  max-height: 200px;
  overflow-y: auto;
}
</style>
