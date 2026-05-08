<template>
  <view class="page">
    <view class="header">
      <text class="title">创建账号</text>
      <text class="subtitle">加入 U Ju，发现更多精彩活动</text>
    </view>

    <view class="form-area">
      <view class="input-item"><input v-model="form.phone" type="number" placeholder="手机号" maxlength="11" /></view>
      <view class="input-row">
        <view class="input-item flex-1"><input v-model="form.username" placeholder="用户名" /></view>
      </view>
      <view class="input-item">
        <input v-model="form.password" :type="showPwd ? 'text' : 'password'" placeholder="设置密码（6位以上）" />
        <text class="toggle" @tap="showPwd = !showPwd">{{ showPwd ? '🙈' : '👁️' }}</text>
      </view>

      <button class="submit-btn" :loading="loading" @click="handleRegister">{{ loading ? '注册中...' : '注 册' }}</button>

      <view class="footer-text">已有账号？<text class="link" @tap="goLogin">立即登录</text></view>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { userApi } from '@/api'

const form = reactive({ phone: '', username: '', password: '' })
const loading = ref(false)
const showPwd = ref(false)

const handleRegister = async () => {
  if (!form.phone || !form.password) return uni.showToast({ title: '请填写完整', icon: 'none' })
  if (form.password.length < 6) return uni.showToast({ title: '密码至少6位', icon: 'none' })
  loading.value = true
  try {
    await userApi.register(form)
    uni.showToast({ title: '注册成功，请登录', icon: 'success' })
    setTimeout(() => uni.redirectTo({ url: '/pages/user/login' }), 1500)
  } catch {} finally { loading.value = false }
}
const goLogin = () => uni.redirectTo({ url: '/pages/user/login' })
</script>

<style scoped>
.page { min-height: 100vh; background: var(--color-bg); padding-top: env(safe-area-inset-top); }

.header { padding: 80rpx 40rpx 48rpx; }
.title { display: block; font-size: 44rpx; font-weight: 800; color: var(--color-text-1); }
.subtitle { display: block; font-size: 27rpx; color: var(--color-text-4); margin-top: 10rpx; }

.form-area { padding: 0 32rpx; }
.input-item {
  display: flex; align-items: center; gap: 16rpx;
  padding: 26rpx 28rpx; margin-bottom: 20rpx;
  background: var(--color-white); border-radius: var(--radius-md);
  border: 2rpx solid var(--color-border); transition: all 0.2s;
}
.input-item:focus-within { border-color: var(--color-primary); }
.input-item input { flex: 1; font-size: 28rpx; color: var(--color-text-1); }
.input-row { display: flex; gap: 20rpx; }
.flex-1 { flex: 1; }
.toggle { font-size: 30rpx; padding: 8rpx; }

.submit-btn {
  width: 100%; height: 92rpx; line-height: 92rpx; margin-top: 36rpx;
  background: #1677ff; color: #fff;
  font-size: 32rpx; font-weight: 600; border-radius: var(--radius-lg); border: none; letter-spacing: 4rpx;
}
.submit-btn::after { border: none; }

.footer-text { text-align: center; margin-top: 32rpx; font-size: 27rpx; color: var(--color-text-4); }
.link { color: var(--color-primary); font-weight: 500; }
</style>
