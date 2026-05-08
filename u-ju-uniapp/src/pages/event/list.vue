<template>
  <view class="page">
    <view class="search-header">
      <view class="search-input" @tap="goSearch">
        <text class="si-icon">🔍</text>
        <text class="si-placeholder">搜索活动...</text>
      </view>
      <view class="filter-btn" @tap="showFilter = !showFilter">
        <text>筛选</text>
      </view>
    </view>

    <scroll-view scroll-x class="tab-scroll" v-if="!showFilter">
      <view class="tab-list">
        <view v-for="(t, i) in tabs" :key="i" :class="['tab-item', { active: sort === t.value }]" @tap="sort = t.value; load()">
          {{ t.label }}
        </view>
      </view>
    </scroll-view>

    <view v-if="showFilter" class="filter-bar">
      <scroll-view scroll-x>
        <view class="filter-list">
          <view v-for="(c, i) in categories" :key="i"
                :class="['filter-chip', { active: category === c }]"
                @tap="category = c; showFilter = false; load()">{{ c }}</view>
        </view>
      </scroll-view>
    </view>

    <view class="event-list" v-if="events.length || loading">
      <view v-if="loading && !events.length" class="skeleton-list">
        <view v-for="n in 4" :key="n" class="sk-item"><view class="sk-img"></view><view class="sk-body"><view class="sk-line long"></view><view class="sk-line"></view></view></view>
      </view>

      <view v-else v-for="item in events" :key="item.id" class="event-item" @tap="goDetail(item.id)">
        <image :src="item.coverImage || '/static/placeholder.png'" mode="aspectFill" class="ei-cover" />
        <view class="ei-body">
          <text class="ei-title">{{ item.title }}</text>
          <view class="ei-row">
            <text class="ei-tag" :class="{ free: item.isFree === 1 }">{{ item.isFree === 1 ? '免费' : '付费' }}</text>
            <text class="ei-cat">{{ item.category }}</text>
          </view>
          <view class="ei-meta">
            <text>📅 {{ item.startTime?.slice(5,16) }}</text>
            <text>📍 {{ item.location || '线上' }}</text>
          </view>
        </view>
      </view>
    </view>

    <view v-else class="empty-state">
      <text class="empty-icon">🎪</text>
      <text class="empty-text">暂无活动</text>
      <text class="empty-hint">换个关键词试试</text>
    </view>

    <view class="load-more" v-if="hasMore && events.length" @tap="loadMore">
      <text>{{ loading ? '加载中...' : '加载更多' }}</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { eventApi } from '@/api'

const tabs = [{ label: '最新', value: 'latest' }, { label: '最热', value: 'hot' }, { label: '免费', value: 'free' }]
const categories = ['全部', '音乐', '展览', '讲座', '运动', '社交']
const events = ref([])
const loading = ref(false)
const page = ref(1)
const hasMore = ref(true)
const sort = ref('latest')
const category = ref('全部')
const showFilter = ref(false)

const goSearch = () => uni.navigateTo({ url: '/pages/event/search' })
const goDetail = (id) => uni.navigateTo({ url: `/pages/event/detail?id=${id}` })

const load = async (reset = true) => {
  if (reset) { page.value = 1; hasMore.value = true }
  loading.value = true
  try {
    const params = { page: page.value, size: 10, sort: sort.value }
    if (category.value !== '全部') params.category = category.value
    const res = await eventApi.getList(params)
    const list = res.data?.records || res.data || []
    events.value = reset ? list : [...events.value, ...list]
    hasMore.value = list.length >= 10
  } catch {} finally { loading.value = false }
}

const loadMore = () => { if (!loading.value && hasMore.value) { page.value++; load(false) } }

onMounted(() => load())
onShow(() => {})
</script>

<style scoped>
.page { min-height: 100vh; background: var(--color-bg); }

.search-header {
  display: flex; align-items: center; gap: 20rpx;
  padding: 24rpx 32rpx; background: var(--color-white);
}
.search-input {
  flex: 1; display: flex; align-items: center; gap: 12rpx;
  padding: 18rpx 24rpx; background: var(--color-fill); border-radius: var(--radius-xl);
}
.si-icon { font-size: 28rpx; }
.si-placeholder { font-size: 26rpx; color: var(--color-text-4); }
.filter-btn {
  padding: 14rpx 24rpx; border: 2rpx solid var(--color-border);
  border-radius: var(--radius-md); font-size: 26rpx; color: var(--color-text-3);
}

.tab-scroll { white-space: nowrap; background: var(--color-white); padding: 0 32rpx; border-bottom: 1rpx solid var(--color-border-light); }
.tab-list { display: inline-flex; gap: 8rpx; }
.tab-item {
  padding: 20rpx 24rpx; font-size: 28rpx; color: var(--color-text-3);
  position: relative; white-space: nowrap;
}
.tab-item.active { color: var(--color-primary); font-weight: 600; }
.tab-item.active::after {
  content: ''; position: absolute; bottom: 0; left: 50%; transform: translateX(-50%);
  width: 32rpx; height: 4rpx; background: var(--color-primary); border-radius: 2rpx;
}

.filter-bar { padding: 20rpx 32rpx; background: var(--color-white); }
.filter-list { display: inline-flex; gap: 16rpx; white-space: nowrap; }
.filter-chip {
  padding: 12rpx 28rpx; border-radius: var(--radius-xl); font-size: 26rpx;
  background: var(--color-fill); color: var(--color-text-3);
}
.filter-chip.active { background: var(--color-primary-bg); color: var(--color-primary); }

.event-list { padding: 24rpx 32rpx; }
.event-item {
  display: flex; gap: 24rpx; background: var(--color-white);
  border-radius: var(--radius-lg); margin-bottom: 20rpx;
  overflow: hidden; border: 1rpx solid var(--color-border-light);
}
.ei-cover { width: 220rpx; height: 180rpx; border-radius: var(--radius-lg) 0 0 var(--radius-lg); flex-shrink: 0; }
.ei-body { flex: 1; padding: 20rpx 20rpx 20rpx 0; display: flex; flex-direction: column; justify-content: space-between; }
.ei-title {
  font-size: 30rpx; font-weight: 600; color: var(--color-text-1);
  display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 2; overflow: hidden;
}
.ei-row { display: flex; gap: 12rpx; margin-top: 12rpx; }
.ei-tag {
  font-size: 22rpx; padding: 4rpx 14rpx; border-radius: var(--radius-sm);
  background: var(--color-warning-bg); color: var(--color-warning);
}
.ei-tag.free { background: var(--color-success-bg); color: var(--color-success); }
.ei-cat { font-size: 22rpx; color: var(--color-text-4); padding: 4rpx 0; }
.ei-meta { display: flex; justify-content: space-between; font-size: 22rpx; color: var(--color-text-4); margin-top: auto; }

.skeleton-list .sk-item { display: flex; gap: 20rpx; background: var(--color-white); border-radius: var(--radius-lg); margin-bottom: 20rpx; padding: 20rpx; }
.sk-img { width: 200rpx; height: 160rpx; border-radius: var(--radius-md); background: linear-gradient(90deg,#f0f0f0 25%,#e8e8e8 50%,#f0f0f0 75%); animation: shimmer 1.5s infinite; flex-shrink: 0; }
.sk-body { flex: 1; }
.sk-line { height: 24rpx; background: #f0f0f0; border-radius: 6rpx; margin-bottom: 14rpx; }
.sk-line.long { width: 85%; }

.empty-state { text-align: center; padding: 120rpx 0; }
.empty-icon { font-size: 80rpx; display: block; margin-bottom: 20rpx; }
.empty-text { font-size: 30rpx; color: var(--color-text-3); display: block; margin-bottom: 8rpx; }
.empty-hint { font-size: 26rpx; color: var(--color-text-4); }

.load-more { text-align: center; padding: 30rpx; font-size: 26rpx; color: var(--color-primary); }

@keyframes shimmer { 0% { background-position: -200% 0; } 100% { background-position: 200% 0; } }
</style>
