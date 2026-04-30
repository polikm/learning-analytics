<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2 class="login-title">学情数据统计分析平台</h2>
        <p class="login-subtitle">Learning Analytics Platform</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-width="0"
        size="large"
      >
        <el-form-item prop="tenantId">
          <el-select
            v-model="loginForm.tenantId"
            placeholder="请选择租户"
            style="width: 100%"
          >
            <el-option
              v-for="item in tenantList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <div class="login-options">
            <el-checkbox v-model="rememberPassword">记住密码</el-checkbox>
            <el-link type="primary" :underline="false">忘记密码?</el-link>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            style="width: 100%"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)
const rememberPassword = ref(false)

const loginForm = reactive({
  tenantId: '',
  username: '',
  password: '',
})

const loginRules: FormRules = {
  tenantId: [{ required: true, message: '请选择租户', trigger: 'change' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不少于6位', trigger: 'blur' },
  ],
}

const tenantList = ref([
  { value: '1', label: '示例学校' },
  { value: '2', label: '测试学校' },
])

async function handleLogin() {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await authStore.login({
        username: loginForm.username,
        password: loginForm.password,
        tenantId: loginForm.tenantId,
      })
      ElMessage.success('登录成功')
      const redirect = (route.query.redirect as string) || '/admin/dashboard'
      router.push(redirect)
    } catch (error: any) {
      ElMessage.error(error.message || '登录失败')
    } finally {
      loading.value = false
    }
  })
}

onMounted(() => {
  // 读取记住的密码
  const saved = localStorage.getItem('remembered_user')
  if (saved) {
    try {
      const data = JSON.parse(saved)
      loginForm.username = data.username || ''
      loginForm.password = data.password || ''
      loginForm.tenantId = data.tenantId || ''
      rememberPassword.value = true
    } catch {
      // ignore
    }
  }
})
</script>

<style scoped lang="scss">
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .login-card {
    width: 420px;
    padding: 40px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);

    .login-header {
      text-align: center;
      margin-bottom: 32px;

      .login-title {
        font-size: 24px;
        font-weight: 700;
        color: #303133;
        margin-bottom: 8px;
      }

      .login-subtitle {
        font-size: 13px;
        color: #909399;
      }
    }

    .login-options {
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
  }
}
</style>
