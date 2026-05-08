<template>
  <view class="page">
    <view class="section-title">活动报名</view>

    <view v-if="eventInfo" class="event-preview">
      <image :src="eventInfo.coverImage || '/static/placeholder.png'" mode="aspectFill" class="ep-cover" />
      <view class="ep-body">
        <text class="ep-title">{{ eventInfo.title }}</text>
        <view class="ep-meta">
          <text :class="['ep-tag', { free: eventInfo.isFree === 1 }]">{{ eventInfo.isFree === 1 ? '免费' : '付费' }}</text>
          <text>📅 {{ eventInfo.startTime?.slice(0,16) }}</text>
        </view>
      </view>
    </view>

    <view class="form-card">
      <view class="fc-title">填写报名信息</view>
      <view class="input-group">
        <view class="input-item">
          <text class="ii-icon">👤</text>
          <input v-model="form.realName" placeholder="真实姓名（必填）" />
        </view>
        <view class="input-item">
          <text class="ii-icon">📱</text>
          <input v-model="form.phone" type="number" placeholder="手机号（必填）" maxlength="11" />
        </view>
        <view class="input-item">
          <text class="ii-icon">📧</text>
          <input v-model="form.email" placeholder="邮箱（选填）" />
        </view>
      </view>

      <view v-if="eventInfo?.needReview === 1" class="review-hint">
        <text>⏳ 该活动需要审核，提交后请耐心等待</text>
      </view>

      <button class="submit-btn" :loading="submitting" @click="handleSubmit">{{ submitting ? '提交中...' : '确认报名' }}</button>
    </view>

    <view class="agreement">
      <text class="ag-text">提交即表示同意</text>
      <text class="ag-link">《活动报名协议》</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { registrationApi } from '@/api'

const props = defineProps<{ eventId?: string }>()
const eventInfo = ref<any>(null)
const submitting = ref(false)
const form = reactive({ realName: '', phone: '', email: '' })

const loadEvent = async () => {
  if (!props.eventId) return
  try {
    const m = await import('@/api')
    eventInfo.value = await m.eventApi.getDetail(Number(props.eventId))
  } catch {}
}

const handleSubmit = async () => {
  if (!form.realName || !form.phone) {
    uni.showToast({ title: '请填写姓名和手机号', icon: 'none' }); return
  }
  if (form.phone.length !== 11) {
    uni.showToast({ title: '手机号格式不正确', icon: 'none' }); return
  }
  submitting.value = true
  try {
    await registrationApi.create({
      eventId: Number(props.eventId),
      formData: JSON.stringify(form),
    })
    uni.showToast({ title: eventInfo.value?.needReview === 1 ? '已提交，请等待审核' : '报名成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 1500)
  } catch {} finally { submitting.value = false }
}

loadEvent()
</script>

<style scoped>
.page { min-height: 100vh; background: var(--color-bg); padding-bottom: 40rpx; }
.section-title { padding: 28rpx 32rpx; font-size: 30rpx; font-weight: 700; color: var(--color-text-1); }

.event-preview { display: flex; gap: 20rpx; background: var(--color-white); margin: 0 32rpx 24rpx; border-radius: var(--radius-lg); padding: 22rpx; border: 1rpx solid var(--color-border-light); }
.ep-cover { width: 160rpx; height: 120rpx; border-radius: var(--radius-md); flex-shrink: 0; }
.ep-body { flex: 1; display: flex; flex-direction: column; justify-content: space-between; }
.ep-title { font-size: 28rpx; font-weight: 600; color: var(--color-text-1); display: -webkit-box; -webkit-line-clamp: 2; overflow: hidden; }
.ep-meta { display: flex; align-items: center; gap: 12rpx; font-size: 24rpx; color: var(--color-text-4); }
.ep-tag { padding: 2rpx 12rpx; border-radius: var(--radius-sm); font-size: 22rpx; font-weight: 500; background: var(--color-primary-bg); color: var(--color-primary); }
.ep-tag.free { background: #ecfdf5; color: var(--color-success); }

.form-card { margin: 0 32rpx; background: var(--color-white); border-radius: var(--radius-lg); padding: 32rpx; border: 1rpx solid var(--color-border-light); }
.fc-title { font-size: 29rpx; font-weight: 600; color: var(--color-text-1); margin-bottom: 28rpx; }

.input-group { display: flex; flex-direction: column; gap: 4rpx; }
.input-item {
  display: flex; align-items: center; gap: 16rpx;
  padding: 26rpx 24rpx; background: var(--color-fill);
  border-radius: var(--radius-md); border: 2rpx solid transparent;
  transition: all 0.2s; margin-bottom: 16rpx;
}
.input-item:focus-within { border-color: var(--color-primary); background: #fff; }
.ii-icon { font-size: 30rpx; width: 36rpx; text-align: center; }
.input-item input { flex: 1; font-size: 28rpx; color: var(--color-text-1); }

.review-hint {
  margin-top: 20rpx; padding: 18rpx 20rpx;
  background: #fef3c7; border-radius: var(--radius-sm);
}
.review-hint text { font-size: 25rpx; color: #b45309; }

.submit-btn {
  width: 100%; height: 92rpx; line-height: 92rpx; margin-top: 32rpx;
  background: #1677ff;
  color: #fff; font-size: 32rpx; font-weight: 600;
  border-radius: var(--radius-lg); border: none; letter-spacing: 2rpx;
}
.submit-btn::after { border: none; }

.agreement { text-align: center; margin-top: 28rpx; }
.ag-text { font-size: 24rpx; color: var(--color-text-4); }
.ag-link { font-size: 24rpx; color: var(--color-primary); font-weight: 500; }
</style>
