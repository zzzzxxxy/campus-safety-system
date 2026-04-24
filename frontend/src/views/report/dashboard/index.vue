<template>
  <div class="report-page" v-loading="loading">
    <el-card shadow="never" class="filter-card">
      <div class="filter-row">
        <div class="title-block">
          <h2>数据报表</h2>
          <p>设备、预警、访客统计可视化分析</p>
        </div>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          :clearable="false"
          style="width: 300px"
          @change="fetchAll"
        />
      </div>
    </el-card>

    <el-row :gutter="16" class="stat-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="item in statCards" :key="item.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>F130 设备状态统计</template>
          <v-chart class="chart" :option="deviceStatusOption" autoresize />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>F130 设备类型分布</template>
          <v-chart class="chart" :option="deviceTypeOption" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>F131 预警级别分布</template>
          <v-chart class="chart" :option="warningLevelOption" autoresize />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>F131 预警类型占比</template>
          <v-chart class="chart" :option="warningTypeOption" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>F131 预警月度趋势</template>
          <v-chart class="chart" :option="warningTrendOption" autoresize />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>F132 访客每日统计</template>
          <v-chart class="chart" :option="visitorDailyOption" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>F133 资产类型分布</template>
          <v-chart class="chart" :option="assetTypeOption" autoresize />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>F133 资产区域分布</template>
          <v-chart class="chart" :option="assetLocationOption" autoresize />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAssetStats, getDeviceStats, getVisitorStats, getWarningStats } from '@/api/report'
import { dictLabel, deviceStatusOptions, warningLevelOptions, warningTypeOptions } from '@/utils/dict'

type CountItem = { type?: string | number; level?: string | number; date?: string; month?: string; count: number }
type DeviceStats = { totalCount?: number; normalCount?: number; faultCount?: number; repairCount?: number; scrapCount?: number; typeDistribution?: CountItem[] }
type WarningStats = { totalCount?: number; unhandledCount?: number; handlingCount?: number; handledCount?: number; closedCount?: number; levelDistribution?: CountItem[]; typeDistribution?: CountItem[]; monthlyTrend?: CountItem[] }
type VisitorStats = { totalCount?: number; pendingCount?: number; approvedCount?: number; rejectedCount?: number; dailyStats?: CountItem[] }
type AssetStats = { totalCount?: number; totalValue?: number; typeDistribution?: CountItem[]; locationDistribution?: CountItem[] }

const loading = ref(false)
const dateRange = ref(defaultRange())
const deviceStats = ref<DeviceStats>({})
const warningStats = ref<WarningStats>({})
const visitorStats = ref<VisitorStats>({})
const assetStats = ref<AssetStats>({})

function formatDate(d: Date) {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

function defaultRange() {
  const end = new Date()
  const start = new Date()
  start.setDate(end.getDate() - 29)
  return [formatDate(start), formatDate(end)]
}

const params = computed(() => ({ startDate: dateRange.value[0], endDate: dateRange.value[1] }))

const statCards = computed(() => [
  { label: '设备总数', value: deviceStats.value.totalCount ?? 0 },
  { label: '预警总数', value: warningStats.value.totalCount ?? 0 },
  { label: '访客总数', value: visitorStats.value.totalCount ?? 0 },
  { label: '资产总数', value: assetStats.value.totalCount ?? 0 }
])

const deviceStatusOption = computed(() => {
  const data = [
    { name: '正常', value: deviceStats.value.normalCount ?? 0 },
    { name: '故障', value: deviceStats.value.faultCount ?? 0 },
    { name: '维修', value: deviceStats.value.repairCount ?? 0 },
    { name: '报废', value: deviceStats.value.scrapCount ?? 0 }
  ]
  return barOption(data.map((i) => i.name), data.map((i) => i.value), '#409eff', '设备数')
})

const deviceTypeOption = computed(() => pieOption('设备类型', (deviceStats.value.typeDistribution || []).map((i) => ({ name: String(i.type || '未知'), value: i.count }))))

const warningLevelOption = computed(() => pieOption('预警级别', (warningStats.value.levelDistribution || []).map((i) => ({ name: dictLabel(warningLevelOptions, i.level, '未知'), value: i.count }))))

const warningTypeOption = computed(() => pieOption('预警类型', (warningStats.value.typeDistribution || []).map((i) => ({ name: dictLabel(warningTypeOptions, i.type, '未知'), value: i.count }))))

const warningTrendOption = computed(() => {
  const list = warningStats.value.monthlyTrend || []
  return lineOption(list.map((i) => i.month || ''), list.map((i) => i.count), '预警数')
})

const visitorDailyOption = computed(() => {
  const list = visitorStats.value.dailyStats || []
  return barOption(list.map((i) => String(i.date || '').slice(5)), list.map((i) => i.count), '#67c23a', '访客量')
})

const assetTypeOption = computed(() => pieOption('资产类型', (assetStats.value.typeDistribution || []).map((i) => ({ name: String(i.type || '未知'), value: i.count }))))

const assetLocationOption = computed(() => barOption((assetStats.value.locationDistribution || []).map((i) => String(i.type || '未填写')), (assetStats.value.locationDistribution || []).map((i) => i.count), '#e6a23c', '资产数'))

function barOption(xData: string[], yData: number[], color: string, name: string) {
  return { tooltip: { trigger: 'axis' }, grid: { left: 42, right: 20, top: 28, bottom: 36 }, xAxis: { type: 'category', data: xData }, yAxis: { type: 'value', minInterval: 1 }, series: [{ name, type: 'bar', barMaxWidth: 34, data: yData, itemStyle: { color, borderRadius: [6, 6, 0, 0] } }] }
}

function lineOption(xData: string[], yData: number[], name: string) {
  return { tooltip: { trigger: 'axis' }, grid: { left: 42, right: 20, top: 28, bottom: 36 }, xAxis: { type: 'category', boundaryGap: false, data: xData }, yAxis: { type: 'value', minInterval: 1 }, series: [{ name, type: 'line', smooth: true, data: yData, areaStyle: { opacity: 0.08 } }] }
}

function pieOption(name: string, data: Array<{ name: string; value: number }>) {
  return { tooltip: { trigger: 'item' }, legend: { bottom: 0, left: 'center' }, series: [{ name, type: 'pie', radius: ['38%', '65%'], center: ['50%', '45%'], itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 }, data }] }
}

async function fetchAll() {
  loading.value = true
  try {
    const [deviceRes, warningRes, visitorRes, assetRes] = await Promise.all([
      getDeviceStats(),
      getWarningStats(params.value),
      getVisitorStats(params.value),
      getAssetStats()
    ])
    deviceStats.value = (deviceRes as any)?.data?.data || {}
    warningStats.value = (warningRes as any)?.data?.data || {}
    visitorStats.value = (visitorRes as any)?.data?.data || {}
    assetStats.value = (assetRes as any)?.data?.data || {}
  } catch (e: any) {
    ElMessage.error(e?.message || '加载报表数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(fetchAll)
</script>

<style scoped lang="scss">
.report-page {
  padding: 20px;
}
.filter-card,
.stat-row,
.chart-card {
  margin-bottom: 16px;
}
.filter-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}
.title-block h2 {
  margin: 0 0 6px;
  font-size: 22px;
  color: #303133;
}
.title-block p {
  margin: 0;
  color: #909399;
}
.stat-card {
  .stat-value {
    font-size: 28px;
    font-weight: 700;
    color: #303133;
  }
  .stat-label {
    margin-top: 6px;
    color: #909399;
  }
}
.chart {
  width: 100%;
  height: 340px;
}
</style>
