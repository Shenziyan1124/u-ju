<template>
  <view class="container">
    <view v-for="item in list" :key="item.id" class="event-card" @click="goRegistrations(item.id)">
      <text class="event-title">{{ item.title }}</text>
      <view class="event-meta">
        <text :class="['status', item.status === 1 ? 'active' : '']">{{ ['草稿', '已发布', '已下架', '已取消'][item.status] }}</text>
        <text class="views">{{ item.viewCount }} 浏览</text>
      </view>
    </view>
    <button class="btn-add" @click="goPublish">+ 发布活动</button>
    <view class="empty" v-if="list.length === 0">暂无活动</view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { eventApi } from '@/api'

const list = ref<any[]>([])
const loadData = async () => {
  try { const res: any = await eventApi.getMyEvents({ page: 1, size: 50 }); list.value = res.records || [] } catch {}
}
const goRegistrations = (id: number) => { uni.navigateTo({ url: `/pages/organizer/registrations?eventId=${id}` }) }
const goPublish = () => { uni.navigateTo({ url: '/pages/organizer/publish' }) }
onMounted(loadData)
</script>

<style scoped>
.container { padding: 20rpx; }
.event-card { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; }
.event-title { font-size: 30rpx; font-weight: bold; display: block; margin-bottom: 12rpx; }
.event-meta { display: flex; justify-content: space-between; }
.status { font-size: 24rpx; color: #909399; }
.status.active { color: #67c23a; }
.views { font-size: 24rpx; color: #999; }
.btn-add { position: fixed; right: 40rpx; bottom: 120rpx; width: 120rpx; height: 120rpx; border-radius: 60rpx; background: #409eff; color: #fff; font-size: 48rpx; line-height: 120rpx; text-align: center; border: none; }
.empty { text-align: center; padding: 100rpx 0; color: #999; }
</style>
