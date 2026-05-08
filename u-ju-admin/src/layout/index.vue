<template>
  <el-container class="layout">
    <el-aside :width="isCollapse ? '64px' : '240px'" class="sidebar">
      <div class="sidebar-logo" @click="router.push('/dashboard')">
        <div class="logo-icon">UJ</div>
        <transition name="fade">
          <span v-if="!isCollapse" class="logo-text">U Ju 管理平台</span>
        </transition>
      </div>
      <el-menu
        :default-active="route.path"
        :collapse="isCollapse"
        router
        :collapse-transition="false"
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>数据概览</template>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/certifications">
          <el-icon><Stamp /></el-icon>
          <template #title>认证审核</template>
        </el-menu-item>
        <el-menu-item index="/events">
          <el-icon><Calendar /></el-icon>
          <template #title>活动管理</template>
        </el-menu-item>
        <el-menu-item index="/orders">
          <el-icon><ShoppingCart /></el-icon>
          <template #title>订单管理</template>
        </el-menu-item>
        <el-menu-item index="/content">
          <el-icon><Document /></el-icon>
          <template #title>内容管理</template>
        </el-menu-item>
        <el-menu-item index="/settings">
          <el-icon><Setting /></el-icon>
          <template #title>系统设置</template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container class="main-area">
      <el-header class="topbar">
        <div class="topbar-left">
          <el-icon class="toggle-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>{{ route.meta.title || '首页' }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="topbar-right">
          <el-dropdown trigger="click">
            <div class="user-dropdown">
              <div class="avatar">
                <el-icon><UserFilled /></el-icon>
              </div>
              <span class="user-name">管理员</span>
              <el-icon class="arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout { height: 100vh; }

.sidebar {
  background: var(--sidebar-bg);
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  border-right: none;
}

.sidebar-logo {
  height: var(--header-height);
  display: flex;
  align-items: center;
  padding: 0 var(--spacing-md);
  gap: var(--spacing-sm);
  cursor: pointer;
  border-bottom: 1px solid rgba(255,255,255,0.06);
  flex-shrink: 0;
  transition: padding var(--transition-normal);
}
.sidebar-logo:hover { background: rgba(255,255,255,0.03); }
.logo-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 13px;
  font-weight: 800;
  flex-shrink: 0;
}
.logo-text {
  font-size: var(--font-size-lg);
  font-weight: 700;
  color: #fff;
  white-space: nowrap;
  letter-spacing: -0.3px;
}

.sidebar-menu {
  border-right: none !important;
  background: transparent !important;
  padding: var(--spacing-sm) 0;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}
.sidebar-menu:not(.el-menu--collapse) {
  width: 240px;
}

:deep(.sidebar-menu .el-menu-item) {
  height: 44px;
  line-height: 44px;
  margin: 2px 8px;
  border-radius: var(--radius-md);
  color: var(--sidebar-text);
  font-size: var(--font-size-base);
  transition: all var(--transition-fast);
}
:deep(.sidebar-menu .el-menu-item:hover) {
  background: rgba(255,255,255,0.06);
  color: #e0e0e0;
}
:deep(.sidebar-menu .el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(79,70,229,0.85), rgba(124,58,237,0.7));
  color: #fff;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(79,70,229,0.25);
}
:deep(.sidebar-menu .el-menu-item .el-icon) {
  font-size: 17px;
  width: 20px;
  justify-content: center;
}

.topbar {
  height: var(--header-height);
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--color-bg-white);
  border-bottom: 1px solid var(--color-border-light);
  padding: 0 var(--spacing-lg);
  box-shadow: var(--shadow-sm);
  z-index: 10;
}
.topbar-left { display: flex; align-items: center; gap: var(--spacing-md); }
.toggle-btn {
  font-size: 18px;
  cursor: pointer;
  color: var(--color-text-secondary);
  padding: 6px;
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
}
.toggle-btn:hover { color: var(--color-primary); background: var(--color-primary-bg); }
:deep(.el-breadcrumb__inner) { color: var(--color-text-secondary); font-weight: 500; }
:deep(.el-breadcrumb__separator) { color: var(--color-text-placeholder); }

.topbar-right { display: flex; align-items: center; }
.user-dropdown {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  padding: 6px 12px;
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}
.user-dropdown:hover { background: var(--color-bg-hover); }
.avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 14px;
}
.user-name { font-size: var(--font-size-base); font-weight: 500; color: var(--color-text-primary); }
.arrow { font-size: 12px; color: var(--color-text-placeholder); }

.main-content {
  background: var(--color-bg-page);
  padding: var(--spacing-lg);
  overflow-y: auto;
}

.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
.fade-slide-enter-active { transition: all 0.25s ease; }
.fade-slide-leave-active { transition: all 0.2s ease; }
.fade-slide-enter-from { opacity: 0; transform: translateY(8px); }
.fade-slide-leave-to { opacity: 0; transform: translateY(-8px); }
</style>
