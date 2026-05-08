<template>
  <view class="page">
    <scroll-view scroll-x class="tab-scroll">
      <view class="tab-list">
        <view v-for="(t, i) in tabs" :key="i" :class="['tab-item', { active: status === t.value }]"
              @tap="status = t.value; load()">{{ t.label }}</view>
      </view>
    </scroll-view>

    <view class="list" v-if="list.length || loading">
      <view v-for="item in list" :key="item.id" class="card" @tap="goDetail(item.id)">
        <image :src="item.eventCover || '/static/placeholder.png'" mode="aspectFill" class="cover" />
        <view class="body">
          <text class="title">{{ item.eventTitle || '活动报名' }}</text>
          <view class="meta">
            <text class="time">📅 {{ item.createdAt?.slice(5,16) }}</text>
          </view>
          <view class="footer">
            <text :class="['st', 's' + item.status]">{{ statusText[item.status] || '未知' }}</text>
            <button v-if="item.status === 0" class="cancel-btn" @tap.stop="cancel(item)">取消</button>
          </view>
        </view>
      </view>

      <view v-if="!list.length && !loading" class="empty-state">
        <text class="empty-icon">📋</text><text class="empty-text">暂无报名记录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { registrationApi } from '@/api'

const tabs = [{ label: '全部', value: '' }, { label: '待审核', value: 0 }, { label: '已通过', value: 1 }, { label: '已拒绝', value: 2 }]
const statusText = ['待审核', '已通过', '已拒绝', '已取消']
const list = ref([])
const loading = ref(false)
const status = ref('')

const goDetail = (id) => uni.navigateTo({ url: `/pages/registration/detail?id=${id}` })
const cancel = async (item) => {
  uni.showModal({ title: '确认取消？', success: async (res) => {
    if (res.confirm) { try { await registrationApi.cancel(item.id); load() } catch {} }
  }})
}

const load = async () => {
  loading.value = true
  try {
    const params = { page: 1, size: 20 }
    if (status.value !== '') params.status = status.value
    const res = await registrationApi.getMyList(params)
    list.value = res.data?.records || res.data || []
  } catch {} finally { loading.value = false }
}
load()
</script>

<style scoped>
.page { min-height: 100%; background: var(--color-bg); }

.tab-scroll { white-space: nowrap; background: var(--color-white); padding: 20rpx 32rpx; border-bottom: 1rpx solid var(--color-border-light); }
.tab-list { display: inline-flex; gap: 8rpx; }
.tab-item { padding: 16rpx 28rpx; font-size: 27rpx; color: var(--color-text-3); position: relative; white-space: nowrap; border-radius: var(--radius-xl); }
.tab-item.active { background: var(--color-primary-bg); color: var(--color-primary); font-weight: 600; }

.list { padding: 24rpx 32rpx; }
.card {
  display: flex; gap: 22rpx; background: var(--color-white);
  border-radius: var(--radius-lg); padding: 22rpx; margin-bottom: 20rpx;
  border: 1rpx solid var(--color-border-light);
}
.cover { width: 180rpx; height: 140rpx; border-radius: var(--radius-md); flex-shrink: 0; }
.body { flex: 1; display: flex; flex-direction: column; justify-content: space-between; }
.title { font-size: 29rpx; font-weight: 600; color: var(--color-text-1); display: -webkit-box; -webkit-line-clamp: 1; overflow: hidden; }
.meta { margin-top: auto; }
.time { font-size: 24rpx; color: var(--color-text-4); }
.footer { display: flex; justify-content: space-between; align-items: center; margin-top: 14rpx; }
.st { font-size: 24rpx; font-weight: 500; }
.st.s0 { color: var(--color-warning); }
.st.s1 { color: var(--color-success); }
.st.s2 { color: var(--color-danger); }
.cancel-btn { padding: 8rpx 24rpx; font-size: 24rpx; background: transparent; color: var(--color-danger); border: 2rpx solid var(--color-border); border-radius: var(--radius-sm); line-height: 1.4; }
.cancel-btn::after { border: none; }

.empty-state { text-align: center; padding: 100rpx 0; }
.empty-icon { font-size: 72rpx; display: block; margin-bottom: 16rpx; }
.empty-text { font-size: 28rpx; color: var(--color-text-4); }
</style>
