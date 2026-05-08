<template>
  <view class="page">
    <view class="header">
      <image :src="user.avatar || '/static/avatar-default.png'" class="avatar" mode="aspectFill" />
      <text class="hint">点击更换头像</text>
    </view>

    <view class="form-section">
      <text class="section-title">基本信息</text>
      <view class="form-item"><text class="label">用户名</text><input v-model="form.username" placeholder="请输入用户名" /></view>
      <view class="form-item"><text class="label">真实姓名</text><input v-model="form.realName" placeholder="请输入真实姓名" /></view>
      <view class="form-item"><text class="label">手机号</text><input v-model="form.phone" disabled /></view>
      <view class="form-item"><text class="label">邮箱</text><input v-model="form.email" placeholder="选填" /></view>
      <view class="form-item picker-row">
        <text class="label">性别</text>
        <picker :range="['未设置','男','女']" :value="form.gender || 0" @change="form.gender = $event.detail.value">
          <text class="picker-val">{{ ['未设置','男','女'][form.gender || 0] }} ›</text>
        </picker>
      </view>
    </view>

    <view class="form-section">
      <text class="section-title">账号安全</text>
      <view class="action-item" @tap="goChangePwd">
        <text class="ai-icon">🔒</text><text class="ai-text">修改密码</text><text class="ai-arrow">›</text>
      </view>
    </view>

    <button class="save-btn" :loading="saving" @click="handleSave">{{ saving ? '保存中...' : '保 存' }}</button>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { userApi } from '@/api'

const saving = ref(false)
const form = reactive({ username: '', realName: '', phone: '', email: '', gender: 0 })

const handleSave = async () => {
  if (!form.username) return uni.showToast({ title: '请填写用户名', icon: 'none' })
  saving.value = true
  try { await userApi.updateProfile(form); uni.showToast({ title: '保存成功', icon: 'success' }); setTimeout(() => uni.navigateBack(), 1000) }
  catch {} finally { saving.value = false }
}
const goChangePwd = () => uni.navigateTo({ url: '/pages/user/settings?mode=pwd' })
</script>

<style scoped>
.page { min-height: 100vh; background: var(--color-bg); }

.header {
  display: flex; flex-direction: column; align-items: center; gap: 16rpx;
  padding: 48rpx 0 32rpx; background: var(--color-white);
}
.avatar {
  width: 140rpx; height: 140rpx; border-radius: var(--radius-full);
  border: 4rpx solid var(--color-primary-bg);
}
.hint { font-size: 24rpx; color: var(--color-text-4); }

.form-section { padding: 28rpx 32rpx; margin-top: 20rpx; }
.section-title { font-size: 30rpx; font-weight: 600; color: var(--color-text-1); display: block; margin-bottom: 24rpx; }

.form-item {
  display: flex; align-items: center; gap: 16rpx;
  padding: 26rpx 28rpx; margin-bottom: 18rpx;
  background: var(--color-white); border-radius: var(--radius-md);
  border: 2rpx solid var(--color-border); transition: all 0.2s;
}
.form-item:focus-within { border-color: var(--color-primary); }
.label { width: 120rpx; font-size: 27rpx; color: var(--color-text-3); flex-shrink: 0; }
.form-item input { flex: 1; font-size: 28rpx; color: var(--color-text-1); }
.form-item input[disabled] { color: var(--color-text-4); }
.picker-row { justify-content: space-between; }
.picker-val { font-size: 27rpx; color: var(--color-text-3); }

.action-item {
  display: flex; align-items: center; gap: 16rpx;
  padding: 26rpx 28rpx; margin-bottom: 14rpx;
  background: var(--color-white); border-radius: var(--radius-md);
}
.ai-icon { font-size: 32rpx; width: 40rpx; text-align: center; }
.ai-text { flex: 1; font-size: 29rpx; color: var(--color-text-2); }
.ai-arrow { font-size: 28rpx; color: var(--color-text-4); }

.save-btn {
  margin: 32rpx; height: 92rpx; line-height: 92rpx;
  background: #1677ff; color: #fff;
  font-size: 32rpx; font-weight: 600; border-radius: var(--radius-lg); border: none; letter-spacing: 4rpx;
}
.save-btn::after { border: none; }
</style>
