<template>
  <div class="mobile-layout">
    <!-- 顶部标题栏 -->
    <div class="mobile-header">
      <span class="header-title">{{ pageTitle }}</span>
    </div>

    <!-- 主内容区 -->
    <div class="mobile-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>

    <!-- 底部Tab导航 -->
    <div class="mobile-tabbar">
      <div
        v-for="tab in tabs"
        :key="tab.path"
        class="tab-item"
        :class="{ active: currentPath === tab.path }"
        @click="$router.push(tab.path)"
      >
        <el-icon :size="22"><component :is="tab.icon" /></el-icon>
        <span class="tab-label">{{ tab.label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const currentPath = computed(() => route.path)
const pageTitle = computed(() => (route.meta.title as string) || '学情分析平台')

interface TabItem {
  path: string
  label: string
  icon: string
}

const tabs = computed<TabItem[]>(() => {
  if (currentPath.value.startsWith('/parent')) {
    return [
      { path: '/parent/home', label: '首页', icon: 'HomeFilled' },
      { path: '/parent/reports', label: '学情', icon: 'DataAnalysis' },
      { path: '/parent/profile', label: '我的', icon: 'User' },
    ]
  }
  return [
    { path: '/student/home', label: '首页', icon: 'HomeFilled' },
    { path: '/student/scores', label: '成绩', icon: 'Trophy' },
    { path: '/student/profile', label: '我的', icon: 'User' },
  ]
})
</script>

<style scoped lang="scss">
.mobile-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: var(--color-bg-page);
}

.mobile-header {
  height: 48px;
  background: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  .header-title {
    color: #fff;
    font-size: 16px;
    font-weight: 600;
  }
}

.mobile-content {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.mobile-tabbar {
  height: var(--tab-bar-height);
  background: var(--color-bg-white);
  display: flex;
  align-items: center;
  border-top: 1px solid var(--border-color-light);
  flex-shrink: 0;

  .tab-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 2px;
    cursor: pointer;
    color: var(--color-text-secondary);
    transition: color 0.2s;

    &.active {
      color: var(--color-primary);
    }

    .tab-label {
      font-size: 11px;
    }
  }
}
</style>
