<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <div class="login-logo">
          <el-icon :size="36" color="#409eff"><School /></el-icon>
        </div>
        <h2 class="login-title">校园安全管理系统</h2>
        <p class="login-subtitle">Campus Safety Management System</p>
      </div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        size="large"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <div class="login-options">
          <el-checkbox v-model="rememberPassword">记住密码</el-checkbox>
          <span class="campus-theme-tip">平安校园 · 智慧守护</span>
        </div>
        <el-form-item>
          <el-button
            type="primary"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <span>毕业设计 &copy; 校园安全管理系统</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, School } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const rememberPassword = ref(false)
const REMEMBER_KEY = 'campus_safety_remember_login'

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 5, max: 20, message: '密码长度在 5 到 20 个字符', trigger: 'blur' }
  ]
})

const doLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    await userStore.login({ username: loginForm.username, password: loginForm.password })

    if (rememberPassword.value) {
      localStorage.setItem(REMEMBER_KEY, JSON.stringify({
        username: loginForm.username,
        password: btoa(encodeURIComponent(loginForm.password))
      }))
    } else {
      localStorage.removeItem(REMEMBER_KEY)
    }

    // Preload user info (roles/menus) to avoid router guard immediately resetting token
    // when /auth/info fails.
    await userStore.getInfo()

    const redirect = (route.query.redirect as string) || '/'
    await router.push(redirect)
    ElMessage.success('登录成功')
  } catch (error: any) {
    // Error already handled by request interceptor
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  try {
    const raw = localStorage.getItem(REMEMBER_KEY)
    if (!raw) return
    const saved = JSON.parse(raw)
    loginForm.username = saved?.username || ''
    loginForm.password = saved?.password ? decodeURIComponent(atob(saved.password)) : ''
    rememberPassword.value = Boolean(loginForm.username && loginForm.password)
  } catch {
    localStorage.removeItem(REMEMBER_KEY)
  }
})

const handleLogin = async () => {
  // Element Plus Form ref may not be available in some build/auto-import setups.
  // Fall back to manual validation to avoid blocking login.
  const form = loginFormRef.value as any
  if (form && typeof form.validate === 'function') {
    try {
      const valid = await form.validate()
      if (!valid) return
      await doLogin()
    } catch {
      // validate() may throw when invalid
      return
    }
  } else {
    await doLogin()
  }
}
</script>

<style scoped lang="scss">
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100vh;
  position: relative;
  overflow: hidden;
  background:
    radial-gradient(circle at 15% 20%, rgba(64, 158, 255, 0.24), transparent 28%),
    radial-gradient(circle at 82% 18%, rgba(103, 194, 58, 0.18), transparent 26%),
    linear-gradient(135deg, #eef6ff 0%, #f7fbf4 48%, #edf2f7 100%);

  &::before,
  &::after {
    content: '';
    position: absolute;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.48);
    filter: blur(1px);
  }

  &::before {
    width: 360px;
    height: 360px;
    left: -120px;
    bottom: -110px;
  }

  &::after {
    width: 260px;
    height: 260px;
    right: -90px;
    top: -80px;
  }
}

.login-card {
  width: 420px;
  padding: 40px 36px 30px;
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10px);
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(64, 158, 255, 0.12);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-logo {
  margin-bottom: 12px;
}

.login-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 6px 0;
  letter-spacing: 2px;
}

.login-subtitle {
  font-size: 13px;
  color: #909399;
  margin: 0;
  letter-spacing: 1px;
}

.login-form {
  .el-form-item {
    margin-bottom: 22px;
  }
}

.login-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: -4px 0 18px;
  font-size: 13px;
}

.campus-theme-tip {
  color: #67c23a;
}

.login-button {
  width: 100%;
  height: 42px;
  font-size: 15px;
  letter-spacing: 4px;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 12px;
  color: #c0c4cc;
}
</style>
