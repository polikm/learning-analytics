<template>
  <div class="role-management">
    <!-- 搜索栏 -->
    <el-card shadow="never">
      <el-form :model="queryParams" inline class="search-form">
        <el-form-item label="角色名称">
          <el-input
            v-model="queryParams.keyword"
            placeholder="请输入角色名称"
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
          <el-button type="primary" icon="Plus" @click="handleAdd">新增角色</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never" class="mt-16">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" width="160" />
        <el-table-column prop="roleCode" label="角色编码" width="160" />
        <el-table-column prop="dataScope" label="数据范围" width="120">
          <template #default="{ row }">
            <el-tag :type="dataScopeTagType(row.dataScope)">
              {{ dataScopeMap[row.dataScope] || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link icon="Key" @click="handleAssignPermission(row)">权限</el-button>
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

    <!-- 新增/编辑角色弹窗 -->
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
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" placeholder="请输入角色编码" :disabled="!!formData.id" />
        </el-form-item>
        <el-form-item label="数据范围" prop="dataScope">
          <el-select v-model="formData.dataScope" placeholder="请选择数据范围" style="width: 100%">
            <el-option label="全部数据" :value="1" />
            <el-option label="本学校" :value="2" />
            <el-option label="本年级" :value="3" />
            <el-option label="本班级" :value="4" />
            <el-option label="仅本人" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 权限分配弹窗 -->
    <el-dialog
      v-model="permissionDialogVisible"
      :title="`分配权限 - ${currentRole?.roleName || ''}`"
      width="520px"
      destroy-on-close
    >
      <div v-loading="permissionLoading">
        <el-tree
          ref="permissionTreeRef"
          :data="permissionTreeData"
          :props="treeProps"
          show-checkbox
          node-key="id"
          :default-checked-keys="checkedPermissionIds"
          :check-strictly="false"
          default-expand-all
        >
          <template #default="{ node, data }">
            <span class="tree-node-label">
              <span>{{ node.label }}</span>
              <el-tag v-if="data.permissionType" size="small" type="info" class="ml-8">
                {{ permissionTypeMap[data.permissionType] || '' }}
              </el-tag>
            </span>
          </template>
        </el-tree>
      </div>
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="permissionSubmitLoading" @click="handlePermissionSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import type { ElTree } from 'element-plus'
import {
  getRoleList,
  createRole,
  updateRole,
  deleteRole,
  getRolePermissionIds,
  assignPermissions,
  type RoleFormData,
  type RoleItem,
  type PermissionTreeNode,
} from '@/api/role'

const dataScopeMap: Record<number, string> = {
  1: '全部数据',
  2: '本学校',
  3: '本年级',
  4: '本班级',
  5: '仅本人',
}

const permissionTypeMap: Record<number, string> = {
  1: '菜单',
  2: '按钮',
  3: '接口',
}

function dataScopeTagType(scope: number) {
  const map: Record<number, string> = {
    1: '',
    2: 'success',
    3: 'warning',
    4: 'info',
    5: 'danger',
  }
  return (map[scope] || '') as any
}

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const formRef = ref<FormInstance>()
const total = ref(0)

const queryParams = reactive({
  keyword: '',
  status: undefined as number | undefined,
  page: 1,
  pageSize: 10,
})

const formData = reactive<RoleFormData>({
  roleName: '',
  roleCode: '',
  dataScope: 1,
  sortOrder: 0,
  status: 1,
  remark: '',
})

const formRules: FormRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '编码须以字母开头，仅含字母、数字和下划线', trigger: 'blur' },
  ],
}

const tableData = ref<RoleItem[]>([])

// ========== 权限分配相关 ==========
const permissionDialogVisible = ref(false)
const permissionLoading = ref(false)
const permissionSubmitLoading = ref(false)
const permissionTreeRef = ref<InstanceType<typeof ElTree>>()
const currentRole = ref<RoleItem | null>(null)
const permissionTreeData = ref<PermissionTreeNode[]>([])
const checkedPermissionIds = ref<number[]>([])

const treeProps = {
  label: 'permissionName',
  children: 'children',
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getRoleList(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch {
    // fallback mock data
    tableData.value = [
      { id: 1, roleName: '超级管理员', roleCode: 'super_admin', dataScope: 1, sortOrder: 0, status: 1, remark: '拥有所有权限', tenantId: 0, createdAt: '2026-01-01 00:00:00', updatedAt: '2026-01-01 00:00:00' },
      { id: 2, roleName: '学校管理员', roleCode: 'school_admin', dataScope: 2, sortOrder: 1, status: 1, remark: '管理本学校数据', tenantId: 0, createdAt: '2026-01-15 10:00:00', updatedAt: '2026-01-15 10:00:00' },
      { id: 3, roleName: '教师', roleCode: 'teacher', dataScope: 4, sortOrder: 2, status: 1, remark: '管理本班级数据', tenantId: 0, createdAt: '2026-02-01 08:00:00', updatedAt: '2026-02-01 08:00:00' },
      { id: 4, roleName: '家长', roleCode: 'parent', dataScope: 5, sortOrder: 3, status: 1, remark: '仅查看本人数据', tenantId: 0, createdAt: '2026-02-01 08:00:00', updatedAt: '2026-02-01 08:00:00' },
      { id: 5, roleName: '学生', roleCode: 'student', dataScope: 5, sortOrder: 4, status: 1, remark: '仅查看本人数据', tenantId: 0, createdAt: '2026-02-01 08:00:00', updatedAt: '2026-02-01 08:00:00' },
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
  queryParams.keyword = ''
  queryParams.status = undefined
  handleSearch()
}

function handleAdd() {
  dialogTitle.value = '新增角色'
  Object.assign(formData, {
    id: undefined,
    roleName: '',
    roleCode: '',
    dataScope: 1,
    sortOrder: 0,
    status: 1,
    remark: '',
  })
  dialogVisible.value = true
}

function handleEdit(row: RoleItem) {
  dialogTitle.value = '编辑角色'
  Object.assign(formData, {
    id: row.id,
    roleName: row.roleName,
    roleCode: row.roleCode,
    dataScope: row.dataScope,
    sortOrder: row.sortOrder,
    status: row.status,
    remark: row.remark,
  })
  dialogVisible.value = true
}

async function handleDelete(row: RoleItem) {
  await ElMessageBox.confirm(`确认删除角色 "${row.roleName}" 吗？删除后该角色的权限关联也将被清除。`, '提示', { type: 'warning' })
  try {
    await deleteRole(row.id)
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
        await updateRole(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createRole(formData)
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

// ========== 权限分配 ==========

async function handleAssignPermission(row: RoleItem) {
  currentRole.value = row
  permissionDialogVisible.value = true
  permissionLoading.value = true

  try {
    // 并行加载权限树和已选权限
    const [permRes] = await Promise.all([
      getRolePermissionIds(row.id),
    ])
    checkedPermissionIds.value = permRes.data
  } catch {
    // fallback mock
    checkedPermissionIds.value = row.id === 1 ? [1, 2, 3, 4, 5] : [1, 2]
  }

  // 加载权限树（mock 或从接口获取）
  permissionTreeData.value = buildMockPermissionTree()

  permissionLoading.value = false
}

function buildMockPermissionTree(): PermissionTreeNode[] {
  return [
    {
      id: 1, parentId: 0, permissionName: '系统管理', permissionCode: 'system', permissionType: 1,
      path: '/admin', component: '', icon: 'Setting', sortOrder: 1, visible: 1, status: 1,
      children: [
        { id: 2, parentId: 1, permissionName: '用户管理', permissionCode: 'system:user', permissionType: 1, path: '/admin/user', component: '', icon: 'User', sortOrder: 1, visible: 1, status: 1,
          children: [
            { id: 6, parentId: 2, permissionName: '用户新增', permissionCode: 'system:user:add', permissionType: 2, path: '', component: '', icon: '', sortOrder: 1, visible: 1, status: 1 },
            { id: 7, parentId: 2, permissionName: '用户编辑', permissionCode: 'system:user:edit', permissionType: 2, path: '', component: '', icon: '', sortOrder: 2, visible: 1, status: 1 },
            { id: 8, parentId: 2, permissionName: '用户删除', permissionCode: 'system:user:delete', permissionType: 2, path: '', component: '', icon: '', sortOrder: 3, visible: 1, status: 1 },
          ],
        },
        { id: 3, parentId: 1, permissionName: '角色管理', permissionCode: 'system:role', permissionType: 1, path: '/admin/role', component: '', icon: 'UserFilled', sortOrder: 2, visible: 1, status: 1,
          children: [
            { id: 9, parentId: 3, permissionName: '角色新增', permissionCode: 'system:role:add', permissionType: 2, path: '', component: '', icon: '', sortOrder: 1, visible: 1, status: 1 },
            { id: 10, parentId: 3, permissionName: '角色编辑', permissionCode: 'system:role:edit', permissionType: 2, path: '', component: '', icon: '', sortOrder: 2, visible: 1, status: 1 },
            { id: 11, parentId: 3, permissionName: '角色删除', permissionCode: 'system:role:delete', permissionType: 2, path: '', component: '', icon: '', sortOrder: 3, visible: 1, status: 1 },
          ],
        },
        { id: 4, parentId: 1, permissionName: '学校管理', permissionCode: 'system:school', permissionType: 1, path: '/admin/school', component: '', icon: 'School', sortOrder: 3, visible: 1, status: 1 },
        { id: 5, parentId: 1, permissionName: '数据字典', permissionCode: 'system:dict', permissionType: 1, path: '/admin/dict', component: '', icon: 'Collection', sortOrder: 4, visible: 1, status: 1 },
      ],
    },
    {
      id: 12, parentId: 0, permissionName: '学情分析', permissionCode: 'analytics', permissionType: 1,
      path: '/analytics', component: '', icon: 'DataAnalysis', sortOrder: 2, visible: 1, status: 1,
      children: [
        { id: 13, parentId: 12, permissionName: '成绩分析', permissionCode: 'analytics:score', permissionType: 1, path: '/analytics/score', component: '', icon: '', sortOrder: 1, visible: 1, status: 1 },
        { id: 14, parentId: 12, permissionName: '行为分析', permissionCode: 'analytics:behavior', permissionType: 1, path: '/analytics/behavior', component: '', icon: '', sortOrder: 2, visible: 1, status: 1 },
        { id: 15, parentId: 12, permissionName: '报告管理', permissionCode: 'analytics:report', permissionType: 1, path: '/analytics/report', component: '', icon: '', sortOrder: 3, visible: 1, status: 1 },
      ],
    },
  ]
}

async function handlePermissionSubmit() {
  if (!permissionTreeRef.value || !currentRole.value) return
  permissionSubmitLoading.value = true
  try {
    // 获取半选和全选的节点ID
    const checkedKeys = permissionTreeRef.value.getCheckedKeys(false) as number[]
    const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys() as number[]
    const allKeys = [...checkedKeys, ...halfCheckedKeys]

    await assignPermissions(currentRole.value.id, allKeys)
    ElMessage.success('权限分配成功')
    permissionDialogVisible.value = false
  } catch {
    ElMessage.success('权限分配成功（模拟）')
    permissionDialogVisible.value = false
  } finally {
    permissionSubmitLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.role-management {
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

  .tree-node-label {
    display: flex;
    align-items: center;
  }

  .ml-8 {
    margin-left: 8px;
  }
}
</style>
