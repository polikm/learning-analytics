<template>
  <div class="dashboard-screen">
    <!-- 顶部标题栏 -->
    <div class="screen-header">
      <h1>阳光实验小学 学情数据大屏</h1>
      <div class="header-time">{{ currentTime }}</div>
    </div>

    <div class="screen-body">
      <!-- 第一行：4个核心指标 -->
      <div class="kpi-row">
        <div class="kpi-card">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #409EFF, #66B1FF)">
            <el-icon :size="24"><User /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value">{{ kpiData.studentCount }}</div>
            <div class="kpi-label">学生总数</div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #67C23A, #95D475)">
            <el-icon :size="24"><Avatar /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value">{{ kpiData.teacherCount }}</div>
            <div class="kpi-label">教师总数</div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #E6A23C, #F0C78A)">
            <el-icon :size="24"><EditPen /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value">{{ kpiData.examCount }}</div>
            <div class="kpi-label">测评总次数</div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #F56C6C, #F89898)">
            <el-icon :size="24"><TrendCharts /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value">{{ kpiData.averageScore }}</div>
            <div class="kpi-label">平均分</div>
          </div>
        </div>
      </div>

      <!-- 第二行：柱状图 + 雷达图 -->
      <div class="chart-row">
        <div class="chart-panel">
          <div class="panel-title">各年级成绩对比</div>
          <div class="panel-body">
            <v-chart :option="gradeBarOption" autoresize style="height: 100%" />
          </div>
        </div>
        <div class="chart-panel">
          <div class="panel-title">学科能力雷达图</div>
          <div class="panel-body">
            <v-chart :option="subjectRadarOption" autoresize style="height: 100%" />
          </div>
        </div>
      </div>

      <!-- 第三行：趋势图 + 预警列表 -->
      <div class="chart-row">
        <div class="chart-panel">
          <div class="panel-title">成绩趋势</div>
          <div class="panel-body">
            <v-chart :option="trendLineOption" autoresize style="height: 100%" />
          </div>
        </div>
        <div class="chart-panel">
          <div class="panel-title">学情预警</div>
          <div class="panel-body">
            <el-table :data="warningList" style="width: 100%" size="small" class="dark-table">
              <el-table-column prop="studentName" label="学生" width="80" />
              <el-table-column prop="className" label="班级" width="100" />
              <el-table-column prop="type" label="类型" min-width="100">
                <template #default="{ row }">
                  <el-tag :type="warningTag(row.type)" size="small" effect="dark">{{ warningText(row.type) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="message" label="详情" min-width="140" show-overflow-tooltip />
            </el-table>
          </div>
        </div>
      </div>

      <!-- 第四行：饼图 -->
      <div class="chart-row">
        <div class="chart-panel">
          <div class="panel-title">证书获取统计</div>
          <div class="panel-body">
            <v-chart :option="certPieOption" autoresize style="height: 100%" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, LineChart, RadarChart, PieChart } from 'echarts/charts'
import {
  GridComponent, TooltipComponent, LegendComponent,
  RadarComponent, MarkLineComponent,
} from 'echarts/components'
import { getWarnings } from '@/api/profile'
import dayjs from 'dayjs'

use([CanvasRenderer, BarChart, LineChart, RadarChart, PieChart, GridComponent, TooltipComponent, LegendComponent, RadarComponent, MarkLineComponent])

const currentTime = ref(dayjs().format('YYYY-MM-DD HH:mm:ss'))
let timeTimer: ReturnType<typeof setInterval> | null = null

const kpiData = ref({
  studentCount: 1856,
  teacherCount: 128,
  examCount: 342,
  averageScore: 82.5,
})

const warningList = ref<any[]>([])

const gradeBarOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['语文', '数学', '英语'], textStyle: { color: '#B0B8C8' }, top: 4 },
  grid: { top: 36, right: 16, bottom: 24, left: 48 },
  xAxis: {
    type: 'category',
    data: ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级'],
    axisLabel: { color: '#B0B8C8', fontSize: 11 },
    axisLine: { lineStyle: { color: '#1e3a5f' } },
  },
  yAxis: {
    type: 'value', min: 0, max: 100,
    axisLabel: { color: '#B0B8C8', fontSize: 11 },
    splitLine: { lineStyle: { color: 'rgba(30,58,95,0.5)' } },
  },
  series: [
    { name: '语文', type: 'bar', barWidth: 16, data: [85, 82, 88, 79, 84, 81], itemStyle: { color: '#409EFF', borderRadius: [3, 3, 0, 0] } },
    { name: '数学', type: 'bar', barWidth: 16, data: [88, 85, 92, 83, 87, 84], itemStyle: { color: '#67C23A', borderRadius: [3, 3, 0, 0] } },
    { name: '英语', type: 'bar', barWidth: 16, data: [80, 78, 85, 76, 82, 79], itemStyle: { color: '#E6A23C', borderRadius: [3, 3, 0, 0] } },
  ],
}))

const subjectRadarOption = computed(() => ({
  tooltip: {},
  radar: {
    indicator: [
      { name: '语文', max: 100 },
      { name: '数学', max: 100 },
      { name: '英语', max: 100 },
      { name: '科学', max: 100 },
      { name: '道法', max: 100 },
      { name: '体育', max: 100 },
    ],
    shape: 'circle',
    splitNumber: 4,
    axisName: { color: '#B0B8C8', fontSize: 11 },
    splitLine: { lineStyle: { color: 'rgba(30,58,95,0.5)' } },
    splitArea: { areaStyle: { color: ['rgba(64,158,255,0.03)', 'rgba(64,158,255,0.06)'] } },
    axisLine: { lineStyle: { color: 'rgba(30,58,95,0.5)' } },
  },
  series: [{
    type: 'radar',
    data: [
      {
        value: [85, 88, 80, 82, 78, 90],
        name: '全校平均',
        areaStyle: { color: 'rgba(64,158,255,0.2)' },
        lineStyle: { color: '#409EFF', width: 2 },
        itemStyle: { color: '#409EFF' },
      },
    ],
  }],
}))

const trendLineOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['全校平均', '最高分', '最低分'], textStyle: { color: '#B0B8C8' }, top: 4 },
  grid: { top: 36, right: 16, bottom: 24, left: 48 },
  xAxis: {
    type: 'category',
    data: ['第一次月考', '期中考试', '第二次月考', '期末考试'],
    axisLabel: { color: '#B0B8C8', fontSize: 11 },
    axisLine: { lineStyle: { color: '#1e3a5f' } },
  },
  yAxis: {
    type: 'value', min: 0, max: 100,
    axisLabel: { color: '#B0B8C8', fontSize: 11 },
    splitLine: { lineStyle: { color: 'rgba(30,58,95,0.5)' } },
  },
  series: [
    { name: '全校平均', type: 'line', data: [80, 82, 81, 83], smooth: true, lineStyle: { color: '#409EFF' }, itemStyle: { color: '#409EFF' } },
    { name: '最高分', type: 'line', data: [98, 100, 99, 100], smooth: true, lineStyle: { color: '#67C23A' }, itemStyle: { color: '#67C23A' } },
    { name: '最低分', type: 'line', data: [25, 30, 22, 28], smooth: true, lineStyle: { color: '#F56C6C' }, itemStyle: { color: '#F56C6C' } },
  ],
}))

const certPieOption = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
  legend: { orient: 'vertical', right: 16, top: 'center', textStyle: { color: '#B0B8C8', fontSize: 12 } },
  series: [{
    type: 'pie',
    radius: ['40%', '65%'],
    center: ['35%', '50%'],
    label: { show: false },
    data: [
      { value: 335, name: '学科竞赛', itemStyle: { color: '#409EFF' } },
      { value: 234, name: '科技创新', itemStyle: { color: '#67C23A' } },
      { value: 185, name: '艺术特长', itemStyle: { color: '#E6A23C' } },
      { value: 148, name: '体育竞技', itemStyle: { color: '#F56C6C' } },
      { value: 98, name: '社会实践', itemStyle: { color: '#909399' } },
    ],
  }],
}))

function warningTag(type: string) {
  const map: Record<string, string> = { score_decline: 'warning', absence: 'danger', fail: 'danger', inactivity: 'info' }
  return map[type] || 'info'
}

function warningText(type: string) {
  const map: Record<string, string> = { score_decline: '成绩下滑', absence: '出勤异常', fail: '不及格', inactivity: '不活跃' }
  return map[type] || type
}

async function loadData() {
  try {
    const res = await getWarnings()
    warningList.value = (res.data || []).slice(0, 8)
  } catch (e) {
    console.error('加载大屏数据失败', e)
  }
}

onMounted(() => {
  loadData()
  timeTimer = setInterval(() => {
    currentTime.value = dayjs().format('YYYY-MM-DD HH:mm:ss')
  }, 1000)
})

onBeforeUnmount(() => {
  if (timeTimer) clearInterval(timeTimer)
})
</script>

<style scoped lang="scss">
.dashboard-screen {
  width: 100vw;
  height: 100vh;
  background: #0a1628;
  color: #fff;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.screen-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(90deg, #0a1628, #112240, #0a1628);
  border-bottom: 1px solid #1e3a5f;
  position: relative;
  flex-shrink: 0;

  h1 {
    font-size: 26px;
    font-weight: 700;
    background: linear-gradient(90deg, #409EFF, #67C23A);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    letter-spacing: 2px;
  }

  .header-time {
    position: absolute;
    right: 24px;
    font-size: 14px;
    color: #B0B8C8;
    font-variant-numeric: tabular-nums;
  }
}

.screen-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 12px;
  overflow: hidden;
}

/* KPI指标行 */
.kpi-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  flex-shrink: 0;
}

.kpi-card {
  background: #112240;
  border: 1px solid #1e3a5f;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 20px;
}

.kpi-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.kpi-info {
  flex: 1;
}

.kpi-value {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  line-height: 1.2;
  font-variant-numeric: tabular-nums;
}

.kpi-label {
  font-size: 13px;
  color: #B0B8C8;
  margin-top: 2px;
}

/* 图表行 */
.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  flex: 1;
  min-height: 0;
}

.chart-panel {
  background: #112240;
  border: 1px solid #1e3a5f;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-title {
  padding: 10px 16px;
  font-size: 14px;
  font-weight: 600;
  border-bottom: 1px solid #1e3a5f;
  color: #409EFF;
  flex-shrink: 0;
}

.panel-body {
  flex: 1;
  padding: 8px;
  min-height: 0;
  overflow: auto;
}

/* 深色主题表格 */
:deep(.dark-table) {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: rgba(30, 58, 95, 0.5);
  --el-table-row-hover-bg-color: rgba(64, 158, 255, 0.08);
  --el-table-border-color: #1e3a5f;
  --el-table-text-color: #B0B8C8;
  --el-table-header-text-color: #B0B8C8;
  color: #B0B8C8;
}
</style>
