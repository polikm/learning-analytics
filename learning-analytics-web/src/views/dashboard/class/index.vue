<template>
  <div class="dashboard-screen">
    <!-- 顶部标题栏 -->
    <div class="screen-header">
      <h1>{{ className }} 学情数据大屏</h1>
      <div class="header-time">{{ currentTime }}</div>
    </div>

    <div class="screen-body">
      <!-- 班级成绩概览 -->
      <div class="kpi-row">
        <div class="kpi-card">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #409EFF, #66B1FF)">
            <el-icon :size="24"><TrendCharts /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value">{{ kpiData.averageScore }}</div>
            <div class="kpi-label">平均分</div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #67C23A, #95D475)">
            <el-icon :size="24"><Top /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value">{{ kpiData.maxScore }}</div>
            <div class="kpi-label">最高分</div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #E6A23C, #F0C78A)">
            <el-icon :size="24"><CircleCheck /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value">{{ kpiData.passRate }}%</div>
            <div class="kpi-label">及格率</div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #F56C6C, #F89898)">
            <el-icon :size="24"><Star /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value">{{ kpiData.excellentRate }}%</div>
            <div class="kpi-label">优秀率</div>
          </div>
        </div>
      </div>

      <!-- 学生成绩分布 + 学科对比雷达图 -->
      <div class="chart-row">
        <div class="chart-panel">
          <div class="panel-title">学生成绩分布</div>
          <div class="panel-body">
            <v-chart :option="scoreDistOption" autoresize style="height: 100%" />
          </div>
        </div>
        <div class="chart-panel">
          <div class="panel-title">学科对比雷达图</div>
          <div class="panel-body">
            <v-chart :option="subjectRadarOption" autoresize style="height: 100%" />
          </div>
        </div>
      </div>

      <!-- 预警学生列表 -->
      <div class="chart-row single">
        <div class="chart-panel">
          <div class="panel-title">预警学生列表</div>
          <div class="panel-body">
            <el-table :data="warningStudents" style="width: 100%" size="small" class="dark-table">
              <el-table-column prop="studentName" label="姓名" width="90" />
              <el-table-column prop="studentNo" label="学号" width="100" />
              <el-table-column prop="type" label="预警类型" width="110">
                <template #default="{ row }">
                  <el-tag :type="warningTag(row.type)" size="small" effect="dark">{{ warningText(row.type) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="detail" label="详情" min-width="200" show-overflow-tooltip />
            </el-table>
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
import { BarChart, RadarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, RadarComponent } from 'echarts/components'
import { getClassOverview } from '@/api/profile'
import dayjs from 'dayjs'

use([CanvasRenderer, BarChart, RadarChart, GridComponent, TooltipComponent, RadarComponent])

const route = useRoute()
const classId = route.params.classId as string

const currentTime = ref(dayjs().format('YYYY-MM-DD HH:mm:ss'))
let timeTimer: ReturnType<typeof setInterval> | null = null

const className = ref('三年级2班')
const kpiData = ref({
  averageScore: 82.5,
  maxScore: 98,
  passRate: 92.3,
  excellentRate: 35.6,
})

const warningStudents = ref<any[]>([])

const scoreDistOption = computed(() => ({
  tooltip: { trigger: 'axis', formatter: '{b}<br/>人数: {c} 人' },
  grid: { top: 20, right: 16, bottom: 24, left: 48 },
  xAxis: {
    type: 'category',
    data: ['0-29', '30-59', '60-69', '70-79', '80-89', '90-100'],
    axisLabel: { color: '#B0B8C8', fontSize: 11 },
    axisLine: { lineStyle: { color: '#1e3a5f' } },
  },
  yAxis: {
    type: 'value',
    axisLabel: { color: '#B0B8C8', fontSize: 11 },
    splitLine: { lineStyle: { color: 'rgba(30,58,95,0.5)' } },
  },
  series: [{
    type: 'bar',
    data: [2, 3, 8, 15, 18, 12],
    barWidth: 28,
    itemStyle: {
      color: (params: any) => {
        const colors = ['#F56C6C', '#F56C6C', '#E6A23C', '#409EFF', '#67C23A', '#67C23A']
        return colors[params.dataIndex] || '#409EFF'
      },
      borderRadius: [4, 4, 0, 0],
    },
  }],
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
        value: [85, 92, 78, 88, 82],
        name: '班级平均',
        areaStyle: { color: 'rgba(64,158,255,0.2)' },
        lineStyle: { color: '#409EFF', width: 2 },
        itemStyle: { color: '#409EFF' },
      },
      {
        value: [90, 95, 85, 92, 88],
        name: '年级平均',
        areaStyle: { color: 'rgba(103,194,58,0.1)' },
        lineStyle: { color: '#67C23A', width: 2, type: 'dashed' },
        itemStyle: { color: '#67C23A' },
      },
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
    const res = await getClassOverview(Number(classId))
    const data = res.data
    if (data) {
      className.value = data.className || '三年级2班'
      kpiData.value = {
        averageScore: data.averageScore || 82.5,
        maxScore: data.topStudents?.[0]?.score || 98,
        passRate: data.passRate || 92.3,
        excellentRate: data.excellentRate || 35.6,
      }
      warningStudents.value = (data.warningStudents || []).map((w: any) => ({
        studentName: w.name,
        studentNo: '-',
        type: w.type,
        detail: w.detail,
      }))
    }
  } catch (e) {
    console.error('加载班级大屏数据失败', e)
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

  &.single {
    grid-template-columns: 1fr;
  }
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
