<script setup lang="ts">
import { onBeforeMount } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

onBeforeMount(() => {
  const path = '/' + ((route.params.path as string) || '')
  router.replace({ path, query: route.query })
})
</script>

<template>
  <div />
</template>
