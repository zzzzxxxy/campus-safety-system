<template>
  <div class="page-container">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="访客姓名">
          <el-input v-model="query.visitorName" placeholder="请输入" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="query.phone" placeholder="请输入" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="it in visitorStatusOptions" :key="it.value" :label="it.label" :value="it.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="到访时间">
          <el-date-picker
            v-model="queryRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            format="YYYY-MM-DD HH:mm:ss"
            :default-time="defaultTime"
            style="width: 360px"
            clearable
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
          <el-button type="primary" v-permission="'visitor:record:add'" @click="openAdd">登记访客</el-button>
          <el-button v-permission="'visitor:record:list'" @click="handleExport">导出</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchList">刷新</el-button>
        </div>
      </div>

      <el-table :data="tableData" border v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="visitorName" label="姓名" min-width="120" />
        <el-table-column prop="visitorPhone" label="手机号" min-width="140" />
        <el-table-column prop="idCard" label="身份证" min-width="180" show-overflow-tooltip />
        <el-table-column prop="reason" label="事由" min-width="160" show-overflow-tooltip />
        <el-table-column prop="visitee" label="被访人" min-width="120" />
        <el-table-column prop="department" label="部门" min-width="140" />
        <el-table-column prop="visitTime" label="预计到访" min-width="170" />
        <el-table-column prop="leaveTime" label="预计离开" min-width="170" />
        <el-table-column prop="actualVisitTime" label="实际到访" min-width="170" />
        <el-table-column prop="actualLeaveTime" label="实际离开" min-width="170" />
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'visitor:record:edit'" @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link v-permission="'visitor:record:delete'" @click="handleDelete(row)">删除</el-button>

            <el-button
              v-if="Number(row.status) === 0"
              type="warning"
              link
              v-permission="'visitor:record:audit'"
              @click="openAudit(row)"
            >审核</el-button>

            <el-button
              v-if="Number(row.status) === 1 && !row.actualVisitTime"
              type="success"
              link
              v-permission="'visitor:record:audit'"
              @click="handleCheckIn(row)"
            >签到</el-button>

            <el-button
              v-if="row.actualVisitTime && !row.actualLeaveTime"
              type="success"
              link
              v-permission="'visitor:record:audit'"
              @click="handleCheckOut(row)"
            >签退</el-button>
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

    <!-- 新增/编辑 -->
    <el-dialog v-model="formVisible" :title="formTitle" width="860px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="访客姓名" prop="visitorName">
              <el-input v-model="form.visitorName" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="身份证" prop="idCard">
              <el-input v-model="form.idCard" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车牌号" prop="vehicleNumber">
              <el-input v-model="form.vehicleNumber" placeholder="可选" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="来访事由" prop="visitReason">
              <el-input v-model="form.visitReason" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="被访部门" prop="visitDepartment">
              <el-input v-model="form.visitDepartment" placeholder="请输入" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="被访人" prop="visitPerson">
              <el-input v-model="form.visitPerson" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计到访" prop="visitTime">
              <el-date-picker
                v-model="form.visitTime"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择"
                style="width: 100%"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="预计离开" prop="leaveTime">
              <el-date-picker
                v-model="form.leaveTime"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择"
                style="width: 100%"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="可选" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 审核 -->
    <el-dialog v-model="auditVisible" title="审核访客" width="560px" destroy-on-close>
      <el-form ref="auditRef" :model="auditForm" :rules="auditRules" label-width="100px">
        <el-form-item label="审核结果" prop="status">
          <el-radio-group v-model="auditForm.status">
            <el-radio :value="1">通过</el-radio>
            <el-radio :value="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见" prop="remark">
          <el-input v-model="auditForm.remark" type="textarea" :rows="3" placeholder="可选" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" :loading="auditLoading" @click="handleAuditSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  addVisitor,
  auditVisitor,
  checkInVisitor,
  checkOutVisitor,
  deleteVisitor,
  exportVisitor,
  getVisitorPage,
  updateVisitor
} from '@/api/visitor'
import { dictLabel, dictTagType, visitorStatusOptions } from '@/utils/dict'

interface VisitorRecord {
  id: number
  visitorName?: string
  visitorPhone?: string
  idCard?: string
  reason?: string
  visitee?: string
  department?: string
  visitTime?: string
  leaveTime?: string
  actualVisitTime?: string
  actualLeaveTime?: string
  status?: number
  remark?: string
}

const loading = ref(false)
const tableData = ref<VisitorRecord[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const query = reactive({
  visitorName: '',
  phone: '',
  status: undefined as undefined | number,
  startTime: '',
  endTime: ''
})

const queryRange = ref<[string, string] | null>(null)
const defaultTime: [Date, Date] = [
  new Date(2000, 1, 1, 0, 0, 0),
  new Date(2000, 1, 1, 23, 59, 59)
]

function applyRange() {
  if (queryRange.value && queryRange.value.length === 2) {
    query.startTime = queryRange.value[0]
    query.endTime = queryRange.value[1]
  } else {
    query.startTime = ''
    query.endTime = ''
  }
}

function statusLabel(v: any) {
  return dictLabel(visitorStatusOptions, v)
}
function statusTagType(v: any) {
  return dictTagType(visitorStatusOptions, v) || 'info'
}

async function fetchList() {
  loading.value = true
  try {
    applyRange()
    const params: any = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      visitorName: query.visitorName || undefined,
      phone: query.phone || undefined,
      status: query.status,
      startTime: query.startTime || undefined,
      endTime: query.endTime || undefined
    }
    const res = await getVisitorPage(params)
    const data = res.data.data
    tableData.value = data.records || []
    total.value = Number(data.total || 0)
  } catch {
    ElMessage.error('加载列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  fetchList()
}

function handleReset() {
  query.visitorName = ''
  query.phone = ''
  query.status = undefined
  queryRange.value = null
  query.startTime = ''
  query.endTime = ''
  pageNum.value = 1
  fetchList()
}

function handleSizeChange() {
  pageNum.value = 1
  fetchList()
}

// 新增/编辑
const formVisible = ref(false)
const formMode = ref<'add' | 'edit'>('add')
const formTitle = ref('登记访客')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  id: undefined as undefined | number,
  visitorName: '',
  phone: '',
  idCard: '',
  vehicleNumber: '',
  visitReason: '',
  visitDepartment: '',
  visitPerson: '',
  visitTime: '',
  leaveTime: '',
  remark: ''
})

const rules: FormRules = {
  visitorName: [{ required: true, message: '请输入访客姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  visitReason: [{ required: true, message: '请输入来访事由', trigger: 'blur' }],
  visitDepartment: [{ required: true, message: '请输入被访部门', trigger: 'blur' }],
  visitPerson: [{ required: true, message: '请输入被访人', trigger: 'blur' }],
  vehicleNumber: [
    {
      pattern: /^$|^[\u4e00-\u9fa5][A-Z][A-Z0-9]{5,6}$/,
      message: '车牌号格式不正确',
      trigger: 'blur'
    }
  ],
  visitTime: [{ required: true, message: '请选择预计到访时间', trigger: 'change' }],
  leaveTime: [{ required: true, message: '请选择预计离开时间', trigger: 'change' }]
}

function resetForm() {
  form.id = undefined
  form.visitorName = ''
  form.phone = ''
  form.idCard = ''
  form.vehicleNumber = ''
  form.visitReason = ''
  form.visitDepartment = ''
  form.visitPerson = ''
  form.visitTime = ''
  form.leaveTime = ''
  form.remark = ''
}

function openAdd() {
  formMode.value = 'add'
  formTitle.value = '登记访客'
  resetForm()
  formVisible.value = true
}

function openEdit(row: any) {
  formMode.value = 'edit'
  formTitle.value = '编辑访客'
  resetForm()
  form.id = row.id
  form.visitorName = row.visitorName || ''
  form.phone = row.visitorPhone || ''
  form.idCard = row.idCard || ''
  form.visitReason = row.reason || ''
  form.visitDepartment = row.department || ''
  form.visitPerson = row.visitee || ''
  form.visitTime = row.visitTime || ''
  form.leaveTime = row.leaveTime || ''
  form.remark = row.remark || ''
  formVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()

  if (form.leaveTime && form.visitTime && form.leaveTime < form.visitTime) {
    ElMessage.error('预计离开时间不能早于预计到访时间')
    return
  }

  submitLoading.value = true
  try {
    const payload: any = { ...form }
    if (formMode.value === 'add') {
      await addVisitor(payload)
      ElMessage.success('登记成功')
    } else {
      await updateVisitor(payload)
      ElMessage.success('保存成功')
    }
    formVisible.value = false
    fetchList()
  } catch {
    ElMessage.error('保存失败')
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(`确认删除访客记录(ID=${row.id})？`, '提示', { type: 'warning' })
    await deleteVisitor(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (error: any) {
    if (error !== 'cancel' && error !== 'close') ElMessage.error('删除失败')
  }
}

// 审核
const auditVisible = ref(false)
const auditLoading = ref(false)
const auditRef = ref<FormInstance>()
const auditForm = reactive({
  id: 0,
  status: 1,
  remark: ''
})
const auditRules: FormRules = {
  status: [{ required: true, message: '请选择审核结果', trigger: 'change' }]
}

function openAudit(row: any) {
  auditForm.id = row.id
  auditForm.status = 1
  auditForm.remark = ''
  auditVisible.value = true
}

async function handleAuditSubmit() {
  if (!auditRef.value) return
  await auditRef.value.validate()
  auditLoading.value = true
  try {
    await auditVisitor({ id: auditForm.id, status: auditForm.status, remark: auditForm.remark || undefined })
    ElMessage.success('审核成功')
    auditVisible.value = false
    fetchList()
  } catch {
    ElMessage.error('审核失败')
  } finally {
    auditLoading.value = false
  }
}

async function handleCheckIn(row: any) {
  try {
    await ElMessageBox.confirm(`确认对访客(ID=${row.id})执行签到？`, '提示', { type: 'warning' })
    await checkInVisitor(row.id)
    ElMessage.success('签到成功')
    fetchList()
  } catch (error: any) {
    if (error !== 'cancel' && error !== 'close') ElMessage.error('签到失败')
  }
}

async function handleCheckOut(row: any) {
  try {
    await ElMessageBox.confirm(`确认对访客(ID=${row.id})执行签退？`, '提示', { type: 'warning' })
    await checkOutVisitor(row.id)
    ElMessage.success('签退成功')
    fetchList()
  } catch (error: any) {
    if (error !== 'cancel' && error !== 'close') ElMessage.error('签退失败')
  }
}

function parseFilenameFromDisposition(disposition: string): string | null {
  if (!disposition) return null
  // filename*=UTF-8''xxx
  const m1 = disposition.match(/filename\*=(?:UTF-8'')?([^;]+)/i)
  if (m1 && m1[1]) {
    try {
      return decodeURIComponent(m1[1].replace(/"/g, '').trim())
    } catch {
      return m1[1].replace(/"/g, '').trim()
    }
  }
  const m2 = disposition.match(/filename=([^;]+)/i)
  if (m2 && m2[1]) return m2[1].replace(/"/g, '').trim()
  return null
}

function downloadBlob(blob: Blob, filename: string) {
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.click()
  window.URL.revokeObjectURL(url)
}

async function handleExport() {
  loading.value = true
  try {
    applyRange()
    const params: any = {
      visitorName: query.visitorName || undefined,
      phone: query.phone || undefined,
      status: query.status,
      startTime: query.startTime || undefined,
      endTime: query.endTime || undefined
    }
    const res: any = await exportVisitor(params)
    const disposition = res.headers?.['content-disposition'] || res.headers?.['Content-Disposition']
    const filename = parseFilenameFromDisposition(disposition) || `visitor_records_${Date.now()}.csv`
    const blob = res.data as Blob
    downloadBlob(blob, filename)
    ElMessage.success('导出成功')
  } catch {
    ElMessage.error('导出失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped lang="scss">
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.search-card {
  :deep(.el-card__body) {
    padding-bottom: 0;
  }
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.pagination {
  margin-top: 14px;
  display: flex;
  justify-content: flex-end;
}
</style>
