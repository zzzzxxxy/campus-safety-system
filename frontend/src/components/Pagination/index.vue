<template>
  <div class="pagination-wrapper" v-if="total > 0">
    <el-pagination
      background
      :current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      :page-sizes="pageSizes"
      :layout="layout"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
    />
  </div>
</template>

<script setup lang="ts">
interface Props {
  pageNum: number
  pageSize: number
  total: number
  pageSizes?: number[]
  layout?: string
}

const props = withDefaults(defineProps<Props>(), {
  pageSizes: () => [10, 20, 30, 50, 100],
  layout: 'total, sizes, prev, pager, next, jumper'
})

const emit = defineEmits<{
  (e: 'update:pageNum', v: number): void
  (e: 'update:pageSize', v: number): void
  (e: 'pagination', payload: { pageNum: number; pageSize: number }): void
}>()

function handleCurrentChange(v: number) {
  emit('update:pageNum', v)
  emit('pagination', { pageNum: v, pageSize: props.pageSize })
}

function handleSizeChange(v: number) {
  // change size should reset to page 1
  emit('update:pageSize', v)
  emit('update:pageNum', 1)
  emit('pagination', { pageNum: 1, pageSize: v })
}
</script>

<style scoped lang="scss">
.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
