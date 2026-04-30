import dayjs from 'dayjs'

/**
 * 格式化日期
 * @param date 日期值
 * @param format 格式模板，默认 YYYY-MM-DD HH:mm:ss
 */
export function formatDate(date: string | Date | number | null | undefined, format = 'YYYY-MM-DD HH:mm:ss'): string {
  if (!date) return ''
  return dayjs(date).format(format)
}

/**
 * 格式化日期为短格式
 */
export function formatDateShort(date: string | Date | number | null | undefined): string {
  return formatDate(date, 'YYYY-MM-DD')
}

/**
 * 判断当前用户是否拥有指定权限
 * @param permissions 所需权限列表
 * @param userRole 当前用户角色
 */
export function hasPermission(permissions: string[], userRole?: string): boolean {
  if (!userRole) return false
  // 管理员拥有所有权限
  if (userRole === 'admin' || userRole === 'ADMIN') return true
  return permissions.includes(userRole)
}

/**
 * 下载文件
 * @param url 文件地址
 * @param filename 文件名
 */
export function downloadFile(url: string, filename?: string) {
  const link = document.createElement('a')
  link.href = url
  if (filename) {
    link.download = filename
  }
  link.click()
}

/**
 * 重置表单
 * @param formRef 表单引用
 */
export function resetForm(formRef: any) {
  if (formRef && formRef.value) {
    formRef.value.resetFields()
  }
}

/**
 * 防抖函数
 */
export function debounce(fn: Function, delay = 300) {
  let timer: ReturnType<typeof setTimeout> | null = null
  return function (this: any, ...args: any[]) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

/**
 * 获取存储的token
 */
export function getToken(): string {
  return localStorage.getItem('token') || ''
}

/**
 * 设置token
 */
export function setToken(token: string) {
  localStorage.setItem('token', token)
}

/**
 * 移除token
 */
export function removeToken() {
  localStorage.removeItem('token')
  localStorage.removeItem('refreshToken')
}
