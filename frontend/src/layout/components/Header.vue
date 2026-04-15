<template>
  <div class="header-container">
    <div class="header-left">
      <el-icon
        class="hamburger"
        :size="20"
        @click="toggleCollapse"
      >
        <Fold v-if="!collapse" />
        <Expand v-else />
      </el-icon>
      <Breadcrumb />
    </div>
    <div class="header-right">
      <el-dropdown trigger="click" @command="handleCommand">
        <div class="user-info">
          <el-avatar
            :size="30"
            :src="avatarUrl"
            :icon="UserFilled"
          />
          <span class="username">{{ displayName }}</span>
          <el-icon :size="14"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>个人信息
            </el-dropdown-item>
            <el-dropdown-item command="password">
              <el-icon><Lock /></el-icon>修改密码
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  Fold,
  Expand,
  ArrowDown,
  User,
  UserFilled,
  Lock,
  SwitchButton
} from '@element-plus/icons-vue'
import Breadcrumb from './Breadcrumb.vue'
import { useUserStore } from '@/stores/user'

const props = defineProps<{
  collapse: boolean
}>()

const emit = defineEmits<{
  (e: 'update:collapse', value: boolean): void
}>()

const userStore = useUserStore()

const displayName = computed(() => {
  return (
    userStore.userInfo?.nickName
    || userStore.userInfo?.nickname
    || userStore.userInfo?.username
    || '管理员'
  )
})

const avatarUrl = computed(() => {
  const v = userStore.userInfo?.avatar
  return v ? String(v) : ''
})

function toggleCollapse() {
  emit('update:collapse', !props.collapse)
}

function handleCommand(command: string) {
  switch (command) {
    case 'profile':
      ElMessage.info('个人信息功能开发中')
      break
    case 'password':
      ElMessage.info('修改密码功能开发中')
      break
    case 'logout':
      handleLogout()
      break
  }
}

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await userStore.logout()
  } catch {
    // User cancelled
  }
}
</script>

<style scoped lang="scss">
.header-container {
  height: var(--header-height, 50px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  background: #fff;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.hamburger {
  cursor: pointer;
  color: #606266;
  transition: color 0.2s;

  &:hover {
    color: #409eff;
  }
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background-color 0.2s;

  &:hover {
    background-color: rgba(0, 0, 0, 0.04);
  }
}

.username {
  font-size: 14px;
  color: #37352f;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
