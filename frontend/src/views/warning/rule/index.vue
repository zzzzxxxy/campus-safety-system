<template>
  <div class="page-container">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="规则名称">
          <el-input v-model="query.ruleName" placeholder="请输入" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="预警类型">
          <el-select v-model="query.warningType" placeholder="全部" clearable style="width: 160px">
            <el-option v-for="it in warningTypeOptions" :key="it.value" :label="it.label" :value="it.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="危险级别">
          <el-select v-model="query.warningLevel" placeholder="全部" clearable style="width: 160px">
            <el-option v-for="it in warningLevelOptions" :key="it.value" :label="it.label" :value="it.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.enabled" placeholder="全部" clearable style="width: 140px">
            <el-option label="启用" :value="0" />
            <el-option label="禁用" :value="1" />
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
          <el-button type="primary" v-permission="'warning:rule:add'" @click="openAdd">新增规则</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchList">刷新</el-button>
        </div>
      </div>

      <el-table :data="tableData" border v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="ruleName" label="规则名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="warningType" label="预警类型" width="130">
          <template #default="{ row }">
            <span>{{ warningTypeLabel(row.warningType) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="warningLevel" label="危险级别" width="130">
          <template #default="{ row }">
            <el-tag :type="levelTagType(row.warningLevel)">{{ warningLevelLabel(row.warningLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ruleCondition" label="触发条件" min-width="220" show-overflow-tooltip />
        <el-table-column prop="enabled" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="Number(row.enabled) === 0 ? 'success' : 'info'">{{ Number(row.enabled) === 0 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'warning:rule:edit'" @click="openEdit(row)">编辑</el-button>
            <el-button type="warning" link v-permission="'warning:rule:edit'" @click="toggleStatus(row)">
              {{ Number(row.enabled) === 0 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" link v-permission="'warning:rule:delete'" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="formVisible" :title="formTitle" width="720px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="form.ruleName" placeholder="请输入" />
        </el-form-item>

        <el-form-item label="预警类型" prop="warningType">
          <el-select v-model="form.warningType" placeholder="请选择" style="width: 100%">
            <el-option v-for="it in warningTypeOptions" :key="it.value" :label="it.label" :value="it.value" />
          </el-select>
        </el-form-item>

        <el-form-item label="危险级别" prop="warningLevel">
          <el-select v-model="form.warningLevel" placeholder="请选择" style="width: 100%">
            <el-option v-for="it in warningLevelOptions" :key="it.value" :label="it.label" :value="it.value" />
          </el-select>
        </el-form-item>

        <el-form-item label="触发条件" prop="ruleCondition">
          <el-input v-model="form.ruleCondition" type="textarea" :rows="3" placeholder="如：温度>60℃ 或 频次>=3" />
        </el-form-item>

        <el-form-item label="状态" prop="enabled">
          <el-radio-group v-model="form.enabled">
            <el-radio :value="0">启用</el-radio>
            <el-radio :value="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="可选" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { addWarningRule, changeWarningRuleStatus, deleteWarningRule, getWarningRulePage, updateWarningRule } from '@/api/warning'

interface WarningRuleRow {
  id: number
  ruleName?: string
  warningType?: number
  warningLevel?: number
  ruleCondition?: string
  enabled?: number
  remark?: string
  createTime?: string
}

const warningTypeOptions = [
  { label: '安全', value: 1 },
  { label: '设备', value: 2 },
  { label: '访客', value: 3 },
  { label: '其他', value: 4 }
]

const warningLevelOptions = [
  { label: '低', value: 1 },
  { label: '中', value: 2 },
  { label: '高', value: 3 },
  { label: '严重', value: 4 }
]

function warningTypeLabel(v: any) {
  const n = Number(v)
  return warningTypeOptions.find((x) => x.value === n)?.label || String(v ?? '')
}

function warningLevelLabel(v: any) {
  const n = Number(v)
  return warningLevelOptions.find((x) => x.value === n)?.label || String(v ?? '')
}

const levelTagType = (level?: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' | undefined => {
  const n = Number(level)
  if (n === 1) return 'info'
  if (n === 2) return 'warning'
  if (n === 3) return 'danger'
  if (n === 4) return 'danger'
  return undefined
}

const loading = ref(false)
const tableData = ref<WarningRuleRow[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const query = reactive({
  ruleName: '',
  warningType: undefined as number | undefined,
  warningLevel: undefined as number | undefined,
  enabled: undefined as number | undefined
})

function handleSearch() {
  pageNum.value = 1
  fetchList()
}

function handleReset() {
  query.ruleName = ''
  query.warningType = undefined
  query.warningLevel = undefined
  query.enabled = undefined
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
    const res: any = await getWarningRulePage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...query
    })
    const data = res.data.data
    tableData.value = data.records || []
    total.value = Number(data.total || 0)
  } finally {
    loading.value = false
  }
}

// ========== 新增/编辑 ==========
const formVisible = ref(false)
const formTitle = ref('')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  id: undefined as number | undefined,
  ruleName: '',
  warningType: undefined as number | undefined,
  warningLevel: undefined as number | undefined,
  ruleCondition: '',
  enabled: 0 as number,
  remark: ''
})

const rules: FormRules = {
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  warningType: [{ required: true, message: '请选择预警类型', trigger: 'change' }],
  warningLevel: [{ required: true, message: '请选择危险级别', trigger: 'change' }]
}

function resetForm() {
  form.id = undefined
  form.ruleName = ''
  form.warningType = undefined
  form.warningLevel = undefined
  form.ruleCondition = ''
  form.enabled = 0
  form.remark = ''
}

function openAdd() {
  resetForm()
  formTitle.value = '新增预警规则'
  formVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

function openEdit(row: WarningRuleRow) {
  resetForm()
  form.id = row.id
  form.ruleName = row.ruleName || ''
  form.warningType = row.warningType as any
  form.warningLevel = row.warningLevel as any
  form.ruleCondition = row.ruleCondition || ''
  form.enabled = Number(row.enabled ?? 0)
  form.remark = row.remark || ''
  formTitle.value = '编辑预警规则'
  formVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()
  submitLoading.value = true
  try {
    const payload: any = {
      id: form.id,
      ruleName: form.ruleName,
      warningType: form.warningType,
      warningLevel: form.warningLevel,
      ruleCondition: form.ruleCondition,
      enabled: form.enabled,
      remark: form.remark
    }

    if (form.id) {
      await updateWarningRule(payload)
      ElMessage.success('修改成功')
    } else {
      await addWarningRule(payload)
      ElMessage.success('新增成功')
    }

    formVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row: WarningRuleRow) {
  await ElMessageBox.confirm(`确认删除规则「${row.ruleName || row.id}」？此操作不可恢复。`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await deleteWarningRule(row.id)
  ElMessage.success('删除成功')
  fetchList()
}

async function toggleStatus(row: WarningRuleRow) {
  const enabled = Number(row.enabled) === 0 ? 1 : 0
  await changeWarningRuleStatus(row.id, enabled)
  ElMessage.success(enabled === 0 ? '已启用' : '已禁用')
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
