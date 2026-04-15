<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <div class="title">菜单管理</div>
          <div class="actions">
            <el-button type="primary" @click="onAddRoot">新增根菜单</el-button>
            <el-button @click="fetchMenus">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="menuList"
        row-key="id"
        border
        default-expand-all
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="menuName" label="菜单名称" min-width="220" />
        <el-table-column prop="orderNum" label="排序" width="80" />
        <el-table-column prop="menuType" label="类型" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.menuType === 'M'" type="info">目录</el-tag>
            <el-tag v-else-if="row.menuType === 'C'" type="success">菜单</el-tag>
            <el-tag v-else-if="row.menuType === 'F'" type="warning">按钮</el-tag>
            <span v-else>{{ row.menuType }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由" min-width="160" show-overflow-tooltip />
        <el-table-column prop="component" label="组件" min-width="200" show-overflow-tooltip />
        <el-table-column prop="perms" label="权限标识" min-width="180" show-overflow-tooltip />
        <el-table-column prop="icon" label="图标" width="120" show-overflow-tooltip />
        <el-table-column prop="visible" label="显示" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.visible === 0" type="success">显示</el-tag>
            <el-tag v-else type="info">隐藏</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="success">正常</el-tag>
            <el-tag v-else type="danger">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="onAddChild(row)">新增子菜单</el-button>
            <el-button size="small" type="primary" link @click="onEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="680px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="110px"
      >
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="parentTree"
            :props="treeSelectProps"
            check-strictly
            default-expand-all
            clearable
            filterable
            style="width: 100%"
            placeholder="请选择上级菜单"
          />
        </el-form-item>

        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>

        <el-form-item label="排序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" :max="9999" controls-position="right" style="width: 100%" />
        </el-form-item>

        <el-form-item label="类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio-button label="M">目录</el-radio-button>
            <el-radio-button label="C">菜单</el-radio-button>
            <el-radio-button label="F">按钮</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="路由" prop="path">
          <el-input v-model="form.path" placeholder="例如：system/user" />
        </el-form-item>

        <el-form-item label="组件" prop="component">
          <el-input v-model="form.component" placeholder="例如：system/user/index" />
        </el-form-item>

        <el-form-item label="权限标识" prop="perms">
          <el-input v-model="form.perms" placeholder="例如：system:user:list" />
        </el-form-item>

        <el-form-item label="图标" prop="icon">
          <el-input v-model="form.icon" placeholder="Element Plus Icon 名称，例如：Setting" />
        </el-form-item>

        <el-form-item label="是否显示" prop="visible">
          <el-radio-group v-model="form.visible">
            <el-radio :label="0">显示</el-radio>
            <el-radio :label="1">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="onSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { addMenu, deleteMenu, getMenuList, updateMenu } from '@/api/system'

interface SysMenuDTO {
  id?: number
  menuName: string
  parentId: number
  orderNum: number
  path: string
  component: string
  menuType: 'M' | 'C' | 'F' | string
  perms: string
  icon: string
  visible: 0 | 1 | number
  status: 0 | 1 | number
  children?: SysMenuDTO[]
}

const loading = ref(false)
const submitLoading = ref(false)
const menuList = ref<SysMenuDTO[]>([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()

const form = reactive<SysMenuDTO>({
  id: undefined,
  menuName: '',
  parentId: 0,
  orderNum: 0,
  path: '',
  component: '',
  menuType: 'C',
  perms: '',
  icon: '',
  visible: 0,
  status: 0
})

const rules = reactive<FormRules>({
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }]
})

const treeSelectProps = {
  value: 'id',
  label: 'menuName',
  children: 'children'
}

function normalizeMenus(list: any): SysMenuDTO[] {
  if (!Array.isArray(list)) return []
  return list
}

async function fetchMenus() {
  loading.value = true
  try {
    const res = await getMenuList()
    menuList.value = normalizeMenus((res as any).data?.data)
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.id = undefined
  form.menuName = ''
  form.parentId = 0
  form.orderNum = 0
  form.path = ''
  form.component = ''
  form.menuType = 'C'
  form.perms = ''
  form.icon = ''
  form.visible = 0
  form.status = 0
}

function fillFormFromRow(row: SysMenuDTO) {
  form.id = row.id
  form.menuName = row.menuName ?? ''
  form.parentId = (row.parentId ?? 0) as number
  form.orderNum = (row.orderNum ?? 0) as number
  form.path = row.path ?? ''
  form.component = row.component ?? ''
  form.menuType = (row.menuType ?? 'C') as any
  form.perms = row.perms ?? ''
  form.icon = row.icon ?? ''
  form.visible = (row.visible ?? 0) as any
  form.status = (row.status ?? 0) as any
}

function collectIds(node: SysMenuDTO, set: Set<number>) {
  if (typeof node.id === 'number') set.add(node.id)
  const children = node.children || []
  children.forEach((c) => collectIds(c, set))
}

function filterTreeExcludeIds(list: SysMenuDTO[], excluded: Set<number>): SysMenuDTO[] {
  const dfs = (nodes: SysMenuDTO[]): SysMenuDTO[] => {
    const out: SysMenuDTO[] = []
    for (const n of nodes) {
      const id = n.id
      if (typeof id === 'number' && excluded.has(id)) continue
      const nn: SysMenuDTO = { ...n }
      if (nn.children?.length) nn.children = dfs(nn.children)
      out.push(nn)
    }
    return out
  }
  return dfs(list)
}

const excludedParentIds = computed(() => {
  // when editing: exclude self and descendants from parent selection
  if (!form.id) return new Set<number>()
  const set = new Set<number>()
  const findAndCollect = (nodes: SysMenuDTO[]): boolean => {
    for (const n of nodes) {
      if (n.id === form.id) {
        collectIds(n, set)
        return true
      }
      if (n.children?.length && findAndCollect(n.children)) return true
    }
    return false
  }
  findAndCollect(menuList.value)
  return set
})

const parentTree = computed(() => {
  const tree = filterTreeExcludeIds(menuList.value, excludedParentIds.value)
  return [
    {
      id: 0,
      menuName: '根目录',
      children: tree
    }
  ]
})

function openDialog(title: string) {
  dialogTitle.value = title
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

function onAddRoot() {
  resetForm()
  form.parentId = 0
  openDialog('新增根菜单')
}

function onAddChild(row: SysMenuDTO) {
  resetForm()
  form.parentId = (row.id ?? 0) as number
  openDialog('新增子菜单')
}

function onEdit(row: SysMenuDTO) {
  resetForm()
  fillFormFromRow(row)
  openDialog('编辑菜单')
}

async function onDelete(row: SysMenuDTO) {
  if (!row.id) return
  try {
    await ElMessageBox.confirm(
      `确定删除菜单「${row.menuName}」吗？（若存在子菜单将无法删除）`,
      '提示',
      { type: 'warning' }
    )
    await deleteMenu(row.id)
    ElMessage.success('删除成功')
    await fetchMenus()
  } catch (e) {
    // cancel or error already handled
  }
}

async function onSubmit() {
  formRef.value?.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      const payload: SysMenuDTO = {
        id: form.id,
        menuName: form.menuName,
        parentId: form.parentId,
        orderNum: form.orderNum,
        path: form.path,
        component: form.component,
        menuType: form.menuType,
        perms: form.perms,
        icon: form.icon,
        visible: form.visible,
        status: form.status
      }
      if (payload.id) {
        await updateMenu(payload)
        ElMessage.success('更新成功')
      } else {
        await addMenu(payload)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      await fetchMenus()
    } finally {
      submitLoading.value = false
    }
  })
}

onMounted(() => {
  fetchMenus()
})
</script>

<style scoped lang="scss">
.page-container {
  padding: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;

  .title {
    font-size: 16px;
    font-weight: 600;
  }

  .actions {
    display: flex;
    align-items: center;
    gap: 8px;
  }
}
</style>
