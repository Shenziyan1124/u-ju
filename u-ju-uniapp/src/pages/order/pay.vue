<template>
  <view class="page">
    <view class="pay-header">
      <text class="ph-title">订单支付</text>
      <text class="ph-amount">¥{{ order?.amount || '0.00' }}</text>
      <text class="ph-desc">请选择支付方式完成付款</text>
    </view>

    <view class="method-section">
      <view class="ms-title">支付方式</view>
      <view :class="['method-item', { active: payMethod === 0 }]" @tap="payMethod = 0">
        <view class="mi-icon wechat">微</view>
        <view class="mi-info">
          <text class="mi-name">微信支付</text>
          <text class="mi-hint">推荐使用</text>
        </view>
        <view :class="['mi-radio', { on: payMethod === 0 }]"></view>
      </view>
      <view :class="['method-item', { active: payMethod === 1 }]" @tap="payMethod = 1">
        <view class="mi-icon alipay">支</view>
        <view class="mi-info">
          <text class="mi-name">支付宝</text>
          <text class="mi-hint">安全快捷</text>
        </view>
        <view :class="['mi-radio', { on: payMethod === 1 }]"></view>
      </view>
    </view>

    <view v-if="order" class="order-summary">
      <view class="os-row"><text class="os-label">订单号</text><text class="os-val">{{ order.orderNo }}</text></view>
      <view class="os-row"><text class="os-label">商品</text><text class="os-val">{{ order.eventName || '活动报名' }}</text></view>
      <view class="os-row"><text class="os-label">超时时间</text><text class="os-val warn">{{ expireText }}</text></view>
    </view>

    <button class="pay-btn" :loading="paying" @click="handlePay">{{ paying ? '支付中...' : '确认支付 ¥' + (order?.amount || '0.00') }}</button>

    <view class="safe-hint">
      <text>🔒 安全提示：支付环境已加密保护</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onUnmounted } from 'vue'
import { orderApi } from '@/api'
import { onShow } from '@dcloudio/uni-app'

const props = defineProps<{ registrationId?: string }>()
const order = ref<any>(null)
const payMethod = ref(0)
const paying = ref(false)
let timer: any = null
const createdAt = ref<Date>(new Date())

const expireText = computed(() => {
  const elapsed = Math.floor((Date.now() - createdAt.value.getTime()) / 1000)
  const remain = Math.max(0, 900 - elapsed)
  const m = Math.floor(remain / 60)
  const s = remain % 60
  return `${m.toString().padStart(2,'0')}:${s.toString().padStart(2,'0')}`
})

const createOrder = async () => {
  if (!props.registrationId) return
  try {
    order.value = await orderApi.create(Number(props.registrationId))
    createdAt.value = new Date()
  } catch {}
}

const handlePay = async () => {
  if (!order.value || paying.value) return
  paying.value = true
  try {
    await orderApi.pay(order.value.id, payMethod.value)
    clearInterval(timer)
    uni.redirectTo({ url: `/pages/order/result?orderNo=${order.value.orderNo}&status=success` })
  } catch {
    paying.value = false
  }
}

onShow(() => {
  createOrder()
  timer = setInterval(() => {}, 1000)
})
onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.page { min-height: 100vh; background: var(--color-bg); }

.pay-header { text-align: center; padding: 60rpx 48rpx 40rpx; background: #1677ff; }
.ph-title { display: block; font-size: 28rpx; color: rgba(255,255,255,0.8); margin-bottom: 12rpx; }
.ph-amount { display: block; font-size: 64rpx; font-weight: 800; color: #fff; letter-spacing: -2rpx; }
.ph-desc { display: block; font-size: 26rpx; color: rgba(255,255,255,0.7); margin-top: 8rpx; }

.method-section { margin: -30rpx 32rpx 24rpx; background: var(--color-white); border-radius: var(--radius-lg); padding: 28rpx; border: 1rpx solid var(--color-border-light); position: relative; z-index: 1; }
.ms-title { font-size: 28rpx; font-weight: 600; color: var(--color-text-1); margin-bottom: 24rpx; }

.method-item { display: flex; align-items: center; gap: 20rpx; padding: 24rpx; border-radius: var(--radius-md); border: 2rpx solid var(--color-border); margin-bottom: 16rpx; transition: all 0.2s; }
.method-item:last-child { margin-bottom: 0; }
.method-item.active { border-color: var(--color-primary); background: var(--color-primary-bg); }

.mi-icon { width: 64rpx; height: 64rpx; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; font-size: 26rpx; font-weight: 700; color: #fff; flex-shrink: 0; }
.mi-icon.wechat { background: #07c160; }
.mi-icon.alipay { background: #1677ff; }
.mi-info { flex: 1; display: flex; flex-direction: column; gap: 4rpx; }
.mi-name { font-size: 29rpx; font-weight: 600; color: var(--color-text-1); }
.mi-hint { font-size: 24rpx; color: var(--color-text-4); }
.mi-radio { width: 40rpx; height: 40rpx; border-radius: 50%; border: 3rpx solid var(--color-border); flex-shrink: 0; transition: all 0.2s; }
.mi-radio.on { border-color: var(--color-primary); background: var(--color-primary); box-shadow: inset 0 0 0 6rpx #fff; }

.order-summary { margin: 0 32rpx 32rpx; background: var(--color-white); border-radius: var(--radius-lg); padding: 24rpx 28rpx; }
.os-row { display: flex; justify-content: space-between; padding: 14rpx 0; }
.os-label { font-size: 26rpx; color: var(--color-text-3); }
.os-val { font-size: 26rpx; color: var(--color-text-1); }
.os-val.warn { color: var(--color-warning); font-weight: 500; }

.pay-btn {
  margin: 0 32rpx; height: 96rpx; line-height: 96rpx;
  background: #1677ff;
  color: #fff; font-size: 33rpx; font-weight: 600;
  border-radius: var(--radius-lg); border: none;
}
.pay-btn::after { border: none; }

.safe-hint { text-align: center; padding: 32rpx 0; }
.safe-hint text { font-size: 24rpx; color: var(--color-text-4); }
</style>
