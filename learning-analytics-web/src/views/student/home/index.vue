<template>
  <div class="student-home">
    <!-- 顶部欢迎语+头像 -->
    <el-card shadow="never" class="welcome-card mb-16">
      <div class="welcome-section">
        <div class="avatar-placeholder">
          <el-icon :size="32"><UserFilled /></el-icon>
        </div>
        <div class="welcome-text">
          <h3>{{ greetingText }}，{{ studentName }}</h3>
          <p>今天也要加油学习哦</p>
        </div>
      </div>
    </el-card>

    <!-- 待办测评卡片列表 -->
    <el-card shadow="never" class="mb-16">
      <template #header>
        <div class="flex-between">
          <span class="card-title">待完成测评</span>
          <el-tag v-if="pendingExams.length" type="warning" size="small">{{ pendingExams.length }} 项</el-tag>
        </div>
      </template>

      <div v-if="pendingExams.length" class="exam-list">
        <div v-for="exam in pendingExams" :key="exam.id" class="exam-card">
          <div class="exam-info">
            <div class="exam-name">{{ exam.title }}</div>
            <div class="exam-meta">
              <el-tag size="small" type="primary">{{ exam.subject }}</el-tag>
              <span class="exam-time">{{ exam.startTime }}</span>
            </div>
          </div>
          <el-button type="primary" size="small" @click="goTaking(exam.id)">去答题</el-button>
        </div>
      </div>
      <el-empty v-else description="暂无待完成的测评" :image-size="60" />
    </el-card>

    <!-- 最近成绩概览 -->
    <el-row :gutter="12" class="mb-16">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #409EFF">{{ scoreStats.averageScore }}</div>
          <div class="stat-label">平均分</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #67C23A">{{ scoreStats.maxScore }}</div>
          <div class="stat-label">最高分</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #E6A23C">{{ scoreStats.totalExams }}</div>
          <div class="stat-label">测评次数</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 学习建议区域 -->
    <el-card shadow="never">
      <template #header>
        <div class="flex-between">
          <span class="card-title">学习建议</span>
          <el-icon color="#409EFF"><MagicStick /></el-icon>
        </div>
      </template>
      <div class="suggestion-list">
        <div v-for="(item, idx) in suggestions" :key="idx" class="suggestion-item">
          <div class="suggestion-icon" :style="{ background: item.bgColor }">
            <el-icon :size="16" :color="item.iconColor"><component :is="item.icon" /></el-icon>
          </div>
          <div class="suggestion-text">{{ item.text }}</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getStudentProfile } from '@/api/profile'
import { getExamList } from '@/api/exam'

const router = useRouter()
const studentName = ref('同学')
const pendingExams = ref<any[]>([])
const scoreStats = ref({ averageScore: '--', maxScore: '--', totalExams: 0 })

const greetingText = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return '上午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const suggestions = ref([
  { text: '数学知识点掌握良好，继续保持！可以尝试挑战更高难度的题目。', icon: 'TrendCharts', iconColor: '#67C23A', bgColor: 'rgba(103,194,58,0.1)' },
  { text: '英语词汇量需要加强，建议每天坚持背诵10个新单词。', icon: 'Reading', iconColor: '#409EFF', bgColor: 'rgba(64,158,255,0.1)' },
  { text: '科学实验部分表现优秀，动手能力强，值得表扬！', icon: 'Star', iconColor: '#E6A23C', bgColor: 'rgba(230,162,60,0.1)' },
])

function goTaking(examId: number) {
  router.push(`/student/exam/${examId}/taking`)
}

async function loadData() {
  try {
    const [profileRes, examRes] = await Promise.all([
      getStudentProfile(1),
      getExamList({ page: 1, pageSize: 10, status: 'in_progress' }),
    ])
    const profile = profileRes.data
    if (profile) {
      studentName.value = profile.studentName || '同学'
      scoreStats.value = {
        averageScore: profile.averageScore || '--',
        maxScore: profile.recentExams?.length
          ? Math.max(...profile.recentExams.map((e: any) => e.score))
          : '--',
        totalExams: profile.totalExams || 0,
      }
    }
    pendingExams.value = examRes.data?.list || []
  } catch (e) {
    console.error('加载学生首页数据失败', e)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.welcome-card {
  background: linear-gradient(135deg, #409EFF 0%, #66B1FF 100%);
  border: none;
}
.welcome-card :deep(.el-card__body) {
  padding: 16px;
}
.welcome-section {
  display: flex;
  align-items: center;
  gap: 14px;
}
.avatar-placeholder {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}
.welcome-text h3 {
  margin-bottom: 4px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}
.welcome-text p {
  color: rgba(255, 255, 255, 0.85);
  font-size: 13px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.exam-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.exam-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: #F8F9FB;
  border-radius: 8px;
}
.exam-info { flex: 1; }
.exam-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
  margin-bottom: 4px;
}
.exam-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}
.exam-time {
  font-size: 12px;
  color: #909399;
}

.stat-card {
  text-align: center;
  border-radius: 8px;
}
.stat-card :deep(.el-card__body) {
  padding: 16px 12px;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}
.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.suggestion-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 12px;
  background: #F8F9FB;
  border-radius: 8px;
}
.suggestion-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 1px;
}
.suggestion-text {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
}
</style>
