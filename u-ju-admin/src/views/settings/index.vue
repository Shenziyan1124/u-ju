<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">系统设置</div>
    </div>

    <el-row :gutter="20">
      <el-col :span="16">
        <div class="data-card">
          <div class="data-card__header"><span class="data-card__title">基本信息</span></div>
          <div class="data-card__body">
            <el-form label-width="100px" style="max-width: 560px">
              <el-form-item label="平台名称">
                <el-input v-model="form.platformName" placeholder="请输入平台名称" />
              </el-form-item>
              <el-form-item label="联系邮箱">
                <el-input v-model="form.contactEmail" placeholder="请输入联系邮箱" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSave">保存修改</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </el-col>

      <el-col :span="8">
        <div class="data-card">
          <div class="data-card__header"><span class="data-card__title">账号信息</span></div>
          <div class="data-card__body account-info">
            <div class="account-avatar">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="account-detail">
              <p class="account-name">管理员</p>
              <p class="account-phone">13800000000</p>
            </div>
            <el-button type="danger" plain size="small" @click="handleLogout">退出登录</el-button>
          </div>
        </div>

        <div class="data-card" style="margin-top: var(--spacing-md)">
          <div class="data-card__header"><span class="data-card__title">关于系统</span></div>
          <div class="data-card__body">
            <p class="about-text">U Ju 活动管理平台 v1.0.0</p>
            <p class="about-subtext">基于 Vue 3 + Spring Boot 构建</p>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

const form = reactive({ platformName: 'U Ju 活动管理平台', contactEmail: '' })

const handleSave = () => { ElMessage.success('设置已保存') }
const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.account-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg) 0;
}
.account-avatar {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 28px;
}
.account-detail { text-align: center; }
.account-name { font-weight: 600; color: var(--color-text-primary); margin-bottom: 2px; }
.account-phone { font-size: var(--font-size-sm); color: var(--color-text-secondary); }

.about-text { font-weight: 600; color: var(--color-text-primary); }
.about-subtitle, .about-subtext { font-size: var(--font-size-sm); color: var(--color-text-secondary); margin-top: var(--spacing-xs); }
</style>
