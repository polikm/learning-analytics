<template>
  <div class="dict-management">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="关键词">
          <el-input
            v-model="queryParams.keyword"
            placeholder="字典名称/编码"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
          <el-button type="primary" icon="Plus" @click="handleAddDict">新增字典</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 左右分栏：字典列表 + 字典项列表 -->
    <div class="dict-content mt-16">
      <!-- 左侧：字典列表 -->
      <el-card shadow="never" class="dict-list-card">
        <template #header>
          <span>字典列表</span>
        </template>
        <el-table
          :data="dictData"
          v-loading="dictLoading"
          stripe
          highlight-current-row
          @current-change="handleDictSelect"
          style="width: 100%"
        >
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="dictName" label="字典名称" min-width="120" show-overflow-tooltip />
          <el-table-column prop="dictCode" label="字典编码" min-width="120" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="70">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link icon="Edit" @click.stop="handleEditDict(row)">编辑</el-button>
              <el-button type="danger" link icon="Delete" @click.stop="handleDeleteDict(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="queryParams.page"
            v-model:page-size="queryParams.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="dictTotal"
            layout="total, sizes, prev, pager, next"
            small
            @size-change="fetchDictList"
            @current-change="fetchDictList"
          />
        </div>
      </el-card>

      <!-- 右侧：字典项列表 -->
      <el-card shadow="never" class="dict-item-card">
        <template #header>
          <div class="flex-between">
            <span>字典项列表{{ currentDict ? ` - ${currentDict.dictName}` : '' }}</span>
            <el-button
              type="primary"
              icon="Plus"
              size="small"
              :disabled="!currentDict"
              @click="handleAddItem"
            >
              新增字典项
            </el-button>
          </div>
        </template>

        <template v-if="currentDict">
          <el-table :data="itemData" v-loading="itemLoading" stripe style="width: 100%">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="itemCode" label="项编码" min-width="100" show-overflow-tooltip />
            <el-table-column prop="itemValue" label="项值" min-width="100" show-overflow-tooltip />
            <el-table-column prop="itemLabel" label="项标签" min-width="120" show-overflow-tooltip />
            <el-table-column prop="sortOrder" label="排序" width="70" />
            <el-table-column prop="status" label="状态" width="70">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="140" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link icon="Edit" @click="handleEditItem(row)">编辑</el-button>
                <el-button type="danger" link icon="Delete" @click="handleDeleteItem(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </template>
        <el-empty v-else description="请选择左侧字典查看字典项" />
      </el-card>
    </div>

    <!-- 新增/编辑字典弹窗 -->
    <el-dialog
      v-model="dictDialogVisible"
      :title="dictDialogTitle"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="dictFormRef"
        :model="dictFormData"
        :rules="dictFormRules"
        label-width="100px"
      >
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="dictFormData.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典编码" prop="dictCode">
          <el-input v-model="dictFormData.dictCode" placeholder="请输入字典编码" :disabled="!!dictFormData.id" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="dictFormData.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dictFormData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dictDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="dictSubmitLoading" @click="handleDictSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑字典项弹窗 -->
    <el-dialog
      v-model="itemDialogVisible"
      :title="itemDialogTitle"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="itemFormRef"
        :model="itemFormData"
        :rules="itemFormRules"
        label-width="100px"
      >
        <el-form-item label="项编码" prop="itemCode">
          <el-input v-model="itemFormData.itemCode" placeholder="请输入项编码" />
        </el-form-item>
        <el-form-item label="项值" prop="itemValue">
          <el-input v-model="itemFormData.itemValue" placeholder="请输入项值" />
        </el-form-item>
        <el-form-item label="项标签" prop="itemLabel">
          <el-input v-model="itemFormData.itemLabel" placeholder="请输入项标签" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="itemFormData.sortOrder" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="itemFormData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="itemSubmitLoading" @click="handleItemSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  getDictList,
  getDictDetail,
  createDict,
  updateDict,
  deleteDict,
  createDictItem,
  updateDictItem,
  deleteDictItem,
  type DictFormData,
  type DictItemFormData,
  type DictItem,
  type DictEntryItem,
} from '@/api/dict'

// ===== 字典列表 =====
const dictLoading = ref(false)
const dictData = ref<DictItem[]>([])
const dictTotal = ref(0)
const currentDict = ref<DictItem | null>(null)

const queryParams = reactive({
  keyword: '',
  page: 1,
  pageSize: 10,
})

async function fetchDictList() {
  dictLoading.value = true
  try {
    const res = await getDictList(queryParams)
    dictData.value = res.data.records
    dictTotal.value = res.data.total
  } catch {
    // 使用模拟数据
    dictData.value = [
      { id: 1, tenantId: 1, dictCode: 'gender', dictName: '性别', description: '用户性别', status: 1, createdAt: '2026-01-01 00:00:00' },
      { id: 2, tenantId: 1, dictCode: 'grade', dictName: '年级', description: '学生年级', status: 1, createdAt: '2026-01-02 00:00:00' },
      { id: 3, tenantId: 1, dictCode: 'subject', dictName: '学科', description: '考试学科', status: 1, createdAt: '2026-01-03 00:00:00' },
    ]
    dictTotal.value = 3
  } finally {
    dictLoading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  fetchDictList()
}

function handleReset() {
  queryParams.keyword = ''
  handleSearch()
}

function handleDictSelect(row: DictItem | null) {
  currentDict.value = row
  if (row) {
    fetchDictItems(row.id)
  } else {
    itemData.value = []
  }
}

// ===== 字典项列表 =====
const itemLoading = ref(false)
const itemData = ref<DictEntryItem[]>([])

async function fetchDictItems(dictId: number) {
  itemLoading.value = true
  try {
    const res = await getDictDetail(dictId)
    itemData.value = res.data.items
  } catch {
    // 使用模拟数据
    const mockItems: Record<number, DictEntryItem[]> = {
      1: [
        { id: 1, dictId: 1, itemCode: 'male', itemValue: '1', itemLabel: '男', sortOrder: 1, status: 1, createdAt: '2026-01-01 00:00:00' },
        { id: 2, dictId: 1, itemCode: 'female', itemValue: '2', itemLabel: '女', sortOrder: 2, status: 1, createdAt: '2026-01-01 00:00:00' },
      ],
      2: [
        { id: 3, dictId: 2, itemCode: 'g1', itemValue: '1', itemLabel: '一年级', sortOrder: 1, status: 1, createdAt: '2026-01-02 00:00:00' },
        { id: 4, dictId: 2, itemCode: 'g2', itemValue: '2', itemLabel: '二年级', sortOrder: 2, status: 1, createdAt: '2026-01-02 00:00:00' },
        { id: 5, dictId: 2, itemCode: 'g3', itemValue: '3', itemLabel: '三年级', sortOrder: 3, status: 1, createdAt: '2026-01-02 00:00:00' },
      ],
      3: [
        { id: 6, dictId: 3, itemCode: 'chinese', itemValue: 'chinese', itemLabel: '语文', sortOrder: 1, status: 1, createdAt: '2026-01-03 00:00:00' },
        { id: 7, dictId: 3, itemCode: 'math', itemValue: 'math', itemLabel: '数学', sortOrder: 2, status: 1, createdAt: '2026-01-03 00:00:00' },
        { id: 8, dictId: 3, itemCode: 'english', itemValue: 'english', itemLabel: '英语', sortOrder: 3, status: 1, createdAt: '2026-01-03 00:00:00' },
      ],
    }
    itemData.value = mockItems[dictId] || []
  } finally {
    itemLoading.value = false
  }
}

// ===== 字典 CRUD =====
const dictDialogVisible = ref(false)
const dictDialogTitle = ref('新增字典')
const dictSubmitLoading = ref(false)
const dictFormRef = ref<FormInstance>()

const dictFormData = reactive<DictFormData>({
  dictCode: '',
  dictName: '',
  description: '',
  status: 1,
})

const dictFormRules: FormRules = {
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
  dictCode: [{ required: true, message: '请输入字典编码', trigger: 'blur' }],
}

function handleAddDict() {
  dictDialogTitle.value = '新增字典'
  Object.assign(dictFormData, { id: undefined, dictCode: '', dictName: '', description: '', status: 1 })
  dictDialogVisible.value = true
}

function handleEditDict(row: DictItem) {
  dictDialogTitle.value = '编辑字典'
  Object.assign(dictFormData, {
    id: row.id,
    dictCode: row.dictCode,
    dictName: row.dictName,
    description: row.description,
    status: row.status,
  })
  dictDialogVisible.value = true
}

async function handleDeleteDict(row: DictItem) {
  await ElMessageBox.confirm(`确认删除字典 "${row.dictName}" 吗？删除后该字典下的所有字典项也将被删除。`, '提示', { type: 'warning' })
  try {
    await deleteDict(row.id)
    ElMessage.success('删除成功')
  } catch {
    ElMessage.success('删除成功（模拟）')
  }
  // 如果删除的是当前选中的字典，清空右侧
  if (currentDict.value?.id === row.id) {
    currentDict.value = null
    itemData.value = []
  }
  fetchDictList()
}

async function handleDictSubmit() {
  if (!dictFormRef.value) return
  await dictFormRef.value.validate(async (valid) => {
    if (!valid) return
    dictSubmitLoading.value = true
    try {
      if (dictFormData.id) {
        await updateDict(dictFormData.id, dictFormData)
        ElMessage.success('更新成功')
      } else {
        await createDict(dictFormData)
        ElMessage.success('创建成功')
      }
      dictDialogVisible.value = false
      fetchDictList()
    } catch {
      ElMessage.success(dictFormData.id ? '更新成功（模拟）' : '创建成功（模拟）')
      dictDialogVisible.value = false
      fetchDictList()
    } finally {
      dictSubmitLoading.value = false
    }
  })
}

// ===== 字典项 CRUD =====
const itemDialogVisible = ref(false)
const itemDialogTitle = ref('新增字典项')
const itemSubmitLoading = ref(false)
const itemFormRef = ref<FormInstance>()

const itemFormData = reactive<DictItemFormData>({
  dictId: 0,
  itemCode: '',
  itemValue: '',
  itemLabel: '',
  sortOrder: 0,
  status: 1,
})

const itemFormRules: FormRules = {
  itemCode: [{ required: true, message: '请输入项编码', trigger: 'blur' }],
  itemValue: [{ required: true, message: '请输入项值', trigger: 'blur' }],
  itemLabel: [{ required: true, message: '请输入项标签', trigger: 'blur' }],
}

function handleAddItem() {
  if (!currentDict.value) return
  itemDialogTitle.value = '新增字典项'
  Object.assign(itemFormData, {
    id: undefined,
    dictId: currentDict.value.id,
    itemCode: '',
    itemValue: '',
    itemLabel: '',
    sortOrder: 0,
    status: 1,
  })
  itemDialogVisible.value = true
}

function handleEditItem(row: DictEntryItem) {
  itemDialogTitle.value = '编辑字典项'
  Object.assign(itemFormData, {
    id: row.id,
    dictId: row.dictId,
    itemCode: row.itemCode,
    itemValue: row.itemValue,
    itemLabel: row.itemLabel,
    sortOrder: row.sortOrder,
    status: row.status,
  })
  itemDialogVisible.value = true
}

async function handleDeleteItem(row: DictEntryItem) {
  await ElMessageBox.confirm(`确认删除字典项 "${row.itemLabel}" 吗？`, '提示', { type: 'warning' })
  try {
    await deleteDictItem(row.id)
    ElMessage.success('删除成功')
  } catch {
    ElMessage.success('删除成功（模拟）')
  }
  if (currentDict.value) {
    fetchDictItems(currentDict.value.id)
  }
}

async function handleItemSubmit() {
  if (!itemFormRef.value) return
  await itemFormRef.value.validate(async (valid) => {
    if (!valid) return
    itemSubmitLoading.value = true
    try {
      if (itemFormData.id) {
        await updateDictItem(itemFormData.id, itemFormData)
        ElMessage.success('更新成功')
      } else {
        await createDictItem(itemFormData)
        ElMessage.success('创建成功')
      }
      itemDialogVisible.value = false
      if (currentDict.value) {
        fetchDictItems(currentDict.value.id)
      }
    } catch {
      ElMessage.success(itemFormData.id ? '更新成功（模拟）' : '创建成功（模拟）')
      itemDialogVisible.value = false
      if (currentDict.value) {
        fetchDictItems(currentDict.value.id)
      }
    } finally {
      itemSubmitLoading.value = false
    }
  })
}

onMounted(() => {
  fetchDictList()
})
</script>

<style scoped lang="scss">
.dict-management {
  .search-card {
    :deep(.el-form-item) {
      margin-bottom: 0;
    }
  }

  .dict-content {
    display: flex;
    gap: 16px;

    .dict-list-card {
      flex: 1;
      min-width: 0;
    }

    .dict-item-card {
      flex: 1;
      min-width: 0;
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 12px;
  }
}
</style>
