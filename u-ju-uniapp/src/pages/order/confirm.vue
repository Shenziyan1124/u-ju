<template>
  <view class="page">
    <view class="section-title">确认订单</view>

    <view v-if="eventInfo" class="event-card">
      <image :src="eventInfo.coverImage || '/static/placeholder.png'" mode="aspectFill" class="ec-cover" />
      <view class="ec-body">
        <text class="ec-title">{{ eventInfo.title }}</text>
        <view class="ec-meta">
          <text class="ec-time">📅 {{ eventInfo.startTime?.slice(5,16) }}</text>
          <text class="ec-addr">📍 {{ eventInfo.address || '线上活动' }}</text>
        </view>
      </view>
    </view>

    <view class="form-section">
      <view class="fs-title">报名信息</view>
      <view class="input-item">
        <text class="ii-label">姓名</text>
        <input v-model="form.realName" placeholder="请输入真实姓名" />
      </view>
      <view class="input-item">
        <text class="ii-label">手机号</text>
        <input v-model="form.phone" type="number" placeholder="请输入手机号" maxlength="11" />
      </view>
      <view class="input-item">
        <text class="ii-label">邮箱</text>
        <input v-model="form.email" placeholder="选填" />
      </view>
    </view>

    <view v-if="tickets.length" class="ticket-section">
      <view class="fs-title">选择票型</view>
      <view v-for="(t, i) in tickets" :key="i" :class="['ticket-item', { active: selectedTicket === i }]" @tap="selectedTicket = i">
        <view class="ti-info">
          <text class="ti-name">{{ t.name }}</text>
          <text class="ti-desc">{{ t.description || '标准票' }}</text>
        </view>
        <view class="ti-right">
          <text class="ti-price">¥{{ t.price }}</text>
          <view :class="['ti-check', { on: selectedTicket === i }]">✓</view>
        </view>
      </view>
    </view>

    <view class="bottom-bar">
      <view class="bb-left">
        <text class="bb-label">合计：</text>
        <text class="bb-amount">¥{{ totalAmount }}</text>
      </view>
      <button class="bb-btn" @click="submitOrder">提交订单</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { registrationApi, orderApi } from '@/api'

const props = defineProps<{ eventId?: string }>()
const eventInfo = ref<any>(null)
const tickets = ref<any[]>([])
const selectedTicket = ref(0)
const form = reactive({ realName: '', phone: '', email: '' })

const totalAmount = computed(() => {
  if (!tickets.value.length) return 0
  return tickets.value[selectedTicket.value]?.price || 0
})

const loadEvent = async () => {
  if (!props.eventId) return
  try {
    const res = await import('@/api').then(m => m.eventApi)
    eventInfo.value = await res.getDetail(Number(props.eventId))
    if (eventInfo.value?.tickets) {
      tickets.value = JSON.parse(eventInfo.value.tickets)
    }
  } catch {}
}

const submitOrder = async () => {
  if (!form.realName || !form.phone) {
    uni.showToast({ title: '请填写姓名和手机号', icon: 'none' }); return
  }
  try {
    const reg = await registrationApi.create({
      eventId: Number(props.eventId),
      ticketId: tickets.value[selectedTicket.value]?.id,
      formData: JSON.stringify(form),
    })
    uni.navigateTo({ url: `/pages/order/pay?registrationId=${reg.id}` })
  } catch {}
}

loadEvent()
</script>

<style scoped>
.page { min-height: 100%; background: var(--color-bg); padding-bottom: 140rpx; }
.section-title { padding: 28rpx 32rpx; font-size: 30rpx; font-weight: 700; color: var(--color-text-1); }

.event-card { display: flex; gap: 22rpx; background: var(--color-white); margin: 0 32rpx 24rpx; border-radius: var(--radius-lg); padding: 24rpx; border: 1rpx solid var(--color-border-light); }
.ec-cover { width: 180rpx; height: 140rpx; border-radius: var(--radius-md); flex-shrink: 0; }
.ec-body { flex: 1; display: flex; flex-direction: column; justify-content: space-between; }
.ec-title { font-size: 29rpx; font-weight: 600; color: var(--color-text-1); display: -webkit-box; -webkit-line-clamp: 2; overflow: hidden; }
.ec-meta { display: flex; flex-direction: column; gap: 6rpx; }
.ec-time, .ec-addr { font-size: 24rpx; color: var(--color-text-4); }

.form-section, .ticket-section { background: var(--color-white); margin: 0 32rpx 24rpx; border-radius: var(--radius-lg); padding: 28rpx; border: 1rpx solid var(--color-border-light); }
.fs-title { font-size: 28rpx; font-weight: 600; color: var(--color-text-1); margin-bottom: 24rpx; padding-bottom: 18rpx; border-bottom: 1rpx solid var(--color-border-light); }

.input-item { display: flex; align-items: center; gap: 16rpx; padding: 22rpx 0; border-bottom: 1rpx solid var(--color-border-light); }
.input-item:last-child { border-bottom: none; }
.ii-label { font-size: 27rpx; color: var(--color-text-2); width: 100rpx; flex-shrink: 0; }
.input-item input { flex: 1; font-size: 28rpx; color: var(--color-text-1); }

.ticket-item { display: flex; align-items: center; justify-content: space-between; padding: 24rpx; margin-bottom: 16rpx; border-radius: var(--radius-md); border: 2rpx solid var(--color-border); transition: all 0.2s; }
.ticket-item.active { border-color: var(--color-primary); background: var(--color-primary-bg); }
.ti-info { display: flex; flex-direction: column; gap: 8rpx; }
.ti-name { font-size: 29rpx; font-weight: 600; color: var(--color-text-1); }
.ti-desc { font-size: 24rpx; color: var(--color-text-4); }
.ti-right { display: flex; align-items: center; gap: 16rpx; }
.ti-price { font-size: 34rpx; font-weight: 800; color: var(--color-danger); }
.ti-check { width: 44rpx; height: 44rpx; border-radius: 50%; border: 2rpx solid var(--color-border); display: flex; align-items: center; justify-content: center; font-size: 24rpx; color: #fff; }
.ti-check.on { background: var(--color-primary); border-color: var(--color-primary); }

.bottom-bar {
  position: fixed; left: 0; right: 0; bottom: 0;
  display: flex; align-items: center; justify-content: space-between;
  padding: 16rpx 32rpx; padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  background: var(--color-white); box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.06); z-index: 100;
}
.bb-left { display: flex; align-items: baseline; }
.bb-label { font-size: 27rpx; color: var(--color-text-2); }
.bb-amount { font-size: 40rpx; font-weight: 800; color: var(--color-danger); }
.bb-btn {
  min-width: 240rpx; height: 80rpx; line-height: 80rpx;
  background: #1677ff;
  color: #fff; font-size: 29rpx; font-weight: 600;
  border-radius: var(--radius-lg); border: none; padding: 0 40rpx;
}
.bb-btn::after { border: none; }
</style>
