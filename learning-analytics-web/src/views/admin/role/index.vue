<template>
  <div class="page-skeleton">
    <el-card shadow="never">
      <template #header>
        <div class="flex-between">
          <span>角色管理</span>
          <el-button type="primary" icon="Plus">新增角色</el-button>
        </div>
      </template>
      <el-empty description="功能开发中，敬请期待">
        <el-button type="primary">了解进度</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.page-skeleton {
  min-height: 400px;
}
</style>
