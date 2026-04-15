<template>
  <div class="dashboard-container">
    <div class="welcome-section">
      <h2 class="welcome-title">欢迎使用校园安全管理系统</h2>
      <p class="welcome-desc">Campus Safety Management System</p>
    </div>

    <el-row :gutter="16" class="stat-cards">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background-color: rgba(64, 158, 255, 0.1); color: #409eff;">
              <el-icon :size="28"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.warningCount }}</div>
              <div class="stat-label">预警信息</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background-color: rgba(103, 194, 58, 0.1); color: #67c23a;">
              <el-icon :size="28"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.visitorCount }}</div>
              <div class="stat-label">访客记录</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background-color: rgba(230, 162, 60, 0.1); color: #e6a23c;">
              <el-icon :size="28"><Monitor /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.assetCount }}</div>
              <div class="stat-label">资产设备</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-inner">
            <div class="stat-icon" style="background-color: rgba(245, 108, 108, 0.1); color: #f56c6c;">
              <el-icon :size="28"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.reportCount }}</div>
              <div class="stat-label">安全报告</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="mt-20">
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">系统公告</span>
          </template>
          <div class="notice-list">
            <div class="notice-item">
              <el-tag size="small" type="danger">重要</el-tag>
              <span class="notice-text">校园安全管理系统已上线运行</span>
            </div>
            <div class="notice-item">
              <el-tag size="small" type="info">通知</el-tag>
              <span class="notice-text">请各部门及时更新安全设备信息</span>
            </div>
            <div class="notice-item">
              <el-tag size="small" type="warning">提醒</el-tag>
              <span class="notice-text">本月安全检查即将开始</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" plain :icon="Warning">预警管理</el-button>
            <el-button type="success" plain :icon="User">访客登记</el-button>
            <el-button type="warning" plain :icon="Monitor">资产巡检</el-button>
            <el-button type="danger" plain :icon="Document">生成报告</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { Warning, User, Monitor, Document } from '@element-plus/icons-vue'

const stats = reactive({
  warningCount: 12,
  visitorCount: 58,
  assetCount: 236,
  reportCount: 7
})
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 20px;
}

.welcome-section {
  margin-bottom: 24px;
}

.welcome-title {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 6px 0;
}

.welcome-desc {
  font-size: 13px;
  color: #909399;
  margin: 0;
}

.stat-cards {
  .el-col {
    margin-bottom: 12px;
  }
}

.stat-card {
  .stat-card-inner {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .stat-info {
    flex: 1;
  }

  .stat-value {
    font-size: 28px;
    font-weight: 700;
    color: #303133;
    line-height: 1.2;
  }

  .stat-label {
    font-size: 13px;
    color: #909399;
    margin-top: 4px;
  }
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.notice-list {
  .notice-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px 0;
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);

    &:last-child {
      border-bottom: none;
    }
  }

  .notice-text {
    font-size: 14px;
    color: #606266;
  }
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
