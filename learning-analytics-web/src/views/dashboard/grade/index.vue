<template>
  <div class="dashboard-screen">
    <!-- 顶部标题栏 -->
    <div class="screen-header">
      <h1>{{ gradeName }} 学情数据大屏</h1>
      <div class="header-time">{{ currentTime }}</div>
    </div>

    <div class="screen-body">
      <!-- 年级总览指标 -->
      <div class="kpi-row">
        <div class="kpi-card">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #409EFF, #66B1FF)">
            <el-icon :size="24"><School /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value">{{ kpiData.classCount }}</div>
            <div class="kpi-label">班级数</div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #67C23A, #95D475)">
            <el-icon :size="24"><User /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value">{{ kpiData.studentCount }}</div>
            <div class="kpi-label">学生数</div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #E6A23C, #F0C78A)">
            <el-icon :size="24"><TrendCharts /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value">{{ kpiData.averageScore }}</div>
            <div class="kpi-label">平均分</div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #F56C6C, #F89898)">
            <el-icon :size="24"><DataLine /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value">{{ kpiData.passRate }}%</div>
            <div class="kpi-label">及格率</div>
          </div>
        </div>
      </div>

      <!-- 班级成绩对比 + 知识点热力图 -->
      <div class="chart-row">
        <div class="chart-panel">
          <div class="panel-title">班级成绩对比</div>
          <div class="panel-body">
            <v-chart :option="classBarOption" autoresize style="height: 100%" />
          </div>
        </div>
        <div class="chart-panel">
          <div class="panel-title">知识点掌握度热力图</div>
          <div class="panel-body">
            <v-chart :option="heatmapOption" autoresize style="height: 100%" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute } from 'vue-router'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, HeatmapChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent, VisualMapComponent } from 'echarts/components'
import { getGradeOverview } from '@/api/profile'
import dayjs from 'dayjs'

use([CanvasRenderer, BarChart, HeatmapChart, GridComponent, TooltipComponent, LegendComponent, VisualMapComponent])

const route = useRoute()
const gradeId = route.params.gradeId as string

const currentTime = ref(dayjs().format('YYYY-MM-DD HH:mm:ss'))
let timeTimer: ReturnType<typeof setInterval> | null = null

const gradeName = ref('三年级')
const kpiData = ref({
  classCount: 6,
  studentCount: 312,
  averageScore: 82.3,
  passRate: 91.5,
})

const classNames = ['1班', '2班', '3班', '4班', '5班', '6班']
const subjects = ['语文', '数学', '英语', '科学', '道法']

const classBarOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['平均分', '及格率'], textStyle: { color: '#B0B8C8' }, top: 4 },
  grid: { top: 36, right: 48, bottom: 24, left: 48 },
  xAxis: {
    type: 'category',
    data: classNames,
    axisLabel: { color: '#B0B8C8', fontSize: 11 },
    axisLine: { lineStyle: { color: '#1e3a5f' } },
  },
  yAxis: [
    {
      type: 'value', min: 0, max: 100, name: '分数',
      nameTextStyle: { color: '#B0B8C8' },
      axisLabel: { color: '#B0B8C8', fontSize: 11 },
      splitLine: { lineStyle: { color: 'rgba(30,58,95,0.5)' } },
    },
    {
      type: 'value', min: 0, max: 100, name: '%',
      nameTextStyle: { color: '#B0B8C8' },
      axisLabel: { color: '#B0B8C8', fontSize: 11 },
      splitLine: { show: false },
    },
  ],
  series: [
    {
      name: '平均分', type: 'bar', barWidth: 20,
      data: [85, 82, 88, 79, 84, 81],
      itemStyle: { color: '#409EFF', borderRadius: [3, 3, 0, 0] },
    },
    {
      name: '及格率', type: 'bar', barWidth: 20, yAxisIndex: 1,
      data: [95, 92, 97, 88, 93, 90],
      itemStyle: { color: '#67C23A', borderRadius: [3, 3, 0, 0] },
    },
  ],
}))

// 知识点掌握度热力图数据
const heatmapData = [
  [0, 0, 85], [0, 1, 92], [0, 2, 78], [0, 3, 88], [0, 4, 82],
  [1, 0, 80], [1, 1, 88], [1, 2, 75], [1, 3, 85], [1, 4, 79],
  [2, 0, 88], [2, 1, 95], [2, 2, 82], [2, 3, 90], [2, 4, 85],
  [3, 0, 76], [3, 1, 82], [3, 2, 70], [3, 3, 80], [3, 4, 75],
  [4, 0, 82], [4, 1, 90], [4, 2, 80], [4, 3, 86], [4, 4, 81],
]

const heatmapOption = computed(() => ({
  tooltip: {
    position: 'top',
    formatter: (params: any) => `${subjects[params.value[0]]} - ${classNames[params.value[1]]}<br/>掌握度: ${params.value[2]}%`,
  },
  grid: { top: 8, right: 16, bottom: 40, left: 60 },
  xAxis: {
    type: 'category',
    data: classNames,
    axisLabel: { color: '#B0B8C8', fontSize: 11 },
    axisLine: { lineStyle: { color: '#1e3a5f' } },
    splitArea: { show: true, areaStyle: { color: ['rgba(30,58,95,0.2)', 'rgba(30,58,95,0.4)'] } },
  },
  yAxis: {
    type: 'category',
    data: subjects,
    axisLabel: { color: '#B0B8C8', fontSize: 11 },
    axisLine: { lineStyle: { color: '#1e3a5f' } },
    splitArea: { show: true, areaStyle: { color: ['rgba(30,58,95,0.2)', 'rgba(30,58,95,0.4)'] } },
  },
  visualMap: {
    min: 60,
    max: 100,
    calculable: true,
    orient: 'horizontal',
    left: 'center',
    bottom: 0,
    textStyle: { color: '#B0B8C8' },
    inRange: {
      color: ['#1a3a5c', '#1e5a8a', '#409EFF', '#66B1FF', '#95D475'],
    },
  },
  series: [{
    type: 'heatmap',
    data: heatmapData,
    label: { show: true, color: '#fff', fontSize: 11, formatter: '{c}%' },
    emphasis: {
      itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0, 0, 0, 0.5)' },
    },
  }],
}))

async function loadData() {
  try {
    const res = await getGradeOverview(Number(gradeId))
    const data = res.data
    if (data) {
      gradeName.value = data.gradeName || '三年级'
      kpiData.value = {
        classCount: data.classCount || 6,
        studentCount: data.studentCount || 312,
        averageScore: data.averageScore || 82.3,
        passRate: data.passRate || 91.5,
      }
    }
  } catch (e) {
    console.error('加载年级大屏数据失败', e)
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

.kpi-info { flex: 1; }

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
</style>
