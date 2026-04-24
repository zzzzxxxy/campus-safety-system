<template>
  <div class="dashboard-container" v-loading="loading">
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
            <div class="card-header">
              <span class="card-title">预警趋势（近7天）</span>
              <el-tag size="small" type="info">来自 /report/dashboard</el-tag>
            </div>
          </template>
          <v-chart class="chart" :option="warningTrendOption" autoresize />
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">访客统计（本月每日）</span>
              <el-tag size="small" type="success">来自 /report/visitor-stats</el-tag>
            </div>
          </template>
          <v-chart class="chart" :option="visitorBarOption" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="mt-20">
      <el-col :xs="24" :md="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">设备类型分布</span>
              <el-tag size="small" type="info">来自 /report/device-stats</el-tag>
            </div>
          </template>
          <v-chart class="chart" :option="devicePieOption" autoresize />
        </el-card>
      </el-col>
      <el-col :xs="24" :md="16">
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

    <el-row :gutter="16" class="mt-20">
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">最近预警（最新5条）</span>
              <el-tag size="small" type="info">来自 /warning/record/page</el-tag>
            </div>
          </template>
          <el-table :data="recentWarnings" size="small" style="width: 100%" v-loading="recentLoading">
            <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
            <el-table-column prop="warningLevel" label="级别" width="80">
              <template #default="scope">
                <el-tag size="small" :type="levelTagType(scope.row.warningLevel)">{{ levelText(scope.row.warningLevel) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="scope">
                <el-tag size="small" :type="statusTagType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" width="160" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Warning, User, Monitor, Document } from '@element-plus/icons-vue'

import { getDashboard, getDeviceStats, getVisitorStats } from '@/api/report'
import { getWarningRecordPage } from '@/api/warning'
import { dictLabel, dictTagType, warningLevelOptions, warningRecordStatusOptions } from '@/utils/dict'

type TrendItem = { date: string; count: number }

type DashboardVO = {
  todayVisitorCount?: number
  totalDeviceCount?: number
  onlineDeviceCount?: number
  unhandledWarningCount?: number
  totalAssetCount?: number
  todayWarningCount?: number
  visitorTrend?: TrendItem[]
  warningTrend?: TrendItem[]
}

type DeviceTypeItem = { type: string; count: number }

type DeviceStatsVO = {
  typeDistribution?: DeviceTypeItem[]
}

type VisitorStatsVO = {
  dailyStats?: TrendItem[]
}

type WarningRecord = {
  id?: number
  title?: string
  warningType?: string | number
  warningLevel?: string | number
  status?: number
  createTime?: string
}

const loading = ref(false)
const dashboard = reactive<DashboardVO>({
  todayVisitorCount: 0,
  totalDeviceCount: 0,
  onlineDeviceCount: 0,
  unhandledWarningCount: 0,
  totalAssetCount: 0,
  todayWarningCount: 0,
  visitorTrend: [],
  warningTrend: []
})

const stats = computed(() => {
  return {
    warningCount: dashboard.unhandledWarningCount ?? 0,
    visitorCount: dashboard.todayVisitorCount ?? 0,
    assetCount: dashboard.totalAssetCount ?? 0,
    reportCount: dashboard.totalDeviceCount ?? 0
  }
})

const warningTrendOption = computed(() => {
  const list = Array.isArray(dashboard.warningTrend) ? dashboard.warningTrend : []
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: 36, right: 16, top: 16, bottom: 28 },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: list.map((i) => i.date)
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '预警数',
        type: 'line',
        smooth: true,
        data: list.map((i) => i.count),
        areaStyle: { opacity: 0.08 }
      }
    ]
  }
})

const visitorDailyStats = ref<TrendItem[]>([])
const visitorBarOption = computed(() => {
  const list = visitorDailyStats.value || []
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: 36, right: 16, top: 16, bottom: 28 },
    xAxis: {
      type: 'category',
      data: list.map((i) => i.date.slice(5))
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        name: '访客量',
        type: 'bar',
        barMaxWidth: 28,
        data: list.map((i) => i.count),
        itemStyle: {
          borderRadius: [6, 6, 0, 0],
          color: '#67c23a'
        }
      }
    ]
  }
})

const deviceTypeDistribution = ref<DeviceTypeItem[]>([])
const devicePieOption = computed(() => {
  const list = deviceTypeDistribution.value || []
  return {
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, left: 'center' },
    series: [
      {
        name: '设备类型',
        type: 'pie',
        radius: ['38%', '66%'],
        avoidLabelOverlap: true,
        itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
        label: { show: false },
        emphasis: { label: { show: true, fontSize: 14, fontWeight: 600 } },
        labelLine: { show: false },
        data: list.map((i) => ({ name: String(i.type), value: i.count }))
      }
    ]
  }
})

const recentLoading = ref(false)
const recentWarnings = ref<WarningRecord[]>([])

function levelText(level?: string | number) {
  return dictLabel(warningLevelOptions, level, '未知')
}

function levelTagType(level?: string | number) {
  return dictTagType(warningLevelOptions, level) || 'info'
}

function statusText(status?: number) {
  return dictLabel(warningRecordStatusOptions, status, '未知')
}

function statusTagType(status?: number) {
  return dictTagType(warningRecordStatusOptions, status) || 'info'
}

async function fetchDashboard() {
  loading.value = true
  try {
    const res = await getDashboard()
    const data = (res as any)?.data?.data as DashboardVO
    Object.assign(dashboard, data || {})
  } catch (e: any) {
    ElMessage.error(e?.message || '获取仪表盘数据失败')
  } finally {
    loading.value = false
  }
}

function currentMonthRange() {
  const now = new Date()
  const start = new Date(now.getFullYear(), now.getMonth(), 1)
  const end = new Date(now.getFullYear(), now.getMonth() + 1, 0)
  const format = (d: Date) => `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
  return { startDate: format(start), endDate: format(end) }
}

async function fetchVisitorStats() {
  try {
    const res = await getVisitorStats(currentMonthRange())
    const data = (res as any)?.data?.data as VisitorStatsVO
    visitorDailyStats.value = (data?.dailyStats || []) as any
  } catch (e: any) {
    ElMessage.error(e?.message || '获取访客统计失败')
  }
}

async function fetchDeviceStats() {
  try {
    const res = await getDeviceStats()
    const data = (res as any)?.data?.data as DeviceStatsVO
    deviceTypeDistribution.value = (data?.typeDistribution || []) as any
  } catch (e: any) {
    ElMessage.error(e?.message || '获取设备统计失败')
  }
}

async function fetchRecentWarnings() {
  recentLoading.value = true
  try {
    const res = await getWarningRecordPage({ pageNum: 1, pageSize: 5 })
    const payload = (res as any)?.data?.data
    const records = payload?.records || payload?.list || []
    recentWarnings.value = Array.isArray(records) ? records : []
  } catch (e: any) {
    ElMessage.error(e?.message || '获取最近预警失败')
  } finally {
    recentLoading.value = false
  }
}

onMounted(() => {
  fetchDashboard()
  fetchVisitorStats()
  fetchDeviceStats()
  fetchRecentWarnings()
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

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.chart {
  width: 100%;
  height: 320px;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
