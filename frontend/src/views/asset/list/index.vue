<template>
  <div class="page-container">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="资产名称">
          <el-input v-model="query.assetName" placeholder="请输入" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="资产类型">
          <el-input v-model="query.assetType" placeholder="请输入" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="资产状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="在用" :value="0" />
            <el-option label="闲置" :value="1" />
            <el-option label="报废" :value="2" />
            <el-option label="维修" :value="3" />
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
          <el-button type="primary" v-permission="'asset:asset:add'" @click="openAdd">新增资产</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchList">刷新</el-button>
        </div>
      </div>

      <el-table :data="tableData" border v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="assetName" label="资产名称" min-width="140" show-overflow-tooltip />
        <el-table-column prop="assetCode" label="资产编号" min-width="140" show-overflow-tooltip />
        <el-table-column prop="assetType" label="资产类型" min-width="120" />
        <el-table-column prop="location" label="位置" min-width="120" show-overflow-tooltip />
        <el-table-column prop="department" label="部门" min-width="120" />
        <el-table-column prop="responsible" label="负责人" min-width="120" />
        <el-table-column prop="assetValue" label="金额(元)" min-width="120">
          <template #default="{ row }">
            <span>{{ formatMoney(row.assetValue) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="purchaseDate" label="购入日期" min-width="120" />
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'asset:asset:edit'" @click="openEdit(row)">编辑</el-button>
            <el-button type="info" link v-permission="'asset:asset:list'" @click="openDetail(row)">详情</el-button>
            <el-button type="danger" link v-permission="'asset:asset:delete'" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑（F101） -->
    <el-drawer v-model="formVisible" :title="formTitle" size="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="资产名称" prop="assetName">
          <el-input v-model="form.assetName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="资产编号" prop="assetCode">
          <el-input v-model="form.assetCode" placeholder="可选" />
        </el-form-item>
        <el-form-item label="资产类型" prop="assetType">
          <el-input v-model="form.assetType" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-input v-model="form.department" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="负责人" prop="custodian">
          <el-input v-model="form.custodian" placeholder="请输入" />
        </el-form-item>

        <el-form-item label="购入日期" prop="purchaseDate">
          <el-date-picker
            v-model="form.purchaseDate"
            type="date"
            value-format="YYYY-MM-DD"
            format="YYYY-MM-DD"
            placeholder="请选择"
            style="width: 100%"
            clearable
          />
        </el-form-item>

        <el-form-item label="金额(元)" prop="purchasePrice">
          <el-input v-model="form.purchasePrice" placeholder="支持两位小数" />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择" style="width: 100%">
            <el-option label="在用" :value="0" />
            <el-option label="闲置" :value="1" />
            <el-option label="报废" :value="2" />
            <el-option label="维修" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="可选" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div style="display: flex; justify-content: flex-end; gap: 12px">
          <el-button @click="formVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
        </div>
      </template>
    </el-drawer>

    <!-- 详情（F102） -->
    <el-dialog v-model="detailVisible" title="资产详情" width="720px" destroy-on-close>
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="资产名称">{{ detail.assetName }}</el-descriptions-item>
        <el-descriptions-item label="资产编号">{{ detail.assetCode }}</el-descriptions-item>
        <el-descriptions-item label="资产类型">{{ detail.assetType }}</el-descriptions-item>
        <el-descriptions-item label="位置">{{ detail.location }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ detail.department }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ detail.responsible }}</el-descriptions-item>
        <el-descriptions-item label="购入日期">{{ detail.purchaseDate }}</el-descriptions-item>
        <el-descriptions-item label="金额(元)">{{ formatMoney(detail.assetValue) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { addAssetInfo, deleteAssetInfo, getAssetInfoPage, updateAssetInfo } from '@/api/asset'
import request from '@/utils/request'

interface AssetInfoRow {
  id: number
  assetName?: string
  assetCode?: string
  assetType?: string
  assetValue?: string | number
  purchaseDate?: string
  location?: string
  department?: string
  responsible?: string
  status?: number
  remark?: string
}

const loading = ref(false)
const tableData = ref<AssetInfoRow[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const query = reactive({
  assetName: '',
  assetType: '',
  status: undefined as undefined | number
})

const handleSearch = () => {
  pageNum.value = 1
  fetchList()
}

const handleReset = () => {
  query.assetName = ''
  query.assetType = ''
  query.status = undefined
  pageNum.value = 1
  fetchList()
}

const handleSizeChange = () => {
  pageNum.value = 1
  fetchList()
}

const fetchList = async () => {
  loading.value = true
  try {
    const params: any = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      assetName: query.assetName,
      assetType: query.assetType,
      status: query.status
    }
    const res: any = await getAssetInfoPage(params)
    tableData.value = res?.data?.records || []
    total.value = Number(res?.data?.total || 0)
  } finally {
    loading.value = false
  }
}

const statusLabel = (status?: number) => {
  const s = Number(status)
  if (s === 0) return '在用'
  if (s === 1) return '闲置'
  if (s === 2) return '报废'
  if (s === 3) return '维修'
  return '未知'
}

const statusTagType = (status?: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' | undefined => {
  const s = Number(status)
  if (s === 0) return 'success'
  if (s === 1) return 'info'
  if (s === 2) return 'danger'
  if (s === 3) return 'warning'
  return undefined
}

const formatMoney = (v: any) => {
  if (v === null || v === undefined || v === '') return '-'
  const num = Number(v)
  if (Number.isNaN(num)) return String(v)
  return num.toFixed(2)
}

// ========== 新增/编辑（F101） ==========
const formVisible = ref(false)
const formTitle = ref('')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  id: undefined as undefined | number,
  assetName: '',
  assetCode: '',
  assetType: '',
  location: '',
  department: '',
  custodian: '',
  purchaseDate: '',
  purchasePrice: '',
  status: 0 as number,
  remark: ''
})

const moneyValidator = (_rule: any, value: any, callback: any) => {
  if (value === undefined || value === null || value === '') return callback()
  const str = String(value).trim()
  // 数字校验：允许整数或两位小数
  if (!/^\d+(\.\d{1,2})?$/.test(str)) {
    return callback(new Error('金额格式不正确，最多两位小数'))
  }
  return callback()
}

const rules: FormRules = {
  assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  assetType: [{ required: true, message: '请输入资产类型', trigger: 'blur' }],
  purchasePrice: [{ validator: moneyValidator, trigger: 'blur' }]
}

const resetForm = () => {
  form.id = undefined
  form.assetName = ''
  form.assetCode = ''
  form.assetType = ''
  form.location = ''
  form.department = ''
  form.custodian = ''
  form.purchaseDate = ''
  form.purchasePrice = ''
  form.status = 0
  form.remark = ''
}

const openAdd = () => {
  resetForm()
  formTitle.value = '新增资产'
  formVisible.value = true
}

const openEdit = (row: AssetInfoRow) => {
  resetForm()
  form.id = row.id
  form.assetName = row.assetName || ''
  form.assetCode = row.assetCode || ''
  form.assetType = row.assetType || ''
  form.location = row.location || ''
  form.department = row.department || ''
  form.custodian = row.responsible || ''
  form.purchaseDate = row.purchaseDate || ''
  form.purchasePrice = row.assetValue !== undefined && row.assetValue !== null ? String(row.assetValue) : ''
  form.status = Number(row.status ?? 0)
  form.remark = row.remark || ''

  formTitle.value = '编辑资产'
  formVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  submitLoading.value = true
  try {
    const payload: any = {
      id: form.id,
      assetName: form.assetName,
      assetCode: form.assetCode,
      assetType: form.assetType,
      location: form.location,
      department: form.department,
      custodian: form.custodian,
      purchaseDate: form.purchaseDate || null,
      purchasePrice: form.purchasePrice === '' ? null : Number(form.purchasePrice),
      status: form.status,
      remark: form.remark
    }

    if (form.id) {
      await updateAssetInfo(payload)
      ElMessage.success('修改成功')
    } else {
      await addAssetInfo(payload)
      ElMessage.success('新增成功')
    }

    formVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

// ========== 详情（F102） ==========
const detailVisible = ref(false)
const detail = ref<AssetInfoRow | null>(null)

const openDetail = async (row: AssetInfoRow) => {
  detailVisible.value = true
  detail.value = null
  // 复用 Axios 封装：这里直接 GET /asset/info/{id}
  const res: any = await request.get(`/asset/info/${row.id}`)
  detail.value = res?.data || null
}

// ========== 删除（F102，二次确认） ==========
const handleDelete = async (row: AssetInfoRow) => {
  await ElMessageBox.confirm(`确认删除资产「${row.assetName || row.id}」？此操作不可恢复。`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await deleteAssetInfo(row.id)
  ElMessage.success('删除成功')
  fetchList()
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

.search-card {
  margin-bottom: 0;
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
