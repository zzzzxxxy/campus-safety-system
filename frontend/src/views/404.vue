<template>
  <div class="not-found">
    <h1>404</h1>
    <p>页面不存在</p>
    <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #f0f2f5;

  h1 {
    font-size: 120px;
    color: #409eff;
    margin-bottom: 10px;
    font-weight: 700;
  }

  p {
    font-size: 20px;
    color: #606266;
    margin-bottom: 30px;
  }
}
</style>
