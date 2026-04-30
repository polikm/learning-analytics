<template>
  <div class="student-scores">
    <!-- 成绩趋势折线图 -->
    <el-card shadow="never" class="mb-16">
      <template #header><span class="card-title">成绩趋势</span></template>
      <v-chart :option="trendChartOption" autoresize style="height: 260px" />
    </el-card>

    <!-- 成绩列表 -->
    <el-card shadow="never">
      <template #header>
        <div class="flex-between">
          <span class="card-title">成绩列表</span>
          <el-tag size="small" type="info">{{ scoreList.length }} 条记录</el-tag>
        </div>
      </template>
      <el-table :data="scoreList" stripe style="width: 100%" size="small">
        <el-table-column prop="examName" label="测评名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="subject" label="学科" width="80" />
        <el-table-column prop="score" label="分数" width="80" align="center">
          <template #default="{ row }">
            <span :style="{ color: row.score >= 80 ? '#67C23A' : row.score >= 60 ? '#409EFF' : '#F56C6C', fontWeight: 600 }">
              {{ row.score }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="classRank" label="班级排名" width="90" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.classRank <= 3" type="danger" size="small" effect="plain">第{{ row.classRank }}名</el-tag>
            <span v-else>第{{ row.classRank }}名</span>
          </template>
        </el-table-column>
        <el-table-column prop="date" label="时间" width="100" />
      </el-table>
      <el-empty v-if="!scoreList.length" description="暂无成绩记录" :image-size="60" />
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
import { getStudentProfile } from '@/api/profile'

use([CanvasRenderer, LineChart, GridComponent, TooltipComponent, MarkLineComponent])

const scoreList = ref<any[]>([])

const trendChartOption = computed(() => {
  const exams = scoreList.value.length ? scoreList.value : []
  return {
    grid: { top: 20, right: 16, bottom: 24, left: 40 },
    tooltip: { trigger: 'axis', formatter: (params: any) => {
      const p = params[0]
      const exam = exams[p.dataIndex]
      return `${p.name}<br/>分数: ${p.value}<br/>学科: ${exam?.subject || '-'}<br/>排名: 第${exam?.classRank || '-'}名`
    }},
    xAxis: {
      type: 'category',
      data: exams.map((e) => e.examName.length > 6 ? e.examName.slice(0, 6) + '..' : e.examName),
      axisLabel: { fontSize: 11, color: '#909399', rotate: exams.length > 6 ? 30 : 0 },
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
      markLine: {
        silent: true,
        lineStyle: { color: '#F56C6C', type: 'dashed' },
        data: [{ yAxis: 60, label: { formatter: '及格线', fontSize: 10 } }],
      },
    }],
  }
})

async function loadData() {
  try {
    const res = await getStudentProfile(1)
    const profile = res.data
    if (profile) {
      scoreList.value = (profile.recentExams || []).map((e: any) => ({
        examName: e.examName,
        subject: e.subject,
        score: e.score,
        classRank: e.rank || '-',
        date: e.date,
      }))
    }
  } catch (e) {
    console.error('加载成绩数据失败', e)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
</style>
