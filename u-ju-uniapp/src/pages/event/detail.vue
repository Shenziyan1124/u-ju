<template>
  <view class="page">
    <image :src="event.coverImage || '/static/placeholder.png'" mode="aspectFill" class="cover" />

    <view class="body">
      <view class="title-row">
        <text class="title">{{ event.title }}</text>
        <view class="fav-btn" @tap="toggleFav">
          <text>{{ isFav ? '❤️' : '🤍' }}</text>
        </view>
      </view>

      <view class="tags">
        <text class="tag" v-if="event.isFree === 1">免费</text>
        <text class="tag primary">{{ event.category || '活动' }}</text>
        <text class="tag">{{ event.status === 1 ? '报名中' : '已结束' }}</text>
      </view>

      <view class="info-grid">
        <view class="info-item"><text class="info-icon">📅</text><text>{{ event.startTime?.slice(0,16) }} ~ {{ event.endTime?.slice(10,16) }}</text></view>
        <view class="info-item"><text class="info-icon">📍</text><text>{{ event.location || '线上活动' }}</text></view>
        <view class="info-item"><text class="info-icon">👥</text><text>已报 {{ event.registerCount || 0 }} 人 / 限 {{ event.maxParticipants || '不限' }} 人</text></view>
        <view class="info-item"><text class="info-icon">🏢</text><text>{{ event.organizerName || '主办方' }}</text></view>
      </view>

      <view class="section-block">
        <text class="sb-title">活动详情</text>
        <text class="sb-content">{{ event.description || '暂无详细介绍' }}</text>
      </view>

      <view class="section-block" v-if="tickets.length">
        <text class="sb-title">票种选择</text>
        <view v-for="(t, i) in tickets" :key="i"
              :class="['ticket-card', { active: selectedTicket === i }]"
              @tap="selectedTicket = i">
          <view class="tc-name">{{ t.name }}<text class="tc-price">{{ t.price > 0 ? '¥' + t.price : '免费' }}</text></view>
          <text class="tc-remain">剩余 {{ t.stock - (t.sold || 0) }} 张</text>
        </view>
      </view>
    </view>

    <view class="bottom-bar">
      <view class="price-info" v-if="event.isFree !== 1">
        <text class="price-label">票价</text>
        <text class="price-value">¥{{ currentPrice || 0 }}</text>
      </view>
      <button class="action-btn" :class="{ disabled: event.status !== 1 }" @tap="handleRegister">
        {{ event.status === 1 ? (event.isFree === 1 ? '立即报名' : '立即购票') : '活动已结束' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { eventApi, favoriteApi, registrationApi, orderApi } from '@/api'

const event = ref({})
const tickets = ref([])
const selectedTicket = ref(0)
const isFav = ref(false)

const currentPrice = computed(() => {
  if (!tickets.value.length) return 0
  return tickets.value[selectedTicket.value]?.price || 0
})

onLoad(async ({ id }) => {
  try {
    const res = await eventApi.getDetail(id)
    event.value = res.data
    const tRes = await eventApi.getTickets(id)
    tickets.value = tRes.data?.records || tRes.data || []
  } catch {}
})

const toggleFav = async () => {
  try {
    await favoriteApi.toggle(event.value.id)
    isFav.value = !isFav.value
  } catch (e) { console.error(e) }
}

const handleRegister = async () => {
  if (event.value.status !== 1) return
  uni.showLoading({ title: '处理中...' })
  try {
    await registrationApi.create({ eventId: event.value.id })
    if (event.value.isFree !== 1 && tickets.value.length) {
      const ticketId = tickets.value[selectedTicket.value]?.id
      await orderApi.create(event.value.id, ticketId)
      uni.hideLoading()
      uni.navigateTo({ url: `/pages/order/confirm?eventId=${event.value.id}&ticketId=${ticketId}` })
    } else {
      uni.hideLoading()
      uni.showToast({ title: '报名成功', icon: 'success' })
    }
  } catch { uni.hideLoading() }
}
</script>

<style scoped>
.page { min-height: 100vh; background: var(--color-bg); padding-bottom: 140rpx; }

.cover { width: 100%; height: 440rpx; }
.body { margin-top: -40rpx; position: relative; z-index: 1; border-radius: var(--radius-xl) var(--radius-xl) 0 0; background: var(--color-white); padding: 32rpx; min-height: calc(100vh - 400rpx); }

.title-row { display: flex; justify-content: space-between; align-items: flex-start; gap: 20rpx; }
.title { font-size: 38rpx; font-weight: 700; color: var(--color-text-1); line-height: 1.3; flex: 1; }
.fv-btn { flex-shrink: 0; padding: 12rpx; }
.fav-btn { width: 72rpx; height: 72rpx; display: flex; align-items: center; justify-content: center; background: var(--color-fill); border-radius: var(--radius-full); font-size: 32rpx; }

.tags { display: flex; gap: 14rpx; margin-top: 24rpx; flex-wrap: wrap; }
.tag {
  font-size: 22rpx; padding: 6rpx 18rpx; border-radius: var(--radius-sm);
  background: var(--color-fill); color: var(--color-text-3);
}
.tag.primary { background: var(--color-primary-bg); color: var(--color-primary); }

.info-grid { margin-top: 28rpx; }
.info-item { display: flex; align-items: center; gap: 12rpx; padding: 16rpx 0; border-bottom: 1rpx solid var(--color-border-light); font-size: 27rpx; color: var(--color-text-2); }
.info-icon { font-size: 28rpx; width: 36rpx; text-align: center; }
.info-item:last-child { border-bottom: none; }

.section-block { margin-top: 32rpx; }
.sb-title { font-size: 30rpx; font-weight: 600; color: var(--color-text-1); display: block; margin-bottom: 16rpx; }
.sb-content { font-size: 28rpx; color: var(--color-text-2); line-height: 1.7; }

.ticket-card {
  padding: 24rpx; border: 2rpx solid var(--color-border); border-radius: var(--radius-md);
  margin-bottom: 16rpx; transition: all 0.2s;
}
.ticket-card.active { border-color: var(--color-primary); background: var(--color-primary-bg); }
.tc-name { font-size: 28rpx; font-weight: 600; color: var(--color-text-1); display: flex; justify-content: space-between; }
.tc-price { color: var(--color-primary); font-weight: 700; }
.tc-remain { font-size: 24rpx; color: var(--color-text-4); margin-top: 8rpx; display: block; }

.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  display: flex; align-items: center; justify-content: space-between;
  padding: 20rpx 32rpx; background: var(--color-white);
  box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.06);
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}
.price-info { display: flex; align-items: baseline; gap: 8rpx; }
.price-label { font-size: 26rpx; color: var(--color-text-3); }
.price-value { font-size: 44rpx; font-weight: 800; color: var(--color-danger); }
.action-btn {
  flex: 1; margin-left: 32rpx; height: 88rpx; line-height: 88rpx;
  text-align: center; background: #1677ff;
  color: #fff; font-size: 30rpx; font-weight: 600;
  border-radius: var(--radius-lg); border: none;
}
.action-btn.disabled { opacity: 0.5; }
</style>
