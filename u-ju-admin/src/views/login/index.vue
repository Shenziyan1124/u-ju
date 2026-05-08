<template>
  <div class="login-page">
    <div class="login-left">
      <div class="brand">
        <div class="brand-logo">U Ju</div>
        <h1 class="brand-title">活动管理平台</h1>
        <p class="brand-desc">高效、智能的一站式活动运营管理系统</p>
      </div>
      <div class="features">
        <div class="feature-item">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据可视化分析</span>
        </div>
        <div class="feature-item">
          <el-icon><UserFilled /></el-icon>
          <span>用户精细化管理</span>
        </div>
        <div class="feature-item">
          <el-icon><Document /></el-icon>
          <span>全流程审核追踪</span>
        </div>
      </div>
    </div>
    <div class="login-right">
      <div class="login-form-wrapper">
        <h2 class="form-title">欢迎回来</h2>
        <p class="form-subtitle">请登录您的管理员账号</p>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="login-form">
          <el-form-item prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" size="large" :prefix-icon="Phone" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" :prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" :loading="loading" class="login-btn" @click="handleLogin">
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>
        <div class="login-footer">
          <span class="footer-hint">默认账号: 13800000000 / admin</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { Phone, Lock, DataAnalysis, UserFilled, Document } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ phone: '', password: '' })
const rules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleLogin = async () => {
  await formRef.value?.validate()
  loading.value = true
  try {
    await userStore.login(form.phone, form.password)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch {
    ElMessage.error('登录失败，请检查账号密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  height: 100vh;
  min-height: 600px;
}
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 50%, #a78bfa 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 80px;
  position: relative;
  overflow: hidden;
}
.login-left::before {
  content: '';
  position: absolute;
  top: -100px;
  right: -100px;
  width: 300px;
  height: 300px;
  background: rgba(255,255,255,0.08);
  border-radius: 50%;
}
.login-left::after {
  content: '';
  position: absolute;
  bottom: -60px;
  left: -40px;
  width: 200px;
  height: 200px;
  background: rgba(255,255,255,0.06);
  border-radius: 50%;
}
.brand { position: relative; z-index: 1; }
.brand-logo {
  width: 56px;
  height: 56px;
  background: rgba(255,255,255,0.2);
  backdrop-filter: blur(10px);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  font-weight: 800;
  margin-bottom: var(--spacing-lg);
  letter-spacing: -1px;
}
.brand-title {
  color: #fff;
  font-size: 32px;
  font-weight: 700;
  margin-bottom: var(--spacing-sm);
  letter-spacing: -0.5px;
}
.brand-desc {
  color: rgba(255,255,255,0.75);
  font-size: var(--font-size-md);
  line-height: 1.6;
  max-width: 360px;
}
.features {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  margin-top: var(--spacing-xxl);
  position: relative;
  z-index: 1;
}
.feature-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  color: rgba(255,255,255,0.85);
  font-size: var(--font-size-base);
}
.feature-item .el-icon {
  width: 28px;
  height: 28px;
  background: rgba(255,255,255,0.15);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}
.login-right {
  width: 480px;
  background: var(--color-bg-white);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl);
}
.login-form-wrapper { width: 100%; max-width: 380px; }
.form-title {
  font-size: var(--font-size-2xl);
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-xs);
}
.form-subtitle {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-xl);
}
.login-form :deep(.el-input__wrapper) {
  border-radius: var(--radius-md);
  box-shadow: none;
  border: 1px solid var(--color-border);
  transition: all var(--transition-fast);
}
.login-form :deep(.el-input__wrapper:hover) {
  border-color: var(--color-primary-light);
}
.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px var(--color-primary-bg);
}
.login-btn {
  width: 100%;
  height: 44px;
  border-radius: var(--radius-md);
  font-size: var(--font-size-md);
  font-weight: 600;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  border: none;
  letter-spacing: 2px;
  transition: all var(--transition-normal);
}
.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(79, 70, 229, 0.35);
}
.login-btn:active { transform: translateY(0); }
.login-footer {
  text-align: center;
  margin-top: var(--spacing-lg);
}
.footer-hint {
  font-size: var(--font-size-xs);
  color: var(--color-text-placeholder);
  background: var(--color-info-bg);
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--radius-sm);
  display: inline-block;
}
@media (max-width: 900px) {
  .login-left { display: none; }
  .login-right { width: 100%; }
}
</style>
