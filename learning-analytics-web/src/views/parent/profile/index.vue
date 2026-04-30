<template>
  <div class="parent-profile">
    <!-- 学生信息卡片 -->
    <el-card shadow="never" class="student-info-card mb-16">
      <div class="student-info">
        <div class="avatar-placeholder">
          <el-icon :size="36"><UserFilled /></el-icon>
        </div>
        <div class="student-detail">
          <div class="student-name">{{ studentInfo.name }}</div>
          <div class="student-meta">
            <el-tag size="small" type="primary">{{ studentInfo.className }}</el-tag>
            <span class="school-name">{{ studentInfo.schoolName }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- Tab切换 -->
    <el-tabs v-model="activeTab" class="profile-tabs">
      <!-- 学业表现Tab -->
      <el-tab-pane label="学业表现" name="academic">
        <!-- 各科成绩雷达图 -->
        <el-card shadow="never" class="mb-16">
          <template #header><span class="card-title">各科成绩雷达图</span></template>
          <v-chart :option="radarOption" autoresize style="height: 280px" />
        </el-card>

        <!-- 成绩趋势折线图 -->
        <el-card shadow="never" class="mb-16">
          <template #header><span class="card-title">成绩趋势</span></template>
          <v-chart :option="trendOption" autoresize style="height: 260px" />
        </el-card>

        <!-- 最近测评列表 -->
        <el-card shadow="never">
          <template #header><span class="card-title">最近测评</span></template>
          <el-table :data="recentExams" stripe style="width: 100%" size="small">
            <el-table-column prop="examName" label="测评名称" min-width="120" show-overflow-tooltip />
            <el-table-column prop="subject" label="学科" width="80" />
            <el-table-column prop="score" label="分数" width="80" align="center">
              <template #default="{ row }">
                <span :style="{ color: row.score >= 80 ? '#67C23A' : row.score >= 60 ? '#409EFF' : '#F56C6C', fontWeight: 600 }">
                  {{ row.score }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="totalScore" label="满分" width="60" align="center" />
            <el-table-column prop="rank" label="排名" width="60" align="center" />
            <el-table-column prop="date" label="时间" width="100" />
          </el-table>
          <el-empty v-if="!recentExams.length" description="暂无测评记录" :image-size="60" />
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
          <el-table :data="knowledgeList" stripe style="width: 100%" size="small">
            <el-table-column prop="knowledgePointName" label="知识点" min-width="140" show-overflow-tooltip />
            <el-table-column prop="correctRate" label="正确率" width="120" align="center">
              <template #default="{ row }">
                <span :style="{ color: row.correctRate >= 80 ? '#67C23A' : row.correctRate >= 60 ? '#409EFF' : '#F56C6C', fontWeight: 600 }">
                  {{ row.correctRate }}%
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="masteryLevel" label="掌握等级" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="masteryTagType(row.correctRate)" size="small">{{ masteryText(row.correctRate) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="questionCount" label="练习次数" width="90" align="center" />
          </el-table>
          <el-empty v-if="!knowledgeList.length" description="暂无知识点数据" :image-size="60" />
        </el-card>
      </el-tab-pane>

      <!-- 学习行为Tab -->
      <el-tab-pane label="学习行为" name="behavior">
        <el-card shadow="never" class="mb-16">
          <template #header><span class="card-title">学习时长统计（最近7天）</span></template>
          <v-chart :option="behaviorBarOption" autoresize style="height: 260px" />
        </el-card>

        <el-card shadow="never">
          <template #header><span class="card-title">最近学习活动</span></template>
          <el-timeline>
            <el-timeline-item
              v-for="(item, idx) in activityList"
              :key="idx"
              :timestamp="item.time"
              placement="top"
              :color="item.color"
            >
              <div class="activity-item">
                <span class="activity-title">{{ item.title }}</span>
                <span class="activity-desc">{{ item.desc }}</span>
              </div>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-if="!activityList.length" description="暂无学习活动" :image-size="60" />
        </el-card>
      </el-tab-pane>

      <!-- 综合素质Tab -->
      <el-tab-pane label="综合素质" name="quality">
        <el-card shadow="never">
          <template #header><span class="card-title">证书列表</span></template>
          <el-timeline>
            <el-timeline-item
              v-for="cert in certificateList"
              :key="cert.id"
              :timestamp="cert.issueDate"
              placement="top"
              color="#67C23A"
            >
              <div class="cert-item">
                <div class="cert-header">
                  <span class="cert-name">{{ cert.name }}</span>
                  <el-tag :type="certLevelType(cert.level)" size="small">{{ cert.level }}</el-tag>
                </div>
                <div class="cert-desc">{{ cert.issuingAuthority }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-if="!certificateList.length" description="暂无证书" :image-size="60" />
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
import { LineChart, BarChart, RadarChart } from 'echarts/charts'
import {
  GridComponent, TooltipComponent, MarkLineComponent,
  RadarComponent, LegendComponent,
} from 'echarts/components'
import { getStudentProfile, getKnowledgeMastery } from '@/api/profile'
import { getCertificateList } from '@/api/certificate'

use([CanvasRenderer, LineChart, BarChart, RadarChart, GridComponent, TooltipComponent, MarkLineComponent, RadarComponent, LegendComponent])

const activeTab = ref('academic')

const studentInfo = ref({ name: '张小明', className: '三年级2班', schoolName: '阳光实验小学' })
const recentExams = ref<any[]>([])
const knowledgeList = ref<any[]>([])
const certificateList = ref<any[]>([])
const selectedSubject = ref('数学')
const subjectList = ref(['语文', '数学', '英语', '科学'])

const activityList = ref([
  { time: '2026-04-29 14:30', title: '完成数学单元测试', desc: '得分 92 分，用时 45 分钟', color: '#67C23A' },
  { time: '2026-04-28 16:00', title: '英语词汇练习', desc: '练习 30 个单词，正确率 85%', color: '#409EFF' },
  { time: '2026-04-27 10:00', title: '语文阅读理解', desc: '完成 3 篇阅读理解，用时 30 分钟', color: '#409EFF' },
  { time: '2026-04-26 15:00', title: '科学实验报告', desc: '提交实验报告，获得教师评语', color: '#E6A23C' },
  { time: '2026-04-25 09:00', title: '数学口算练习', desc: '完成 50 道口算题，正确率 96%', color: '#67C23A' },
])

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
      name: '成绩',
      areaStyle: { color: 'rgba(64,158,255,0.2)' },
      lineStyle: { color: '#409EFF', width: 2 },
      itemStyle: { color: '#409EFF' },
    }],
  }],
}))

const trendOption = computed(() => {
  const exams = recentExams.value.length ? recentExams.value : [
    { examName: '第一次月考', score: 82, date: '03-01' },
    { examName: '期中考试', score: 88, date: '04-10' },
    { examName: '第二次月考', score: 85, date: '05-15' },
    { examName: '单元测试', score: 92, date: '06-08' },
    { examName: '期末考试', score: 90, date: '07-05' },
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

const behaviorBarOption = computed(() => ({
  grid: { top: 20, right: 16, bottom: 24, left: 40 },
  tooltip: { trigger: 'axis', formatter: '{b}<br/>学习时长: {c} 分钟' },
  xAxis: {
    type: 'category',
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    axisLabel: { fontSize: 11, color: '#909399' },
    axisLine: { lineStyle: { color: '#E4E7ED' } },
  },
  yAxis: {
    type: 'value',
    axisLabel: { fontSize: 11, color: '#909399', formatter: '{value}分' },
    splitLine: { lineStyle: { color: '#F2F3F5' } },
  },
  series: [{
    type: 'bar',
    data: [45, 60, 30, 75, 50, 90, 40],
    barWidth: 24,
    itemStyle: {
      color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [
        { offset: 0, color: '#409EFF' },
        { offset: 1, color: '#66B1FF' },
      ] },
      borderRadius: [4, 4, 0, 0],
    },
  }],
}))

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

function certLevelType(level: string) {
  if (level === '一等奖' || level === '国家级') return 'danger'
  if (level === '二等奖' || level === '省级') return 'warning'
  return 'success'
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
    const [profileRes, certRes] = await Promise.all([
      getStudentProfile(1),
      getCertificateList({ studentId: 1, page: 1, pageSize: 20 }),
    ])
    const profile = profileRes.data
    if (profile) {
      studentInfo.value = {
        name: profile.studentName || '张小明',
        className: profile.className || '三年级2班',
        schoolName: '阳光实验小学',
      }
      recentExams.value = profile.recentExams || []
    }
    certificateList.value = certRes.data?.list || []
    loadKnowledge()
  } catch (e) {
    console.error('加载学情档案失败', e)
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
.student-detail { flex: 1; }
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

.profile-tabs :deep(.el-tabs__header) {
  margin-bottom: 12px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.activity-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.activity-title {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}
.activity-desc {
  font-size: 12px;
  color: #909399;
}

.cert-item { padding: 2px 0; }
.cert-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.cert-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}
.cert-desc {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}
</style>
