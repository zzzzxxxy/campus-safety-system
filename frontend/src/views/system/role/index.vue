<template>
  <div class="page-container">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="角色名称">
          <el-input v-model="query.roleName" placeholder="请输入" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="角色标识">
          <el-input v-model="query.roleKey" placeholder="请输入" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="正常" :value="0" />
            <el-option label="停用" :value="1" />
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
          <el-button type="primary" @click="openAdd">新增角色</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchList">刷新</el-button>
        </div>
      </div>

      <el-table :data="tableData" border v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="roleName" label="角色名称" min-width="160" />
        <el-table-column prop="roleKey" label="角色标识" min-width="160" />
        <el-table-column prop="sort" label="排序" width="90" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="Number(row.status) === 0 ? 'success' : 'danger'">
              {{ Number(row.status) === 0 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="220" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="角色名称" prop="roleName">
              <el-input v-model="form.roleName" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色标识" prop="roleKey">
              <el-input v-model="form.roleKey" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="form.sort" :min="0" :max="9999" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :label="0">正常</el-radio>
                <el-radio :label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="菜单权限">
              <el-tree
                ref="treeRef"
                :data="menuTree"
                node-key="id"
                show-checkbox
                :props="treeProps"
                :check-strictly="false"
                default-expand-all
                :expand-on-click-node="false"
                class="menu-tree"
              />
              <div class="tree-actions">
                <el-button size="small" @click="checkAll">全选</el-button>
                <el-button size="small" @click="uncheckAll">全不选</el-button>
              </div>
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
import { computed, onMounted, reactive, ref, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules, TreeInstance } from 'element-plus'
import {
  getRolePage,
  addRole,
  updateRole,
  deleteRole,
  getMenuList,
  getMenuIdsByRoleId
} from '@/api/system'

interface Role {
  id: number
  roleName: string
  roleKey: string
  sort: number
  status: number
  remark?: string
}

interface RoleDTO extends Partial<Role> {
  menuIds: number[]
}

interface SysMenu {
  id: number
  menuName: string
  children?: SysMenu[]
}

const loading = ref(false)
const submitLoading = ref(false)

const query = reactive<{ roleName: string; roleKey: string; status: number | undefined }>({
  roleName: '',
  roleKey: '',
  status: undefined
})

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref<Role[]>([])

const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit'>('add')
const dialogTitle = computed(() => (dialogMode.value === 'add' ? '新增角色' : '编辑角色'))

const formRef = ref<FormInstance>()
const treeRef = ref<TreeInstance>()

const form = reactive<RoleDTO>({
  id: undefined,
  roleName: '',
  roleKey: '',
  sort: 0,
  status: 0,
  remark: '',
  menuIds: []
})

const rules = reactive<FormRules>({
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入角色标识', trigger: 'blur' }]
})

const menuTree = ref<SysMenu[]>([])
const treeProps = { label: 'menuName', children: 'children' }

function normalizePageData(payload: any): { list: any[]; total: number } {
  const data = payload?.data ?? payload
  const list = data?.records ?? data?.list ?? data?.rows ?? data?.items ?? []
  const t = data?.total ?? data?.count ?? list.length ?? 0
  return { list, total: t }
}

async function fetchMenus() {
  const res = await getMenuList()
  // backend wrapper: {code,msg,data}
  menuTree.value = (res.data.data || []) as SysMenu[]
}

async function fetchList() {
  loading.value = true
  try {
    const params: any = {
      roleName: query.roleName || undefined,
      roleKey: query.roleKey || undefined,
      status: query.status,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      current: pageNum.value,
      size: pageSize.value
    }
    const res = await getRolePage(params)
    const page = normalizePageData(res.data)
    tableData.value = page.list as Role[]
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
  query.roleName = ''
  query.roleKey = ''
  query.status = undefined
  pageNum.value = 1
  fetchList()
}

function handleSizeChange() {
  pageNum.value = 1
  fetchList()
}

function resetForm() {
  form.id = undefined
  form.roleName = ''
  form.roleKey = ''
  form.sort = 0
  form.status = 0
  form.remark = ''
  form.menuIds = []

  nextTick(() => {
    formRef.value?.clearValidate()
    treeRef.value?.setCheckedKeys([])
  })
}

function openAdd() {
  dialogMode.value = 'add'
  dialogVisible.value = true
  resetForm()
}

async function openEdit(row: Role) {
  dialogMode.value = 'edit'
  dialogVisible.value = true

  // fill role base fields from table row (no getRoleById api here)
  form.id = row.id
  form.roleName = row.roleName
  form.roleKey = row.roleKey
  form.sort = Number(row.sort ?? 0)
  form.status = Number(row.status ?? 0)
  form.remark = row.remark || ''

  nextTick(() => {
    formRef.value?.clearValidate()
  })

  // load role menu ids and check tree
  try {
    const res = await getMenuIdsByRoleId(row.id)
    const ids = (res.data.data || []) as number[]
    form.menuIds = ids
    await nextTick()
    treeRef.value?.setCheckedKeys(ids)
  } catch (e) {
    // ignore: request interceptor already toasted
    treeRef.value?.setCheckedKeys([])
  }
}

function collectMenuIds(): number[] {
  const tree = treeRef.value
  if (!tree) return []

  const checked = (tree.getCheckedKeys(false) as any[]).map((v) => Number(v))
  const half = (tree.getHalfCheckedKeys() as any[]).map((v) => Number(v))
  const set = new Set<number>()
  checked.forEach((v) => set.add(v))
  half.forEach((v) => set.add(v))
  return Array.from(set).filter((v) => !Number.isNaN(v))
}

function flattenMenuIds(list: SysMenu[]): number[] {
  const ids: number[] = []
  const dfs = (nodes: SysMenu[]) => {
    nodes.forEach((n) => {
      ids.push(n.id)
      if (n.children && n.children.length) dfs(n.children)
    })
  }
  dfs(list)
  return ids
}

function checkAll() {
  const allIds = flattenMenuIds(menuTree.value)
  treeRef.value?.setCheckedKeys(allIds)
}

function uncheckAll() {
  treeRef.value?.setCheckedKeys([])
}

async function handleSubmit() {
  await formRef.value?.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      const dto: RoleDTO = {
        id: form.id,
        roleName: form.roleName,
        roleKey: form.roleKey,
        sort: Number(form.sort ?? 0),
        status: Number(form.status ?? 0),
        remark: form.remark,
        menuIds: collectMenuIds()
      }

      if (dialogMode.value === 'add') {
        await addRole(dto)
        ElMessage.success('新增成功')
      } else {
        await updateRole(dto)
        ElMessage.success('保存成功')
      }
      dialogVisible.value = false
      fetchList()
    } finally {
      submitLoading.value = false
    }
  })
}

async function handleDelete(row: Role) {
  await ElMessageBox.confirm(`确认删除角色【${row.roleName}】吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })

  await deleteRole(row.id)
  ElMessage.success('删除成功')

  // If deleting last item on page, go back a page.
  if (tableData.value.length <= 1 && pageNum.value > 1) {
    pageNum.value -= 1
  }
  fetchList()
}

onMounted(async () => {
  await fetchMenus()
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

.menu-tree {
  width: 100%;
  max-height: 360px;
  overflow: auto;
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 6px;
  padding: 10px;
}

.tree-actions {
  margin-top: 8px;
}
</style>
