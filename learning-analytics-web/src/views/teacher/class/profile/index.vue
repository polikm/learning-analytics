<template>
  <div class="page-container class-profile-page">
    <!-- 班级信息卡片 -->
    <el-card shadow="hover" class="info-card">
      <div class="class-info">
        <div class="class-info-main">
          <div class="class-avatar">
            <el-icon :size="32"><School /></el-icon>
          </div>
          <div class="class-detail">
            <h2 class="class-name">{{ classInfo.className || '加载中...' }}</h2>
            <div class="class-meta">
              <el-tag size="small" type="info">{{ classInfo.gradeName }}</el-tag>
              <span class="meta-item">班主任：{{ classInfo.teacherName || '--' }}</span>
              <span class="meta-item">学生人数：<strong>{{ classInfo.studentCount || 0 }}</strong></span>
            </div>
          </div>
        </div>
        <div class="class-stats">
          <div class="class-stat-item">
            <div class="class-stat-value" style="color: #409EFF">{{ classInfo.averageScore?.toFixed(1) || '--' }}</div>
            <div class="class-stat-label">平均分</div>
          </div>
          <el-divider direction="vertical" />
          <div class="class-stat-item">
            <div class="class-stat-value" style="color: #67C23A">{{ classInfo.passRate ? (classInfo.passRate * 100).toFixed(1) + '%' : '--' }}</div>
            <div class="class-stat-label">及格率</div>
          </div>
          <el-divider direction="vertical" />
          <div class="class-stat-item">
            <div class="class-stat-value" style="color: #E6A23C">{{ classInfo.excellentRate ? (classInfo.excellentRate * 100).toFixed(1) + '%' : '--' }}</div>
            <div class="class-stat-label">优秀率</div>
          </div>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="mt-20">
      <!-- 学生列表 -->
      <el-col :span="14">
        <el-card shadow="hover">
          <template #header>
            <div class="flex-between">
              <span class="card-title">学生列表</span>
              <el-input
                v-model="studentKeyword"
                placeholder="搜索学生"
                clearable
                prefix-icon="Search"
                style="width: 200px"
                size="small"
              />
            </div>
          </template>
          <el-table :data="filteredStudents" stripe style="width: 100%" max-height="460">
            <el-table-column prop="name" label="姓名" width="100" />
            <el-table-column prop="studentNo" label="学号" width="120" />
            <el-table-column prop="gender" label="性别" width="70" align="center">
              <template #default="{ row }">{{ row.gender === 1 ? '男' : '女' }}</template>
            </el-table-column>
            <el-table-column prop="lastScore" label="最近成绩" width="100" align="center">
              <template #default="{ row }">
                <span v-if="row.lastScore" :style="{ color: row.lastScore >= 60 ? '#67C23A' : '#F56C6C', fontWeight: 600 }">
                  {{ row.lastScore }}
                </span>
                <span v-else style="color: #C0C4CC">--</span>
              </template>
            </el-table-column>
            <el-table-column prop="lastExam" label="最近测评" min-width="140" show-overflow-tooltip />
            <el-table-column prop="warning" label="预警" width="90" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.warning" type="danger" size="small" effect="dark">
                  <el-icon><Warning /></el-icon> 预警
                </el-tag>
                <el-tag v-else type="success" size="small" effect="plain">正常</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template #default="{ row }">
                <el-button text type="primary" size="small" @click="handleViewStudent(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 班级成绩概览雷达图 -->
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">班级成绩概览</span>
          </template>
          <v-chart :option="radarOption" style="height: 400px" autoresize />
        </el-card>

        <!-- 预警学生列表 -->
        <el-card shadow="hover" class="mt-16">
          <template #header>
            <div class="flex-between">
              <span class="card-title">预警学生</span>
              <el-tag type="danger" size="small">{{ warningStudents.length }} 人</el-tag>
            </div>
          </template>
          <div v-if="warningStudents.length" class="warning-list">
            <div v-for="ws in warningStudents" :key="ws.studentId" class="warning-item">
              <el-icon color="#F56C6C"><Warning /></el-icon>
              <span class="warning-name">{{ ws.name }}</span>
              <el-tag :type="ws.type === 'score_decline' ? 'warning' : 'danger'" size="small">{{ ws.detail }}</el-tag>
            </div>
          </div>
          <el-empty v-else description="暂无预警学生" :image-size="50" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { RadarChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, RadarComponent } from 'echarts/components'
import { getClassOverview } from '@/api/profile'
import type { ClassOverview } from '@/api/profile'

use([CanvasRenderer, RadarChart, TitleComponent, TooltipComponent, RadarComponent])

const route = useRoute()
const classId = computed(() => Number(route.params.classId))
const studentKeyword = ref('')

const classInfo = reactive<Partial<ClassOverview>>({})

const students = ref<any[]>([])
const warningStudents = ref<any[]>([])

const filteredStudents = computed(() => {
  if (!studentKeyword.value) return students.value
  const kw = studentKeyword.value.toLowerCase()
  return students.value.filter(s =>
    s.name.toLowerCase().includes(kw) || s.studentNo.toLowerCase().includes(kw)
  )
})

const radarOption = computed(() => {
  const subjects = classInfo.subjectAverages || []
  const indicators = subjects.map(s => ({ name: s.subject, max: 100 }))
  const values = subjects.map(s => s.average)
  return {
    tooltip: {},
    radar: {
      indicator: indicators.length ? indicators : [
        { name: '语文', max: 100 },
        { name: '数学', max: 100 },
        { name: '英语', max: 100 },
        { name: '物理', max: 100 },
        { name: '化学', max: 100 },
      ],
      shape: 'polygon',
      splitNumber: 5,
      axisName: { color: '#606266', fontSize: 12 },
      splitArea: { areaStyle: { color: ['rgba(64, 158, 255, 0.05)', 'rgba(64, 158, 255, 0.1)'] } },
    },
    series: [{
      type: 'radar',
      data: [{
        value: values.length ? values : [0, 0, 0, 0, 0],
        name: '班级平均分',
        areaStyle: { color: 'rgba(64, 158, 255, 0.2)' },
        lineStyle: { color: '#409EFF', width: 2 },
        itemStyle: { color: '#409EFF' },
      }],
    }],
  }
})

function handleViewStudent(row: any) {
  // Navigate to student profile if needed
}

async function loadData() {
  try {
    const res = await getClassOverview(classId.value)
    const data = res.data
    Object.assign(classInfo, data)

    // Build student list from overview data
    students.value = (data?.topStudents || []).map(s => ({
      id: s.studentId,
      name: s.name,
      studentNo: '--',
      gender: 1,
      lastScore: s.score,
      lastExam: '最近测评',
      warning: false,
    }))

    warningStudents.value = (data?.warningStudents || []).map(w => ({
      studentId: w.studentId,
      name: w.name,
      type: w.type,
      detail: w.detail,
    }))
  } catch (e) {
    console.error('加载班级学情失败', e)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.class-profile-page {
  padding: 4px;
}

.mt-16 {
  margin-top: 16px;
}

.mt-20 {
  margin-top: 20px;
}

.info-card {
  border-radius: 8px;
  border: none;
}

.class-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.class-info-main {
  display: flex;
  align-items: center;
  gap: 16px;
}

.class-avatar {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: linear-gradient(135deg, #409EFF, #66B1FF);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.class-name {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
}

.class-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: #909399;
}

.meta-item {
  margin-left: 4px;
}

.class-stats {
  display: flex;
  align-items: center;
  gap: 24px;
}

.class-stat-item {
  text-align: center;
}

.class-stat-value {
  font-size: 22px;
  font-weight: 700;
}

.class-stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
}

.warning-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.warning-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  background: #FEF0F0;
  border-radius: 4px;
  font-size: 13px;
}

.warning-name {
  font-weight: 500;
  min-width: 60px;
}
</style>
