<template>
  <div class="tags-view-container">
    <el-scrollbar class="tags-scrollbar">
      <div class="tags-wrapper">
        <span
          v-for="tag in visitedViews"
          :key="tag.path"
          class="tags-view-item"
          :class="{ active: isActive(tag.path) }"
          @click="goTo(tag.path)"
          @contextmenu.prevent="openMenu(tag, $event)"
        >
          {{ tag.title }}
          <el-icon
            v-if="!tag.affix"
            class="tag-close"
            :size="12"
            @click.stop="closeSelectedTag(tag)"
          >
            <Close />
          </el-icon>
        </span>
      </div>
    </el-scrollbar>

    <ul
      v-show="contextMenu.visible"
      class="contextmenu"
      :style="{ left: `${contextMenu.left}px`, top: `${contextMenu.top}px` }"
    >
      <li @click="refreshSelectedTag">刷新当前</li>
      <li @click="closeSelectedTag(contextMenu.tag)">关闭当前</li>
      <li @click="closeOthersTags(contextMenu.tag)">关闭其他</li>
      <li @click="closeAllTags">关闭全部</li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, reactive, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Close } from '@element-plus/icons-vue'

type TagView = {
  path: string
  fullPath: string
  title: string
  affix?: boolean
}

const route = useRoute()
const router = useRouter()

const HOME_TAG: TagView = {
  path: '/dashboard',
  fullPath: '/dashboard',
  title: '仪表盘',
  affix: true
}

const visitedViews = reactive<TagView[]>([HOME_TAG])
const contextMenu = reactive({
  visible: false,
  left: 0,
  top: 0,
  tag: HOME_TAG as TagView
})

const currentTag = computed<TagView>(() => ({
  path: route.path,
  fullPath: route.fullPath,
  title: (route.meta?.title as string) || '未命名页面',
  affix: route.path === HOME_TAG.path
}))

function addView() {
  if (!route.name || route.meta?.hidden) return
  if (route.path === '/login' || route.path === '/404' || route.path === '/403') return
  const existed = visitedViews.find((v) => v.path === route.path)
  if (existed) {
    existed.fullPath = route.fullPath
    existed.title = currentTag.value.title
    return
  }
  visitedViews.push(currentTag.value)
}

function isActive(path: string) {
  return path === route.path
}

function goTo(path: string) {
  router.push(path)
}

function openMenu(tag: TagView, e: MouseEvent) {
  contextMenu.tag = tag
  contextMenu.left = e.clientX
  contextMenu.top = e.clientY
  contextMenu.visible = true
}

function closeMenu() {
  contextMenu.visible = false
}

function closeSelectedTag(tag: TagView) {
  if (!tag || tag.affix) {
    closeMenu()
    return
  }
  const index = visitedViews.findIndex((v) => v.path === tag.path)
  if (index >= 0) visitedViews.splice(index, 1)
  if (isActive(tag.path)) {
    const nextTag = visitedViews[index] || visitedViews[index - 1] || HOME_TAG
    router.push(nextTag.fullPath || nextTag.path)
  }
  closeMenu()
}

function closeOthersTags(tag: TagView) {
  const target = tag || currentTag.value
  for (let i = visitedViews.length - 1; i >= 0; i--) {
    const item = visitedViews[i]
    if (!item.affix && item.path !== target.path) visitedViews.splice(i, 1)
  }
  if (!isActive(target.path)) router.push(target.fullPath || target.path)
  closeMenu()
}

function closeAllTags() {
  for (let i = visitedViews.length - 1; i >= 0; i--) {
    if (!visitedViews[i].affix) visitedViews.splice(i, 1)
  }
  if (route.path !== HOME_TAG.path) router.push(HOME_TAG.path)
  closeMenu()
}

function refreshSelectedTag() {
  const fullPath = route.fullPath
  closeMenu()
  router.replace('/redirect' + fullPath)
}

watch(() => route.fullPath, () => nextTick(addView), { immediate: true })
watch(() => contextMenu.visible, (visible) => {
  if (visible) document.body.addEventListener('click', closeMenu, { once: true })
})
</script>

<style scoped lang="scss">
.tags-view-container {
  height: 38px;
  background: rgba(255, 255, 255, 0.78);
  border-left: 1px solid rgba(255, 255, 255, 0.72);
  border-right: 1px solid rgba(255, 255, 255, 0.72);
  border-bottom: 1px solid rgba(15, 66, 48, 0.08);
  display: flex;
  align-items: center;
  position: relative;
  z-index: 5;
  backdrop-filter: blur(18px);
}

.tags-scrollbar {
  width: 100%;
}

.tags-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 14px;
  white-space: nowrap;
}

.tags-view-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 26px;
  padding: 0 11px;
  border: 1px solid rgba(15, 66, 48, 0.10);
  border-radius: 999px;
  color: #65736c;
  font-size: 12px;
  font-weight: 650;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.76);
  transition: all 0.2s ease;

  &:hover {
    color: var(--primary-color);
    border-color: rgba(0, 163, 122, 0.28);
    transform: translateY(-1px);
  }

  &.active {
    color: #ffffff;
    border-color: transparent;
    background: var(--gradient-safety);
    box-shadow: 0 8px 18px rgba(0, 163, 122, 0.18);
  }
}

.tag-close {
  border-radius: 50%;

  &:hover {
    background: rgba(255, 255, 255, 0.22);
  }
}

.contextmenu {
  position: fixed;
  z-index: 3000;
  margin: 0;
  padding: 8px;
  list-style: none;
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid rgba(15, 66, 48, 0.10);
  border-radius: 14px;
  box-shadow: var(--shadow-card);
  backdrop-filter: blur(18px);

  li {
    min-width: 102px;
    padding: 8px 14px;
    color: #51635b;
    font-size: 13px;
    border-radius: 10px;
    cursor: pointer;

    &:hover {
      color: #0f513d;
      background: #e7fff6;
    }
  }
}
</style>
