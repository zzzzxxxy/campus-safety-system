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
        background-color="transparent"
        text-color="rgba(236, 253, 245, 0.78)"
        active-text-color="#ffffff"
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
  background:
    linear-gradient(180deg, rgba(5, 53, 38, 0.96) 0%, rgba(10, 86, 63, 0.94) 58%, rgba(7, 61, 46, 0.96) 100%);
  display: flex;
  flex-direction: column;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 26px;
  box-shadow: 0 22px 48px rgba(5, 53, 38, 0.28);
  overflow: hidden;
}

.sidebar-logo {
  height: 68px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.10);
  flex-shrink: 0;
  padding: 0 18px;
  overflow: hidden;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.10), rgba(255, 255, 255, 0.02));
}

.sidebar-logo :deep(.el-icon) {
  color: #4ade80 !important;
  filter: drop-shadow(0 0 10px rgba(74, 222, 128, 0.42));
}

.sidebar-title {
  font-size: 16px;
  font-weight: 800;
  color: #ffffff;
  white-space: nowrap;
  letter-spacing: 1px;
}

:deep(.el-scrollbar__view) {
  padding: 12px 0 18px;
}

:deep(.el-menu) {
  border-right: none;
  background: transparent;

  .el-menu-item,
  .el-sub-menu__title {
    position: relative;
    height: 46px;
    line-height: 46px;
    font-size: 14px;
    font-weight: 650;
    margin: 5px 10px;
    border-radius: 15px;
    color: rgba(236, 253, 245, 0.78);
    transition: all 0.22s ease;

    .el-icon {
      color: rgba(187, 247, 208, 0.88);
      font-size: 18px;
    }

    &:hover {
      color: #ffffff;
      background: rgba(255, 255, 255, 0.10);
      transform: translateX(2px);
    }
  }

  .el-menu-item.is-active {
    color: #ffffff;
    background: linear-gradient(135deg, rgba(0, 185, 135, 0.96), rgba(22, 163, 74, 0.88));
    box-shadow: 0 12px 24px rgba(0, 185, 135, 0.28);

    &::before {
      content: '';
      position: absolute;
      left: -10px;
      top: 12px;
      width: 4px;
      height: 22px;
      border-radius: 999px;
      background: #fbbf24;
      box-shadow: 0 0 12px rgba(251, 191, 36, 0.76);
    }
  }

  .el-sub-menu.is-active > .el-sub-menu__title {
    color: #ffffff;
    background: rgba(255, 255, 255, 0.10);
  }

  .el-sub-menu .el-menu-item {
    min-width: auto;
  }

  &.el-menu--collapse {
    width: 100%;

    .el-menu-item,
    .el-sub-menu__title {
      justify-content: center;
      padding: 0 !important;
    }
  }
}
</style>
