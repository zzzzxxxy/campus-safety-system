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
      >
        <el-icon v-if="onlyOneChild.meta?.icon || item.meta?.icon">
          <component :is="onlyOneChild.meta?.icon || item.meta?.icon" />
        </el-icon>
        <template #title>
          <span>{{ onlyOneChild.meta?.title || item.meta?.title }}</span>
        </template>
      </el-menu-item>

      <el-sub-menu v-else :index="itemFullPath">
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

function resolvePath(path: string): string {
  if (!path) return props.basePath
  if (path.startsWith('/')) return path
  const base = props.basePath || ''
  if (!base) return `/${path}`
  if (base.endsWith('/')) return base + path
  return `${base}/${path}`
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
