<template>
  <view class="page">
    <scroll-view scroll-x class="tab-scroll">
      <view class="tab-list">
        <view v-for="(t, i) in tabs" :key="i" :class="['tab-item', { active: status === t.value }]"
              @tap="status = t.value; load(true)">{{ t.label }}</view>
      </view>
    </scroll-view>

    <view class="order-list" v-if="orders.length || loading">
      <view v-if="loading && !orders.length" v-for="n in 3" :key="'sk'+n" class="order-card sk">
        <view class="sk-line long"></view><view class="sk-line"></view><view class="sk-line short"></view>
      </view>

      <view v-for="item in orders" :key="item.id" class="order-card" @tap="goDetail(item.id)">
        <view class="oc-header">
          <text class="oc-event">{{ item.eventName }}</text>
          <text :class="['oc-status', 's' + item.status]">{{ statusText[item.status] }}</text>
        </view>
        <view class="oc-body">
          <text class="oc-amount">¥{{ item.amount || 0 }}</text>
          <text class="oc-time">{{ item.createdAt?.slice(5,16) }}</text>
        </view>
        <view class="oc-footer">
          <button v-if="item.status === 0" class="oc-btn primary" @tap.stop="payOrder(item)">去支付</button>
          <button v-if="item.status === 1" class="oc-btn plain" @tap.stop="refundOrder(item)">申请退款</button>
        </view>
      </view>

      <view v-if="!orders.length && !loading" class="empty-state">
        <text class="empty-icon">🧾</text>
        <text class="empty-text">暂无订单</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { orderApi } from '@/api'

const tabs = [{ label: '全部', value: '' }, { label: '待支付', value: 0 }, { label: '已支付', value: 1 }, { label: '已完成', value: 2 }]
const statusText = ['待支付', '已支付', '已完成', '已取消', '退款中', '已退款']
const orders = ref([])
const loading = ref(false)
const status = ref('')

const goDetail = (id) => uni.navigateTo({ url: `/pages/order/detail?id=${id}` })
const payOrder = async (item) => {
  try { await orderApi.pay(item.id, 1); uni.showToast({ title: '支付成功', icon: 'success' }); load() }
  catch {}
}
const refundOrder = (item) => {
  uni.showModal({ title: '确认退款？', success: async (res) => {
    if (res.confirm) { try { await orderApi.refund(item.id); load() } catch {} }
  }})
}

const load = async () => {
  loading.value = true
  try {
    const params = { page: 1, size: 20 }
    if (status.value !== '') params.status = status.value
    const res = await orderApi.getMyList(params)
    orders.value = res.data?.records || res.data || []
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

.order-list { padding: 24rpx 32rpx; }
.order-card { background: var(--color-white); border-radius: var(--radius-lg); padding: 28rpx; margin-bottom: 20rpx; border: 1rpx solid var(--color-border-light); }
.oc-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18rpx; }
.oc-event { font-size: 29rpx; font-weight: 600; color: var(--color-text-1); flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-right: 16rpx; }
.oc-status { font-size: 24rpx; font-weight: 500; flex-shrink: 0; }
.oc-status.s0 { color: var(--color-warning); }
.oc-status.s1 { color: var(--color-primary); }
.oc-status.s2 { color: var(--color-success); }
.oc-status.s4 { color: var(--color-danger); }

.oc-body { display: flex; justify-content: space-between; align-items: center; padding-bottom: 20rpx; border-bottom: 1rpx dashed var(--color-border-light); margin-bottom: 18rpx; }
.oc-amount { font-size: 36rpx; font-weight: 800; color: var(--color-danger); }
.oc-time { font-size: 24rpx; color: var(--color-text-4); }

.oc-footer { display: flex; justify-content: flex-end; gap: 16rpx; }
.oc-btn { padding: 12rpx 30rpx; border-radius: var(--radius-md); font-size: 26rpx; border: none; line-height: 1.4; }
.oc-btn.primary { background: var(--color-primary); color: #fff; }
.oc-btn.plain { background: transparent; color: var(--color-text-3); border: 2rpx solid var(--color-border); }
.oc-btn::after { border: none; }

.sk .sk-line { height: 24rpx; background: #f0f0f0; border-radius: 6rpx; margin-bottom: 14rpx; }
.sk .sk-line.long { width: 70%; }
.sk .sk-line.short { width: 40%; }

.empty-state { text-align: center; padding: 100rpx 0; }
.empty-icon { font-size: 72rpx; display: block; margin-bottom: 16rpx; }
.empty-text { font-size: 28rpx; color: var(--color-text-4); }
</style>
