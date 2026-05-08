<template>
  <view class="page">
    <view class="result-header" :class="statusType">
      <view class="rh-icon-wrap">
        <text class="rh-icon">{{ statusType === 'success' ? '✓' : '✗' }}</text>
      </view>
      <text class="rh-title">{{ statusType === 'success' ? '支付成功' : '支付失败' }}</text>
      <text class="rh-desc">{{ statusType === 'success' ? '您的报名已生效，请查看电子票' : '支付遇到问题，请稍后重试' }}</text>
    </view>

    <view v-if="orderInfo" class="info-card">
      <view class="ic-row"><text class="ic-label">订单号</text><text class="ic-value">{{ orderInfo.orderNo }}</text></view>
      <view class="ic-row"><text class="ic-label">支付金额</text><text class="ic-value amount">¥{{ orderInfo.amount }}</text></view>
      <view class="ic-row"><text class="ic-label">支付方式</text><text class="ic-value">{{ ['微信支付', '支付宝'][orderInfo.payMethod] || '-' }}</text></view>
      <view class="ic-row"><text class="ic-label">支付时间</text><text class="ic-value">{{ formatTime(orderInfo.paidAt) }}</text></view>
    </view>

    <view class="action-area">
      <button v-if="statusType === 'success'" class="btn-primary" @tap="goTicket">查看电子票</button>
      <button v-if="statusType === 'fail'" class="btn-primary" @tap="retryPay">重新支付</button>
      <button class="btn-outline" @tap="goOrders">返回订单列表</button>
    </view>

    <view class="hint-text">
      <text>如有问题请联系客服</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const props = defineProps<{ orderNo?: string; status?: string }>()
const orderInfo = ref<any>(null)

const statusType = computed(() => {
  if (props.status === 'success' || props.status === '1') return 'success'
  return 'fail'
})

const formatTime = (d: string) => d ? d.slice(0, 16).replace('T', ' ') : '-'

const goTicket = () => uni.navigateTo({ url: '/pages/registration/list?tab=approved' })
const goOrders = () => uni.redirectTo({ url: '/pages/order/list' })
const retryPay = () => uni.navigateBack()
</script>

<style scoped>
.page { min-height: 100vh; background: var(--color-bg); }

.result-header { text-align: center; padding: 120rpx 48rpx 60rpx; }
.result-header.success { background: #52c41a; }
.result-header.fail { background: #ff4d4f; }

.rh-icon-wrap {
  width: 140rpx; height: 140rpx; border-radius: 50%;
  background: rgba(255,255,255,0.2); display: flex;
  align-items: center; justify-content: center; margin: 0 auto 32rpx;
}
.rh-icon { font-size: 64rpx; color: #fff; font-weight: bold; }
.rh-title { display: block; font-size: 40rpx; font-weight: 700; color: #fff; margin-bottom: 12rpx; }
.rh-desc { display: block; font-size: 27rpx; color: rgba(255,255,255,0.85); }

.info-card { margin: -30rpx 32rpx 0; background: var(--color-white); border-radius: var(--radius-lg); padding: 32rpx; border: 1rpx solid var(--color-border-light); position: relative; z-index: 1; }
.ic-row { display: flex; justify-content: space-between; padding: 18rpx 0; }
.ic-row + .ic-row { border-top: 1rpx solid var(--color-border-light); }
.ic-label { font-size: 27rpx; color: var(--color-text-3); }
.ic-value { font-size: 28rpx; color: var(--color-text-1); font-weight: 500; }
.ic-value.amount { font-size: 36rpx; font-weight: 800; color: var(--color-danger); }

.action-area { padding: 48rpx 32rpx 0; display: flex; flex-direction: column; gap: 20rpx; }
.btn-primary {
  height: 92rpx; line-height: 92rpx; border-radius: var(--radius-lg);
  background: #1677ff;
  color: #fff; font-size: 31rpx; font-weight: 600; border: none;
}
.btn-primary::after { border: none; }
.btn-outline {
  height: 92rpx; line-height: 92rpx; border-radius: var(--radius-lg);
  background: transparent; color: var(--color-primary);
  font-size: 31rpx; border: 2rpx solid var(--color-primary);
}
.btn-outline::after { border: none; }

.hint-text { text-align: center; padding: 40rpx 0; }
.hint-text text { font-size: 24rpx; color: var(--color-text-4); }
</style>
