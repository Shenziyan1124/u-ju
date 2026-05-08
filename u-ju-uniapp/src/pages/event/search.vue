<template>
  <view class="page">
    <view class="search-header">
      <view class="search-input">
        <text class="si-icon">🔍</text>
        <input v-model="keyword" placeholder="搜索活动、话题..." confirm-type="search" @confirm="doSearch" focus />
      </view>
      <text class="cancel-btn" @tap="goBack">取消</text>
    </view>

    <view class="history" v-if="!keyword && historyList.length">
      <text class="section-label">搜索历史</text>
      <view class="tag-list">
        <text v-for="(h, i) in historyList" :key="i" class="hist-tag" @tap="keyword = h; doSearch()">{{ h }}</text>
      </view>
    </view>

    <view v-if="searched">
      <text class="result-count">找到 {{ results.length }} 个相关活动</text>
    </view>

    <scroll-view scroll-y class="result-list" v-if="results.length || loading">
      <view v-if="loading && !results.length" v-for="n in 4" :key="'sk'+n" class="sk-card"><view class="sk-img"></view><view class="sk-body"><view class="sk-line long"></view><view class="sk-line"></view></view></view>

      <view v-for="item in results" :key="item.id" class="event-item" @tap="goDetail(item.id)">
        <image :src="item.coverImage || '/static/placeholder.png'" mode="aspectFill" class="cover" />
        <view class="body">
          <text class="title">{{ item.title }}</text>
          <view class="meta-row">
            <text :class="['tag', { free: item.isFree === 1 }]">{{ item.isFree === 1 ? '免费' : '付费' }}</text>
            <text class="cat">{{ item.category }}</text>
          </view>
          <text class="time">📅 {{ item.startTime?.slice(5,16) }}</text>
        </view>
      </view>
    </scroll-view>

    <view v-else-if="searched && !results.length && !loading" class="empty-state">
      <text class="empty-icon">🔎</text>
      <text class="empty-text">未找到相关活动</text>
      <text class="empty-hint">换个关键词试试吧</text>
    </view>

    <view v-if="!searched && !loading" class="hot-section">
      <text class="section-label">热门搜索</text>
      <view class="hot-list">
        <text v-for="(h, i) in hotKeywords" :key="i" class="hot-tag" @tap="keyword = h; doSearch()">{{ h }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { eventApi } from '@/api'

const keyword = ref('')
const results = ref([])
const searched = ref(false)
const loading = ref(false)
const historyList = ref(uni.getStorageSync('search_history')?.split(',').filter(Boolean) || [])
const hotKeywords = ['音乐节', '技术分享', '展览', '户外运动', '读书会', '创业路演']

const goBack = () => uni.navigateBack()
const goDetail = (id) => uni.navigateTo({ url: `/pages/event/detail?id=${id}` })

const doSearch = async () => {
  if (!keyword.value.trim()) return
  loading.value = true; searched.value = true
  try {
    const res = await eventApi.getList({ keyword: keyword.value, page: 1, size: 20 })
    results.value = res.data?.records || res.data || []
    const hist = [keyword.value, ...historyList.value].slice(0, 10)
    uni.setStorageSync('search_history', hist.join(','))
    historyList.value = hist
  } catch {} finally { loading.value = false }
}
</script>

<style scoped>
.page { min-height: 100vh; background: var(--color-bg); }

.search-header { display: flex; align-items: center; gap: 20rpx; padding: 24rpx 32rpx; background: var(--color-white); }
.search-input { flex: 1; display: flex; align-items: center; gap: 12rpx; padding: 18rpx 24rpx; background: var(--color-fill); border-radius: var(--radius-xl); }
.si-icon { font-size: 28rpx; }
.search-input input { flex: 1; font-size: 28rpx; color: var(--color-text-1); }
.cancel-btn { font-size: 28rpx; color: var(--color-primary); white-space: nowrap; }

.history, .hot-section { padding: 24rpx 32rpx; }
.section-label { font-size: 26rpx; color: var(--color-text-4); display: block; margin-bottom: 16rpx; }
.tag-list, .hot-list { display: flex; flex-wrap: wrap; gap: 14rpx; }
.hist-tag, .hot-tag { padding: 12rpx 26rpx; background: var(--color-white); color: var(--color-text-3); font-size: 26rpx; border-radius: var(--radius-xl); border: 1rpx solid var(--color-border-light); }
.hot-tag { background: var(--color-primary-bg); color: var(--color-primary); }

.result-count { display: block; padding: 20rpx 32rpx; font-size: 26rpx; color: var(--color-text-4); }
.result-list { height: calc(100vh - 200rpx); padding: 0 32rpx; }

.event-item { display: flex; gap: 22rpx; background: var(--color-white); border-radius: var(--radius-lg); margin-bottom: 20rpx; overflow: hidden; border: 1rpx solid var(--color-border-light); }
.cover { width: 200rpx; height: 160rpx; border-radius: var(--radius-md); flex-shrink: 0; }
.body { flex: 1; padding: 18rpx 18rpx 18rpx 0; display: flex; flex-direction: column; justify-content: space-between; }
.title { font-size: 29rpx; font-weight: 600; color: var(--color-text-1); display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 2; overflow: hidden; line-height: 1.4; }
.meta-row { display: flex; gap: 10rpx; margin-top: 10rpx; }
.tag { font-size: 22rpx; padding: 4rpx 14rpx; border-radius: var(--radius-sm); background: var(--color-warning-bg); color: var(--color-warning); }
.tag.free { background: var(--color-success-bg); color: var(--color-success); }
.cat { font-size: 22rpx; color: var(--color-text-4); padding: 4rpx 0; }
.time { font-size: 23rpx; color: var(--color-text-4); }

.sk-card { display: flex; gap: 18rpx; background: var(--color-white); border-radius: var(--radius-lg); padding: 18rpx; margin-bottom: 18rpx; }
.sk-img { width: 180rpx; height: 140rpx; border-radius: var(--radius-md); background: linear-gradient(90deg,#f0f0f0 25%,#e8e8e8 50%,#f0f0f0 75%); animation: shimmer 1.5s infinite; flex-shrink: 0; }
.sk-body { flex: 1; }
.sk-line { height: 24rpx; background: #f0f0f0; border-radius: 6rpx; margin-bottom: 12rpx; }
.sk-line.long { width: 80%; }

.empty-state { text-align: center; padding: 120rpx 0; }
.empty-icon { font-size: 80rpx; display: block; margin-bottom: 20rpx; }
.empty-text { font-size: 30rpx; color: var(--color-text-3); display: block; margin-bottom: 8rpx; }
.empty-hint { font-size: 26rpx; color: var(--color-text-4); }

@keyframes shimmer { 0% { background-position: -200% 0; } 100% { background-position: 200% 0; } }
</style>
