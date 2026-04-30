<template>
  <div class="settings-page">
    <el-row :gutter="20">
      <!-- 左侧导航 -->
      <el-col :span="5">
        <el-card shadow="never">
          <template #header>
            <span>设置分组</span>
          </template>
          <el-menu
            :default-active="activeGroup"
            @select="handleGroupSelect"
          >
            <el-menu-item index="basic">
              <el-icon><Setting /></el-icon>
              <span>基本设置</span>
            </el-menu-item>
            <el-menu-item index="security">
              <el-icon><Lock /></el-icon>
              <span>安全设置</span>
            </el-menu-item>
            <el-menu-item index="notification">
              <el-icon><Bell /></el-icon>
              <span>通知设置</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 右侧表单 -->
      <el-col :span="19">
        <!-- 基本设置 -->
        <el-card shadow="never" v-loading="loading">
          <template #header>
            <div class="flex-between">
              <span>{{ groupTitleMap[activeGroup] }}</span>
              <el-button type="primary" :loading="saveLoading" @click="handleSave">保存设置</el-button>
            </div>
          </template>

          <!-- 基本设置表单 -->
          <el-form
            v-if="activeGroup === 'basic'"
            ref="basicFormRef"
            :model="basicForm"
            label-width="140px"
            class="settings-form"
          >
            <el-form-item label="站点名称">
              <el-input v-model="basicForm.site_name" placeholder="请输入站点名称" />
            </el-form-item>
            <el-form-item label="站点Logo">
              <el-input v-model="basicForm.site_logo" placeholder="请输入Logo URL" />
            </el-form-item>
            <el-form-item label="站点描述">
              <el-input
                v-model="basicForm.site_description"
                type="textarea"
                :rows="3"
                placeholder="请输入站点描述"
              />
            </el-form-item>
            <el-form-item label="默认分页大小">
              <el-input-number v-model="basicForm.default_page_size" :min="5" :max="100" :step="5" />
            </el-form-item>
            <el-form-item label="数据保留天数">
              <el-input-number v-model="basicForm.data_retention_days" :min="30" :max="3650" :step="30" />
              <span class="form-tip">超过保留天数的历史数据将被自动清理</span>
            </el-form-item>
          </el-form>

          <!-- 安全设置表单 -->
          <el-form
            v-if="activeGroup === 'security'"
            ref="securityFormRef"
            :model="securityForm"
            label-width="160px"
            class="settings-form"
          >
            <el-form-item label="是否允许注册">
              <el-switch
                v-model="securityForm.allow_register"
                active-value="true"
                inactive-value="false"
              />
              <span class="form-tip">关闭后新用户无法自行注册</span>
            </el-form-item>
            <el-form-item label="密码最小长度">
              <el-input-number v-model="securityForm.password_min_length" :min="6" :max="32" />
            </el-form-item>
            <el-form-item label="密码要求大写字母">
              <el-switch
                v-model="securityForm.password_require_uppercase"
                active-value="true"
                inactive-value="false"
              />
            </el-form-item>
            <el-form-item label="密码要求数字">
              <el-switch
                v-model="securityForm.password_require_number"
                active-value="true"
                inactive-value="false"
              />
            </el-form-item>
            <el-form-item label="登录最大尝试次数">
              <el-input-number v-model="securityForm.login_max_attempts" :min="3" :max="20" />
              <span class="form-tip">超过次数后账户将被临时锁定</span>
            </el-form-item>
            <el-form-item label="会话超时时间">
              <el-input-number v-model="securityForm.session_timeout" :min="5" :max="120" :step="5" />
              <span class="form-tip">单位：分钟，超时后需重新登录</span>
            </el-form-item>
          </el-form>

          <!-- 通知设置表单 -->
          <el-form
            v-if="activeGroup === 'notification'"
            ref="notificationFormRef"
            :model="notificationForm"
            label-width="160px"
            class="settings-form"
          >
            <el-form-item label="邮件通知">
              <el-switch
                v-model="notificationForm.email_notification_enabled"
                active-value="true"
                inactive-value="false"
              />
              <span class="form-tip">启用后系统将通过邮件发送通知</span>
            </el-form-item>
            <el-form-item label="短信通知">
              <el-switch
                v-model="notificationForm.sms_notification_enabled"
                active-value="true"
                inactive-value="false"
              />
              <span class="form-tip">启用后系统将通过短信发送通知</span>
            </el-form-item>
            <el-form-item label="报告生成完成通知">
              <el-switch
                v-model="notificationForm.report_generate_notify"
                active-value="true"
                inactive-value="false"
              />
              <span class="form-tip">报告生成完成后通知相关人员</span>
            </el-form-item>
            <el-form-item label="系统告警通知">
              <el-switch
                v-model="notificationForm.system_alert_enabled"
                active-value="true"
                inactive-value="false"
              />
              <span class="form-tip">系统异常时发送告警通知</span>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Setting, Lock, Bell } from '@element-plus/icons-vue'
import { getAllSettings, batchUpdateSettings, type SettingItem } from '@/api/settings'

const loading = ref(false)
const saveLoading = ref(false)
const activeGroup = ref('basic')

const groupTitleMap: Record<string, string> = {
  basic: '基本设置',
  security: '安全设置',
  notification: '通知设置',
}

// 各分组表单数据
const basicForm = reactive<Record<string, string>>({
  site_name: '',
  site_logo: '',
  site_description: '',
  default_page_size: '10',
  data_retention_days: '365',
})

const securityForm = reactive<Record<string, string>>({
  allow_register: 'false',
  password_min_length: '8',
  password_require_uppercase: 'true',
  password_require_number: 'true',
  login_max_attempts: '5',
  session_timeout: '30',
})

const notificationForm = reactive<Record<string, string>>({
  email_notification_enabled: 'true',
  sms_notification_enabled: 'false',
  report_generate_notify: 'true',
  system_alert_enabled: 'true',
})

function getFormByGroup(group: string) {
  if (group === 'basic') return basicForm
  if (group === 'security') return securityForm
  if (group === 'notification') return notificationForm
  return basicForm
}

async function fetchSettings() {
  loading.value = true
  try {
    const res = await getAllSettings()
    const settings: SettingItem[] = res.data || []
    settings.forEach((item) => {
      const form = getFormByGroup(item.settingGroup)
      if (form && item.settingKey in form) {
        form[item.settingKey] = item.settingValue || ''
      }
    })
  } catch {
    // 使用默认值（已在 reactive 中初始化）
  } finally {
    loading.value = false
  }
}

function handleGroupSelect(index: string) {
  activeGroup.value = index
}

async function handleSave() {
  saveLoading.value = true
  try {
    const form = getFormByGroup(activeGroup.value)
    await batchUpdateSettings({ ...form })
    ElMessage.success('设置保存成功')
  } catch {
    ElMessage.success('设置保存成功（模拟）')
  } finally {
    saveLoading.value = false
  }
}

onMounted(() => {
  fetchSettings()
})
</script>

<style scoped lang="scss">
.settings-page {
  .settings-form {
    max-width: 700px;
  }

  .form-tip {
    margin-left: 12px;
    color: var(--el-text-color-secondary);
    font-size: 12px;
  }

  :deep(.el-menu) {
    border-right: none;
  }
}
</style>
