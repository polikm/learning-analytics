<template>
  <div class="student-exam">
    <el-card shadow="never">
      <template #header>
        <span>测评详情</span>
      </template>
      <el-empty description="功能开发中，敬请期待" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
const route = useRoute()
const examId = route.params.examId
</script>
