<template>
  <div class="report-template-management">
    <!-- 类型筛选标签页 + 表格区域 -->
    <el-card shadow="never">
      <template #header>
        <div class="flex-between">
          <span>报告模板列表</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">新增模板</el-button>
        </div>
      </template>

      <!-- 类型筛选标签页 -->
      <el-tabs v-model="activeType" @tab-change="handleTypeChange">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane label="学生报告" name="student" />
        <el-tab-pane label="班级报告" name="class" />
        <el-tab-pane label="年级报告" name="grade" />
        <el-tab-pane label="学校报告" name="school" />
        <el-tab-pane label="学科报告" name="subject" />
      </el-tabs>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="templateName" label="模板名称" min-width="160" />
        <el-table-column prop="templateType" label="报告类型" width="120">
          <template #default="{ row }">
            <el-tag :type="typeTagMap[row.templateType]?.type || 'info'">
              {{ typeTagMap[row.templateType]?.label || row.templateType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isDefault" label="默认模板" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault === 1" type="warning">默认</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link icon="View" @click="handleView(row)">查看</el-button>
            <el-button type="primary" link icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 查看详情弹窗 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="模板详情"
      width="600px"
      destroy-on-close
    >
      <template v-if="viewData">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="模板名称">{{ viewData.templateName }}</el-descriptions-item>
          <el-descriptions-item label="报告类型">
            <el-tag :type="typeTagMap[viewData.templateType]?.type || 'info'">
              {{ typeTagMap[viewData.templateType]?.label || viewData.templateType }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="默认模板">
            {{ viewData.isDefault === 1 ? '是' : '否' }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="viewData.status === 1 ? 'success' : 'danger'">
              {{ viewData.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">{{ viewData.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="模板配置" :span="2">
            <pre class="config-preview">{{ formatConfig(viewData.templateConfig) }}</pre>
          </el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="formData.templateName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="报告类型" prop="templateType">
          <el-select v-model="formData.templateType" placeholder="请选择报告类型" style="width: 100%">
            <el-option label="学生报告" value="student" />
            <el-option label="班级报告" value="class" />
            <el-option label="年级报告" value="grade" />
            <el-option label="学校报告" value="school" />
            <el-option label="学科报告" value="subject" />
          </el-select>
        </el-form-item>
        <el-form-item label="模板配置" prop="templateConfig">
          <el-input
            v-model="formData.templateConfig"
            type="textarea"
            :rows="8"
            placeholder="请输入模板配置（JSON格式）"
          />
        </el-form-item>
        <el-form-item label="设为默认" prop="isDefault">
          <el-switch
            v-model="formData.isDefault"
            :active-value="1"
            :inactive-value="0"
          />
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
  getReportTemplateList,
  getReportTemplateDetail,
  createReportTemplate,
  updateReportTemplate,
  deleteReportTemplate,
  type ReportTemplateFormData,
  type ReportTemplateItem,
} from '@/api/report-template'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('新增模板')
const formRef = ref<FormInstance>()
const activeType = ref('')
const viewData = ref<ReportTemplateItem | null>(null)

const typeTagMap: Record<string, { label: string; type: string }> = {
  student: { label: '学生报告', type: '' },
  class: { label: '班级报告', type: 'success' },
  grade: { label: '年级报告', type: 'warning' },
  school: { label: '学校报告', type: 'danger' },
  subject: { label: '学科报告', type: 'info' },
}

const formData = reactive<ReportTemplateFormData>({
  templateName: '',
  templateType: '',
  templateConfig: '',
  isDefault: 0,
  status: 1,
})

const formRules: FormRules = {
  templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  templateType: [{ required: true, message: '请选择报告类型', trigger: 'change' }],
}

const tableData = ref<any[]>([])

function formatConfig(config: string) {
  if (!config) return '暂无配置'
  try {
    return JSON.stringify(JSON.parse(config), null, 2)
  } catch {
    return config
  }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getReportTemplateList(activeType.value || undefined)
    tableData.value = res.data || []
  } catch {
    // 模拟数据
    const allData = [
      { id: 1, templateName: '学生综合学情报告', templateType: 'student', templateConfig: '{"charts":["radar","bar"],"metrics":["score","rank","attendance"]}', isDefault: 1, status: 1, createdAt: '2025-01-01 00:00:00' },
      { id: 2, templateName: '班级成绩分析报告', templateType: 'class', templateConfig: '{"charts":["bar","line"],"metrics":["avgScore","passRate","excellentRate"]}', isDefault: 1, status: 1, createdAt: '2025-01-01 00:00:00' },
      { id: 3, templateName: '年级对比分析报告', templateType: 'grade', templateConfig: '{"charts":["bar","pie"],"metrics":["avgScore","distribution"]}', isDefault: 0, status: 1, createdAt: '2025-01-02 00:00:00' },
      { id: 4, templateName: '学校整体报告', templateType: 'school', templateConfig: '{}', isDefault: 1, status: 1, createdAt: '2025-01-03 00:00:00' },
      { id: 5, templateName: '学科分析报告', templateType: 'subject', templateConfig: '{"charts":["radar"],"metrics":["mastery","weakness"]}', isDefault: 0, status: 0, createdAt: '2025-01-04 00:00:00' },
    ]
    tableData.value = activeType.value
      ? allData.filter((item) => item.templateType === activeType.value)
      : allData
  } finally {
    loading.value = false
  }
}

function handleTypeChange() {
  fetchData()
}

function handleAdd() {
  dialogTitle.value = '新增模板'
  Object.assign(formData, {
    id: undefined,
    templateName: '',
    templateType: activeType.value || '',
    templateConfig: '',
    isDefault: 0,
    status: 1,
  })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogTitle.value = '编辑模板'
  Object.assign(formData, row)
  dialogVisible.value = true
}

async function handleView(row: any) {
  try {
    const res = await getReportTemplateDetail(row.id)
    viewData.value = res.data
  } catch {
    viewData.value = row
  }
  viewDialogVisible.value = true
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确认删除模板 "${row.templateName}" 吗？`, '提示', { type: 'warning' })
  try {
    await deleteReportTemplate(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    ElMessage.success('删除成功（模拟）')
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
        await updateReportTemplate(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createReportTemplate(formData)
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
.report-template-management {
  .config-preview {
    max-height: 200px;
    overflow: auto;
    background: var(--el-fill-color-lighter);
    padding: 12px;
    border-radius: 4px;
    font-size: 12px;
    margin: 0;
    white-space: pre-wrap;
    word-break: break-all;
  }
}
</style>
