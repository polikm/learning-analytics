<template>
  <div class="admin-dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6" v-for="card in statCards" :key="card.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-content">
            <div class="stat-info">
              <p class="stat-title">{{ card.title }}</p>
              <p class="stat-value">{{ card.value }}</p>
              <p class="stat-desc">
                <span :class="card.trend > 0 ? 'trend-up' : 'trend-down'">
                  {{ card.trend > 0 ? '+' : '' }}{{ card.trend }}%
                </span>
                较上月
              </p>
            </div>
            <div class="stat-icon" :style="{ backgroundColor: card.bgColor }">
              <el-icon :size="28" :color="card.color"><component :is="card.icon" /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span>测评趋势</span>
          </template>
          <div class="chart-placeholder">
            <el-icon :size="48" color="#C0C4CC"><DataAnalysis /></el-icon>
            <p>图表区域（待接入 ECharts）</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span>学科分布</span>
          </template>
          <div class="chart-placeholder">
            <el-icon :size="48" color="#C0C4CC"><PieChart /></el-icon>
            <p>图表区域（待接入 ECharts）</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近活动 -->
    <el-card shadow="hover" class="mt-24">
      <template #header>
        <span>最近活动</span>
      </template>
      <el-table :data="recentActivities" stripe>
        <el-table-column prop="time" label="时间" width="180" />
        <el-table-column prop="content" label="内容" />
        <el-table-column prop="operator" label="操作人" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const statCards = ref([
  {
    title: '学校总数',
    value: '128',
    trend: 12,
    icon: 'School',
    color: '#409EFF',
    bgColor: '#ECF5FF',
  },
  {
    title: '教师人数',
    value: '2,456',
    trend: 8,
    icon: 'User',
    color: '#67C23A',
    bgColor: '#F0F9EB',
  },
  {
    title: '学生人数',
    value: '45,892',
    trend: 15,
    icon: 'Avatar',
    color: '#E6A23C',
    bgColor: '#FDF6EC',
  },
  {
    title: '测评次数',
    value: '1,234',
    trend: -3,
    icon: 'EditPen',
    color: '#F56C6C',
    bgColor: '#FEF0F0',
  },
])

const recentActivities = ref([
  { time: '2026-04-29 10:30', content: '管理员 张三 创建了新学校 "测试学校"', operator: '张三' },
  { time: '2026-04-29 09:15', content: '教师 李四 发布了数学期中测评', operator: '李四' },
  { time: '2026-04-28 16:45', content: '系统完成了数据同步任务', operator: '系统' },
  { time: '2026-04-28 14:20', content: '管理员 王五 修改了角色权限配置', operator: '王五' },
  { time: '2026-04-28 11:00', content: '教师 赵六 导出了三年级成绩报告', operator: '赵六' },
])
</script>

<style scoped lang="scss">
.admin-dashboard {
  .stat-card {
    .stat-card-content {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .stat-info {
        .stat-title {
          font-size: 14px;
          color: var(--color-text-secondary);
          margin-bottom: 8px;
        }

        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: var(--color-text-primary);
          margin-bottom: 8px;
        }

        .stat-desc {
          font-size: 12px;
          color: var(--color-text-secondary);

          .trend-up {
            color: var(--color-success);
          }

          .trend-down {
            color: var(--color-danger);
          }
        }
      }

      .stat-icon {
        width: 56px;
        height: 56px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }

  .chart-row {
    margin-top: 20px;
  }

  .chart-placeholder {
    height: 300px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: var(--color-text-placeholder);

    p {
      margin-top: 12px;
      font-size: 14px;
    }
  }
}
</style>
