<template>
  <view class="page">
    <view class="section-title">发布活动</view>

    <scroll-view scroll-y class="form-scroll">
      <view class="form-card">
        <view class="fc-group">
          <text class="fg-label">基本信息</text>
          <view class="input-item">
            <text class="ii-required">*</text>
            <input v-model="form.title" placeholder="活动标题（1-100字）" maxlength="100" />
          </view>
          <view class="picker-item" @tap="showCategoryPicker = true">
            <text class="ii-required">*</text>
            <text :class="['pi-value', { placeholder: !form.category }]">{{ form.category || '选择活动分类' }}</text>
            <text class="pi-arrow">›</text>
          </view>
          <textarea v-model="form.summary" placeholder="活动简介（10-200字）" maxlength="200" class="textarea-style" />
          <textarea v-model="form.detail" placeholder="活动详情（选填，支持富文本描述）" class="textarea-style long" />
        </view>

        <view class="divider"></view>

        <view class="fc-group">
          <text class="fg-label">时间地点</text>
          <view class="input-item" @tap="showStartPicker">
            <text class="ii-required">*</text>
            <input :value="form.startTime" disabled placeholder="开始时间" />
          </view>
          <view class="input-item" @tap="showEndPicker">
            <text class="ii-required">*</text>
            <input :value="form.endTime" disabled placeholder="结束时间" />
          </view>
          <view class="input-item">
            <input v-model="form.registrationDeadline" placeholder="报名截止时间" />
          </view>
          <view class="picker-item" @tap="showLocationPicker = true">
            <text :class="['pi-value', { placeholder: !form.locationType }]">{{ ['线下','线上','混合'][form.locationType] || '选择地点类型' }}</text>
            <text class="pi-arrow">›</text>
          </view>
          <view class="input-item" v-if="form.locationType === 0 || form.locationType === 2">
            <input v-model="form.address" placeholder="详细地址" />
          </view>
        </view>

        <view class="divider"></view>

        <view class="fc-group">
          <text class="fg-label">报名设置</text>
          <view class="switch-row">
            <text class="sr-label">免费活动</text>
            <switch :checked="form.isFree === 1" @change="form.isFree = $event.detail.value ? 1 : 0" color="#4f46e5" />
          </view>
          <view class="row-inputs">
            <view class="input-item half">
              <input v-model.number="form.minParticipants" type="number" placeholder="最少人数" />
            </view>
            <view class="input-item half">
              <input v-model.number="form.maxParticipants" type="number" placeholder="最多人数" />
            </view>
          </view>
          <view class="switch-row">
            <text class="sr-label">需要审核</text>
            <switch :checked="form.needReview === 1" @change="form.needReview = $event.detail.value ? 1 : 0" color="#4f46e5" />
          </view>
          <view class="switch-row">
            <text class="sr-label">公开展示</text>
            <switch :checked="form.isPublic === 1" @change="form.isPublic = $event.detail.value ? 1 : 0" color="#4f46e5" />
          </view>
        </view>
      </view>
    </scroll-view>

    <view class="bottom-bar">
      <button class="btn-draft" @click="saveDraft">存草稿</button>
      <button class="btn-publish" :loading="publishing" @click="handlePublish">{{ publishing ? '发布中...' : '发布活动' }}</button>
    </view>

    <picker mode="selector" :range="categories" @change="(e: any) => form.category = categories[e.detail.value]" :value="-1" v-if="showCategoryPicker">
      <view style="display:none"></view>
    </picker>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { eventApi } from '@/api'

const categories = ['会议论坛', '培训讲座', '展览展会', '体育赛事', '文艺演出', '社交聚会', '其他']
const showCategoryPicker = ref(false)
const showLocationPicker = ref(false)
const showStartPicker = ref(false)
const showEndPicker = ref(false)
const publishing = ref(false)

const form = reactive({
  title: '', category: '', summary: '', detail: '',
  startTime: '', endTime: '', registrationDeadline: '',
  locationType: 0, address: '',
  isFree: 1, minParticipants: 0, maxParticipants: 100,
  needReview: 0, isPublic: 1, contact: '', refundPolicy: '',
})

const saveDraft = async () => {
  try {
    await eventApi.create({ ...form, status: 0 })
    uni.showToast({ title: '已保存草稿', icon: 'success' })
  } catch {}
}

const handlePublish = async () => {
  if (!form.title || !form.category || !form.startTime) {
    uni.showToast({ title: '请填写必要信息', icon: 'none' }); return
  }
  publishing.value = true
  try {
    await eventApi.create({
      ...form,
      coverImages: JSON.stringify([]),
      tags: JSON.stringify([]),
      status: 1,
    })
    uni.showToast({ title: '发布成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 1200)
  } catch {} finally { publishing.value = false }
}
</script>

<style scoped>
.page { min-height: 100vh; background: var(--color-bg); display: flex; flex-direction: column; }
.section-title { padding: 24rpx 32rpx; font-size: 30rpx; font-weight: 700; color: var(--color-text-1); }

.form-scroll { flex: 1; }
.form-card { margin: 0 32rpx; background: var(--color-white); border-radius: var(--radius-lg); padding: 8rpx 28rpx 32rpx; border: 1rpx solid var(--color-border-light); }

.fc-group { padding: 24rpx 0; }
.fg-label { display: block; font-size: 28rpx; font-weight: 600; color: var(--color-text-1); margin-bottom: 22rpx; }

.input-item {
  display: flex; align-items: center; gap: 12rpx;
  padding: 24rpx 20rpx; margin-bottom: 14rpx;
  background: var(--color-fill); border-radius: var(--radius-md);
  border: 2rpx solid transparent; transition: all 0.2s;
}
.input-item:focus-within { border-color: var(--color-primary); background: #fff; }
.input-item.half { flex: 1; }
.ii-required { color: var(--color-danger); font-size: 28rpx; font-weight: bold; }
.input-item input { flex: 1; font-size: 28rpx; color: var(--color-text-1); }

.picker-item {
  display: flex; align-items: center; gap: 12rpx;
  padding: 24rpx 20rpx; margin-bottom: 14rpx;
  background: var(--color-fill); border-radius: var(--radius-md);
}
.pi-value { flex: 1; font-size: 28rpx; color: var(--color-text-1); }
.pi-value.placeholder { color: var(--color-text-4); }
.pi-arrow { font-size: 32rpx; color: var(--color-text-4); }

.textarea-style {
  width: 100%; min-height: 140rpx; padding: 22rpx 20rpx; margin-bottom: 14rpx;
  background: var(--color-fill); border-radius: var(--radius-md);
  font-size: 28rpx; color: var(--color-text-1); box-sizing: border-box;
}
.textarea-style.long { min-height: 200rpx; }

.divider { height: 1rpx; background: var(--color-border-light); margin: 8rpx 0; }

.switch-row { display: flex; justify-content: space-between; align-items: center; padding: 18rpx 0; }
.sr-label { font-size: 28rpx; color: var(--color-text-1); }
.row-inputs { display: flex; gap: 16rpx; }

.bottom-bar {
  display: flex; gap: 20rpx; padding: 16rpx 32rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  background: var(--color-white); box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.06);
}
.btn-draft {
  flex: 1; height: 84rpx; line-height: 84rpx;
  background: var(--color-fill); color: var(--color-text-2);
  font-size: 29rpx; border-radius: var(--radius-lg); border: none;
}
.btn-draft::after { border: none; }
.btn-publish {
  flex: 2; height: 84rpx; line-height: 84rpx;
  background: #1677ff;
  color: #fff; font-size: 29rpx; font-weight: 600;
  border-radius: var(--radius-lg); border: none;
}
.btn-publish::after { border: none; }
</style>
