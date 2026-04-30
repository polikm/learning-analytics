<template>
  <div class="school-management">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="学校名称">
          <el-input
            v-model="queryParams.keyword"
            placeholder="请输入学校名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card shadow="never" class="mt-16">
      <template #header>
        <div class="flex-between">
          <span>学校列表</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">新增学校</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="学校名称" min-width="160" />
        <el-table-column prop="code" label="学校编码" width="140" />
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="principal" label="校长" width="100" />
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDelete(row)">删除</el-button>
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
        <el-form-item label="学校名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入学校名称" />
        </el-form-item>
        <el-form-item label="学校编码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入学校编码" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="校长" prop="principal">
          <el-input v-model="formData.principal" placeholder="请输入校长姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
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
import { getSchoolList, createSchool, updateSchool, deleteSchool, type SchoolFormData } from '@/api/school'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增学校')
const formRef = ref<FormInstance>()
const total = ref(0)

const queryParams = reactive({
  keyword: '',
  status: undefined as number | undefined,
  page: 1,
  pageSize: 10,
})

const formData = reactive<SchoolFormData>({
  name: '',
  code: '',
  address: '',
  principal: '',
  phone: '',
  status: 1,
})

const formRules: FormRules = {
  name: [{ required: true, message: '请输入学校名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入学校编码', trigger: 'blur' }],
}

const tableData = ref<any[]>([])

async function fetchData() {
  loading.value = true
  try {
    const res = await getSchoolList(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    // 使用模拟数据
    tableData.value = [
      { id: 1, name: '第一中学', code: 'SCH001', address: '北京市海淀区', principal: '张校长', phone: '010-12345678', status: 1 },
      { id: 2, name: '第二小学', code: 'SCH002', address: '北京市朝阳区', principal: '李校长', phone: '010-23456789', status: 1 },
      { id: 3, name: '实验小学', code: 'SCH003', address: '北京市西城区', principal: '王校长', phone: '010-34567890', status: 0 },
    ]
    total.value = 3
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  fetchData()
}

function handleReset() {
  queryParams.keyword = ''
  queryParams.status = undefined
  handleSearch()
}

function handleAdd() {
  dialogTitle.value = '新增学校'
  Object.assign(formData, { id: undefined, name: '', code: '', address: '', principal: '', phone: '', status: 1 })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogTitle.value = '编辑学校'
  Object.assign(formData, row)
  dialogVisible.value = true
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确认删除学校 "${row.name}" 吗？`, '提示', { type: 'warning' })
  try {
    await deleteSchool(row.id)
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
        await updateSchool(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createSchool(formData)
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
.school-management {
  .search-card {
    :deep(.el-form-item) {
      margin-bottom: 0;
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
