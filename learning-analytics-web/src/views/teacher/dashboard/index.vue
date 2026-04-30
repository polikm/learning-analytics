<template>
  <div class="teacher-dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background: linear-gradient(135deg, #409EFF, #66B1FF)">
              <el-icon :size="28"><School /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">我的班级数</div>
              <el-statistic :value="stats.classCount" :value-style="{ fontSize: '28px', fontWeight: 'bold', color: '#409EFF' }" />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background: linear-gradient(135deg, #E6A23C, #F0C78A)">
              <el-icon :size="28"><EditPen /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">待批阅测评</div>
              <el-statistic :value="stats.pendingGrade" :value-style="{ fontSize: '28px', fontWeight: 'bold', color: '#E6A23C' }" />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background: linear-gradient(135deg, #67C23A, #95D475)">
              <el-icon :size="28"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">学生总数</div>
              <el-statistic :value="stats.studentCount" :value-style="{ fontSize: '28px', fontWeight: 'bold', color: '#67C23A' }" />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background: linear-gradient(135deg, #F56C6C, #F89898)">
              <el-icon :size="28"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">预警学生数</div>
              <el-statistic :value="stats.warningCount" :value-style="{ fontSize: '28px', fontWeight: 'bold', color: '#F56C6C' }" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 近期测评 + 学情预警 -->
    <el-row :gutter="20" class="mt-20">
      <el-col :span="14">
        <el-card shadow="hover">
          <template #header>
            <div class="flex-between">
              <span class="card-title">近期测评</span>
              <el-button text type="primary" @click="$router.push('/teacher/exams')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentExams" stripe style="width: 100%">
            <el-table-column prop="title" label="测评名称" min-width="160" show-overflow-tooltip />
            <el-table-column prop="subject" label="学科" width="100" />
            <el-table-column prop="startTime" label="开始时间" width="170">
              <template #default="{ row }">{{ formatDate(row.startTime) }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="examStatusType(row.status)" size="small">{{ examStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template #default="{ row }">
                <el-button text type="primary" size="small" @click="handleViewExam(row)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!recentExams.length" description="暂无近期测评" :image-size="60" />
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header>
            <div class="flex-between">
              <span class="card-title">学情预警</span>
              <el-tag type="danger" size="small">{{ warningStudents.length }} 人</el-tag>
            </div>
          </template>
          <el-table :data="warningStudents" stripe style="width: 100%" max-height="380">
            <el-table-column prop="studentName" label="学生姓名" width="100" />
            <el-table-column prop="className" label="班级" width="100" show-overflow-tooltip />
            <el-table-column prop="type" label="预警类型" min-width="120">
              <template #default="{ row }">
                <el-tag :type="warningTypeTag(row.type)" size="small">{{ warningTypeText(row.type) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!warningStudents.length" description="暂无预警学生" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getExamList } from '@/api/exam'
import { getWarnings } from '@/api/profile'
import dayjs from 'dayjs'

const router = useRouter()

const stats = reactive({
  classCount: 0,
  pendingGrade: 0,
  studentCount: 0,
  warningCount: 0,
})

const recentExams = ref<any[]>([])
const warningStudents = ref<any[]>([])

function formatDate(date: string) {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

function examStatusText(status: string) {
  const map: Record<string, string> = {
    draft: '草稿', published: '已发布', in_progress: '进行中', ended: '已结束', grading: '阅卷中',
  }
  return map[status] || status
}

function examStatusType(status: string) {
  const map: Record<string, string> = {
    draft: 'info', published: 'primary', in_progress: 'success', ended: '', grading: 'warning',
  }
  return map[status] || 'info'
}

function warningTypeText(type: string) {
  const map: Record<string, string> = {
    score_decline: '成绩下滑', absence: '出勤异常', fail: '不及格', inactivity: '学习不活跃',
  }
  return map[type] || type
}

function warningTypeTag(type: string) {
  const map: Record<string, string> = {
    score_decline: 'warning', absence: 'danger', fail: 'danger', inactivity: 'info',
  }
  return map[type] || 'info'
}

function handleViewExam(row: any) {
  router.push('/teacher/exams')
}

async function loadDashboard() {
  try {
    const [examRes, warningRes] = await Promise.all([
      getExamList({ page: 1, pageSize: 5 }),
      getWarnings(),
    ])
    recentExams.value = examRes.data?.list || []
    warningStudents.value = warningRes.data || []

    // 模拟统计数据
    stats.classCount = 6
    stats.pendingGrade = recentExams.value.filter((e: any) => e.status === 'grading').length
    stats.studentCount = 238
    stats.warningCount = warningStudents.value.length
  } catch (e) {
    console.error('加载工作台数据失败', e)
  }
}

onMounted(() => {
  loadDashboard()
})
</script>

<style scoped>
.teacher-dashboard {
  padding: 4px;
}

.mt-20 {
  margin-top: 20px;
}

.stat-card {
  border-radius: 8px;
  border: none;
}

.stat-card-inner {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 4px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
</style>
