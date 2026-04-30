import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi, getUserInfo as getUserInfoApi, type LoginData, type UserInfo } from '@/api/auth'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  // State
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const userRole = computed(() => userInfo.value?.role || '')
  const userName = computed(() => userInfo.value?.realName || userInfo.value?.username || '')

  // Actions
  async function login(data: LoginData) {
    const res = await loginApi(data)
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    if (res.data.refreshToken) {
      localStorage.setItem('refreshToken', res.data.refreshToken)
    }
    // 登录成功后获取用户信息
    await fetchUserInfo()
  }

  async function fetchUserInfo() {
    try {
      const res = await getUserInfoApi()
      userInfo.value = res.data
    } catch {
      // 获取用户信息失败，清除token
      clearAuth()
    }
  }

  async function logout() {
    try {
      await logoutApi()
    } finally {
      clearAuth()
      router.push('/login')
    }
  }

  function clearAuth() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    userRole,
    userName,
    login,
    logout,
    fetchUserInfo,
  }
})
