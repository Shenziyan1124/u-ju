<template>
  <view class="page">
    <view class="section-title">订单详情</view>

    <view v-if="detail" class="card">
      <view class="status-badge" :class="'sb-' + detail.status">{{ statusMap[detail.status] || '未知' }}</view>

      <view class="info-group">
        <view class="ig-row"><text class="ig-label">订单号</text><text class="ig-value mono">{{ detail.orderNo }}</text></view>
        <view class="ig-row"><text class="ig-label">活动名称</text><text class="ig-value">{{ detail.eventName || '-' }}</text></view>
        <view class="ig-row"><text class="ig-label">支付金额</text><text class="ig-value amount">¥{{ detail.amount || '0.00' }}</text></view>
        <view class="ig-row"><text class="ig-label">支付方式</text><text class="ig-value">{{ ['微信支付','支付宝'][detail.payMethod] || '-' }}</text></view>
        <view class="ig-row"><text class="ig-label">创建时间</text><text class="ig-value">{{ formatDate(detail.createdAt) }}</text></view>
        <view v-if="detail.paidAt" class="ig-row"><text class="ig-label">支付时间</text><text class="ig-value">{{ formatDate(detail.paidAt) }}</text></view>
      </view>
    </view>

    <view class="action-area">
      <button v-if="detail?.status === 0" class="btn-pay" @click="goPay">立即支付</button>
      <button v-if="detail?.status === 0" class="btn-cancel" @click="cancelOrder">取消订单</button>
      <button v-if="detail?.status === 1 || detail?.status === 2" class="btn-refund" @click="refundOrder">申请退款</button>
      <button class="btn-back" @click="goBack">返回列表</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { orderApi } from '@/api'

const props = defineProps<{ id?: string }>()
const detail = ref<any>(null)
const statusMap = ['待支付', '已支付', '已完成', '已取消', '退款中', '已退款']

const loadDetail = async () => {
  if (!props.id) return
  detail.value = await orderApi.getDetail(Number(props.id))
}
const formatDate = (d: string) => d ? d.slice(0, 16).replace('T', ' ') : '-'

const goPay = () => {
  if (!detail.value) return
  uni.navigateTo({ url: `/pages/order/pay?registrationId=${detail.value.registrationId}` })
}
const cancelOrder = async () => {
  uni.showModal({ title: '确认取消？', content: '取消后需重新下单', success: async (res) => {
    if (res.confirm && detail.value) {
      await orderApi.cancel(detail.value.id)
      uni.showToast({ title: '已取消' }); loadDetail()
    }
  }})
}
const refundOrder = async () => {
  uni.showModal({ title: '申请退款？', success: async (res) => {
    if (res.confirm && detail.value) {
      await orderApi.refund(detail.value.id)
      uni.showToast({ title: '已申请' }); loadDetail()
    }
  }})
}
const goBack = () => uni.navigateBack()

loadDetail()
</script>

<style scoped>
.page { min-height: 100vh; background: var(--color-bg); padding-bottom: 40rpx; }
.section-title { padding: 28rpx 32rpx; font-size: 30rpx; font-weight: 700; color: var(--color-text-1); }

.card { margin: 0 32rpx; background: var(--color-white); border-radius: var(--radius-lg); padding: 32rpx; border: 1rpx solid var(--color-border-light); position: relative; }
.status-badge {
  position: absolute; top: -16rpx; right: 32rpx;
  padding: 8rpx 24rpx; border-radius: var(--radius-full);
  font-size: 24rpx; font-weight: 600; color: #fff;
}
.sb-0 { background: var(--color-warning); }
.sb-1, .sb-2 { background: var(--color-success); }
.sb-3 { background: var(--color-text-4); }
.sb-4 { background: var(--color-danger); }
.sb-5 { background: var(--color-text-3); }

.info-group { margin-top: 16rpx; }
.ig-row { display: flex; justify-content: space-between; padding: 20rpx 0; border-bottom: 1rpx solid var(--color-border-light); }
.ig-row:last-child { border-bottom: none; }
.ig-label { font-size: 27rpx; color: var(--color-text-3); flex-shrink: 0; margin-right: 24rpx; }
.ig-value { font-size: 28rpx; color: var(--color-text-1); text-align: right; word-break: break-all; }
.ig-value.mono { font-family: monospace; font-size: 26rpx; }
.ig-value.amount { font-size: 36rpx; font-weight: 800; color: var(--color-danger); }

.action-area { padding: 40rpx 32rpx 0; display: flex; flex-direction: column; gap: 18rpx; }
.btn-pay {
  height: 88rpx; line-height: 88rpx; border-radius: var(--radius-lg);
  background: #1677ff;
  color: #fff; font-size: 31rpx; font-weight: 600; border: none;
}
.btn-pay::after { border: none; }
.btn-cancel, .btn-refund, .btn-back {
  height: 84rpx; line-height: 84rpx; border-radius: var(--radius-lg);
  font-size: 29rpx; border: 2rpx solid var(--color-border); background: #fff;
}
.btn-cancel { color: var(--color-text-3); }
.btn-refund { color: var(--color-danger); border-color: var(--color-danger); }
.btn-back { color: var(--color-text-2); }
.btn-cancel::after, .btn-refund::after, .btn-back::after { border: none; }
</style>
