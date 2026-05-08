<template>
  <view class="page">
    <view class="section-title">报名详情</view>

    <view v-if="detail" class="card">
      <view class="status-banner" :class="'sb-' + detail.status">
        <text class="sb-icon">{{ statusIcon[detail.status] }}</text>
        <text class="sb-text">{{ statusMap[detail.status] || '未知' }}</text>
      </view>

      <view class="info-section">
        <view class="is-title">活动信息</view>
        <view class="is-row"><text class="is-label">活动名称</text><text class="is-val">{{ detail.eventTitle || '-' }}</text></view>
        <view class="is-row"><text class="is-label">报名时间</text><text class="is-val">{{ formatDate(detail.createdAt) }}</text></view>
      </view>

      <view class="ticket-section" v-if="detail.ticketCode">
        <view class="ts-header">
          <text class="ts-title">电子票</text>
          <text class="ts-hint">入场时出示此码</text>
        </view>
        <view class="ticket-code-wrap">
          <text class="ticket-code">{{ detail.ticketCode }}</text>
        </view>
        <button class="copy-btn" @click="copyCode">复制票码</button>
      </view>

      <view v-if="detail.formData" class="form-data-section">
        <view class="is-title">报名信息</view>
        <view v-for="(v, k) in parsedForm" :key="k" class="is-row">
          <text class="is-label">{{ fieldLabels[k] || k }}</text>
          <text class="is-val">{{ v }}</text>
        </view>
      </view>
    </view>

    <view v-if="detail && (detail.status === 0 || detail.status === 1)" class="action-area">
      <button class="btn-danger" @click="cancelReg">取消报名</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { registrationApi } from '@/api'
import { onShow } from '@dcloudio/uni-app'

const props = defineProps<{ id?: string }>()
const detail = ref<any>(null)

const statusMap = ['待审核', '已通过 ✓', '已拒绝 ✗', '已取消']
const statusIcon = ['⏳', '✓', '✗', '—']
const fieldLabels: Record<string, string> = { realName: '姓名', phone: '手机号', email: '邮箱' }

const parsedForm = computed(() => {
  if (!detail.value?.formData) return {}
  try { return JSON.parse(detail.value.formData) } catch { return {} }
})

const formatDate = (d: string) => d ? d.slice(0, 16).replace('T', ' ') : '-'

const loadDetail = async () => {
  if (!props.id) return
  detail.value = await registrationApi.getDetail(Number(props.id))
}
const copyCode = () => {
  if (!detail.value?.ticketCode) return
  uni.setClipboardData({ data: detail.value.ticketCode })
}
const cancelReg = async () => {
  uni.showModal({
    title: '确认取消',
    content: '取消后需重新报名',
    success: async (res) => {
      if (res.confirm && detail.value) {
        await registrationApi.cancel(detail.value.id)
        uni.showToast({ title: '已取消' }); loadDetail()
      }
    }
  })
}

onShow(loadDetail)
</script>

<style scoped>
.page { min-height: 100vh; background: var(--color-bg); padding-bottom: 40rpx; }
.section-title { padding: 28rpx 32rpx; font-size: 30rpx; font-weight: 700; color: var(--color-text-1); }

.card { margin: 0 32rpx; background: var(--color-white); border-radius: var(--radius-lg); padding: 32rpx; border: 1rpx solid var(--color-border-light); }

.status-banner {
  display: flex; align-items: center; justify-content: center; gap: 12rpx;
  padding: 28rpx; border-radius: var(--radius-lg); margin-bottom: 28rpx;
}
.sb-0 { background: #fef3c7; }
.sb-1 { background: #ecfdf5; }
.sb-2 { background: #fee2e2; }
.sb-3 { background: var(--color-fill); }
.sb-icon { font-size: 32rpx; }
.sb-text { font-size: 29rpx; font-weight: 600; }
.sb-0 .sb-text { color: #b45309; }
.sb-1 .sb-text { color: #059669; }
.sb-2 .sb-text { color: #dc2626; }
.sb-3 .sb-text { color: var(--color-text-4); }

.info-section, .form-data-section { margin-bottom: 28rpx; }
.is-title { font-size: 27rpx; font-weight: 600; color: var(--color-text-1); margin-bottom: 18rpx; padding-bottom: 14rpx; border-bottom: 1rpx solid var(--color-border-light); }
.is-row { display: flex; justify-content: space-between; padding: 16rpx 0; }
.is-label { font-size: 26rpx; color: var(--color-text-3); flex-shrink: 0; margin-right: 20rpx; }
.is-val { font-size: 27rpx; color: var(--color-text-1); text-align: right; word-break: break-all; }

.ticket-section {
  background: #1677ff;
  border-radius: var(--radius-lg); padding: 32rpx; margin-bottom: 28rpx;
}
.ts-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20rpx; }
.ts-title { font-size: 28rpx; font-weight: 700; color: #fff; }
.ts-hint { font-size: 24rpx; color: rgba(255,255,255,0.7); }
.ticket-code-wrap {
  background: rgba(255,255,255,0.15); border: 2rpx dashed rgba(255,255,255,0.4);
  border-radius: var(--radius-md); padding: 24rpx; text-align: center;
}
.ticket-code {
  font-size: 40rpx; font-weight: 800; letter-spacing: 6rpx;
  color: #fff; font-family: monospace;
}
.copy-btn {
  margin-top: 20rpx; height: 68rpx; line-height: 68rpx;
  background: rgba(255,255,255,0.2); color: #fff;
  font-size: 27rpx; border-radius: var(--radius-md); border: none;
}
.copy-btn::after { border: none; }

.action-area { padding: 32rpx; }
.btn-danger {
  width: 100%; height: 88rpx; line-height: 88rpx;
  background: transparent; color: var(--color-danger);
  font-size: 30rpx; border: 2rpx solid var(--color-danger);
  border-radius: var(--radius-lg);
}
.btn-danger::after { border: none; }
</style>
