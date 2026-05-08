<template>
  <view class="page">
    <view class="login-bg">
      <view class="status-bar"></view>
      <view class="brand-area">
        <text class="brand-name">U Ju</text>
        <text class="brand-slogan">发现精彩活动</text>
      </view>
    </view>

    <view class="form-card">
      <text class="form-title">欢迎回来</text>
      <text class="form-subtitle">登录你的账号，开始探索</text>

      <view class="input-group">
        <view class="input-item">
          <text class="input-icon">📱</text>
          <input v-model="form.phone" type="number" placeholder="请输入手机号" maxlength="11" />
        </view>
        <view class="input-item">
          <text class="input-icon">🔒</text>
          <input v-model="form.password" :type="showPwd ? 'text' : 'password'" placeholder="请输入密码" />
          <text class="toggle-pwd" @tap="showPwd = !showPwd">{{ showPwd ? '🙈' : '👁️' }}</text>
        </view>
      </view>

      <button class="submit-btn" :loading="loading" @click="handleLogin">{{ loading ? '登录中...' : '登 录' }}</button>

      <view class="form-footer">
        <text class="footer-link" @tap="goRegister">还没有账号？立即注册</text>
      </view>

      <view class="divider"><text>其他方式</text></view>

      <view class="social-login">
        <view class="social-btn wechat" @tap="wechatLogin">
          <text>微信登录</text>
        </view>
      </view>

      <view class="agreement">
        <text class="agree-text">登录即表示同意</text>
        <text class="agree-link">《用户协议》</text>
        <text class="agree-text">和</text>
        <text class="agree-link">《隐私政策》</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { userApi } from '@/api'

const form = reactive({ phone: '', password: '' })
const loading = ref(false)
const showPwd = ref(false)

const handleLogin = async () => {
  if (!form.phone || form.password) return uni.showToast({ title: '请填写完整', icon: 'none' })
  loading.value = true
  try {
    const res = await userApi.login(form)
    uni.setStorageSync('token', res.data.token)
    uni.setStorageSync('user', res.data)
    uni.switchTab({ url: '/pages/index/index' })
  } catch {} finally { loading.value = false }
}

const goRegister = () => uni.redirectTo({ url: '/pages/user/register' })
const wechatLogin = () => uni.showToast({ title: '微信登录开发中', icon: 'none' })
</script>

<style scoped>
.page { min-height: 100%; background: var(--color-bg); }

.login-bg {
  background: #1677ff;
  padding-top: env(safe-area-inset-top);
}
.status-bar { height: 44rpx; }
.brand-area { padding: 80rpx 48rpx 120rpx; }
.brand-name {
  display: block; font-size: 56rpx; font-weight: 800; color: #fff;
  letter-spacing: -2rpx; margin-bottom: 12rpx;
}
.brand-slogan { display: block; font-size: 28rpx; color: rgba(255,255,255,0.8); }

.form-card {
  margin: -60rpx 32rpx 0; background: var(--color-white);
  border-radius: var(--radius-xl); padding: 48rpx 36rpx;
  position: relative; z-index: 1;
}
.form-title { display: block; font-size: 40rpx; font-weight: 700; color: var(--color-text-1); }
.form-subtitle { display: block; font-size: 26rpx; color: var(--color-text-4); margin-top: 8rpx; margin-bottom: 40rpx; }

.input-group { gap: 24rpx; display: flex; flex-direction: column; }
.input-item {
  display: flex; align-items: center; gap: 16rpx;
  padding: 24rpx 28rpx; background: var(--color-fill);
  border-radius: var(--radius-md); border: 2rpx solid transparent;
  transition: all 0.2s;
}
.input-item:focus-within { border-color: var(--color-primary); background: #fff; }
.input-icon { font-size: 30rpx; width: 36rpx; text-align: center; }
.input-item input { flex: 1; font-size: 28rpx; color: var(--color-text-1); }
.toggle-pwd { font-size: 30rpx; padding: 8rpx; }

.submit-btn {
  width: 100%; height: 92rpx; line-height: 92rpx;
  margin-top: 40rpx; border-radius: var(--radius-lg);
  background: #1677ff;
  color: #fff; font-size: 32rpx; font-weight: 600;
  border: none; letter-spacing: 4rpx;
}
.submit-btn::after { border: none; }

.form-footer { text-align: center; margin-top: 28rpx; }
.footer-link { font-size: 27rpx; color: var(--color-primary); }

.divider {
  display: flex; align-items: center; margin: 40rpx 0 28rpx;
}
.divider::before, .divider::after {
  content: ''; flex: 1; height: 1rpx; background: var(--color-border);
}
.divider text { padding: 0 20rpx; font-size: 24rpx; color: var(--color-text-4); }

.social-login { display: flex; justify-content: center; }
.social-btn {
  padding: 20rpx 48rpx; border-radius: var(--radius-xl); font-size: 28rpx;
  font-weight: 500;
}
.social-btn.wechat { background: #ecfdf5; color: #059669; }

.agreement { text-align: center; margin-top: 28rpx; }
.agree-text { font-size: 22rpx; color: var(--color-text-4); }
.agree-link { font-size: 22rpx; color: var(--color-primary); }
</style>
