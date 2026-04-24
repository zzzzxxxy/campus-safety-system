<template>
  <template v-if="!item.meta?.hidden">
    <!-- Leaf menu item -->
    <el-menu-item v-if="!hasChildren" :index="itemFullPath">
      <el-icon v-if="item.meta?.icon">
        <component :is="item.meta.icon" />
      </el-icon>
      <template #title>
        <span>{{ item.meta?.title }}</span>
      </template>
    </el-menu-item>

    <!-- Has children: render submenu (or flatten if only one child) -->
    <template v-else>
      <!-- flatten when only one visible child -->
      <el-menu-item
        v-if="onlyOneChild"
        :index="resolvePath(onlyOneChild.path)"
        :class="{ 'is-single-child': true }"
      >
        <el-icon v-if="onlyOneChild.meta?.icon || item.meta?.icon">
          <component :is="onlyOneChild.meta?.icon || item.meta?.icon" />
        </el-icon>
        <template #title>
          <span>{{ onlyOneChild.meta?.title || item.meta?.title }}</span>
        </template>
      </el-menu-item>

      <el-sub-menu v-else :index="itemFullPath" popper-class="safety-menu-popper">
        <template #title>
          <el-icon v-if="item.meta?.icon">
            <component :is="item.meta.icon" />
          </el-icon>
          <span>{{ item.meta?.title }}</span>
        </template>

        <sidebar-item
          v-for="child in visibleChildren"
          :key="child.path"
          :item="child"
          :base-path="resolvePath(child.path)"
        />
      </el-sub-menu>
    </template>
  </template>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

const props = defineProps<{
  item: RouteRecordRaw
  basePath: string
}>()

const itemFullPath = computed(() => props.basePath)

function normalizePath(path: string): string {
  const clean = path.replace(/\/+/g, '/')
  return clean.startsWith('/') ? clean : `/${clean}`
}

function resolvePath(path: string): string {
  if (!path) return normalizePath(props.basePath || '/')
  if (path.startsWith('/')) return normalizePath(path)

  const base = props.basePath || ''
  if (!base || base === '/') return normalizePath(path)
  return normalizePath(`${base.replace(/\/+$/, '')}/${path}`)
}

const visibleChildren = computed(() => {
  return (props.item.children || []).filter((c) => !c.meta?.hidden)
})

const hasChildren = computed(() => visibleChildren.value.length > 0)

const onlyOneChild = computed<RouteRecordRaw | null>(() => {
  if (visibleChildren.value.length === 1) {
    return visibleChildren.value[0]
  }
  return null
})
</script>


<style scoped lang="scss">
:deep(.el-icon) {
  width: 20px;
  height: 20px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.08);
}

:deep(.is-single-child) {
  position: relative;
}
</style>
