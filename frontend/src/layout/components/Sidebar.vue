<template>
  <div class="sidebar-container">
    <div class="sidebar-logo">
      <el-icon :size="22" color="#409eff"><School /></el-icon>
      <span v-show="!collapse" class="sidebar-title">校园安全管理</span>
    </div>
    <el-scrollbar>
      <el-menu
        :key="menuKey"
        :default-active="activeMenu"
        :default-openeds="openeds"
        :collapse="collapse"
        :collapse-transition="false"
        background-color="#f6f5f4"
        text-color="#37352f"
        active-text-color="#409eff"
        :unique-opened="true"
        router
      >
        <sidebar-item
          v-for="route in menuRoutes"
          :key="route.path"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { School } from '@element-plus/icons-vue'
import { usePermissionStore } from '@/stores/permission'
import SidebarItem from './SidebarItem.vue'

defineProps<{
  collapse: boolean
}>()

const route = useRoute()
const permissionStore = usePermissionStore()

const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta?.activeMenu) {
    return meta.activeMenu as string
  }
  return path
})

const menuRoutes = computed(() => {
  return permissionStore.routes.filter(
    (r) => !r.meta?.hidden && r.path !== '/login' && r.path !== '/404'
  )
})

// ensure current active path's ancestors are expanded
const openeds = computed(() => {
  const matched = route.matched || []
  const paths = matched
    .map((m) => m.path)
    .filter((p) => p && p !== '/' && p !== '/login' && p !== '/404')
  // exclude leaf itself (last) -> open parents only
  return paths.slice(0, -1)
})

const menuKey = computed(() => `${activeMenu.value}-${openeds.value.join(',')}`)
</script>

<style scoped lang="scss">
.sidebar-container {
  height: 100%;
  background-color: #f6f5f4;
  display: flex;
  flex-direction: column;
  border-right: 1px solid rgba(0, 0, 0, 0.06);
}

.sidebar-logo {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  flex-shrink: 0;
  padding: 0 16px;
  overflow: hidden;
}

.sidebar-title {
  font-size: 15px;
  font-weight: 600;
  color: #37352f;
  white-space: nowrap;
  letter-spacing: 1px;
}

:deep(.el-menu) {
  border-right: none;

  .el-menu-item {
    height: 44px;
    line-height: 44px;
    font-size: 14px;
    margin: 2px 8px;
    border-radius: 6px;
    padding-left: 20px !important;

    &:hover {
      background-color: rgba(0, 0, 0, 0.04);
    }

    &.is-active {
      background-color: rgba(64, 158, 255, 0.08);
      font-weight: 500;
    }
  }

  .el-sub-menu {
    .el-sub-menu__title {
      height: 44px;
      line-height: 44px;
      font-size: 14px;
      margin: 2px 8px;
      border-radius: 6px;

      &:hover {
        background-color: rgba(0, 0, 0, 0.04);
      }
    }

    .el-menu-item {
      min-width: auto;
    }
  }
}
</style>
