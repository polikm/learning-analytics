<template>
  <div class="page-container">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input v-model="searchParams.keyword" placeholder="搜索学生或证书名称" clearable prefix-icon="Search" style="width: 220px" @keyup.enter="handleSearch" />
        <el-select v-model="searchParams.type" placeholder="证书类型" clearable style="width: 140px">
          <el-option label="学科竞赛" value="competition" />
          <el-option label="技能证书" value="skill" />
          <el-option label="荣誉证书" value="honor" />
          <el-option label="等级考试" value="grade_exam" />
          <el-option label="其他" value="other" />
        </el-select>
        <el-select v-model="searchParams.reviewStatus" placeholder="审核状态" clearable style="width: 120px">
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已驳回" value="rejected" />
        </el-select>
        <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
        <el-button icon="Refresh" @click="handleReset">重置</el-button>
        <div class="flex-1" />
        <el-button type="primary" icon="Plus" @click="handleCreate">录入证书</el-button>
      </div>

      <!-- 证书列表 -->
      <el-table :data="tableData" stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="studentName" label="学生" width="100" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="className" label="班级" width="100" />
        <el-table-column prop="name" label="证书名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="certTypeTag(row.type)">{{ certTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="等级" width="80" align="center" />
        <el-table-column prop="issuingAuthority" label="颁发机构" width="140" show-overflow-tooltip />
        <el-table-column prop="issueDate" label="颁发日期" width="110">
          <template #default="{ row }">{{ row.issueDate }}</template>
        </el-table-column>
        <el-table-column prop="reviewStatus" label="审核状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="reviewStatusTag(row.reviewStatus)" size="small">{{ reviewStatusText(row.reviewStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button text type="warning" size="small" @click="handleReview(row)" v-if="row.reviewStatus === 'pending'">审核</el-button>
            <el-popconfirm title="确定删除该证书吗?" @confirm="handleDelete(row)">
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

    <!-- 录入证书弹窗 -->
    <el-dialog v-model="formVisible" :title="isEdit ? '编辑证书' : '录入证书'" width="600px" destroy-on-close :close-on-click-modal="false">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="选择学生" prop="studentId">
          <el-select v-model="formData.studentId" placeholder="请搜索选择学生" style="width: 100%" filterable remote :remote-method="searchStudents">
            <el-option v-for="s in studentOptions" :key="s.id" :label="`${s.name} (${s.studentNo}) - ${s.className}`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="证书名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入证书名称" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="证书类型" prop="type">
              <el-select v-model="formData.type" placeholder="请选择" style="width: 100%">
                <el-option label="学科竞赛" value="competition" />
                <el-option label="技能证书" value="skill" />
                <el-option label="荣誉证书" value="honor" />
                <el-option label="等级考试" value="grade_exam" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="等级">
              <el-input v-model="formData.level" placeholder="如：一等奖、A级" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="颁发机构" prop="issuingAuthority">
          <el-input v-model="formData.issuingAuthority" placeholder="请输入颁发机构" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="颁发日期" prop="issueDate">
              <el-date-picker v-model="formData.issueDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="证书编号">
              <el-input v-model="formData.certificateNo" placeholder="请输入证书编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="扫描件">
          <el-upload
            action="/api/v1/upload"
            :limit="1"
            :on-success="handleUploadSuccess"
            accept=".jpg,.jpeg,.png,.pdf"
          >
            <el-button type="primary" text icon="Upload">上传扫描件</el-button>
            <template #tip>
              <div style="color: #909399; font-size: 12px">支持 JPG/PNG/PDF 格式</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 审核弹窗 -->
    <el-dialog v-model="reviewVisible" title="审核证书" width="480px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="学生">
          <span>{{ reviewData.studentName }} ({{ reviewData.studentNo }})</span>
        </el-form-item>
        <el-form-item label="证书名称">
          <span>{{ reviewData.name }}</span>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import {
  getCertificateList, createCertificate, updateCertificate, deleteCertificate, reviewCertificate,
} from '@/api/certificate'
import { getStudentList } from '@/api/student'
import type { CertificateItem, CertificateFormData } from '@/api/certificate'
import type { StudentItem } from '@/api/student'

const formRef = ref<FormInstance>()
const loading = ref(false)
const submitLoading = ref(false)
const reviewLoading = ref(false)
const tableData = ref<CertificateItem[]>([])
const total = ref(0)
const formVisible = ref(false)
const reviewVisible = ref(false)
const isEdit = ref(false)
const studentOptions = ref<StudentItem[]>([])

const searchParams = reactive({
  page: 1,
  pageSize: 10,
  keyword: '',
  type: '',
  reviewStatus: '',
})

const formData = reactive<CertificateFormData>({
  studentId: 0,
  name: '',
  type: '',
  level: '',
  issuingAuthority: '',
  issueDate: '',
  certificateNo: '',
  scanFile: '',
})

const reviewData = reactive<Partial<CertificateItem>>({})
const reviewForm = reactive({
  status: 'approved' as 'approved' | 'rejected',
  remark: '',
})

const formRules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  name: [{ required: true, message: '请输入证书名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择证书类型', trigger: 'change' }],
  issuingAuthority: [{ required: true, message: '请输入颁发机构', trigger: 'blur' }],
  issueDate: [{ required: true, message: '请选择颁发日期', trigger: 'change' }],
}

function certTypeText(type: string) {
  const map: Record<string, string> = {
    competition: '学科竞赛', skill: '技能证书', honor: '荣誉证书', grade_exam: '等级考试', other: '其他',
  }
  return map[type] || type
}

function certTypeTag(type: string) {
  const map: Record<string, string> = {
    competition: 'danger', skill: '', honor: 'success', grade_exam: 'warning', other: 'info',
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

function handleSearch() {
  searchParams.page = 1
  loadData()
}

function handleReset() {
  searchParams.keyword = ''
  searchParams.type = ''
  searchParams.reviewStatus = ''
  searchParams.page = 1
  loadData()
}

async function searchStudents(query: string) {
  if (!query) return
  try {
    const res = await getStudentList({ page: 1, pageSize: 20, keyword: query })
    studentOptions.value = res.data?.list || []
  } catch (e) {
    console.error(e)
  }
}

function handleUploadSuccess(response: any) {
  formData.scanFile = response.data?.url || ''
  ElMessage.success('上传成功')
}

function handleCreate() {
  isEdit.value = false
  formData.studentId = 0
  formData.name = ''
  formData.type = ''
  formData.level = ''
  formData.issuingAuthority = ''
  formData.issueDate = ''
  formData.certificateNo = ''
  formData.scanFile = ''
  studentOptions.value = []
  formVisible.value = true
}

function handleEdit(row: CertificateItem) {
  isEdit.value = true
  Object.assign(formData, {
    id: row.id,
    studentId: row.studentId,
    name: row.name,
    type: row.type,
    level: row.level,
    issuingAuthority: row.issuingAuthority,
    issueDate: row.issueDate,
    certificateNo: row.certificateNo,
    scanFile: row.scanFile,
  })
  studentOptions.value = [{ id: row.studentId, name: row.studentName, studentNo: row.studentNo, className: row.className } as any]
  formVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value && formData.id) {
      await updateCertificate(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createCertificate(formData)
      ElMessage.success('录入成功')
    }
    formVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row: CertificateItem) {
  try {
    await deleteCertificate(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    console.error(e)
  }
}

function handleReview(row: CertificateItem) {
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
    await reviewCertificate(reviewData.id!, reviewForm.status, reviewForm.remark)
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
    const res = await getCertificateList(searchParams)
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
</style>
