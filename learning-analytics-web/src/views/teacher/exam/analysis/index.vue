<template>
  <div class="page-container analysis-page">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background: linear-gradient(135deg, #409EFF, #66B1FF)">
              <el-icon :size="24"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">平均分</div>
              <div class="stat-value primary">{{ statistics.averageScore?.toFixed(1) || '--' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background: linear-gradient(135deg, #67C23A, #95D475)">
              <el-icon :size="24"><Top /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">最高分</div>
              <div class="stat-value success">{{ statistics.maxScore || '--' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background: linear-gradient(135deg, #F56C6C, #F89898)">
              <el-icon :size="24"><Bottom /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">最低分</div>
              <div class="stat-value danger">{{ statistics.minScore || '--' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background: linear-gradient(135deg, #E6A23C, #F0C78A)">
              <el-icon :size="24"><DataLine /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">及格率</div>
              <div class="stat-value warning">{{ statistics.passRate ? (statistics.passRate * 100).toFixed(1) + '%' : '--' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <!-- 成绩分布柱状图 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">成绩分布</span>
          </template>
          <v-chart :option="scoreDistOption" style="height: 320px" autoresize />
        </el-card>
      </el-col>

      <!-- 知识点掌握度热力图 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">知识点掌握度</span>
          </template>
          <v-chart :option="knowledgeHeatOption" style="height: 320px" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <!-- 题目正确率排行 -->
    <el-card shadow="hover" class="mt-20">
      <template #header>
        <span class="card-title">题目正确率排行</span>
      </template>
      <el-table :data="questionAnalysis" stripe style="width: 100%">
        <el-table-column type="index" label="排名" width="70" align="center">
          <template #default="{ $index }">
            <el-tag
              v-if="$index < 3"
              :type="$index === 0 ? 'danger' : $index === 1 ? 'warning' : ''"
              size="small"
              round
            >{{ $index + 1 }}</el-tag>
            <span v-else>{{ $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="题目内容" min-width="300" show-overflow-tooltip>
          <template #default="{ row }">{{ stripHtml(row.content) }}</template>
        </el-table-column>
        <el-table-column prop="type" label="题型" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ questionTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="knowledgePoint" label="知识点" width="120" show-overflow-tooltip />
        <el-table-column prop="correctRate" label="正确率" width="140" align="center" sortable>
          <template #default="{ row }">
            <div class="rate-cell">
              <el-progress
                :percentage="Math.round(row.correctRate * 100)"
                :stroke-width="8"
                :color="getProgressColor(row.correctRate)"
                :show-text="false"
                style="flex: 1"
              />
              <span class="rate-text" :style="{ color: getProgressColor(row.correctRate) }">
                {{ (row.correctRate * 100).toFixed(1) }}%
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="averageScore" label="平均得分率" width="120" align="center" sortable>
          <template #default="{ row }">{{ (row.averageScore * 100).toFixed(1) }}%</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, HeatmapChart } from 'echarts/charts'
import {
  TitleComponent, TooltipComponent, GridComponent, VisualMapComponent,
  LegendComponent,
} from 'echarts/components'
import { getExamStatistics, getQuestionAnalysis } from '@/api/score'
import type { ExamStatistics, QuestionAnalysis } from '@/api/score'

use([CanvasRenderer, BarChart, HeatmapChart, TitleComponent, TooltipComponent, GridComponent, VisualMapComponent, LegendComponent])

const route = useRoute()
const examId = computed(() => Number(route.params.id))

const statistics = ref<Partial<ExamStatistics>>({})
const questionAnalysis = ref<QuestionAnalysis[]>([])

const scoreDistOption = computed(() => {
  const dist = statistics.value.scoreDistribution || []
  const categories = dist.map(d => d.range)
  const values = dist.map(d => d.count)
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: { type: 'category', data: categories, axisLabel: { fontSize: 12 } },
    yAxis: { type: 'value', name: '人数', axisLabel: { fontSize: 12 } },
    series: [{
      type: 'bar',
      data: values,
      barWidth: '50%',
      itemStyle: {
        borderRadius: [4, 4, 0, 0],
        color: (params: any) => {
          const colors = ['#F56C6C', '#F56C6C', '#E6A23C', '#E6A23C', '#409EFF', '#409EFF', '#67C23A', '#67C23A', '#67C23A', '#67C23A']
          return colors[params.dataIndex] || '#409EFF'
        },
      },
      label: { show: true, position: 'top', fontSize: 12 },
    }],
  }
})

const knowledgeHeatOption = computed(() => {
  const analysis = questionAnalysis.value
  const knowledgeMap = new Map<string, { total: number; correct: number }>()
  analysis.forEach(q => {
    const kp = q.knowledgePoint || '未分类'
    const existing = knowledgeMap.get(kp) || { total: 0, correct: 0 }
    existing.total++
    existing.correct += q.correctRate
    knowledgeMap.set(kp, existing)
  })

  const kpNames = Array.from(knowledgeMap.keys())
  const data: [number, number, number][] = []
  kpNames.forEach((name, idx) => {
    const info = knowledgeMap.get(name)!
    const avgRate = info.total > 0 ? info.correct / info.total : 0
    data.push([idx, 0, Math.round(avgRate * 100)])
  })

  return {
    tooltip: {
      position: 'top',
      formatter: (params: any) => {
        return `${kpNames[params.data[0]]}<br/>掌握度: ${params.data[2]}%`
      },
    },
    grid: { left: '3%', right: '12%', bottom: '3%', top: '3%', containLabel: true },
    xAxis: { type: 'category', data: kpNames, axisLabel: { rotate: 30, fontSize: 11 }, splitArea: { show: true } },
    yAxis: { type: 'category', data: ['掌握度'], axisLabel: { fontSize: 12 } },
    visualMap: {
      min: 0, max: 100, calculable: true, orient: 'horizontal', left: 'center', bottom: '0%',
      inRange: { color: ['#F56C6C', '#E6A23C', '#409EFF', '#67C23A'] },
      text: ['高', '低'],
    },
    series: [{
      type: 'heatmap',
      data: data,
      label: { show: true, formatter: '{c}%', fontSize: 12 },
      emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0, 0, 0, 0.5)' } },
    }],
  }
})

function stripHtml(html: string) {
  return html?.replace(/<[^>]+>/g, '') || ''
}

function questionTypeText(type: string) {
  const map: Record<string, string> = {
    single_choice: '单选', multi_choice: '多选', true_false: '判断', fill_blank: '填空', short_answer: '简答',
  }
  return map[type] || type
}

function getProgressColor(rate: number) {
  if (rate >= 0.8) return '#67C23A'
  if (rate >= 0.6) return '#409EFF'
  if (rate >= 0.4) return '#E6A23C'
  return '#F56C6C'
}

async function loadData() {
  try {
    const [statRes, analysisRes] = await Promise.all([
      getExamStatistics(examId.value),
      getQuestionAnalysis(examId.value),
    ])
    statistics.value = statRes.data || {}
    questionAnalysis.value = analysisRes.data || []
  } catch (e) {
    console.error('加载分析数据失败', e)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.analysis-page {
  padding: 4px;
}

.mt-20 {
  margin-top: 20px;
}

.stat-card {
  border-radius: 8px;
  border: none;
}

.stat-card-inner {
  display: flex;
  align-items: center;
  gap: 14px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
}

.stat-value.primary { color: #409EFF; }
.stat-value.success { color: #67C23A; }
.stat-value.danger { color: #F56C6C; }
.stat-value.warning { color: #E6A23C; }

.card-title {
  font-size: 15px;
  font-weight: 600;
}

.rate-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rate-text {
  font-size: 13px;
  font-weight: 600;
  min-width: 50px;
  text-align: right;
}
</style>
