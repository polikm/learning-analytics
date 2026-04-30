<template>
  <div class="student-profile">
    <!-- Tab切换 -->
    <el-tabs v-model="activeTab" class="profile-tabs">
      <!-- 成绩分析Tab -->
      <el-tab-pane label="成绩分析" name="analysis">
        <!-- 各科雷达图 -->
        <el-card shadow="never" class="mb-16">
          <template #header><span class="card-title">各科成绩分析</span></template>
          <v-chart :option="radarOption" autoresize style="height: 280px" />
        </el-card>

        <!-- 趋势图 -->
        <el-card shadow="never">
          <template #header><span class="card-title">成绩趋势</span></template>
          <v-chart :option="trendOption" autoresize style="height: 260px" />
        </el-card>
      </el-tab-pane>

      <!-- 知识掌握Tab -->
      <el-tab-pane label="知识掌握" name="knowledge">
        <el-card shadow="never">
          <template #header>
            <div class="flex-between">
              <span class="card-title">知识点掌握度</span>
              <el-select v-model="selectedSubject" placeholder="选择学科" size="small" style="width: 120px" @change="loadKnowledge">
                <el-option v-for="s in subjectList" :key="s" :label="s" :value="s" />
              </el-select>
            </div>
          </template>

          <div v-if="knowledgeList.length" class="knowledge-list">
            <div v-for="item in knowledgeList" :key="item.knowledgePointId" class="knowledge-item">
              <div class="knowledge-header">
                <span class="knowledge-name">{{ item.knowledgePointName }}</span>
                <span class="knowledge-rate" :style="{ color: rateColor(item.correctRate) }">
                  {{ item.correctRate }}%
                </span>
              </div>
              <el-progress
                :percentage="item.correctRate"
                :stroke-width="10"
                :color="rateColor(item.correctRate)"
                :show-text="false"
              />
              <div class="knowledge-meta">
                <span>练习 {{ item.questionCount }} 次</span>
                <el-tag :type="masteryTagType(item.correctRate)" size="small">{{ masteryText(item.correctRate) }}</el-tag>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无知识点数据" :image-size="60" />
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, RadarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, RadarComponent } from 'echarts/components'
import { getStudentProfile, getKnowledgeMastery } from '@/api/profile'

use([CanvasRenderer, LineChart, RadarChart, GridComponent, TooltipComponent, RadarComponent])

const activeTab = ref('analysis')
const knowledgeList = ref<any[]>([])
const selectedSubject = ref('数学')
const subjectList = ref(['语文', '数学', '英语', '科学'])
const recentExams = ref<any[]>([])

const radarOption = computed(() => ({
  tooltip: {},
  radar: {
    indicator: [
      { name: '语文', max: 100 },
      { name: '数学', max: 100 },
      { name: '英语', max: 100 },
      { name: '科学', max: 100 },
      { name: '道法', max: 100 },
    ],
    shape: 'circle',
    splitNumber: 4,
    axisName: { color: '#606266', fontSize: 12 },
    splitLine: { lineStyle: { color: '#E4E7ED' } },
    splitArea: { areaStyle: { color: ['rgba(64,158,255,0.05)', 'rgba(64,158,255,0.1)'] } },
  },
  series: [{
    type: 'radar',
    data: [{
      value: [85, 92, 78, 88, 82],
      name: '我的成绩',
      areaStyle: { color: 'rgba(64,158,255,0.2)' },
      lineStyle: { color: '#409EFF', width: 2 },
      itemStyle: { color: '#409EFF' },
    }],
  }],
}))

const trendOption = computed(() => {
  const exams = recentExams.value.length ? recentExams.value : [
    { examName: '第一次月考', score: 82 },
    { examName: '期中考试', score: 88 },
    { examName: '第二次月考', score: 85 },
    { examName: '单元测试', score: 92 },
    { examName: '期末考试', score: 90 },
  ]
  return {
    grid: { top: 20, right: 16, bottom: 24, left: 40 },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: exams.map((e) => e.examName.length > 6 ? e.examName.slice(0, 6) + '..' : e.examName),
      axisLabel: { fontSize: 11, color: '#909399' },
      axisLine: { lineStyle: { color: '#E4E7ED' } },
    },
    yAxis: {
      type: 'value', min: 0, max: 100,
      axisLabel: { fontSize: 11, color: '#909399' },
      splitLine: { lineStyle: { color: '#F2F3F5' } },
    },
    series: [{
      type: 'line',
      data: exams.map((e) => e.score),
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { color: '#409EFF', width: 2 },
      itemStyle: { color: '#409EFF' },
      areaStyle: {
        color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [
          { offset: 0, color: 'rgba(64,158,255,0.25)' },
          { offset: 1, color: 'rgba(64,158,255,0.02)' },
        ] },
      },
    }],
  }
})

function rateColor(rate: number) {
  if (rate >= 80) return '#67C23A'
  if (rate >= 60) return '#409EFF'
  return '#F56C6C'
}

function masteryTagType(rate: number) {
  if (rate >= 80) return 'success'
  if (rate >= 60) return ''
  return 'danger'
}

function masteryText(rate: number) {
  if (rate >= 90) return '掌握'
  if (rate >= 80) return '良好'
  if (rate >= 60) return '一般'
  return '薄弱'
}

async function loadKnowledge() {
  try {
    const res = await getKnowledgeMastery(1)
    knowledgeList.value = (res.data || []).filter((k: any) => k.subject === selectedSubject.value)
  } catch (e) {
    console.error('加载知识点数据失败', e)
  }
}

async function loadData() {
  try {
    const res = await getStudentProfile(1)
    const profile = res.data
    if (profile) {
      recentExams.value = profile.recentExams || []
    }
    loadKnowledge()
  } catch (e) {
    console.error('加载学情数据失败', e)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.profile-tabs :deep(.el-tabs__header) {
  margin-bottom: 12px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.knowledge-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.knowledge-item {
  padding: 12px;
  background: #F8F9FB;
  border-radius: 8px;
}
.knowledge-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.knowledge-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}
.knowledge-rate {
  font-size: 16px;
  font-weight: 700;
}
.knowledge-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
</style>
