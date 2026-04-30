<template>
  <div class="subject-management">
    <!-- 表格区域 -->
    <el-card shadow="never">
      <template #header>
        <div class="flex-between">
          <span>学科列表</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">新增学科</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="subjectName" label="学科名称" min-width="140" />
        <el-table-column prop="subjectCode" label="学科编码" width="140" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="520px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="学科名称" prop="subjectName">
          <el-input v-model="formData.subjectName" placeholder="请输入学科名称" />
        </el-form-item>
        <el-form-item label="学科编码" prop="subjectCode">
          <el-input v-model="formData.subjectCode" placeholder="请输入学科编码" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="9999" />
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
import { getSubjectList, createSubject, updateSubject, deleteSubject, type SubjectFormData } from '@/api/subject'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增学科')
const formRef = ref<FormInstance>()

const formData = reactive<SubjectFormData>({
  subjectName: '',
  subjectCode: '',
  sortOrder: 0,
  status: 1,
})

const formRules: FormRules = {
  subjectName: [{ required: true, message: '请输入学科名称', trigger: 'blur' }],
  subjectCode: [{ required: true, message: '请输入学科编码', trigger: 'blur' }],
}

const tableData = ref<any[]>([])

async function fetchData() {
  loading.value = true
  try {
    const res = await getSubjectList()
    tableData.value = res.data || []
  } catch {
    // 使用模拟数据
    tableData.value = [
      { id: 1, subjectName: '语文', subjectCode: 'CHINESE', sortOrder: 1, status: 1, createdAt: '2025-01-01 00:00:00' },
      { id: 2, subjectName: '数学', subjectCode: 'MATH', sortOrder: 2, status: 1, createdAt: '2025-01-01 00:00:00' },
      { id: 3, subjectName: '英语', subjectCode: 'ENGLISH', sortOrder: 3, status: 1, createdAt: '2025-01-01 00:00:00' },
      { id: 4, subjectName: '物理', subjectCode: 'PHYSICS', sortOrder: 4, status: 0, createdAt: '2025-01-01 00:00:00' },
    ]
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  dialogTitle.value = '新增学科'
  Object.assign(formData, { id: undefined, subjectName: '', subjectCode: '', sortOrder: 0, status: 1 })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogTitle.value = '编辑学科'
  Object.assign(formData, row)
  dialogVisible.value = true
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确认删除学科 "${row.subjectName}" 吗？`, '提示', { type: 'warning' })
  try {
    await deleteSubject(row.id)
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
        await updateSubject(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createSubject(formData)
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
.subject-management {
  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
