<template>
  <div class="student-certificates">
    <el-card shadow="never">
      <template #header>
        <div class="flex-between">
          <span class="card-title">我的证书</span>
          <el-tag size="small" type="info">{{ certificateList.length }} 份</el-tag>
        </div>
      </template>

      <el-row v-if="certificateList.length" :gutter="12">
        <el-col v-for="cert in certificateList" :key="cert.id" :span="12" class="mb-12">
          <el-card shadow="hover" class="cert-card" :body-style="{ padding: '16px' }">
            <div class="cert-icon">
              <el-icon :size="32" color="#E6A23C"><Trophy /></el-icon>
            </div>
            <div class="cert-info">
              <div class="cert-name">{{ cert.name }}</div>
              <div class="cert-level">
                <el-tag :type="levelTagType(cert.level)" size="small">{{ cert.level }}</el-tag>
              </div>
              <div class="cert-meta">
                <div class="meta-item">
                  <el-icon :size="12"><OfficeBuilding /></el-icon>
                  <span>{{ cert.issuingAuthority }}</span>
                </div>
                <div class="meta-item">
                  <el-icon :size="12"><Calendar /></el-icon>
                  <span>{{ cert.issueDate }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-else description="暂无证书" :image-size="80" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCertificateList } from '@/api/certificate'

const certificateList = ref<any[]>([])

function levelTagType(level: string) {
  if (level === '一等奖' || level === '国家级') return 'danger'
  if (level === '二等奖' || level === '省级') return 'warning'
  return 'success'
}

async function loadData() {
  try {
    const res = await getCertificateList({ studentId: 1, page: 1, pageSize: 20 })
    certificateList.value = res.data?.list || []
  } catch (e) {
    console.error('加载证书数据失败', e)
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

.cert-card {
  border-radius: 8px;
  transition: transform 0.2s;
}
.cert-card:hover {
  transform: translateY(-2px);
}

.cert-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: rgba(230, 162, 60, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
}

.cert-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
}

.cert-level {
  margin-bottom: 8px;
}

.cert-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}
</style>
