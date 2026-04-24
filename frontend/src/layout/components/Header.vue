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
    <div class="header-center">
      <span class="status-pill"><i></i>系统运行正常</span>
      <span class="status-pill warning"><i></i>预警联动在线</span>
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
  height: var(--header-height, 58px);
  display: grid;
  grid-template-columns: minmax(220px, 1fr) auto minmax(180px, 1fr);
  align-items: center;
  gap: 14px;
  padding: 0 18px;
  background: rgba(255, 255, 255, 0.78);
  border: 1px solid rgba(255, 255, 255, 0.72);
  border-bottom: 0;
  border-radius: 24px 24px 0 0;
  box-shadow: 0 12px 30px rgba(16, 50, 38, 0.08);
  backdrop-filter: blur(18px);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.header-center {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  color: #0f513d;
  background: #e7fff6;
  border: 1px solid rgba(0, 163, 122, 0.14);
  font-size: 12px;
  font-weight: 750;

  i {
    width: 7px;
    height: 7px;
    border-radius: 50%;
    background: #16a34a;
    box-shadow: 0 0 0 5px rgba(22, 163, 74, 0.12);
  }

  &.warning {
    color: #7a4a05;
    background: #fff7e6;
    border-color: rgba(245, 158, 11, 0.16);

    i {
      background: #f59e0b;
      box-shadow: 0 0 0 5px rgba(245, 158, 11, 0.14);
    }
  }
}

.hamburger {
  cursor: pointer;
  color: #315346;
  width: 36px;
  height: 36px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #f2faf6;
  transition: all 0.2s;

  &:hover {
    color: #ffffff;
    background: var(--primary-color);
    box-shadow: 0 10px 20px rgba(0, 163, 122, 0.22);
  }
}

.header-right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 5px 10px 5px 5px;
  border-radius: 999px;
  background: rgba(242, 250, 246, 0.92);
  border: 1px solid rgba(15, 66, 48, 0.08);
  transition: all 0.2s;

  &:hover {
    background-color: #ffffff;
    box-shadow: 0 8px 20px rgba(16, 50, 38, 0.08);
  }
}

.username {
  font-size: 14px;
  font-weight: 750;
  color: #173d31;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 980px) {
  .header-container {
    grid-template-columns: 1fr auto;
  }
  .header-center {
    display: none;
  }
}
</style>
