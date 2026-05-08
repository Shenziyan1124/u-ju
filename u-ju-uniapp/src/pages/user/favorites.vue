<template>
  <view class="page">
    <view v-if="list.length || loading" class="list">
      <view v-if="loading && !list.length" v-for="n in 4" :key="'sk'+n" class="sk-card"><view class="sk-img"></view><view class="sk-body"><view class="sk-line long"></view><view class="sk-line"></view></view></view>

      <view v-for="item in list" :key="item.id" class="card" @tap="goDetail(item.id)">
        <image :src="item.coverImage || '/static/placeholder.png'" mode="aspectFill" class="cover" />
        <view class="body">
          <text class="title">{{ item.title }}</text>
          <view class="meta">
            <text :class="['tag', { free: item.isFree === 1 }]">{{ item.isFree === 1 ? '免费' : '付费' }}</text>
            <text class="cat">{{ item.category }}</text>
          </view>
          <text class="time">📅 {{ item.startTime?.slice(5,16) }}</text>
        </view>
      </view>
    </view>
    <view v-else class="empty-state">
      <text class="empty-icon">❤️</text>
      <text class="empty-text">还没有收藏</text>
      <text class="empty-hint">去发现感兴趣的活动吧</text>
      <button class="explore-btn" @tap="goDiscover">去发现</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { favoriteApi } from '@/api'

const list = ref([])
const loading = ref(false)
const goDetail = (id) => uni.navigateTo({ url: `/pages/event/detail?id=${id}` })
const goDiscover = () => uni.switchTab({ url: '/pages/index/index' })

onMounted(async () => {
  loading.value = true
  try { const res = await favoriteApi.getList({ page: 1, size: 50 }); list.value = res.data?.records || res.data || [] }
  catch {} finally { loading.value = false }
})
</script>

<style scoped>
.page { min-height: 100%; background: var(--color-bg); }
.list { padding: 24rpx 32rpx; }

.card {
  display: flex; gap: 22rpx; background: var(--color-white);
  border-radius: var(--radius-lg); padding: 22rpx; margin-bottom: 20rpx;
  overflow: hidden; border: 1rpx solid var(--color-border-light);
}
.cover { width: 200rpx; height: 150rpx; border-radius: var(--radius-md); flex-shrink: 0; }
.body { flex: 1; display: flex; flex-direction: column; justify-content: space-between; padding-top: 4rpx; }
.title { font-size: 29rpx; font-weight: 600; color: var(--color-text-1); display: -webkit-box; -webkit-line-clamp: 2; overflow: hidden; line-height: 1.4; }
.meta { display: flex; gap: 10rpx; margin-top: 10rpx; }
.tag { font-size: 22rpx; padding: 4rpx 14rpx; border-radius: var(--radius-sm); background: var(--color-warning-bg); color: var(--color-warning); }
.tag.free { background: var(--color-success-bg); color: var(--color-success); }
.cat { font-size: 22rpx; color: var(--color-text-4); padding: 4rpx 0; }
.time { font-size: 23rpx; color: var(--color-text-4); margin-top: auto; }

.sk-card { display: flex; gap: 18rpx; background: var(--color-white); border-radius: var(--radius-lg); padding: 22rpx; margin-bottom: 20rpx; }
.sk-img { width: 180rpx; height: 140rpx; border-radius: var(--radius-md); background: linear-gradient(90deg,#f0f0f0 25%,#e8e8e8 50%,#f0f0f0 75%); animation: shimmer 1.5s infinite; flex-shrink: 0; }
.sk-body { flex: 1; }
.sk-line { height: 24rpx; background: #f0f0f0; border-radius: 6rpx; margin-bottom: 12rpx; }
.sk-line.long { width: 75%; }

.empty-state { text-align: center; padding: 120rpx 32rpx; }
.empty-icon { font-size: 80rpx; display: block; margin-bottom: 20rpx; }
.empty-text { font-size: 30rpx; color: var(--color-text-3); display: block; margin-bottom: 8rpx; }
.empty-hint { font-size: 26rpx; color: var(--color-text-4); display: block; margin-bottom: 36rpx; }
.explore-btn {
  display: inline-block; padding: 18rpx 48rpx; background: #1677ff;
  color: #fff; font-size: 28rpx; font-weight: 500; border-radius: var(--radius-xl);
}
@keyframes shimmer { 0% { background-position: -200% 0; } 100% { background-position: 200% 0; } }
</style>
