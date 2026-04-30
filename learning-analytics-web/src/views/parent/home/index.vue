<template>
  <div class="parent-home">
    <!-- 子女信息卡片 -->
    <el-card shadow="never" class="student-info-card mb-16">
      <div class="student-info">
        <div class="avatar-placeholder">
          <el-icon :size="36"><UserFilled /></el-icon>
        </div>
        <div class="student-detail">
          <div class="student-name">{{ childInfo.name }}</div>
          <div class="student-meta">
            <el-tag size="small" type="primary">{{ childInfo.className }}</el-tag>
            <span class="school-name">{{ childInfo.schoolName }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 快捷入口 -->
    <el-row :gutter="12" class="mb-16">
      <el-col :span="12">
        <el-card shadow="hover" class="quick-entry" @click="$router.push('/parent/profile')">
          <el-icon :size="28" color="#409EFF"><DataAnalysis /></el-icon>
          <span>查看学情档案</span>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="quick-entry" @click="$router.push('/parent/reports')">
          <el-icon :size="28" color="#67C23A"><Document /></el-icon>
          <span>查看报告</span>
        </el-card>
      </el-col>
    </el-row>

    <!-- 成绩概览区域 -->
    <el-card shadow="never" class="mb-16">
      <template #header>
        <div class="flex-between">
          <span class="card-title">成绩概览</span>
          <el-button text type="primary" size="small" @click="$router.push('/parent/profile')">查看详情</el-button>
        </div>
      </template>

      <!-- 最近测评成绩列表 -->
      <div class="section-label">最近测评成绩</div>
      <el-timeline v-if="recentScores.length">
        <el-timeline-item
          v-for="item in recentScores"
          :key="item.examId"
          :timestamp="item.date"
          placement="top"
          :color="item.score >= 80 ? '#67C23A' : item.score >= 60 ? '#409EFF' : '#F56C6C'"
        >
          <div class="timeline-content">
            <div class="timeline-header">
              <span class="exam-name">{{ item.examName }}</span>
              <span class="exam-score" :class="scoreClass(item.score)">{{ item.score }}分</span>
            </div>
            <div class="timeline-sub">{{ item.subject }}</div>
          </div>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无测评成绩" :image-size="60" />

      <!-- 成绩趋势迷你折线图 -->
      <div v-if="recentScores.length >= 2" class="section-label mt-16">成绩趋势（最近5次）</div>
      <div v-if="recentScores.length >= 2" class="mini-chart">
        <v-chart :option="trendChartOption" autoresize style="height: 180px" />
      </div>
    </el-card>

    <!-- 预警提示区域 -->
    <el-card v-if="warnings.length" shadow="never" class="mb-16">
      <template #header>
        <div class="flex-between">
          <span class="card-title">预警提示</span>
          <el-tag type="danger" size="small">{{ warnings.length }} 条</el-tag>
        </div>
      </template>
      <el-alert
        v-for="item in warnings"
        :key="item.id"
        :title="item.message"
        :type="warningAlertType(item.level)"
        :description="item.type === 'score_decline' ? '成绩出现下滑趋势，请关注' : item.type === 'fail' ? '最近测评未达到及格线' : '学习活跃度下降，请关注'"
        show-icon
        :closable="false"
        class="mb-8"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, MarkLineComponent } from 'echarts/components'
import { getStudentProfile, getWarnings } from '@/api/profile'

use([CanvasRenderer, LineChart, GridComponent, TooltipComponent, MarkLineComponent])

const childInfo = ref({
  name: '张小明',
  className: '三年级2班',
  schoolName: '阳光实验小学',
})

const recentScores = ref<any[]>([])
const warnings = ref<any[]>([])

const trendChartOption = computed(() => {
  const last5 = recentScores.value.slice(-5)
  return {
    grid: { top: 20, right: 16, bottom: 24, left: 40 },
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>分数: {c}',
    },
    xAxis: {
      type: 'category',
      data: last5.map((s) => s.examName.length > 6 ? s.examName.slice(0, 6) + '...' : s.examName),
      axisLabel: { fontSize: 11, color: '#909399' },
      axisLine: { lineStyle: { color: '#E4E7ED' } },
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: { fontSize: 11, color: '#909399' },
      splitLine: { lineStyle: { color: '#F2F3F5' } },
    },
    series: [
      {
        type: 'line',
        data: last5.map((s) => s.score),
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { color: '#409EFF', width: 2 },
        itemStyle: { color: '#409EFF' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(64,158,255,0.25)' },
              { offset: 1, color: 'rgba(64,158,255,0.02)' },
            ],
          },
        },
        markLine: {
          silent: true,
          lineStyle: { color: '#F56C6C', type: 'dashed' },
          data: [{ yAxis: 60, label: { formatter: '及格线', fontSize: 10 } }],
        },
      },
    ],
  }
})

function scoreClass(score: number) {
  if (score >= 80) return 'score-good'
  if (score >= 60) return 'score-normal'
  return 'score-bad'
}

function warningAlertType(level: string) {
  const map: Record<string, any> = { high: 'error', medium: 'warning', low: 'info' }
  return map[level] || 'info'
}

async function loadData() {
  try {
    const [profileRes, warningRes] = await Promise.all([
      getStudentProfile(1),
      getWarnings(1),
    ])
    const profile = profileRes.data
    if (profile) {
      childInfo.value = {
        name: profile.studentName || '张小明',
        className: profile.className || '三年级2班',
        schoolName: '阳光实验小学',
      }
      recentScores.value = profile.recentExams || []
    }
    warnings.value = warningRes.data || []
  } catch (e) {
    console.error('加载家长首页数据失败', e)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.student-info-card {
  background: linear-gradient(135deg, #409EFF 0%, #66B1FF 100%);
  border: none;
}
.student-info-card :deep(.el-card__body) {
  padding: 16px;
}
.student-info {
  display: flex;
  align-items: center;
  gap: 14px;
}
.avatar-placeholder {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}
.student-detail {
  flex: 1;
}
.student-name {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 6px;
}
.student-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}
.student-meta .el-tag {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: #fff;
}
.school-name {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.85);
}

.quick-entry {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 16px 0;
  transition: transform 0.2s;
}
.quick-entry:hover {
  transform: translateY(-2px);
}
.quick-entry span {
  font-size: 13px;
  color: #606266;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.section-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
  padding-left: 8px;
  border-left: 3px solid #409EFF;
}

.timeline-content {
  padding: 4px 0;
}
.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.exam-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}
.exam-score {
  font-size: 16px;
  font-weight: 700;
}
.score-good { color: #67C23A; }
.score-normal { color: #409EFF; }
.score-bad { color: #F56C6C; }
.timeline-sub {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.mini-chart {
  width: 100%;
}
</style>
