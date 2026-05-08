<template>
  <view class="page">
    <view class="profile-header">
      <image :src="user.avatar || '/static/avatar-default.png'" class="avatar" mode="aspectFill" />
      <view class="user-info">
        <text class="nickname">{{ user.username || '未登录' }}</text>
        <text class="phone">{{ user.phone ? user.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : '' }}</text>
      </view>
      <view class="settings-btn" @tap="goSettings">⚙️</view>
    </view>

    <view class="stats-row">
      <view class="stat-item" @tap="goPage('/pages/registration/list')">
        <text class="stat-num">{{ stats.registrations || 0 }}</text>
        <text class="stat-label">报名</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item" @tap="goPage('/pages/order/list')">
        <text class="stat-num">{{ stats.orders || 0 }}</text>
        <text class="stat-label">订单</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item" @tap="goPage('/pages/user/favorites')">
        <text class="stat-num">{{ stats.favorites || 0 }}</text>
        <text class="stat-label">收藏</text>
      </view>
    </view>

    <view class="menu-section" v-if="isLoggedIn">
      <view class="menu-group">
        <view class="menu-item" @tap="goPage('/pages/event/search?mode=mine')">
          <text class="mi-icon">📋</text><text class="mi-text">我的活动</text><text class="mi-arrow">›</text>
        </view>
        <view class="menu-item" @tap="goPage('/pages/order/list')">
          <text class="mi-icon">🧾</text><text class="mi-text">我的订单</text><text class="mi-arrow">›</text>
        </view>
        <view class="menu-item" @tap="goPage('/pages/user/messages')">
          <text class="mi-icon">💬</text><text class="mi-text">消息通知</text><text class="mi-badge" v-if="unreadCount > 0">{{ unreadCount > 99 ? '99+' : unreadCount }}</text><text class="mi-arrow">›</text>
        </view>
      </view>

      <view class="menu-group">
        <view class="menu-item" @tap="goPage('/pages/user/profile')">
          <text class="mi-icon">✏️</text><text class="mi-text">个人资料</text><text class="mi-arrow">›</text>
        </view>
        <view class="menu-item" @tap="goPage('/pages/organizer/certify')">
          <text class="mi-icon">🏢</text><text class="mi-text">企业认证</text><text class="mi-arrow">›</text>
        </view>
        <view class="menu-item" @tap="goPage('/pages/organizer/publish')">
          <text class="mi-icon">🎤</text><text class="mi-text">发布活动</text><text class="mi-arrow">›</text>
        </view>
      </view>

      <view class="menu-group">
        <view class="menu-item" @tap="goSettings">
          <text class="mi-icon">⚙️</text><text class="mi-text">设置</text><text class="mi-arrow">›</text>
        </view>
      </view>
    </view>

    <view v-else class="login-prompt">
      <button class="login-btn" @tap="goLogin">立即登录</button>
    </view>

    <view class="logout-area" v-if="isLoggedIn">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { userApi, notificationApi } from '@/api'

const user = ref({})
const stats = ref({ registrations: 0, orders: 0, favorites: 0 })
const unreadCount = ref(0)

const isLoggedIn = computed(() => !!uni.getStorageSync('token'))

const goLogin = () => uni.navigateTo({ url: '/pages/user/login' })
const goSettings = () => uni.navigateTo({ url: '/pages/user/settings' })
const goPage = (url) => uni.navigateTo({ url })

const handleLogout = () => {
  uni.showModal({ title: '提示', content: '确定退出登录？', success: (res) => {
    if (res.confirm) {
      uni.removeStorageSync('token')
      uni.removeStorageSync('user')
      user.value = {}
      uni.showToast({ title: '已退出', icon: 'success' })
    }
  }})
}

onShow(async () => {
  const token = uni.getStorageSync('token')
  if (!token) return
  try {
    const uRes = await userApi.getMe()
    user.value = uRes.data
    const nRes = await notificationApi.getUnreadCount()
    unreadCount.value = nRes.data || 0
  } catch {}
})
</script>

<style scoped>
.page { min-height: 100%; background: var(--color-bg); }

.profile-header {
  display: flex; align-items: center; gap: 24rpx;
  padding: 48rpx 32rpx 32rpx; background: var(--color-white);
}
.avatar {
  width: 120rpx; height: 120rpx; border-radius: var(--radius-full);
  border: 4rpx solid var(--color-primary-bg); flex-shrink: 0;
}
.user-info { flex: 1; }
.nickname { font-size: 36rpx; font-weight: 700; color: var(--color-text-1); display: block; }
.phone { font-size: 26rpx; color: var(--color-text-4); margin-top: 6rpx; display: block; }
.settings-btn { width: 64rpx; height: 64rpx; display: flex; align-items: center; justify-content: center; font-size: 32rpx; }

.stats-row {
  display: flex; align-items: center;
  margin: 20rpx 32rpx; padding: 28rpx;
  background: var(--color-white); border-radius: var(--radius-lg);
  border: 1rpx solid var(--color-border-light);
}
.stat-item { flex: 1; text-align: center; }
.stat-num { display: block; font-size: 40rpx; font-weight: 800; color: var(--color-primary); }
.stat-label { display: block; font-size: 24rpx; color: var(--color-text-4); margin-top: 6rpx; }
.stat-divider { width: 1rpx; height: 40rpx; background: var(--color-border-light); }

.menu-section { padding: 28rpx 32rpx; }
.menu-group { background: var(--color-white); border-radius: var(--radius-lg); overflow: hidden; margin-bottom: 20rpx; }
.menu-item {
  display: flex; align-items: center; gap: 20rpx;
  padding: 30rpx 28rpx; position: relative;
}
.menu-item:not(:last-child)::after {
  content: ''; position: absolute; bottom: 0; left: 80rpx; right: 0;
  height: 1rpx; background: var(--color-border-light);
}
.mi-icon { font-size: 34rpx; width: 44rpx; text-align: center; }
.mi-text { flex: 1; font-size: 29rpx; color: var(--color-text-2); }
.mi-arrow { font-size: 30rpx; color: var(--color-text-4); }
.mi-badge {
  min-width: 36rpx; height: 36rpx; line-height: 36rpx; text-align: center;
  font-size: 22rpx; background: var(--color-danger); color: #fff;
  border-radius: var(--radius-full); padding: 0 10rpx; margin-right: 8rpx;
}

.login-prompt { padding: 60rpx 32rpx; text-align: center; }
.login-btn {
  width: 100%; height: 88rpx; line-height: 88rpx;
  background: #1677ff;
  color: #fff; font-size: 30rpx; font-weight: 600; border-radius: var(--radius-lg);
  border: none;
}
.login-btn::after { border: none; }

.logout-area { padding: 40rpx 32rpx; }
.logout-btn {
  width: 100%; height: 84rpx; line-height: 84rpx;
  background: transparent; color: var(--color-danger); font-size: 29rpx;
  border: 2rpx solid var(--color-danger); border-radius: var(--radius-lg);
}
.logout-btn::after { border: none; }
</style>
