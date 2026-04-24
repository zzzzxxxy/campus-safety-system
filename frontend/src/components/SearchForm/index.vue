<template>
  <el-card shadow="never" class="search-form-card">
    <el-form class="search-form" :inline="true" :model="model" @submit.prevent>
      <slot :collapsed="collapsed" />
      <template v-if="!collapsed">
        <slot name="extra" />
      </template>
      <el-form-item class="search-actions">
        <el-button type="primary" :loading="loading" @click="$emit('search')">查询</el-button>
        <el-button :disabled="loading" @click="$emit('reset')">重置</el-button>
        <el-button v-if="collapsible" link type="primary" @click="collapsed = !collapsed">
          {{ collapsed ? '展开' : '收起' }}
          <el-icon class="toggle-icon" :class="{ expanded: !collapsed }"><ArrowDown /></el-icon>
        </el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ArrowDown } from '@element-plus/icons-vue'

const props = withDefaults(defineProps<{
  model?: Record<string, any>
  loading?: boolean
  collapsible?: boolean
  defaultCollapsed?: boolean
}>(), {
  model: () => ({}),
  loading: false,
  collapsible: true,
  defaultCollapsed: false
})

defineEmits<{
  search: []
  reset: []
}>()

const collapsed = ref(props.defaultCollapsed)
</script>

<style scoped lang="scss">
.search-form-card {
  margin-bottom: 16px;
}
.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 0 8px;
}
.search-actions {
  margin-right: 0;
}
.toggle-icon {
  margin-left: 4px;
  transition: transform .2s;
  &.expanded { transform: rotate(180deg); }
}
</style>
