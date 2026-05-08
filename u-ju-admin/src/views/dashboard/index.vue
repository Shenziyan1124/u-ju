<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">数据概览</div>
      <el-button text @click="loadData" :loading="loading">
        <el-icon><Refresh /></el-icon>刷新
      </el-button>
    </div>

    <div class="stat-grid">
      <div class="stat-card" v-for="item in stats" :key="item.label">
        <div :class="['stat-icon', `stat-icon--${item.theme}`]">
          <el-icon><component :is="item.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </div>
      </div>
    </div>

    <el-row :gutter="20" style="margin-top: var(--spacing-lg)">
      <el-col :span="16">
        <div class="data-card">
          <div class="data-card__header">
            <span class="data-card__title">快捷操作</span>
          </div>
          <div class="data-card__body quick-actions">
            <div class="action-item" @click="$router.push('/users')">
              <div class="action-icon" style="background: #eef2ff; color: #4f46e5"><el-icon><User /></el-icon></div>
              <span>用户管理</span>
            </div>
            <div class="action-item" @click="$router.push('/events')">
              <div class="action-icon" style="background: #ecfdf5; color: #10b981"><el-icon><Calendar /></el-icon></div>
              <span>活动审核</span>
            </div>
            <div class="action-item" @click="$router.push('/orders')">
              <div class="action-icon" style="background: #fffbeb; color: #f59e0b"><el-icon><ShoppingCart /></el-icon></div>
              <span>订单处理</span>
            </div>
            <div class="action-item" @click="$router.push('/certifications')">
              <div class="action-icon" style="background: #fef2f2; color: #ef4444"><el-icon><Stamp /></el-icon></div>
              <span>认证审核</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="data-card">
          <div class="data-card__header"><span class="data-card__title">系统信息</span></div>
          <div class="data-card__body sys-info">
            <div class="sys-row">
              <span class="sys-label">平台版本</span>
              <span class="sys-value">v1.0.0</span>
            </div>
            <div class="sys-row">
              <span class="sys-label">运行状态</span>
              <el-tag type="success" size="small">正常</el-tag>
            </div>
            <div class="sys-row">
              <span class="sys-label">数据库</span>
              <el-tag type="info" size="small">MySQL</el-tag>
            </div>
            <div class="sys-row">
              <span class="sys-label">缓存服务</span>
              <el-tag type="success" size="small">Redis</el-tag>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'

const loading = ref(false)
const stats = ref([
  { label: '用户总数', value: 0, icon: 'User', theme: 'primary' },
  { label: '活动总数', value: 0, icon: 'Calendar', theme: 'success' },
  { label: '订单总数', value: 0, icon: 'ShoppingCart', theme: 'warning' },
  { label: '待审认证', value: 0, icon: 'Stamp', theme: 'danger' },
])

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await adminApi.getDashboard()
    stats.value[0].value = res.data.userCount || 0
    stats.value[1].value = res.data.eventCount || 0
    stats.value[2].value = res.data.orderCount || 0
    stats.value[3].value = res.data.certificationCount || 0
  } catch {} finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-md);
}
.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-lg) var(--spacing-md);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-normal);
  border: 1px solid transparent;
}
.action-item:hover {
  background: var(--color-bg-hover);
  border-color: var(--color-border);
  transform: translateY(-2px);
}
.action-icon {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}
.action-item span { font-size: var(--font-size-sm); font-weight: 500; color: var(--color-text-regular); }

.sys-info { display: flex; flex-direction: column; gap: var(--spacing-md); }
.sys-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: var(--spacing-sm);
  border-bottom: 1px dashed var(--color-border-light);
}
.sys-row:last-child { border-bottom: none; padding-bottom: 0; }
.sys-label { font-size: var(--font-size-sm); color: var(--color-text-secondary); }
.sys-value { font-weight: 600; color: var(--color-text-primary); }
</style>
