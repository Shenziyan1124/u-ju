<template>
  <view class="page">
    <view class="hero">
      <view class="hero-content">
        <text class="hero-title">发现精彩活动</text>
        <text class="hero-subtitle">探索你感兴趣的活动，结识志同道合的朋友</text>
      </view>
      <view class="search-bar" @tap="goSearch">
        <text class="search-icon">🔍</text>
        <text class="search-placeholder">搜索活动、话题...</text>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">热门推荐</text>
        <text class="section-more" @tap="goList">查看全部</text>
      </view>

      <scroll-view scroll-x class="category-scroll">
        <view class="category-list">
          <view v-for="(cat, i) in categories" :key="i"
                :class="['category-item', { active: currentCat === cat }]"
                @tap="currentCat = cat; loadEvents()">
            {{ cat }}
          </view>
        </view>
      </scroll-view>

      <view v-if="loading && !events.length" class="skeleton">
        <view v-for="n in 3" :key="n" class="skeleton-card">
          <view class="skeleton-img"></view>
          <view class="skeleton-line long"></view>
          <view class="skeleton-line"></view>
          <view class="skeleton-line short"></view>
        </view>
      </view>

      <view v-else-if="!events.length && !loading" class="empty-state">
        <text class="empty-icon">📭</text>
        <text class="empty-text">暂无活动</text>
      </view>

      <view v-else class="event-grid">
        <view v-for="item in events" :key="item.id" class="event-card" @tap="goDetail(item.id)">
          <image :src="item.coverImage || '/static/placeholder.png'" mode="aspectFill" class="event-cover" />
          <view class="event-info">
            <text class="event-title">{{ item.title }}</text>
            <view class="event-meta">
              <text class="meta-tag" v-if="item.isFree === 1">免费</text>
              <text class="meta-tag primary">{{ item.category || '活动' }}</text>
            </view>
            <view class="event-footer">
              <text class="event-time">📅 {{ formatTime(item.startTime) }}</text>
              <text class="event-count">👥 {{ item.registerCount || 0 }}人</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { eventApi } from '@/api'

const categories = ['全部', '音乐', '展览', '讲座', '运动', '社交', '科技']
const currentCat = ref('全部')
const events = ref([])
const loading = ref(false)

const goSearch = () => uni.navigateTo({ url: '/pages/event/search' })
const goList = () => uni.switchTab({ url: '/pages/event/list' })
const goDetail = (id) => uni.navigateTo({ url: `/pages/event/detail?id=${id}` })

const formatTime = (t) => t ? t.slice(5, 16) : ''

const loadEvents = async () => {
  loading.value = true
  try {
    const params = { page: 1, size: 6 }
    if (currentCat.value !== '全部') params.category = currentCat.value
    const res = await eventApi.getList(params)
    events.value = res.data?.records || res.data || []
  } catch {} finally { loading.value = false }
}

onMounted(loadEvents)
</script>

<style scoped>
.page { min-height: 100%; background: var(--color-bg); }

.hero {
  background: #1677ff;
  padding: 60rpx 32rpx 40rpx;
}
.hero-title { display: block; font-size: 44rpx; font-weight: 700; color: #fff; margin-bottom: 8rpx; }
.hero-subtitle { display: block; font-size: 26rpx; color: rgba(255,255,255,0.8); margin-bottom: 28rpx; }
.search-bar {
  display: flex; align-items: center; gap: 12rpx;
  background: rgba(255,255,255,0.2); border-radius: var(--radius-xl);
  padding: 20rpx 24rpx; backdrop-filter: blur(10px);
}
.search-icon { font-size: 30rpx; }
.search-placeholder { font-size: 26rpx; color: rgba(255,255,255,0.7); }

.section { padding: 32rpx; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx; }
.section-title { font-size: 34rpx; font-weight: 700; color: var(--color-text-1); }
.section-more { font-size: 26rpx; color: var(--color-primary); }

.category-scroll { white-space: nowrap; margin-bottom: 28rpx; }
.category-list { display: inline-flex; gap: 16rpx; }
.category-item {
  padding: 12rpx 28rpx; border-radius: var(--radius-xl);
  background: var(--color-white); color: var(--color-text-3);
  font-size: 26rpx; white-space: nowrap;
  transition: all 0.2s;
}
.category-item.active { background: var(--color-primary); color: #fff; }

.event-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20rpx; }
.event-card {
  background: var(--color-white); border-radius: var(--radius-lg);
  overflow: hidden; border: 1rpx solid var(--color-border-light);
}
.event-cover { width: 100%; height: 240rpx; }
.event-info { padding: 20rpx; }
.event-title {
  font-size: 28rpx; font-weight: 600; color: var(--color-text-1);
  display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 2; overflow: hidden;
  line-height: 1.4; margin-bottom: 12rpx;
}
.event-meta { display: flex; gap: 10rpx; margin-bottom: 14rpx; }
.meta-tag {
  font-size: 22rpx; padding: 4rpx 12rpx; border-radius: var(--radius-sm);
  background: var(--color-fill); color: var(--color-text-3);
}
.meta-tag.primary { background: var(--color-primary-bg); color: var(--color-primary); }
.event-footer { display: flex; justify-content: space-between; }
.event-time, .event-count { font-size: 22rpx; color: var(--color-text-4); }

.skeleton-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20rpx; }
.skeleton-card { background: var(--color-white); border-radius: var(--radius-lg); overflow: hidden; padding: 0; }
.skeleton-img { height: 240rpx; background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%); animation: shimmer 1.5s infinite; }
.skeleton-line { height: 24rpx; background: #f0f0f0; border-radius: 6rpx; margin: 12rpx 16rpx 0; }
.skeleton-line.long { width: 80%; }
.skeleton-line.short { width: 50%; }

.empty-state { text-align: center; padding: 80rpx 0; }
.empty-icon { font-size: 64rpx; display: block; margin-bottom: 16rpx; }
.empty-text { font-size: 28rpx; color: var(--color-text-4); }

@keyframes shimmer { 0% { background-position: -200% 0; } 100% { background-position: 200% 0; } }
</style>
