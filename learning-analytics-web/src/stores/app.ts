import { defineStore } from 'pinia'
import { ref } from 'vue'

/** 租户信息 */
export interface TenantInfo {
  id: number
  name: string
  code: string
  logo?: string
  contactName?: string
  contactPhone?: string
}

export const useAppStore = defineStore('app', () => {
  // State
  const sidebarCollapsed = ref(false)
  const tenantInfo = ref<TenantInfo | null>(null)

  // Actions
  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  function setSidebarCollapsed(val: boolean) {
    sidebarCollapsed.value = val
  }

  function setTenantInfo(info: TenantInfo) {
    tenantInfo.value = info
  }

  return {
    sidebarCollapsed,
    tenantInfo,
    toggleSidebar,
    setSidebarCollapsed,
    setTenantInfo,
  }
})
