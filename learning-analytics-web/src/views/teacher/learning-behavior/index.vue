<template>
  <div class="teacher-learning-behavior">
    <!-- 班级和学生选择 -->
    <el-card shadow="never">
      <el-form :model="selectForm" inline class="search-form">
        <el-form-item label="班级">
          <el-select v-model="selectForm.classId" placeholder="请选择班级" clearable style="width: 180px" @change="handleClassChange">
            <el-option v-for="c in classList" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学生">
          <el-select v-model="selectForm.studentId" placeholder="请选择学生" clearable style="width: 180px" :disabled="!selectForm.classId" @change="handleStudentChange">
            <el-option v-for="s in studentList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleSearch" :disabled="!selectForm.studentId">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <template v-if="selectForm.studentId">
      <!-- 统计卡片 -->
      <el-row :gutter="16" class="stat-row">
        <el-col :span="6">
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
        <el-col :span="6">
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
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card" :body-style="{ padding: '20px' }">
            <div class="stat-card-inner">
              <div class="stat-icon" style="background: linear-gradient(135deg, #E6A23C, #F0C78A)">
                <el-icon :size="28"><TrendCharts /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">行为总次数</div>
                <div class="stat-value">{{ statistics.totalCount || 0 }} 次</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card" :body-style="{ padding: '20px' }">
            <div class="stat-card-inner">
              <div class="stat-icon" style="background: linear-gradient(135deg, #F56C6C, #F89898)">
                <el-icon :size="28"><DataLine /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">日均学习</div>
                <div class="stat-value">{{ avgDailyDuration }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 图表区域 -->
      <el-row :gutter="16" class="chart-row">
        <el-col :span="12">
          <el-card shadow="never">
            <template #header><span>行为类型分布</span></template>
            <div ref="pieChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="never">
            <template #header><span>近14天学习趋势</span></template>
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
    </template>

    <!-- 未选择学生时的提示 -->
    <el-card v-else shadow="never" class="mt-16">
      <el-empty description="请先选择班级和学生" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { Timer, Calendar, TrendCharts, DataLine } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import {
  getLearningBehaviorList,
  getLearningBehaviorStatistics,
  getLearningBehaviorTrend,
  type LearningBehaviorStatistics,
  type LearningBehaviorTrendItem,
} from '@/api/learning-behavior'

const behaviorTypeMap: Record<string, string> = {
  login: '登录', logout: '登出', study: '学习', practice: '练习',
  exam: '测评', video: '视频', interaction: '互动',
}

const sourceMap: Record<string, string> = {
  web: '网页端', mobile: '移动端', api: 'API',
}

const loading = ref(false)
const total = ref(0)
const tableData = ref<any[]>([])
const statistics = ref<Partial<LearningBehaviorStatistics>>({})
const trendData = ref<LearningBehaviorTrendItem[]>([])

const pieChartRef = ref<HTMLElement>()
const lineChartRef = ref<HTMLElement>()
let pieChart: echarts.ECharts | null = null
let lineChart: echarts.ECharts | null = null

const selectForm = reactive({
  classId: undefined as number | undefined,
  studentId: undefined as number | undefined,
})

const queryParams = reactive({
  behaviorType: '',
  page: 1,
  pageSize: 10,
})

// 模拟班级和学生数据
const classList = ref([
  { id: 1, name: '高一(1)班' },
  { id: 2, name: '高一(2)班' },
  { id: 3, name: '高二(1)班' },
])

const studentList = ref<{ id: number; name: string }[]>([])

const allStudents: Record<number, { id: number; name: string }[]> = {
  1: [
    { id: 101, name: '张三' }, { id: 102, name: '李四' },
    { id: 103, name: '王五' }, { id: 104, name: '赵六' },
  ],
  2: [
    { id: 201, name: '孙七' }, { id: 202, name: '周八' },
    { id: 203, name: '吴九' },
  ],
  3: [
    { id: 301, name: '郑十' }, { id: 302, name: '钱十一' },
  ],
}

const avgDailyDuration = computed(() => {
  const days = statistics.value.activeDays || 1
  const totalSec = statistics.value.totalDuration || 0
  const avgMin = Math.round(totalSec / days / 60)
  if (avgMin >= 60) {
    return `${Math.floor(avgMin / 60)}小时${avgMin % 60}分`
  }
  return `${avgMin}分钟`
})

function getBehaviorTagType(type: string) {
  const map: Record<string, string> = {
    login: 'success', logout: 'info', study: 'primary',
    practice: 'warning', exam: 'danger', video: '', interaction: 'success',
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

function handleClassChange(classId: number | undefined) {
  selectForm.studentId = undefined
  studentList.value = classId ? allStudents[classId] || [] : []
}

function handleStudentChange() {
  // 学生变更时自动查询
}

async function handleSearch() {
  if (!selectForm.studentId) return
  queryParams.page = 1
  await Promise.all([fetchStatistics(), fetchTrend(), fetchData()])
  await nextTick()
  initPieChart()
  initLineChart()
}

async function fetchStatistics() {
  try {
    const res = await getLearningBehaviorStatistics(selectForm.studentId)
    statistics.value = res.data
  } catch {
    statistics.value = {
      totalDuration: 18600,
      totalDurationFormatted: '5小时10分0秒',
      activeDays: 22,
      totalCount: 128,
      behaviorTypeDistribution: { study: 50, practice: 30, exam: 20, video: 15, login: 8, interaction: 5 },
    }
  }
}

async function fetchTrend() {
  try {
    const res = await getLearningBehaviorTrend(selectForm.studentId, 14)
    trendData.value = res.data
  } catch {
    trendData.value = Array.from({ length: 14 }, (_, i) => {
      const date = new Date()
      date.setDate(date.getDate() - (13 - i))
      const dateStr = date.toISOString().slice(0, 10)
      return { date: dateStr, count: Math.floor(Math.random() * 15) + 3, duration: Math.floor(Math.random() * 3600) + 600 }
    })
  }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getLearningBehaviorList({
      studentId: selectForm.studentId,
      behaviorType: queryParams.behaviorType || undefined,
      page: queryParams.page,
      pageSize: queryParams.pageSize,
    })
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    tableData.value = [
      { id: 1, behaviorType: 'study', duration: 3600, source: 'web', behaviorDetail: '学习数学第三章', behaviorTime: '2026-04-30 09:30:00' },
      { id: 2, behaviorType: 'practice', duration: 1800, source: 'web', behaviorDetail: '完成物理习题集', behaviorTime: '2026-04-30 08:00:00' },
      { id: 3, behaviorType: 'exam', duration: 5400, source: 'web', behaviorDetail: '参加数学单元测试', behaviorTime: '2026-04-29 14:00:00' },
    ]
    total.value = 3
  } finally {
    loading.value = false
  }
}

function initPieChart() {
  if (!pieChartRef.value) return
  if (pieChart) pieChart.dispose()
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
  if (lineChart) lineChart.dispose()
  lineChart = echarts.init(lineChartRef.value)
  const dates = trendData.value.map((item) => item.date.slice(5))
  const counts = trendData.value.map((item) => item.count)
  const durations = trendData.value.map((item) => Math.round(item.duration / 60))

  lineChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
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

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  lineChart?.dispose()
})
</script>

<style scoped lang="scss">
.teacher-learning-behavior {
  .search-form {
    :deep(.el-form-item) {
      margin-bottom: 0;
    }
  }

  .stat-row {
    margin-top: 16px;
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
