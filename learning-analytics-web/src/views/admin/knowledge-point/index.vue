<template>
  <div class="knowledge-point-management">
    <!-- 顶部学科选择 -->
    <el-card shadow="never" class="mb-16">
      <el-form inline>
        <el-form-item label="选择学科">
          <el-select
            v-model="currentSubjectId"
            placeholder="请选择学科"
            style="width: 240px"
            @change="handleSubjectChange"
          >
            <el-option
              v-for="item in subjectList"
              :key="item.id"
              :label="item.subjectName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Plus" :disabled="!currentSubjectId" @click="handleAdd(0)">
            新增顶级知识点
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 主体区域：左侧树 + 右侧详情 -->
    <el-card shadow="never" v-loading="loading">
      <div class="kp-container">
        <!-- 左侧知识点树 -->
        <div class="kp-tree-panel">
          <div class="panel-title">知识点结构</div>
          <el-input
            v-model="filterText"
            placeholder="搜索知识点..."
            clearable
            class="mb-12"
          />
          <el-tree
            ref="treeRef"
            :data="treeData"
            :props="treeProps"
            node-key="id"
            default-expand-all
            :expand-on-click-node="false"
            :filter-node-method="filterNode"
            highlight-current
            @node-click="handleNodeClick"
          >
            <template #default="{ node, data }">
              <span class="tree-node-label">
                <span class="node-name">{{ data.kpName }}</span>
                <span class="node-actions">
                  <el-button
                    type="primary"
                    link
                    icon="Plus"
                    size="small"
                    @click.stop="handleAdd(data.id)"
                  />
                  <el-button
                    type="primary"
                    link
                    icon="Edit"
                    size="small"
                    @click.stop="handleEdit(data)"
                  />
                  <el-button
                    type="danger"
                    link
                    icon="Delete"
                    size="small"
                    @click.stop="handleDelete(data)"
                  />
                </span>
              </span>
            </template>
          </el-tree>
          <el-empty v-if="!loading && (!treeData || treeData.length === 0)" description="暂无知识点数据" />
        </div>

        <!-- 右侧详情面板 -->
        <div class="kp-detail-panel">
          <div class="panel-title">知识点详情</div>
          <template v-if="selectedNode">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="知识点名称">{{ selectedNode.kpName }}</el-descriptions-item>
              <el-descriptions-item label="知识点编码">{{ selectedNode.kpCode }}</el-descriptions-item>
              <el-descriptions-item label="层级">{{ selectedNode.level }}</el-descriptions-item>
              <el-descriptions-item label="排序">{{ selectedNode.sortOrder }}</el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="selectedNode.status === 1 ? 'success' : 'danger'">
                  {{ selectedNode.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ selectedNode.createdAt }}</el-descriptions-item>
            </el-descriptions>
          </template>
          <el-empty v-else description="请选择左侧知识点查看详情" />
        </div>
      </div>
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
        <el-form-item label="父级知识点" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="treeData"
            :props="{ label: 'kpName', value: 'id', children: 'children' }"
            placeholder="请选择父级（不选则为顶级）"
            clearable
            check-strictly
            :render-after-expand="false"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="知识点名称" prop="kpName">
          <el-input v-model="formData.kpName" placeholder="请输入知识点名称" />
        </el-form-item>
        <el-form-item label="知识点编码" prop="kpCode">
          <el-input v-model="formData.kpCode" placeholder="请输入知识点编码" />
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
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import type { ElTree } from 'element-plus'
import { getSubjectList } from '@/api/subject'
import {
  getKnowledgePointTree,
  createKnowledgePoint,
  updateKnowledgePoint,
  deleteKnowledgePoint,
  type KnowledgePointItem,
  type KnowledgePointFormData,
} from '@/api/knowledge-point'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增知识点')
const formRef = ref<FormInstance>()
const treeRef = ref<InstanceType<typeof ElTree>>()
const filterText = ref('')

const currentSubjectId = ref<number | undefined>(undefined)
const subjectList = ref<any[]>([])
const treeData = ref<KnowledgePointItem[]>([])
const selectedNode = ref<KnowledgePointItem | null>(null)

const treeProps = {
  label: 'kpName',
  children: 'children',
}

const formData = reactive<KnowledgePointFormData>({
  parentId: undefined,
  subjectId: 0,
  kpCode: '',
  kpName: '',
  level: 1,
  sortOrder: 0,
  status: 1,
})

const formRules: FormRules = {
  kpName: [{ required: true, message: '请输入知识点名称', trigger: 'blur' }],
  kpCode: [{ required: true, message: '请输入知识点编码', trigger: 'blur' }],
}

// 搜索过滤
watch(filterText, (val) => {
  treeRef.value?.filter(val)
})

function filterNode(value: string, data: KnowledgePointItem) {
  if (!value) return true
  return data.kpName.includes(value)
}

// 加载学科列表
async function fetchSubjects() {
  try {
    const res = await getSubjectList()
    subjectList.value = res.data || []
  } catch {
    subjectList.value = [
      { id: 1, subjectName: '语文', subjectCode: 'CHINESE' },
      { id: 2, subjectName: '数学', subjectCode: 'MATH' },
      { id: 3, subjectName: '英语', subjectCode: 'ENGLISH' },
    ]
  }
}

// 加载知识点树
async function fetchTree() {
  if (!currentSubjectId.value) {
    treeData.value = []
    return
  }
  loading.value = true
  try {
    const res = await getKnowledgePointTree(currentSubjectId.value)
    treeData.value = res.data || []
  } catch {
    // 模拟数据
    if (currentSubjectId.value === 2) {
      treeData.value = [
        {
          id: 1, kpName: '代数', kpCode: 'ALGEBRA', level: 1, sortOrder: 1, status: 1, parentId: 0, subjectId: 2, createdAt: '2025-01-01',
          children: [
            { id: 4, kpName: '方程', kpCode: 'EQUATION', level: 2, sortOrder: 1, status: 1, parentId: 1, subjectId: 2, createdAt: '2025-01-01', children: [] },
            { id: 5, kpName: '不等式', kpCode: 'INEQUALITY', level: 2, sortOrder: 2, status: 1, parentId: 1, subjectId: 2, createdAt: '2025-01-01', children: [] },
          ],
        },
        {
          id: 2, kpName: '几何', kpCode: 'GEOMETRY', level: 1, sortOrder: 2, status: 1, parentId: 0, subjectId: 2, createdAt: '2025-01-01',
          children: [
            { id: 6, kpName: '平面几何', kpCode: 'PLANE_GEOMETRY', level: 2, sortOrder: 1, status: 1, parentId: 2, subjectId: 2, createdAt: '2025-01-01', children: [] },
          ],
        },
        { id: 3, kpName: '统计与概率', kpCode: 'STATISTICS', level: 1, sortOrder: 3, status: 1, parentId: 0, subjectId: 2, createdAt: '2025-01-01', children: [] },
      ]
    } else {
      treeData.value = []
    }
  } finally {
    loading.value = false
  }
}

function handleSubjectChange() {
  selectedNode.value = null
  fetchTree()
}

function handleNodeClick(data: KnowledgePointItem) {
  selectedNode.value = data
}

function handleAdd(parentId: number) {
  dialogTitle.value = parentId === 0 ? '新增顶级知识点' : '新增子知识点'
  Object.assign(formData, {
    id: undefined,
    parentId: parentId === 0 ? undefined : parentId,
    subjectId: currentSubjectId.value || 0,
    kpCode: '',
    kpName: '',
    level: 1,
    sortOrder: 0,
    status: 1,
  })
  dialogVisible.value = true
}

function handleEdit(data: KnowledgePointItem) {
  dialogTitle.value = '编辑知识点'
  Object.assign(formData, {
    id: data.id,
    parentId: data.parentId,
    subjectId: data.subjectId,
    kpCode: data.kpCode,
    kpName: data.kpName,
    level: data.level,
    sortOrder: data.sortOrder,
    status: data.status,
  })
  dialogVisible.value = true
}

async function handleDelete(data: KnowledgePointItem) {
  if (data.children && data.children.length > 0) {
    ElMessage.warning('该知识点下存在子节点，请先删除子节点')
    return
  }
  await ElMessageBox.confirm(`确认删除知识点 "${data.kpName}" 吗？`, '提示', { type: 'warning' })
  try {
    await deleteKnowledgePoint(data.id)
    ElMessage.success('删除成功')
    fetchTree()
  } catch {
    ElMessage.success('删除成功（模拟）')
    fetchTree()
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (formData.id) {
        await updateKnowledgePoint(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createKnowledgePoint(formData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      fetchTree()
    } catch {
      ElMessage.success(formData.id ? '更新成功（模拟）' : '创建成功（模拟）')
      dialogVisible.value = false
      fetchTree()
    } finally {
      submitLoading.value = false
    }
  })
}

onMounted(() => {
  fetchSubjects()
})
</script>

<style scoped lang="scss">
.knowledge-point-management {
  .kp-container {
    display: flex;
    gap: 20px;
    min-height: 500px;
  }

  .kp-tree-panel {
    flex: 0 0 420px;
    border-right: 1px solid var(--el-border-color-lighter);
    padding-right: 20px;
    overflow: auto;
  }

  .kp-detail-panel {
    flex: 1;
    padding-left: 20px;
  }

  .panel-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
    color: var(--el-text-color-primary);
  }

  .tree-node-label {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    padding-right: 8px;

    .node-name {
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .node-actions {
      display: none;
      margin-left: 8px;
    }
  }

  .el-tree-node__content:hover .node-actions {
    display: inline-flex;
  }
}
</style>
