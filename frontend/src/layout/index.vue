<template>
  <div class="app-layout" :class="{ 'is-collapsed': isCollapsed }">
    <div class="sidebar-wrapper">
      <Sidebar :collapse="isCollapsed" />
    </div>
    <div class="main-wrapper">
      <Header v-model:collapse="isCollapsed" />
      <TagsView />
      <div class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref } from 'vue'
import Sidebar from './components/Sidebar.vue'
import Header from './components/Header.vue'
import TagsView from './components/TagsView.vue'

const isCollapsed = ref(false)

function syncCollapsedByWidth() {
  isCollapsed.value = window.innerWidth <= 768
}

onMounted(() => {
  syncCollapsedByWidth()
  window.addEventListener('resize', syncCollapsedByWidth)
})

onBeforeUnmount(() => window.removeEventListener('resize', syncCollapsedByWidth))
</script>

<style scoped lang="scss">
.app-layout {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.sidebar-wrapper {
  width: var(--sidebar-width, 210px);
  height: 100%;
  flex-shrink: 0;
  transition: width 0.28s ease;
  overflow: hidden;
}

.is-collapsed .sidebar-wrapper {
  width: var(--sidebar-collapsed-width, 64px);
}

.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
}

.main-content {
  flex: 1;
  overflow: auto;
  background-color: #f0f2f5;
}

:deep(.el-table) {
  width: 100%;
}

:deep(.el-table__body-wrapper) {
  overflow-x: auto;
}

@media (max-width: 768px) {
  .sidebar-wrapper {
    width: var(--sidebar-collapsed-width, 64px);
  }
  .main-content {
    min-width: 0;
  }
}

// Route transition
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
