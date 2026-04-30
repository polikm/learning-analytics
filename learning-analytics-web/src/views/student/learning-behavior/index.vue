<template>
  <div class="learning-behavior-page">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background: linear-gradient(135deg, #409EFF, #66B1FF)">
              <el-icon :size="28"><Timer /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">总学习时长</div>
              <div class="stat-value">{{ statistics.totalDurationFormatted || '0秒' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background: linear-gradient(135deg, #67C23A, #95D475)">
              <el-icon :size="28"><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">活跃天数</div>
              <div class="stat-value">{{ statistics.activeDays || 0 }} 天</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background: linear-gradient(135deg, #E6A23C, #F0C78A)">
              <el-icon :size="28"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">本周学习次数</div>
              <div class="stat-value">{{ weekCount }} 次</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <span>行为类型分布</span>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <span>近7天学习趋势</span>
          </template>
          <div ref="lineChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 行为记录列表 -->
    <el-card shadow="never" class="mt-16">
      <template #header>
        <div class="table-header">
          <span>行为记录</span>
          <el-select v-model="queryParams.behaviorType" placeholder="全部类型" clearable style="width: 140px" @change="fetchData">
            <el-option label="登录" value="login" />
            <el-option label="登出" value="logout" />
            <el-option label="学习" value="study" />
            <el-option label="练习" value="practice" />
            <el-option label="测评" value="exam" />
            <el-option label="视频" value="video" />
            <el-option label="互动" value="interaction" />
          </el-select>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="behaviorType" label="行为类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getBehaviorTagType(row.behaviorType)">
              {{ behaviorTypeMap[row.behaviorType] || row.behaviorType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长" width="100">
          <template #default="{ row }">
            {{ row.duration ? formatDuration(row.duration) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="source" label="来源" width="80">
          <template #default="{ row }">
            {{ sourceMap[row.source] || row.source || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="behaviorDetail" label="行为详情" min-width="200" show-overflow-tooltip />
        <el-table-column prop="behaviorTime" label="时间" width="180" />
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { Timer, Calendar, TrendCharts } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import {
  getLearningBehaviorList,
  getLearningBehaviorStatistics,
  getLearningBehaviorTrend,
  type LearningBehaviorStatistics,
  type LearningBehaviorTrendItem,
} from '@/api/learning-behavior'

const behaviorTypeMap: Record<string, string> = {
  login: '登录',
  logout: '登出',
  study: '学习',
  practice: '练习',
  exam: '测评',
  video: '视频',
  interaction: '互动',
}

const sourceMap: Record<string, string> = {
  web: '网页端',
  mobile: '移动端',
  api: 'API',
}

const loading = ref(false)
const total = ref(0)
const weekCount = ref(0)
const tableData = ref<any[]>([])
const statistics = ref<Partial<LearningBehaviorStatistics>>({})
const trendData = ref<LearningBehaviorTrendItem[]>([])

const pieChartRef = ref<HTMLElement>()
const lineChartRef = ref<HTMLElement>()
let pieChart: echarts.ECharts | null = null
let lineChart: echarts.ECharts | null = null

const queryParams = reactive({
  behaviorType: '',
  page: 1,
  pageSize: 10,
})

function getBehaviorTagType(type: string) {
  const map: Record<string, string> = {
    login: 'success',
    logout: 'info',
    study: 'primary',
    practice: 'warning',
    exam: 'danger',
    video: '',
    interaction: 'success',
  }
  return map[type] || ''
}

function formatDuration(seconds: number) {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  if (h > 0) return `${h}小时${m}分${s}秒`
  if (m > 0) return `${m}分${s}秒`
  return `${s}秒`
}

async function fetchStatistics() {
  try {
    const res = await getLearningBehaviorStatistics()
    statistics.value = res.data
  } catch {
    statistics.value = {
      totalDuration: 12600,
      totalDurationFormatted: '3小时30分0秒',
      activeDays: 15,
      totalCount: 86,
      behaviorTypeDistribution: { study: 35, practice: 20, exam: 15, video: 10, login: 4, interaction: 2 },
    }
  }
}

async function fetchTrend() {
  try {
    const res = await getLearningBehaviorTrend(undefined, 7)
    trendData.value = res.data
    // 计算本周学习次数
    weekCount.value = res.data.reduce((sum, item) => sum + item.count, 0)
  } catch {
    trendData.value = [
      { date: '2026-04-24', count: 8, duration: 2400 },
      { date: '2026-04-25', count: 12, duration: 3600 },
      { date: '2026-04-26', count: 6, duration: 1800 },
      { date: '2026-04-27', count: 15, duration: 4500 },
      { date: '2026-04-28', count: 10, duration: 3000 },
      { date: '2026-04-29', count: 9, duration: 2700 },
      { date: '2026-04-30', count: 11, duration: 3300 },
    ]
    weekCount.value = trendData.value.reduce((sum, item) => sum + item.count, 0)
  }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getLearningBehaviorList(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    tableData.value = [
      { id: 1, behaviorType: 'study', duration: 3600, source: 'web', behaviorDetail: '学习数学第三章', behaviorTime: '2026-04-30 09:30:00' },
      { id: 2, behaviorType: 'practice', duration: 1800, source: 'web', behaviorDetail: '完成物理习题集', behaviorTime: '2026-04-30 08:00:00' },
      { id: 3, behaviorType: 'login', duration: null, source: 'web', behaviorDetail: '用户登录系统', behaviorTime: '2026-04-30 07:50:00' },
      { id: 4, behaviorType: 'video', duration: 2700, source: 'mobile', behaviorDetail: '观看化学实验视频', behaviorTime: '2026-04-29 20:00:00' },
      { id: 5, behaviorType: 'exam', duration: 5400, source: 'web', behaviorDetail: '参加数学单元测试', behaviorTime: '2026-04-29 14:00:00' },
    ]
    total.value = 5
  } finally {
    loading.value = false
  }
}

function initPieChart() {
  if (!pieChartRef.value) return
  pieChart = echarts.init(pieChartRef.value)
  const distribution = statistics.value.behaviorTypeDistribution || {}
  const data = Object.entries(distribution).map(([key, value]) => ({
    name: behaviorTypeMap[key] || key,
    value,
  }))
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, left: 'center' },
    color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#B37FEB'],
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
        label: { show: false },
        emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
        data,
      },
    ],
  })
}

function initLineChart() {
  if (!lineChartRef.value) return
  lineChart = echarts.init(lineChartRef.value)
  const dates = trendData.value.map((item) => item.date.slice(5))
  const counts = trendData.value.map((item) => item.count)
  const durations = trendData.value.map((item) => Math.round(item.duration / 60))

  lineChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' },
    },
    legend: { data: ['学习次数', '学习时长(分钟)'], bottom: 0 },
    grid: { left: 50, right: 50, top: 20, bottom: 40 },
    xAxis: { type: 'category', data: dates, boundaryGap: false },
    yAxis: [
      { type: 'value', name: '次数', position: 'left' },
      { type: 'value', name: '分钟', position: 'right' },
    ],
    series: [
      {
        name: '学习次数',
        type: 'line',
        smooth: true,
        data: counts,
        itemStyle: { color: '#409EFF' },
        areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64,158,255,0.3)' },
          { offset: 1, color: 'rgba(64,158,255,0.05)' },
        ]) },
      },
      {
        name: '学习时长(分钟)',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: durations,
        itemStyle: { color: '#67C23A' },
        areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(103,194,58,0.3)' },
          { offset: 1, color: 'rgba(103,194,58,0.05)' },
        ]) },
      },
    ],
  })
}

function handleResize() {
  pieChart?.resize()
  lineChart?.resize()
}

onMounted(async () => {
  await Promise.all([fetchStatistics(), fetchTrend(), fetchData()])
  await nextTick()
  initPieChart()
  initLineChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  lineChart?.dispose()
})
</script>

<style scoped lang="scss">
.learning-behavior-page {
  .stat-row {
    margin-bottom: 16px;
  }

  .stat-card {
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

    .stat-value {
      font-size: 24px;
      font-weight: bold;
      color: #303133;
    }
  }

  .chart-row {
    margin-bottom: 16px;
  }

  .chart-container {
    height: 300px;
  }

  .table-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
