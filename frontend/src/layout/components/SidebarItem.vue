<template>
  <template v-if="!item.meta?.hidden">
    <!-- Single menu item (no children or only one visible child) -->
    <template v-if="!hasVisibleChildren">
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
    </template>

    <!-- Sub menu with multiple children -->
    <el-sub-menu v-else :index="resolvePath(item.path)">
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

<script setup lang="ts">
import { computed } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

const props = defineProps<{
  item: RouteRecordRaw
  basePath?: string
}>()

function resolvePath(path: string): string {
  if (path.startsWith('/')) return path
  const base = props.basePath || ''
  if (base.endsWith('/')) return base + path
  return base ? `${base}/${path}` : path
}

const visibleChildren = computed(() => {
  return (props.item.children || []).filter((c) => !c.meta?.hidden)
})

const hasVisibleChildren = computed(() => {
  return visibleChildren.value.length > 1
})

const onlyOneChild = computed(() => {
  if (visibleChildren.value.length === 1) {
    return visibleChildren.value[0]
  }
  // No children, show the item itself
  if (visibleChildren.value.length === 0) {
    return { ...props.item, path: '' }
  }
  return null
})
</script>
