<template>
  <div class="page-container">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="标题">
          <el-input v-model="query.title" placeholder="请输入" clearable style="width: 220px" />
        </el-form-item>

        <el-form-item label="危险级别">
          <el-select v-model="query.warningLevel" placeholder="全部" clearable style="width: 160px">
            <el-option v-for="it in warningLevelOptions" :key="it.value" :label="it.label" :value="it.value" />
          </el-select>
        </el-form-item>

        <el-form-item label="处理状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 160px">
            <el-option v-for="it in statusOptions" :key="it.value" :label="it.label" :value="it.value" />
          </el-select>
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
          <!-- 记录新增/编辑/删除通常不在大厅高频使用，这里先不放按钮 -->
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchList">刷新</el-button>
        </div>
      </div>

      <el-table :data="tableData" border v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="warningType" label="类型" width="120" show-overflow-tooltip />
        <el-table-column prop="warningLevel" label="危险级别" width="120">
          <template #default="{ row }">
            <el-tag :type="levelTagType(row.warningLevel)">{{ levelLabel(row.warningLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="source" label="来源" width="120" show-overflow-tooltip />
        <el-table-column prop="location" label="位置" min-width="140" show-overflow-tooltip />
        <el-table-column prop="status" label="处理状态" width="140">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handler" label="处理人" width="120" />
        <el-table-column prop="handleTime" label="处理时间" min-width="170" />
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="info" link v-permission="'warning:record:list'" @click="openDetail(row)">详情</el-button>
            <el-button
              v-if="canHandle(row)"
              type="primary"
              link
              v-permission="'warning:record:handle'"
              @click="openHandle(row)"
            >处理</el-button>
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

    <!-- 详情 -->
    <el-dialog v-model="detailVisible" title="预警详情" width="760px" destroy-on-close>
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="标题">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ detail.warningType }}</el-descriptions-item>
        <el-descriptions-item label="危险级别">
          <el-tag :type="levelTagType(detail.warningLevel)">{{ levelLabel(detail.warningLevel) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="来源">{{ detail.source }}</el-descriptions-item>
        <el-descriptions-item label="位置">{{ detail.location }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag :type="statusTagType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理人">{{ detail.handler || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ detail.handleTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ detail.content || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2">{{ detail.handleResult || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 处理（F122） -->
    <el-dialog v-model="handleVisible" title="处理预警" width="560px" destroy-on-close>
      <el-form ref="handleRef" :model="handleForm" :rules="handleRules" label-width="90px">
        <el-form-item label="处理结果" prop="status">
          <el-radio-group v-model="handleForm.status">
            <el-radio :value="2">已处理</el-radio>
            <el-radio :value="3">误报</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="handleResult">
          <el-input v-model="handleForm.handleResult" type="textarea" :rows="4" placeholder="请输入处理说明" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="handleVisible = false">取消</el-button>
        <el-button type="primary" :loading="handleLoading" @click="submitHandle">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getWarningRecordPage, handleWarningRecord } from '@/api/warning'
import request from '@/utils/request'

interface WarningRecordRow {
  id: number
  title?: string
  warningType?: string
  warningLevel?: string | number
  content?: string
  source?: string
  location?: string
  status?: number
  handler?: string
  handleTime?: string
  handleResult?: string
  remark?: string
  createTime?: string
}

const warningLevelOptions = [
  { label: '低', value: 1 },
  { label: '中', value: 2 },
  { label: '高', value: 3 },
  { label: '严重', value: 4 }
]

// 处理状态：按需求“待处理/已处理/误报”
const statusOptions = [
  { label: '待处理', value: 0 },
  { label: '已处理', value: 2 },
  { label: '误报', value: 3 }
]

function levelLabel(v: any) {
  const n = Number(v)
  return warningLevelOptions.find((x) => x.value === n)?.label || String(v ?? '')
}

// 必须根据危险级别上色：严重 danger，中度 warning，高 danger，低 info
const levelTagType = (level?: any): 'primary' | 'success' | 'warning' | 'info' | 'danger' | undefined => {
  const n = Number(level)
  if (n === 1) return 'info'
  if (n === 2) return 'warning'
  if (n === 3) return 'danger'
  if (n === 4) return 'danger'
  return undefined
}

function statusLabel(v: any) {
  const n = Number(v)
  if (n === 0) return '待处理'
  if (n === 1) return '处理中'
  if (n === 2) return '已处理'
  if (n === 3) return '误报'
  return String(v ?? '')
}

const statusTagType = (status?: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' | undefined => {
  const n = Number(status)
  if (n === 0) return 'warning'
  if (n === 1) return 'warning'
  if (n === 2) return 'success'
  if (n === 3) return 'info'
  return undefined
}

function canHandle(row: WarningRecordRow) {
  return Number(row.status) === 0 || Number(row.status) === 1
}

const loading = ref(false)
const tableData = ref<WarningRecordRow[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const query = reactive({
  title: '',
  warningLevel: undefined as number | undefined,
  status: undefined as number | undefined
})

function handleSearch() {
  pageNum.value = 1
  fetchList()
}

function handleReset() {
  query.title = ''
  query.warningLevel = undefined
  query.status = undefined
  pageNum.value = 1
  fetchList()
}

function handleSizeChange() {
  pageNum.value = 1
  fetchList()
}

async function fetchList() {
  loading.value = true
  try {
    const res: any = await getWarningRecordPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      title: query.title,
      warningLevel: query.warningLevel,
      status: query.status
    })
    const data = res.data.data
    tableData.value = data.records || []
    total.value = Number(data.total || 0)
  } finally {
    loading.value = false
  }
}

// ========== 详情 ==========
const detailVisible = ref(false)
const detail = ref<WarningRecordRow | null>(null)

async function openDetail(row: WarningRecordRow) {
  detailVisible.value = true
  detail.value = null
  const res: any = await request.get(`/warning/record/${row.id}`)
  detail.value = res?.data || null
}

// ========== 处理（F122） ==========
const handleVisible = ref(false)
const handleLoading = ref(false)
const handleRef = ref<FormInstance>()

const handleForm = reactive({
  id: undefined as number | undefined,
  status: 2 as number,
  handleResult: ''
})

const handleRules: FormRules = {
  status: [{ required: true, message: '请选择处理结果', trigger: 'change' }],
  handleResult: [{ required: true, message: '请输入备注', trigger: 'blur' }]
}

function openHandle(row: WarningRecordRow) {
  handleForm.id = row.id
  handleForm.status = 2
  handleForm.handleResult = ''
  handleVisible.value = true
  nextTick(() => handleRef.value?.clearValidate())
}

async function submitHandle() {
  if (!handleRef.value) return
  await handleRef.value.validate()
  handleLoading.value = true
  try {
    await handleWarningRecord({
      id: Number(handleForm.id),
      status: Number(handleForm.status),
      handleResult: handleForm.handleResult
    })
    ElMessage.success('处理成功')
    handleVisible.value = false
    fetchList()
  } finally {
    handleLoading.value = false
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
