<template>
  <div class="page-container">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="设备名称">
          <el-input v-model="query.deviceName" placeholder="请输入" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="设备编号">
          <el-input v-model="query.deviceCode" placeholder="请输入" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="正常" :value="0" />
            <el-option label="故障" :value="1" />
            <el-option label="维修" :value="2" />
            <el-option label="报废" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="在线">
          <el-select v-model="query.online" placeholder="全部" clearable style="width: 140px">
            <el-option label="在线" :value="1" />
            <el-option label="离线" :value="0" />
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
          <el-button type="primary" v-permission="'asset:device:add'" @click="openAdd">新增设备</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchList">刷新</el-button>
        </div>
      </div>

      <el-table :data="tableData" border v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="deviceName" label="设备名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="deviceCode" label="设备编号" min-width="160" show-overflow-tooltip />
        <el-table-column prop="deviceType" label="类型" width="120" />
        <el-table-column prop="location" label="位置" min-width="140" show-overflow-tooltip />
        <el-table-column prop="ipAddress" label="IP" width="150" />
        <el-table-column prop="online" label="在线" width="120">
          <template #default="{ row }">
            <el-tag :type="Number(row.online) === 1 ? 'success' : 'info'">
              {{ Number(row.online) === 1 ? '在线' : '离线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="150">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'asset:device:edit'" @click="openEdit(row)">编辑</el-button>
            <el-button type="warning" link v-permission="'asset:device:edit'" @click="handleToggleOnline(row)">
              {{ Number(row.online) === 1 ? '停用(离线)' : '启用(在线)' }}
            </el-button>
            <el-button type="danger" link v-permission="'asset:device:delete'" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="860px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设备名称" prop="deviceName">
              <el-input v-model="form.deviceName" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备编号" prop="deviceCode">
              <el-input v-model="form.deviceCode" placeholder="请输入" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="设备类型" prop="deviceType">
              <el-input v-model="form.deviceType" placeholder="如：摄像头/门禁" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="物理区域/位置" prop="location">
              <el-input v-model="form.location" placeholder="如：东门入口" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="IP地址" prop="ipAddress">
              <el-input v-model="form.ipAddress" placeholder="如：192.168.1.101" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="MAC地址" prop="macAddress">
              <el-input v-model="form.macAddress" placeholder="如：AA:BB:CC:DD:EE:FF" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="在线状态" prop="online">
              <el-radio-group v-model="form.online">
                <el-radio :value="1">在线</el-radio>
                <el-radio :value="0">离线</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择" style="width: 100%">
                <el-option label="正常" :value="0" />
                <el-option label="故障" :value="1" />
                <el-option label="维修" :value="2" />
                <el-option label="报废" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { addDeviceInfo, deleteDeviceInfo, getDeviceInfoPage, updateDeviceInfo } from '@/api/asset'

interface DeviceInfo {
  id: number
  deviceName: string
  deviceCode?: string
  deviceType?: string
  location?: string
  ipAddress?: string
  status?: number
  online?: number
  remark?: string
}

const loading = ref(false)
const tableData = ref<DeviceInfo[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const query = reactive({
  deviceName: '',
  deviceCode: '',
  status: undefined as number | undefined,
  online: undefined as number | undefined
})

function statusLabel(v: any) {
  const n = Number(v)
  if (n === 0) return '正常'
  if (n === 1) return '故障'
  if (n === 2) return '维修'
  if (n === 3) return '报废'
  return String(v ?? '')
}

function statusTagType(v: any) {
  const n = Number(v)
  if (n === 0) return 'success'
  if (n === 1) return 'danger'
  if (n === 2) return 'warning'
  if (n === 3) return 'info'
  return 'info'
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getDeviceInfoPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...query
    })
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
  query.deviceName = ''
  query.deviceCode = ''
  query.status = undefined
  query.online = undefined
  pageNum.value = 1
  fetchList()
}

function handleSizeChange(size: number) {
  pageSize.value = size
  pageNum.value = 1
  fetchList()
}

// dialog
const dialogVisible = ref(false)
const dialogTitle = ref('')
const dialogMode = ref<'add' | 'edit'>('add')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  id: undefined as number | undefined,
  deviceName: '',
  deviceCode: '',
  deviceType: '',
  location: '',
  ipAddress: '',
  macAddress: '',
  online: 1,
  status: 0,
  remark: ''
})

function isValidIp(v: string) {
  const s = (v || '').trim()
  if (!s) return true
  const r = /^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}$/
  return r.test(s)
}

function isValidMac(v: string) {
  const s = (v || '').trim()
  if (!s) return true
  const r = /^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$/
  return r.test(s)
}

const rules: FormRules = {
  deviceName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  ipAddress: [
    {
      validator: (_rule, value, callback) => {
        if (!isValidIp(value)) return callback(new Error('IP格式不正确'))
        callback()
      },
      trigger: 'blur'
    }
  ],
  macAddress: [
    {
      validator: (_rule, value, callback) => {
        if (!isValidMac(value)) return callback(new Error('MAC格式不正确(AA:BB:CC:DD:EE:FF)'))
        callback()
      },
      trigger: 'blur'
    }
  ],
  location: [{ required: true, message: '请输入物理区域/位置', trigger: 'blur' }]
}

function openAdd() {
  dialogMode.value = 'add'
  dialogTitle.value = '新增设备'
  dialogVisible.value = true
  Object.assign(form, {
    id: undefined,
    deviceName: '',
    deviceCode: '',
    deviceType: '',
    location: '',
    ipAddress: '',
    macAddress: '',
    online: 1,
    status: 0,
    remark: ''
  })
  nextTick(() => formRef.value?.clearValidate())
}

function openEdit(row: DeviceInfo) {
  dialogMode.value = 'edit'
  dialogTitle.value = '编辑设备'
  dialogVisible.value = true
  Object.assign(form, {
    id: row.id,
    deviceName: row.deviceName || '',
    deviceCode: row.deviceCode || '',
    deviceType: (row as any).deviceType || '',
    location: (row as any).location || '',
    ipAddress: (row as any).ipAddress || '',
    // macAddress is frontend-only for now
    macAddress: (row as any).macAddress || '',
    online: Number((row as any).online ?? 0),
    status: Number((row as any).status ?? 0),
    remark: (row as any).remark || ''
  })
  nextTick(() => formRef.value?.clearValidate())
}

async function handleSubmit() {
  await formRef.value?.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      const payload: any = {
        id: form.id,
        deviceName: form.deviceName,
        deviceCode: form.deviceCode,
        deviceType: form.deviceType,
        location: form.location,
        ipAddress: form.ipAddress,
        status: form.status,
        online: form.online,
        remark: form.remark
      }

      if (dialogMode.value === 'add') {
        await addDeviceInfo(payload)
        ElMessage.success('新增成功')
      } else {
        await updateDeviceInfo(payload)
        ElMessage.success('保存成功')
      }
      dialogVisible.value = false
      fetchList()
    } finally {
      submitLoading.value = false
    }
  })
}

async function handleToggleOnline(row: DeviceInfo) {
  const nextOnline = Number(row.online) === 1 ? 0 : 1
  try {
    await ElMessageBox.confirm(
      `确认将设备【${row.deviceName}】切换为【${nextOnline === 1 ? '在线' : '离线(停用)'}】吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    // Reuse update API (backend uses BeanUtils.copyProperties)
    await updateDeviceInfo({
      id: row.id,
      deviceName: row.deviceName,
      deviceCode: row.deviceCode,
      deviceType: (row as any).deviceType,
      location: (row as any).location,
      ipAddress: (row as any).ipAddress,
      status: (row as any).status,
      online: nextOnline,
      remark: (row as any).remark
    })

    ElMessage.success('状态已更新')
    row.online = nextOnline
  } catch {
    // cancelled
  }
}

async function handleDelete(row: DeviceInfo) {
  try {
    await ElMessageBox.confirm(`确认删除设备【${row.deviceName}】吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })

    await deleteDeviceInfo(row.id)
    ElMessage.success('删除成功')

    if (tableData.value.length <= 1 && pageNum.value > 1) {
      pageNum.value -= 1
    }
    fetchList()
  } catch (error: any) {
    if (error !== 'cancel' && error !== 'close') ElMessage.error('删除失败')
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped lang="scss">
.page-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 12px;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
