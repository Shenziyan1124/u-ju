<template>
  <view class="page">
    <view v-if="list.length || loading" class="list">
      <view v-for="item in list" :key="item.id" :class="['msg-card', { unread: item.isRead === 0 }]" @tap="markRead(item)">
        <view class="mc-dot" v-if="item.isRead === 0"></view>
        <view class="mc-body">
          <text class="mc-title">{{ item.title || '系统通知' }}</text>
          <text class="mc-content">{{ item.content || '' }}</text>
          <text class="mc-time">{{ formatTime(item.createdAt) }}</text>
        </view>
      </view>

      <view v-if="!loading && !list.length" class="empty-state">
        <text class="empty-icon">📬</text><text class="empty-text">暂无消息</text><text class="empty-hint">消息会在这里展示</text>
      </view>
    </view>

    <view v-if="unreadCount > 0" class="read-all-bar">
      <button @click="markAllRead">全部已读 ({{ unreadCount }})</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { notificationApi } from '@/api'

const list = ref([])
const loading = ref(false)
const unreadCount = ref(0)

const formatTime = (d) => d ? d.slice(5,16).replace('T',' ') : ''
const markRead = async (item) => {
  if (item.isRead === 0) {
    try { await notificationApi.markRead(item.id); item.isRead = 1; unreadCount.value = Math.max(0, unreadCount.value - 1) }
    catch {}
  }
}
const markAllRead = async () => {
  try { await notificationApi.markAllRead(); list.value.forEach(i => i.isRead = 1); unreadCount.value = 0 }
  catch {}
}

onMounted(async () => {
  loading.value = true
  try {
    const [lRes, cRes] = await Promise.all([notificationApi.getList({ page: 1, size: 50 }), notificationApi.getUnreadCount()])
    list.value = lRes.data?.records || lRes.data || []
    unreadCount.value = cRes.data || 0
  } catch {} finally { loading.value = false }
})
</script>

<style scoped>
.page { min-height: 100vh; background: var(--color-bg); padding-bottom: 100rpx; }
.list { padding: 24rpx 32rpx; }

.msg-card {
  display: flex; gap: 20rpx; padding: 28rpx;
  background: var(--color-white); border-radius: var(--radius-lg);
  margin-bottom: 18rpx;
  position: relative;
}
.msg-card.unread { border-left: 6rpx solid var(--color-primary); }
.mc-dot {
  width: 12rpx; height: 12rpx; border-radius: var(--radius-full);
  background: var(--color-primary); flex-shrink: 0; margin-top: 8rpx;
}
.mc-body { flex: 1; min-width: 0; }
.mc-title { font-size: 29rpx; font-weight: 600; color: var(--color-text-1); display: block; margin-bottom: 10rpx; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.mc-content { font-size: 26rpx; color: var(--color-text-3); display: block; margin-bottom: 10rpx; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; overflow: hidden; }
.mc-time { font-size: 23rpx; color: var(--color-text-4); }

.read-all-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  padding: 20rpx 32rpx; background: var(--color-white);
  box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.06);
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}
.read-all-bar button {
  width: 100%; height: 76rpx; line-height: 76rpx;
  background: #1677ff; color: #fff;
  font-size: 28rpx; font-weight: 500; border-radius: var(--radius-lg); border: none;
}
.read-all-bar button::after { border: none; }

.empty-state { text-align: center; padding: 120rpx 0; }
.empty-icon { font-size: 80rpx; display: block; margin-bottom: 20rpx; }
.empty-text { font-size: 30rpx; color: var(--color-text-3); display: block; margin-bottom: 8rpx; }
.empty-hint { font-size: 26rpx; color: var(--color-text-4); }
</style>
