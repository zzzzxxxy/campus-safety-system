<template>
  <div class="page-container">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="模块">
          <el-input v-model="query.module" placeholder="请输入模块" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="query.operUser" placeholder="请输入操作人" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="请求方式">
          <el-select v-model="query.requestMethod" placeholder="全部" clearable style="width: 130px">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="成功" :value="0" />
            <el-option label="异常" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 360px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="danger" plain @click="handleClear">清空日志</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchList">刷新</el-button>
        </div>
      </div>

      <el-table :data="tableData" border v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="description" label="操作描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="requestMethod" label="方式" width="90">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ row.requestMethod }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operUrl" label="请求URL" min-width="220" show-overflow-tooltip />
        <el-table-column prop="operIp" label="IP" width="140" />
        <el-table-column prop="operUser" label="操作人" width="120" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="Number(row.status) === 0 ? 'success' : 'danger'">
              {{ Number(row.status) === 0 ? '成功' : '异常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operTime" label="操作时间" width="180" />
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @current-change="fetchList"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="操作日志详情" width="900px" destroy-on-close>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="模块">{{ detail.module }}</el-descriptions-item>
        <el-descriptions-item label="操作描述">{{ detail.description }}</el-descriptions-item>
        <el-descriptions-item label="请求方式">{{ detail.requestMethod }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detail.operUser || '-' }}</el-descriptions-item>
        <el-descriptions-item label="请求URL" :span="2">{{ detail.operUrl }}</el-descriptions-item>
        <el-descriptions-item label="方法" :span="2">{{ detail.method }}</el-descriptions-item>
        <el-descriptions-item label="IP">{{ detail.operIp }}</el-descriptions-item>
        <el-descriptions-item label="时间">{{ detail.operTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ Number(detail.status) === 0 ? '成功' : '异常' }}</el-descriptions-item>
        <el-descriptions-item label="错误信息">{{ detail.errorMsg || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-divider content-position="left">请求参数</el-divider>
      <pre class="json-box">{{ detail.operParam || '-' }}</pre>
      <el-divider content-position="left">响应结果</el-divider>
      <pre class="json-box">{{ detail.jsonResult || '-' }}</pre>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { clearOperLog, getOperLogPage } from '@/api/system'

interface OperLog {
  id: number
  module?: string
  description?: string
  method?: string
  requestMethod?: string
  operUrl?: string
  operIp?: string
  operParam?: string
  jsonResult?: string
  status?: number
  errorMsg?: string
  operTime?: string
  operUser?: string
}

const loading = ref(false)
const tableData = ref<OperLog[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dateRange = ref<string[]>([])
const detailVisible = ref(false)
const detail = ref<OperLog>({ id: 0 })

const query = reactive({
  module: '',
  operUser: '',
  requestMethod: '',
  status: undefined as number | undefined
})

async function fetchList() {
  loading.value = true
  try {
    const res = await getOperLogPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...query,
      startTime: dateRange.value?.[0],
      endTime: dateRange.value?.[1]
    })
    const data = res.data.data
    tableData.value = data.records || []
    total.value = Number(data.total || 0)
  } catch (e: any) {
    ElMessage.error(e?.message || '加载操作日志失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  fetchList()
}

function handleReset() {
  query.module = ''
  query.operUser = ''
  query.requestMethod = ''
  query.status = undefined
  dateRange.value = []
  pageNum.value = 1
  fetchList()
}

function handleSizeChange(size: number) {
  pageSize.value = size
  pageNum.value = 1
  fetchList()
}

function openDetail(row: OperLog) {
  detail.value = row
  detailVisible.value = true
}

async function handleClear() {
  await ElMessageBox.confirm('确认清空全部操作日志吗？此操作不可恢复。', '提示', { type: 'warning' })
  await clearOperLog()
  ElMessage.success('已清空')
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped lang="scss">
.search-card {
  margin-bottom: 16px;
}
.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
.json-box {
  max-height: 220px;
  overflow: auto;
  padding: 12px;
  border-radius: 8px;
  background: #f6f8fa;
  color: #303133;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
