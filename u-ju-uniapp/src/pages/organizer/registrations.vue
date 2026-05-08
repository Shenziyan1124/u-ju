<template>
  <view class="container">
    <view v-for="item in list" :key="item.id" class="reg-card">
      <view class="reg-header">
        <text class="reg-name">{{ getFormName(item) }}</text>
        <text :class="['reg-status', statusClass(item.status)]">{{ statusText(item.status) }}</text>
      </view>
      <text class="reg-phone">{{ getFormPhone(item) }}</text>
      <text class="reg-time">{{ formatDate(item.createdAt) }}</text>
      <view class="reg-actions" v-if="item.status === 0">
        <button size="mini" type="primary" @click="review(item.id, 1)">通过</button>
        <button size="mini" type="warn" @click="review(item.id, 2)">拒绝</button>
      </view>
    </view>
    <view class="empty" v-if="list.length === 0">暂无报名</view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { registrationApi } from '@/api'

const props = defineProps<{ eventId?: string }>()
const list = ref<any[]>([])

const loadData = async () => {
  if (!props.eventId) return
  try { const res: any = await registrationApi.getEventRegistrations(Number(props.eventId), { page: 1, size: 50 }); list.value = res.records || [] } catch {}
}
const statusText = (s: number) => ['待审核', '已通过', '已拒绝', '已取消'][s] || ''
const statusClass = (s: number) => ['pending', 'approved', 'rejected', 'cancelled'][s] || ''
const formatDate = (d: string) => d ? d.substring(0, 16).replace('T', ' ') : ''
const getFormName = (item: any) => { try { return JSON.parse(item.formData).realName || '-' } catch { return '-' } }
const getFormPhone = (item: any) => { try { return JSON.parse(item.formData).phone || '-' } catch { return '-' } }
const review = async (id: number, status: number) => {
  await registrationApi.review(id, status)
  uni.showToast({ title: '操作成功', icon: 'success' })
  loadData()
}
onMounted(loadData)
</script>

<style scoped>
.container { padding: 20rpx; }
.reg-card { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; }
.reg-header { display: flex; justify-content: space-between; margin-bottom: 12rpx; }
.reg-name { font-size: 30rpx; font-weight: bold; }
.reg-status { font-size: 24rpx; padding: 4rpx 16rpx; border-radius: 20rpx; }
.reg-status.pending { background: #fdf6ec; color: #e6a23c; }
.reg-status.approved { background: #f0f9eb; color: #67c23a; }
.reg-status.rejected { background: #fef0f0; color: #f56c6c; }
.reg-status.cancelled { background: #f4f4f5; color: #909399; }
.reg-phone { font-size: 26rpx; color: #666; display: block; margin-bottom: 8rpx; }
.reg-time { font-size: 24rpx; color: #999; display: block; margin-bottom: 12rpx; }
.reg-actions { display: flex; gap: 16rpx; }
.empty { text-align: center; padding: 100rpx 0; color: #999; }
</style>
