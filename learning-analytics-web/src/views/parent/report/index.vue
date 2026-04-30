<template>
  <div class="parent-report">
    <!-- 报告列表 -->
    <el-card shadow="never" class="mb-16">
      <template #header>
        <div class="flex-between">
          <span class="card-title">学情报告</span>
          <el-tag size="small" type="info">{{ reportList.length }} 份</el-tag>
        </div>
      </template>

      <div v-if="reportList.length" class="report-list">
        <el-card
          v-for="report in reportList"
          :key="report.id"
          shadow="hover"
          class="report-card mb-12"
          @click="handleViewReport(report)"
        >
          <div class="report-info">
            <div class="report-header">
              <el-icon :size="20" color="#409EFF"><Document /></el-icon>
              <span class="report-name">{{ report.name }}</span>
            </div>
            <div class="report-meta">
              <el-tag :type="reportTypeTag(report.type)" size="small">{{ reportTypeText(report.type) }}</el-tag>
              <span class="report-time">{{ report.createdAt }}</span>
            </div>
            <div class="report-status">
              <el-tag :type="reportStatusTag(report.status)" size="small" effect="plain">
                {{ reportStatusText(report.status) }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </div>
      <el-empty v-else description="暂无报告" :image-size="80" />
    </el-card>

    <!-- 报告详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      :title="currentReport?.name || '报告详情'"
      width="90%"
      top="5vh"
      destroy-on-close
    >
      <div v-if="currentReport" class="report-detail">
        <div class="detail-meta">
          <el-descriptions :column="2" size="small" border>
            <el-descriptions-item label="报告类型">{{ reportTypeText(currentReport.type) }}</el-descriptions-item>
            <el-descriptions-item label="生成时间">{{ currentReport.createdAt }}</el-descriptions-item>
            <el-descriptions-item label="生成人">{{ currentReport.generatedByName || '系统' }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="reportStatusTag(currentReport.status)" size="small">
                {{ reportStatusText(currentReport.status) }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <el-divider />

        <div class="detail-content">
          <div v-if="currentReport.status === 'completed'" class="report-preview">
            <div class="preview-placeholder">
              <el-icon :size="48" color="#C0C4CC"><Reading /></el-icon>
              <p>报告内容预览区域</p>
              <p class="preview-hint">完整报告请下载PDF查看</p>
            </div>
          </div>
          <div v-else-if="currentReport.status === 'generating'" class="report-generating">
            <el-icon :size="48" color="#409EFF"><Loading /></el-icon>
            <p>报告正在生成中，请稍后查看...</p>
          </div>
          <div v-else-if="currentReport.status === 'failed'" class="report-failed">
            <el-icon :size="48" color="#F56C6C"><CircleCloseFilled /></el-icon>
            <p>报告生成失败，请联系老师重新生成</p>
          </div>
          <div v-else class="report-pending">
            <el-icon :size="48" color="#E6A23C"><Clock /></el-icon>
            <p>报告等待生成中</p>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button
          v-if="currentReport?.status === 'completed'"
          type="primary"
          @click="handleDownload(currentReport!)"
        >
          <el-icon><Download /></el-icon>
          下载PDF
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentReports, downloadReport, type ReportItem } from '@/api/report'

const reportList = ref<ReportItem[]>([])
const detailVisible = ref(false)
const currentReport = ref<ReportItem | null>(null)

function reportTypeText(type: string) {
  const map: Record<string, string> = {
    student: '个人报告', class: '班级报告', exam: '测评报告', grade: '年级报告',
  }
  return map[type] || type
}

function reportTypeTag(type: string) {
  const map: Record<string, string> = {
    student: 'primary', class: 'success', exam: 'warning', grade: 'info',
  }
  return map[type] || 'info'
}

function reportStatusText(status: string) {
  const map: Record<string, string> = {
    pending: '待生成', generating: '生成中', completed: '已完成', failed: '生成失败',
  }
  return map[status] || status
}

function reportStatusTag(status: string) {
  const map: Record<string, string> = {
    pending: 'info', generating: 'warning', completed: 'success', failed: 'danger',
  }
  return map[status] || 'info'
}

function handleViewReport(report: ReportItem) {
  currentReport.value = report
  detailVisible.value = true
}

async function handleDownload(report: ReportItem) {
  try {
    const res = await downloadReport(report.id)
    const blob = new Blob([res as any], { type: 'application/pdf' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${report.name}.pdf`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('下载成功')
  } catch (e) {
    ElMessage.error('下载失败，请稍后重试')
  }
}

async function loadData() {
  try {
    const res = await getStudentReports(1)
    reportList.value = res.data || []
  } catch (e) {
    console.error('加载报告列表失败', e)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.report-card {
  cursor: pointer;
  transition: transform 0.2s;
}
.report-card:hover {
  transform: translateY(-1px);
}
.report-card :deep(.el-card__body) {
  padding: 12px 16px;
}

.report-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.report-header {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}
.report-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}
.report-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-right: 12px;
}
.report-time {
  font-size: 12px;
  color: #909399;
}

.report-detail {
  max-height: 60vh;
  overflow-y: auto;
}

.report-preview,
.report-generating,
.report-failed,
.report-pending {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #909399;
  gap: 12px;
}
.preview-placeholder p {
  font-size: 14px;
}
.preview-hint {
  font-size: 12px;
  color: #C0C4CC;
}
</style>
