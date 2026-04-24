<template>
  <div class="error-page forbidden">
    <div class="error-card">
      <div class="code">403</div>
      <div class="title">无权限访问</div>
      <div class="desc">当前账号没有访问该页面或资源的权限，请联系管理员开通权限。</div>
      <div class="actions">
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="goBack">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
const goHome = () => router.push('/')
const goBack = () => router.back()
</script>

<style scoped lang="scss">
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f6f9ff 0%, #eef3fb 100%);
}

.error-card {
  width: min(560px, calc(100vw - 32px));
  padding: 48px 40px;
  text-align: center;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 18px 45px rgb(31 45 61 / 10%);
}

.code {
  font-size: 112px;
  line-height: 1;
  font-weight: 800;
  color: #e6a23c;
  letter-spacing: 4px;
}

.title {
  margin-top: 18px;
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}

.desc {
  margin: 14px auto 28px;
  max-width: 420px;
  color: #606266;
  line-height: 1.8;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}
</style>
