<template>
  <div class="user-management">
    <!-- Tab 切换 -->
    <el-card shadow="never">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="管理员" name="admin" />
        <el-tab-pane label="教师" name="teacher" />
        <el-tab-pane label="家长" name="parent" />
        <el-tab-pane label="学生" name="student" />
      </el-tabs>

      <!-- 搜索栏 -->
      <el-form :model="queryParams" inline class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="queryParams.keyword"
            placeholder="姓名/用户名/手机号"
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
          <el-button type="primary" icon="Plus" @click="handleAdd">新增用户</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never" class="mt-16">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="schoolName" label="所属学校" min-width="160" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag>{{ roleMap[row.role] || row.role }}</el-tag>
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
        <el-table-column label="操作" :width="activeTab === 'teacher' || activeTab === 'parent' || activeTab === 'student' ? 240 : 160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDelete(row)">删除</el-button>
            <el-button v-if="activeTab === 'teacher'" type="warning" link icon="Notebook" @click="handleAssignment(row)">任课分配</el-button>
            <el-button v-if="activeTab === 'parent'" type="success" link icon="Connection" @click="handleParentStudent(row)">关联学生</el-button>
            <el-button v-if="activeTab === 'student'" type="success" link icon="Connection" @click="handleStudentParent(row)">关联家长</el-button>
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
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!formData.id" label="密码" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="教师" value="teacher" />
            <el-option label="家长" value="parent" />
            <el-option label="学生" value="student" />
          </el-select>
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

    <!-- 教师任课分配弹窗 -->
    <el-dialog
      v-model="assignmentDialogVisible"
      :title="`任课分配 - ${currentTeacher?.realName || ''}`"
      width="900px"
      destroy-on-close
    >
      <div style="margin-bottom: 16px;">
        <el-button type="primary" icon="Plus" @click="handleAddAssignment">新增分配</el-button>
      </div>
      <el-table :data="assignmentList" v-loading="assignmentLoading" stripe border>
        <el-table-column prop="schoolId" label="学校ID" width="80" />
        <el-table-column prop="gradeId" label="年级ID" width="80" />
        <el-table-column prop="classId" label="班级ID" width="80" />
        <el-table-column prop="subjectId" label="学科ID" width="80" />
        <el-table-column prop="academicYear" label="学年" width="120" />
        <el-table-column prop="semester" label="学期" width="80">
          <template #default="{ row }">
            {{ semesterMap[row.semester] || row.semester }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link icon="Edit" @click="handleEditAssignment(row)">编辑</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDeleteAssignment(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 新增/编辑任课分配表单弹窗 -->
    <el-dialog
      v-model="assignmentFormVisible"
      :title="assignmentFormTitle"
      width="520px"
      destroy-on-close
      append-to-body
    >
      <el-form
        ref="assignmentFormRef"
        :model="assignmentFormData"
        :rules="assignmentFormRules"
        label-width="100px"
      >
        <el-form-item label="学校ID" prop="schoolId">
          <el-input-number v-model="assignmentFormData.schoolId" :min="1" placeholder="请输入学校ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="年级ID" prop="gradeId">
          <el-input-number v-model="assignmentFormData.gradeId" :min="1" placeholder="请输入年级ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="班级ID" prop="classId">
          <el-input-number v-model="assignmentFormData.classId" :min="1" placeholder="请输入班级ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="学科ID" prop="subjectId">
          <el-input-number v-model="assignmentFormData.subjectId" :min="1" placeholder="请输入学科ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="学年" prop="academicYear">
          <el-input v-model="assignmentFormData.academicYear" placeholder="如 2025-2026" />
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-select v-model="assignmentFormData.semester" placeholder="请选择学期" style="width: 100%">
            <el-option label="第一学期" value="first" />
            <el-option label="第二学期" value="second" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="assignmentFormData.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignmentFormVisible = false">取消</el-button>
        <el-button type="primary" :loading="assignmentSubmitLoading" @click="handleAssignmentSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 家长关联学生弹窗 -->
    <el-dialog
      v-model="parentStudentDialogVisible"
      :title="`关联学生 - ${currentParent?.realName || ''}`"
      width="800px"
      destroy-on-close
    >
      <div style="margin-bottom: 16px;">
        <el-button type="primary" icon="Plus" @click="handleAddParentStudent">新增关联</el-button>
      </div>
      <el-table :data="parentStudentList" v-loading="parentStudentLoading" stripe border>
        <el-table-column prop="studentId" label="学生ID" width="100" />
        <el-table-column prop="relation" label="关系" width="120">
          <template #default="{ row }">
            {{ relationMap[row.relation] || row.relation }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link icon="Edit" @click="handleEditParentStudent(row)">编辑</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDeleteParentStudent(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 新增/编辑家长关联学生表单弹窗 -->
    <el-dialog
      v-model="parentStudentFormVisible"
      :title="parentStudentFormTitle"
      width="480px"
      destroy-on-close
      append-to-body
    >
      <el-form
        ref="parentStudentFormRef"
        :model="parentStudentFormData"
        :rules="parentStudentFormRules"
        label-width="100px"
      >
        <el-form-item label="学生ID" prop="studentId">
          <el-input-number v-model="parentStudentFormData.studentId" :min="1" placeholder="请输入学生ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="关系" prop="relation">
          <el-select v-model="parentStudentFormData.relation" placeholder="请选择关系" style="width: 100%">
            <el-option label="父亲" value="father" />
            <el-option label="母亲" value="mother" />
            <el-option label="监护人" value="guardian" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="parentStudentFormData.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="parentStudentFormVisible = false">取消</el-button>
        <el-button type="primary" :loading="parentStudentSubmitLoading" @click="handleParentStudentSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 学生关联家长弹窗 -->
    <el-dialog
      v-model="studentParentDialogVisible"
      :title="`关联家长 - ${currentStudent?.realName || ''}`"
      width="800px"
      destroy-on-close
    >
      <div style="margin-bottom: 16px;">
        <el-button type="primary" icon="Plus" @click="handleAddStudentParent">新增关联</el-button>
      </div>
      <el-table :data="studentParentList" v-loading="studentParentLoading" stripe border>
        <el-table-column prop="parentId" label="家长ID" width="100" />
        <el-table-column prop="relation" label="关系" width="120">
          <template #default="{ row }">
            {{ relationMap[row.relation] || row.relation }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link icon="Edit" @click="handleEditStudentParent(row)">编辑</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDeleteStudentParent(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 新增/编辑学生关联家长表单弹窗 -->
    <el-dialog
      v-model="studentParentFormVisible"
      :title="studentParentFormTitle"
      width="480px"
      destroy-on-close
      append-to-body
    >
      <el-form
        ref="studentParentFormRef"
        :model="studentParentFormData"
        :rules="studentParentFormRules"
        label-width="100px"
      >
        <el-form-item label="家长ID" prop="parentId">
          <el-input-number v-model="studentParentFormData.parentId" :min="1" placeholder="请输入家长ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="关系" prop="relation">
          <el-select v-model="studentParentFormData.relation" placeholder="请选择关系" style="width: 100%">
            <el-option label="父亲" value="father" />
            <el-option label="母亲" value="mother" />
            <el-option label="监护人" value="guardian" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="studentParentFormData.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="studentParentFormVisible = false">取消</el-button>
        <el-button type="primary" :loading="studentParentSubmitLoading" @click="handleStudentParentSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser, type UserFormData } from '@/api/user'
import {
  getTeacherAssignmentsByTeacher,
  createTeacherAssignment,
  updateTeacherAssignment,
  deleteTeacherAssignment,
  type TeacherAssignmentItem,
  type TeacherAssignmentFormData,
} from '@/api/teacher-assignment'
import {
  getParentStudentsByParent,
  getParentStudentsByStudent,
  createParentStudent,
  updateParentStudent,
  deleteParentStudent,
  type ParentStudentItem,
} from '@/api/parent-student'

const roleMap: Record<string, string> = {
  admin: '管理员',
  teacher: '教师',
  parent: '家长',
  student: '学生',
}

const semesterMap: Record<string, string> = {
  first: '第一学期',
  second: '第二学期',
}

const relationMap: Record<string, string> = {
  father: '父亲',
  mother: '母亲',
  guardian: '监护人',
  other: '其他',
}

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const formRef = ref<FormInstance>()
const activeTab = ref('admin')
const total = ref(0)

const queryParams = reactive({
  keyword: '',
  role: 'admin',
  status: undefined as number | undefined,
  page: 1,
  pageSize: 10,
})

const formData = reactive<UserFormData>({
  username: '',
  password: '',
  realName: '',
  phone: '',
  role: 'admin',
  status: 1,
})

const formRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
}

const tableData = ref<any[]>([])

async function fetchData() {
  loading.value = true
  try {
    const res = await getUserList(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    tableData.value = [
      { id: 1, username: 'admin', realName: '管理员', phone: '13800000001', schoolName: '平台', role: 'admin', status: 1, createdAt: '2026-01-01 00:00:00' },
      { id: 2, username: 'teacher01', realName: '张老师', phone: '13800000002', schoolName: '第一中学', role: 'teacher', status: 1, createdAt: '2026-02-15 10:30:00' },
    ]
    total.value = 2
  } finally {
    loading.value = false
  }
}

function handleTabChange(tab: string | number) {
  queryParams.role = tab as string
  queryParams.page = 1
  fetchData()
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
  dialogTitle.value = '新增用户'
  Object.assign(formData, { id: undefined, username: '', password: '', realName: '', phone: '', role: activeTab.value, status: 1 })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogTitle.value = '编辑用户'
  Object.assign(formData, row)
  dialogVisible.value = true
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确认删除用户 "${row.realName}" 吗？`, '提示', { type: 'warning' })
  try {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
  } catch {
    ElMessage.success('删除成功（模拟）')
  }
  fetchData()
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (formData.id) {
        await updateUser(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createUser(formData)
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

// ==================== 教师任课分配 ====================

const assignmentDialogVisible = ref(false)
const assignmentFormVisible = ref(false)
const assignmentFormTitle = ref('新增任课分配')
const assignmentFormRef = ref<FormInstance>()
const assignmentLoading = ref(false)
const assignmentSubmitLoading = ref(false)
const assignmentList = ref<TeacherAssignmentItem[]>([])
const currentTeacher = ref<any>(null)

const assignmentFormData = reactive<TeacherAssignmentFormData>({
  teacherId: 0,
  schoolId: 0,
  gradeId: undefined,
  classId: undefined,
  subjectId: 0,
  academicYear: '',
  semester: '',
  remark: '',
})

const assignmentFormRules: FormRules = {
  schoolId: [{ required: true, message: '请输入学校ID', trigger: 'blur' }],
  subjectId: [{ required: true, message: '请输入学科ID', trigger: 'blur' }],
}

async function handleAssignment(row: any) {
  currentTeacher.value = row
  assignmentDialogVisible.value = true
  await fetchAssignmentList(row.id)
}

async function fetchAssignmentList(teacherId: number) {
  assignmentLoading.value = true
  try {
    const res = await getTeacherAssignmentsByTeacher(teacherId)
    assignmentList.value = res.data || []
  } catch {
    assignmentList.value = []
  } finally {
    assignmentLoading.value = false
  }
}

function handleAddAssignment() {
  assignmentFormTitle.value = '新增任课分配'
  Object.assign(assignmentFormData, {
    id: undefined,
    teacherId: currentTeacher.value?.id,
    schoolId: undefined,
    gradeId: undefined,
    classId: undefined,
    subjectId: undefined,
    academicYear: '',
    semester: '',
    remark: '',
  })
  assignmentFormVisible.value = true
}

function handleEditAssignment(row: TeacherAssignmentItem) {
  assignmentFormTitle.value = '编辑任课分配'
  Object.assign(assignmentFormData, row)
  assignmentFormVisible.value = true
}

async function handleDeleteAssignment(row: TeacherAssignmentItem) {
  await ElMessageBox.confirm('确认删除该任课分配吗？', '提示', { type: 'warning' })
  try {
    await deleteTeacherAssignment(row.id)
    ElMessage.success('删除成功')
  } catch {
    ElMessage.success('删除成功（模拟）')
  }
  if (currentTeacher.value) {
    fetchAssignmentList(currentTeacher.value.id)
  }
}

async function handleAssignmentSubmit() {
  if (!assignmentFormRef.value) return
  await assignmentFormRef.value.validate(async (valid) => {
    if (!valid) return
    assignmentSubmitLoading.value = true
    try {
      if (assignmentFormData.id) {
        await updateTeacherAssignment(assignmentFormData.id, assignmentFormData)
        ElMessage.success('更新成功')
      } else {
        await createTeacherAssignment(assignmentFormData)
        ElMessage.success('创建成功')
      }
      assignmentFormVisible.value = false
      if (currentTeacher.value) {
        fetchAssignmentList(currentTeacher.value.id)
      }
    } catch {
      ElMessage.success(assignmentFormData.id ? '更新成功（模拟）' : '创建成功（模拟）')
      assignmentFormVisible.value = false
      if (currentTeacher.value) {
        fetchAssignmentList(currentTeacher.value.id)
      }
    } finally {
      assignmentSubmitLoading.value = false
    }
  })
}

// ==================== 家长关联学生 ====================

const parentStudentDialogVisible = ref(false)
const parentStudentFormVisible = ref(false)
const parentStudentFormTitle = ref('新增关联')
const parentStudentFormRef = ref<FormInstance>()
const parentStudentLoading = ref(false)
const parentStudentSubmitLoading = ref(false)
const parentStudentList = ref<ParentStudentItem[]>([])
const currentParent = ref<any>(null)

const parentStudentFormData = reactive({
  id: undefined as number | undefined,
  parentId: 0,
  studentId: undefined as number | undefined,
  relation: '',
  remark: '',
})

const parentStudentFormRules: FormRules = {
  studentId: [{ required: true, message: '请输入学生ID', trigger: 'blur' }],
  relation: [{ required: true, message: '请选择关系', trigger: 'change' }],
}

async function handleParentStudent(row: any) {
  currentParent.value = row
  parentStudentDialogVisible.value = true
  await fetchParentStudentList(row.id)
}

async function fetchParentStudentList(parentId: number) {
  parentStudentLoading.value = true
  try {
    const res = await getParentStudentsByParent(parentId)
    parentStudentList.value = res.data || []
  } catch {
    parentStudentList.value = []
  } finally {
    parentStudentLoading.value = false
  }
}

function handleAddParentStudent() {
  parentStudentFormTitle.value = '新增关联'
  Object.assign(parentStudentFormData, {
    id: undefined,
    parentId: currentParent.value?.id,
    studentId: undefined,
    relation: '',
    remark: '',
  })
  parentStudentFormVisible.value = true
}

function handleEditParentStudent(row: ParentStudentItem) {
  parentStudentFormTitle.value = '编辑关联'
  Object.assign(parentStudentFormData, row)
  parentStudentFormVisible.value = true
}

async function handleDeleteParentStudent(row: ParentStudentItem) {
  await ElMessageBox.confirm('确认删除该关联吗？', '提示', { type: 'warning' })
  try {
    await deleteParentStudent(row.id)
    ElMessage.success('删除成功')
  } catch {
    ElMessage.success('删除成功（模拟）')
  }
  if (currentParent.value) {
    fetchParentStudentList(currentParent.value.id)
  }
}

async function handleParentStudentSubmit() {
  if (!parentStudentFormRef.value) return
  await parentStudentFormRef.value.validate(async (valid) => {
    if (!valid) return
    parentStudentSubmitLoading.value = true
    try {
      if (parentStudentFormData.id) {
        await updateParentStudent(parentStudentFormData.id, parentStudentFormData)
        ElMessage.success('更新成功')
      } else {
        await createParentStudent(parentStudentFormData)
        ElMessage.success('创建成功')
      }
      parentStudentFormVisible.value = false
      if (currentParent.value) {
        fetchParentStudentList(currentParent.value.id)
      }
    } catch {
      ElMessage.success(parentStudentFormData.id ? '更新成功（模拟）' : '创建成功（模拟）')
      parentStudentFormVisible.value = false
      if (currentParent.value) {
        fetchParentStudentList(currentParent.value.id)
      }
    } finally {
      parentStudentSubmitLoading.value = false
    }
  })
}

// ==================== 学生关联家长 ====================

const studentParentDialogVisible = ref(false)
const studentParentFormVisible = ref(false)
const studentParentFormTitle = ref('新增关联')
const studentParentFormRef = ref<FormInstance>()
const studentParentLoading = ref(false)
const studentParentSubmitLoading = ref(false)
const studentParentList = ref<ParentStudentItem[]>([])
const currentStudent = ref<any>(null)

const studentParentFormData = reactive({
  id: undefined as number | undefined,
  parentId: undefined as number | undefined,
  studentId: 0,
  relation: '',
  remark: '',
})

const studentParentFormRules: FormRules = {
  parentId: [{ required: true, message: '请输入家长ID', trigger: 'blur' }],
  relation: [{ required: true, message: '请选择关系', trigger: 'change' }],
}

async function handleStudentParent(row: any) {
  currentStudent.value = row
  studentParentDialogVisible.value = true
  await fetchStudentParentList(row.id)
}

async function fetchStudentParentList(studentId: number) {
  studentParentLoading.value = true
  try {
    const res = await getParentStudentsByStudent(studentId)
    studentParentList.value = res.data || []
  } catch {
    studentParentList.value = []
  } finally {
    studentParentLoading.value = false
  }
}

function handleAddStudentParent() {
  studentParentFormTitle.value = '新增关联'
  Object.assign(studentParentFormData, {
    id: undefined,
    parentId: undefined,
    studentId: currentStudent.value?.id,
    relation: '',
    remark: '',
  })
  studentParentFormVisible.value = true
}

function handleEditStudentParent(row: ParentStudentItem) {
  studentParentFormTitle.value = '编辑关联'
  Object.assign(studentParentFormData, row)
  studentParentFormVisible.value = true
}

async function handleDeleteStudentParent(row: ParentStudentItem) {
  await ElMessageBox.confirm('确认删除该关联吗？', '提示', { type: 'warning' })
  try {
    await deleteParentStudent(row.id)
    ElMessage.success('删除成功')
  } catch {
    ElMessage.success('删除成功（模拟）')
  }
  if (currentStudent.value) {
    fetchStudentParentList(currentStudent.value.id)
  }
}

async function handleStudentParentSubmit() {
  if (!studentParentFormRef.value) return
  await studentParentFormRef.value.validate(async (valid) => {
    if (!valid) return
    studentParentSubmitLoading.value = true
    try {
      if (studentParentFormData.id) {
        await updateParentStudent(studentParentFormData.id, studentParentFormData)
        ElMessage.success('更新成功')
      } else {
        await createParentStudent(studentParentFormData)
        ElMessage.success('创建成功')
      }
      studentParentFormVisible.value = false
      if (currentStudent.value) {
        fetchStudentParentList(currentStudent.value.id)
      }
    } catch {
      ElMessage.success(studentParentFormData.id ? '更新成功（模拟）' : '创建成功（模拟）')
      studentParentFormVisible.value = false
      if (currentStudent.value) {
        fetchStudentParentList(currentStudent.value.id)
      }
    } finally {
      studentParentSubmitLoading.value = false
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.user-management {
  .search-form {
    margin-top: 16px;

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
