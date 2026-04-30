<template>
  <el-container class="admin-layout">
    <!-- 左侧侧边栏 -->
    <el-aside :width="appStore.sidebarCollapsed ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <img src="" alt="Logo" class="logo-img" />
        <span v-show="!appStore.sidebarCollapsed" class="logo-text">学情分析平台</span>
      </div>
      <el-menu
        :default-active="currentRoute"
        :collapse="appStore.sidebarCollapsed"
        :collapse-transition="false"
        router
        background-color="#001529"
        text-color="#ffffffa6"
        active-text-color="#409EFF"
      >
        <template v-for="item in menuItems" :key="item.path">
          <el-menu-item :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <!-- 右侧主区域 -->
    <el-container class="main-container">
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon
            class="collapse-btn"
            @click="appStore.toggleSidebar"
          >
            <component :is="appStore.sidebarCollapsed ? 'Expand' : 'Fold'" />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <MessageCenter />
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="user-name">{{ authStore.userName || '用户' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useAuthStore } from '@/stores/auth'
import MessageCenter from '@/components/MessageCenter.vue'

const route = useRoute()
const appStore = useAppStore()
const authStore = useAuthStore()

const currentRoute = computed(() => route.path)

interface MenuItem {
  path: string
  title: string
  icon: string
}

/** 根据角色动态显示菜单 */
const menuItems = computed<MenuItem[]>(() => {
  const role = authStore.userRole
  if (role === 'admin' || role === 'ADMIN') {
    return [
      { path: '/admin/dashboard', title: '工作台', icon: 'Odometer' },
      { path: '/admin/tenants', title: '租户管理', icon: 'OfficeBuilding' },
      { path: '/admin/schools', title: '学校管理', icon: 'School' },
      { path: '/admin/users', title: '用户管理', icon: 'User' },
      { path: '/admin/roles', title: '角色管理', icon: 'Lock' },
      { path: '/admin/dicts', title: '字典管理', icon: 'Collection' },
      { path: '/admin/datasource', title: '数据源管理', icon: 'Coin' },
      { path: '/admin/settings', title: '系统设置', icon: 'Setting' },
    ]
  }
  if (role === 'teacher' || role === 'TEACHER') {
    return [
      { path: '/teacher/dashboard', title: '工作台', icon: 'Odometer' },
      { path: '/teacher/questions', title: '题库管理', icon: 'Document' },
      { path: '/teacher/papers', title: '试卷管理', icon: 'Notebook' },
      { path: '/teacher/exams', title: '测评管理', icon: 'EditPen' },
      { path: '/teacher/certificates', title: '证书管理', icon: 'Postcard' },
      { path: '/teacher/reports', title: '学情报告', icon: 'DataAnalysis' },
    ]
  }
  return []
})

const breadcrumbs = computed(() => {
  const matched = route.matched.filter((item) => item.meta?.title)
  return matched.map((item) => ({
    path: item.path,
    title: item.meta.title as string,
  }))
})

function handleCommand(command: string) {
  if (command === 'logout') {
    authStore.logout()
  } else if (command === 'profile') {
    // 跳转个人中心
  }
}

onMounted(async () => {
  if (authStore.isLoggedIn && !authStore.userInfo) {
    await authStore.fetchUserInfo()
  }
})
</script>

<style scoped lang="scss">
.admin-layout {
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  background-color: #001529;
  transition: width var(--transition-duration);
  overflow: hidden;

  .logo {
    height: var(--header-height);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 16px;
    border-bottom: 1px solid #ffffff1a;

    .logo-img {
      width: 32px;
      height: 32px;
    }

    .logo-text {
      color: #fff;
      font-size: 16px;
      font-weight: 600;
      margin-left: 10px;
      white-space: nowrap;
    }
  }

  .el-menu {
    border-right: none;
    height: calc(100vh - var(--header-height));
    overflow-y: auto;
  }
}

.main-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: var(--header-height) !important;
  background: var(--color-bg-white);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: var(--shadow-sm);
  z-index: 10;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .collapse-btn {
      font-size: 20px;
      cursor: pointer;
      color: var(--color-text-regular);

      &:hover {
        color: var(--color-primary);
      }
    }
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      color: var(--color-text-regular);

      .user-name {
        font-size: 14px;
      }

      &:hover {
        color: var(--color-primary);
      }
    }
  }
}

.main-content {
  background: var(--color-bg-page);
  overflow-y: auto;
  padding: 20px;
}
</style>
