<template>
  <div class="page-container">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="请输入" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="query.nickname" placeholder="请输入" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="query.phone" placeholder="请输入" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="query.userType" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="opt in userTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="正常" :value="0" />
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
          <el-button type="primary" @click="openAdd">新增用户</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchList">刷新</el-button>
        </div>
      </div>

      <el-table :data="tableData" border v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="username" label="用户名" min-width="140" />
        <el-table-column prop="nickname" label="昵称" min-width="140" />
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="userType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag type="info">{{ userTypeLabel(row.userType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="150">
          <template #default="{ row }">
            <div class="status-cell">
              <el-tag :type="Number(row.status) === 0 ? 'success' : 'danger'">
                {{ Number(row.status) === 0 ? '正常' : '禁用' }}
              </el-tag>
              <el-switch
                v-model="row.status"
                :active-value="0"
                :inactive-value="1"
                :before-change="() => handleToggleStatus(row)"
                size="small"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button type="success" link @click="openAssignRoles(row)">分配角色</el-button>
            <el-button type="warning" link @click="openResetPwd(row)">重置密码</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入" :disabled="dialogMode === 'edit'" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="dialogMode === 'add'" label="密码" prop="password">
              <el-input v-model="form.password" placeholder="请输入" show-password />
            </el-form-item>
            <el-form-item v-else label="密码">
              <el-input value="********" disabled />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户类型" prop="userType">
              <el-select v-model="form.userType" placeholder="请选择" style="width: 100%">
                <el-option v-for="opt in userTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :value="0">正常</el-radio>
                <el-radio :value="1">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="角色">
              <el-select
                v-model="form.roleIds"
                multiple
                filterable
                collapse-tags
                collapse-tags-tooltip
                clearable
                placeholder="请选择角色"
                style="width: 100%"
              >
                <el-option v-for="r in roleOptions" :key="r.id" :label="r.roleName" :value="r.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="assignRoleVisible" title="分配角色" width="640px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item label="用户">
          <el-input :value="assignRoleForm.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-select
            v-model="assignRoleForm.roleIds"
            multiple
            filterable
            collapse-tags
            collapse-tags-tooltip
            clearable
            placeholder="请选择角色"
            style="width: 100%"
            :loading="assignRoleLoading"
          >
            <el-option v-for="r in roleOptions" :key="r.id" :label="r.roleName" :value="r.id" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="assignRoleVisible = false">取消</el-button>
        <el-button type="primary" :loading="assignRoleLoading" @click="handleAssignRoles">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="resetPwdVisible" title="重置密码" width="520px" destroy-on-close>
      <el-form ref="resetPwdRef" :model="resetPwdForm" :rules="resetPwdRules" label-width="100px">
        <el-form-item label="用户">
          <el-input :value="resetPwdForm.username" disabled />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="resetPwdForm.newPassword" placeholder="请输入新密码" show-password />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="resetPwdVisible = false">取消</el-button>
        <el-button type="primary" :loading="resetPwdLoading" @click="handleResetPassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getUserPage, addUser, updateUser, deleteUser, resetPassword, getRoleList } from '@/api/system'

interface SysUser {
  id: number
  username: string
  nickname?: string
  phone?: string
  email?: string
  avatar?: string
  userType: number
  status: number
  createTime?: string
  roleIds?: number[]
}

interface SysRole {
  id: number
  roleName: string
}

interface UserDTO {
  id?: number
  username: string
  password?: string
  nickname?: string
  phone?: string
  email?: string
  avatar?: string
  userType: number
  status: number
  roleIds: number[]
}

const userTypeOptions = [
  { label: '管理员', value: 0 },
  { label: '教师', value: 1 },
  { label: '学生', value: 2 },
  { label: '安保', value: 3 }
]

function userTypeLabel(v: any) {
  const n = Number(v)
  const hit = userTypeOptions.find((i) => i.value === n)
  return hit?.label || String(v ?? '')
}

const loading = ref(false)
const submitLoading = ref(false)

const query = reactive<{
  username: string
  nickname: string
  phone: string
  status: number | undefined
  userType: number | undefined
}>({
  username: '',
  nickname: '',
  phone: '',
  status: undefined,
  userType: undefined
})

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref<SysUser[]>([])

const roleOptions = ref<SysRole[]>([])

const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit'>('add')
const dialogTitle = computed(() => (dialogMode.value === 'add' ? '新增用户' : '编辑用户'))

const formRef = ref<FormInstance>()
const form = reactive<UserDTO>({
  id: undefined,
  username: '',
  password: '',
  nickname: '',
  phone: '',
  email: '',
  avatar: '',
  userType: 0,
  status: 0,
  roleIds: []
})

const rules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度 2-20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 5, max: 20, message: '密码长度在 5 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [{ max: 20, message: '昵称最多 20 个字符', trigger: 'blur' }],
  phone: [
    {
      pattern: /^1\d{10}$/,
      message: '手机号格式不正确',
      trigger: 'blur'
    }
  ],
  email: [
    {
      type: 'email',
      message: '邮箱格式不正确',
      trigger: 'blur'
    }
  ],
  userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

const assignRoleVisible = ref(false)
const assignRoleLoading = ref(false)
const assignRoleForm = reactive<{
  id: number | null
  username: string
  nickname: string
  phone: string
  email: string
  avatar: string
  userType: number
  status: number
  roleIds: number[]
}>({
  id: null,
  username: '',
  nickname: '',
  phone: '',
  email: '',
  avatar: '',
  userType: 0,
  status: 0,
  roleIds: []
})

const resetPwdVisible = ref(false)
const resetPwdLoading = ref(false)
const resetPwdRef = ref<FormInstance>()
const resetPwdForm = reactive<{ id: number | null; username: string; newPassword: string }>({
  id: null,
  username: '',
  newPassword: ''
})

const resetPwdRules = reactive<FormRules>({
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 5, max: 20, message: '密码长度在 5 到 20 个字符', trigger: 'blur' }
  ]
})

function normalizePageData(payload: any): { list: any[]; total: number } {
  const data = payload?.data ?? payload
  const list = data?.records ?? data?.list ?? data?.rows ?? data?.items ?? []
  const t = data?.total ?? data?.count ?? list.length ?? 0
  return { list, total: t }
}

async function fetchRoleOptions() {
  try {
    const res = await getRoleList()
    roleOptions.value = (res.data.data || []) as SysRole[]
  } catch (e) {
    roleOptions.value = []
  }
}

async function fetchList() {
  loading.value = true
  try {
    const params: any = {
      username: query.username || undefined,
      nickname: query.nickname || undefined,
      phone: query.phone || undefined,
      status: query.status,
      userType: query.userType,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      current: pageNum.value,
      size: pageSize.value
    }

    const res = await getUserPage(params)
    const page = normalizePageData(res.data)
    tableData.value = (page.list || []) as SysUser[]
    total.value = Number(page.total || 0)
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
  query.username = ''
  query.nickname = ''
  query.phone = ''
  query.status = undefined
  query.userType = undefined
  pageNum.value = 1
  fetchList()
}

function handleSizeChange() {
  pageNum.value = 1
  fetchList()
}

function resetForm() {
  form.id = undefined
  form.username = ''
  form.password = ''
  form.nickname = ''
  form.phone = ''
  form.email = ''
  form.avatar = ''
  form.userType = 0
  form.status = 0
  form.roleIds = []

  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

function openAdd() {
  dialogMode.value = 'add'
  dialogVisible.value = true
  resetForm()
}

function openEdit(row: SysUser) {
  dialogMode.value = 'edit'
  dialogVisible.value = true

  form.id = row.id
  form.username = row.username
  form.password = ''
  form.nickname = row.nickname || ''
  form.phone = row.phone || ''
  form.email = row.email || ''
  form.avatar = row.avatar || ''
  form.userType = Number(row.userType ?? 0)
  form.status = Number(row.status ?? 0)
  form.roleIds = Array.isArray(row.roleIds) ? (row.roleIds as number[]).map((v) => Number(v)) : []

  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

async function handleSubmit() {
  await formRef.value?.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      const dto: UserDTO = {
        id: form.id,
        username: form.username,
        nickname: form.nickname,
        phone: form.phone,
        email: form.email,
        avatar: form.avatar,
        userType: Number(form.userType ?? 0),
        status: Number(form.status ?? 0),
        roleIds: (form.roleIds || []).map((v) => Number(v))
      }

      if (dialogMode.value === 'add') {
        dto.password = form.password
        await addUser(dto)
        ElMessage.success('新增成功')
      } else {
        // 编辑时后端 update() 只有 roleIds 非空才会更新关联；
        // 为了支持“清空角色”，这里把空数组也传下去（不传则无法清空）。
        await updateUser(dto)
        ElMessage.success('保存成功')
      }

      dialogVisible.value = false
      fetchList()
    } finally {
      submitLoading.value = false
    }
  })
}

async function handleDelete(row: SysUser) {
  await ElMessageBox.confirm(`确认删除用户【${row.username}】吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })

  await deleteUser(row.id)
  ElMessage.success('删除成功')

  if (tableData.value.length <= 1 && pageNum.value > 1) {
    pageNum.value -= 1
  }
  fetchList()
}

async function handleToggleStatus(row: SysUser) {
  const nextStatus = Number(row.status) === 0 ? 1 : 0
  try {
    await ElMessageBox.confirm(
      `确认将用户【${row.username}】状态切换为【${nextStatus === 0 ? '正常' : '禁用'}】吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    // 复用编辑接口：只提交必要字段（后端 BeanUtils.copyProperties 会覆盖 status）
    await updateUser({
      id: row.id,
      username: row.username,
      nickname: row.nickname,
      phone: row.phone,
      email: row.email,
      avatar: row.avatar,
      userType: row.userType,
      status: nextStatus,
      roleIds: Array.isArray(row.roleIds) ? row.roleIds : []
    })

    ElMessage.success('状态已更新')
    row.status = nextStatus
    return true
  } catch (e) {
    // cancel / request failed -> keep switch unchanged
    return false
  }
}

function openResetPwd(row: SysUser) {
  resetPwdVisible.value = true
  resetPwdForm.id = row.id
  resetPwdForm.username = row.username
  resetPwdForm.newPassword = ''

  nextTick(() => {
    resetPwdRef.value?.clearValidate()
  })
}

function openAssignRoles(row: SysUser) {
  assignRoleVisible.value = true
  assignRoleForm.id = row.id
  assignRoleForm.username = row.username
  assignRoleForm.nickname = row.nickname || ''
  assignRoleForm.phone = row.phone || ''
  assignRoleForm.email = row.email || ''
  assignRoleForm.avatar = row.avatar || ''
  assignRoleForm.userType = Number(row.userType ?? 0)
  assignRoleForm.status = Number(row.status ?? 0)
  assignRoleForm.roleIds = Array.isArray(row.roleIds) ? (row.roleIds as number[]).map((v) => Number(v)) : []

  // 角色列表可能发生变化，打开时刷新一次，保证回显可用
  fetchRoleOptions()
}

async function handleAssignRoles() {
  if (!assignRoleForm.id) return

  assignRoleLoading.value = true
  try {
    await updateUser({
      id: assignRoleForm.id,
      // 复用 updateUser：必须带上必要字段，避免后端 BeanUtils.copyProperties 覆盖为空
      username: assignRoleForm.username,
      nickname: assignRoleForm.nickname,
      phone: assignRoleForm.phone,
      email: assignRoleForm.email,
      avatar: assignRoleForm.avatar,
      userType: assignRoleForm.userType,
      status: assignRoleForm.status,
      roleIds: (assignRoleForm.roleIds || []).map((v) => Number(v))
    })

    ElMessage.success('角色已更新')
    assignRoleVisible.value = false
    await fetchList()
  } finally {
    assignRoleLoading.value = false
  }
}

async function handleResetPassword() {
  await resetPwdRef.value?.validate(async (valid) => {
    if (!valid) return
    if (!resetPwdForm.id) return

    resetPwdLoading.value = true
    try {
      await resetPassword(resetPwdForm.id, resetPwdForm.newPassword)
      ElMessage.success('重置成功')
      resetPwdVisible.value = false
    } finally {
      resetPwdLoading.value = false
    }
  })
}

onMounted(async () => {
  await fetchRoleOptions()
  await fetchList()
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

.status-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
