<template>
  <div class="app-layout" :class="{ 'is-collapsed': isCollapsed }">
    <div class="sidebar-wrapper">
      <Sidebar :collapse="isCollapsed" />
    </div>
    <div class="main-wrapper">
      <Header v-model:collapse="isCollapsed" />
      <TagsView />
      <div class="main-content">
        <router-view v-slot="{ Component, route }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" :key="route.fullPath" />
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
  position: relative;
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background: transparent;
}

.app-layout::before {
  content: '';
  position: absolute;
  inset: 14px 14px 14px 12px;
  border: 1px solid rgba(255, 255, 255, 0.56);
  border-radius: 30px;
  pointer-events: none;
  z-index: 0;
}

.sidebar-wrapper {
  position: relative;
  z-index: 2;
  width: var(--sidebar-width, 236px);
  height: 100%;
  flex-shrink: 0;
  padding: 14px 0 14px 14px;
  transition: width 0.28s cubic-bezier(0.22, 1, 0.36, 1);
  overflow: hidden;
}

.is-collapsed .sidebar-wrapper {
  width: var(--sidebar-collapsed-width, 72px);
}

.main-wrapper {
  position: relative;
  z-index: 1;
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
  padding: 14px 14px 14px 12px;
}

.main-content {
  flex: 1;
  overflow: auto;
  min-width: 0;
  border-radius: 0 0 24px 24px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.66) 0%, rgba(255, 255, 255, 0.28) 100%),
    rgba(238, 243, 239, 0.58);
  backdrop-filter: blur(12px);
}

:deep(.el-table) {
  width: 100%;
}

:deep(.el-table__body-wrapper) {
  overflow-x: auto;
}

@media (max-width: 768px) {
  .app-layout::before {
    inset: 8px;
    border-radius: 22px;
  }

  .sidebar-wrapper {
    width: var(--sidebar-collapsed-width, 72px);
    padding: 8px 0 8px 8px;
  }

  .main-wrapper {
    padding: 8px 8px 8px 6px;
  }

  .main-content {
    min-width: 0;
  }
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
