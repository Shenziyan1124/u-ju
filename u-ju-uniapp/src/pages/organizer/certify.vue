<template>
  <view class="page">
    <view class="section-title">企业认证</view>

    <view v-if="certStatus !== null" class="status-card" :class="'sc-' + certStatus">
      <text class="sc-icon">{{ ['⏳','✓','✗'][certStatus] }}</text>
      <view class="sc-body">
        <text class="sc-title">{{ ['审核中','认证通过','认证被拒'][certStatus] }}</text>
        <text v-if="certRemark" class="sc-remark">{{ certRemark }}</text>
      </view>
    </view>

    <view class="form-card" v-if="!certStatus || certStatus === 2">
      <view class="fc-title">企业信息</view>
      <view class="tip-bar">
        <text>ℹ️ 认证通过后即可发布活动</text>
      </view>

      <view class="input-item">
        <text class="ii-label">企业名称</text><text class="ii-star">*</text>
        <input v-model="form.companyName" placeholder="请输入企业全称" />
      </view>
      <view class="input-item">
        <text class="ii-label">信用代码</text><text class="ii-star">*</text>
        <input v-model="form.creditCode" placeholder="统一社会信用代码" />
      </view>
      <view class="input-item">
        <text class="ii-label">企业地址</text><text class="ii-star">*</text>
        <input v-model="form.companyAddress" placeholder="详细地址" />
      </view>
      <view class="input-item">
        <text class="ii-label">联系人</text><text class="ii-star">*</text>
        <input v-model="form.contactPerson" placeholder="联系人姓名" />
      </view>
      <view class="input-item">
        <text class="ii-label">联系电话</text><text class="ii-star">*</text>
        <input v-model="form.contactPhone" type="number" placeholder="手机号码" maxlength="11" />
      </view>

      <view class="upload-area" @tap="chooseLicense">
        <image v-if="form.businessLicense" :src="form.businessLicense" mode="aspectFit" class="preview-img" />
        <view v-else class="upload-placeholder">
          <text class="up-icon">📷</text>
          <text class="up-text">上传营业执照</text>
          <text class="up-hint">支持 jpg/png，不超过 5MB</text>
        </view>
      </view>

      <button class="submit-btn" :loading="submitting" @click="handleSubmit">{{ submitting ? '提交中...' : '提交认证' }}</button>
    </view>

    <view v-if="certStatus === 1" class="cert-success-card">
      <text class="cs-icon">🎉</text>
      <text class="cs-title">您已成为认证发布者</text>
      <text class="cs-desc">现在可以发布活动了</text>
      <button class="go-publish-btn" @tap="goPublish">去发布活动</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { certificationApi } from '@/api'

const form = reactive({
  companyName: '', creditCode: '', companyAddress: '',
  contactPerson: '', contactPhone: '', businessLicense: '',
})
const submitting = ref(false)
const certStatus = ref<number | null>(null)
const certRemark = ref('')

const loadCert = async () => {
  try {
    const data = await certificationApi.getMine()
    certStatus.value = data.status
    certRemark.value = data.reviewRemark || ''
  } catch { certStatus.value = null }
}

const chooseLicense = () => {
  uni.chooseImage({
    count: 1, sizeType: ['compressed'],
    success: (res) => { form.businessLicense = res.tempFilePaths[0] }
  })
}

const handleSubmit = async () => {
  const required = ['companyName', 'creditCode', 'companyAddress', 'contactPerson', 'contactPhone']
  for (const f of required) {
    if (!form[f as keyof typeof form]) {
      uni.showToast({ title: '请填写完整信息', icon: 'none' }); return
    }
  }
  submitting.value = true
  try {
    await certificationApi.submit(form)
    uni.showToast({ title: '提交成功', icon: 'success' })
    loadCert()
  } catch {} finally { submitting.value = false }
}

const goPublish = () => uni.navigateTo({ url: '/pages/organizer/publish' })

loadCert()
</script>

<style scoped>
.page { min-height: 100vh; background: var(--color-bg); padding-bottom: 40rpx; }
.section-title { padding: 28rpx 32rpx; font-size: 30rpx; font-weight: 700; color: var(--color-text-1); }

.status-card {
  display: flex; align-items: center; gap: 20rpx; margin: 0 32rpx 24rpx;
  padding: 24rpx 28rpx; border-radius: var(--radius-lg);
}
.sc-0 { background: #fef3c7; }
.sc-1 { background: #ecfdf5; }
.sc-2 { background: #fee2e2; }
.sc-icon { font-size: 40rpx; }
.sc-body { flex: 1; }
.sc-title { display: block; font-size: 28rpx; font-weight: 600; margin-bottom: 4rpx; }
.sc-0 .sc-title { color: #b45309; }
.sc-1 .sc-title { color: #059669; }
.sc-2 .sc-title { color: #dc2626; }
.sc-remark { display: block; font-size: 25rpx; color: var(--color-text-4); margin-top: 4rpx; }

.form-card { margin: 0 32rpx; background: var(--color-white); border-radius: var(--radius-lg); padding: 32rpx 28rpx; border: 1rpx solid var(--color-border-light); }
.fc-title { font-size: 29rpx; font-weight: 600; color: var(--color-text-1); margin-bottom: 20rpx; }

.tip-bar {
  padding: 16rpx 20rpx; margin-bottom: 24rpx;
  background: var(--color-primary-bg); border-radius: var(--radius-sm);
}
.tip-bar text { font-size: 25rpx; color: var(--color-primary); }

.input-item {
  display: flex; align-items: center; gap: 8rpx;
  padding: 24rpx 20rpx; margin-bottom: 14rpx;
  background: var(--color-fill); border-radius: var(--radius-md);
  border: 2rpx solid transparent; transition: all 0.2s;
}
.input-item:focus-within { border-color: var(--color-primary); background: #fff; }
.ii-label { font-size: 26rpx; color: var(--color-text-3); flex-shrink: 0; }
.ii-star { color: var(--color-danger); font-size: 26rpx; }
.input-item input { flex: 1; font-size: 28rpx; color: var(--color-text-1); }

.upload-area {
  margin: 20rpx 0; border: 2rpx dashed var(--color-border);
  border-radius: var(--radius-lg); overflow: hidden;
}
.preview-img { width: 100%; height: 300rpx; }
.upload-placeholder {
  display: flex; flex-direction: column; align-items: center;
  padding: 48rpx 0; gap: 10rpx;
}
.up-icon { font-size: 48rpx; }
.up-text { font-size: 28rpx; color: var(--color-text-2); }
.up-hint { font-size: 24rpx; color: var(--color-text-4); }

.submit-btn {
  width: 100%; height: 92rpx; line-height: 92rpx; margin-top: 28rpx;
  background: #1677ff;
  color: #fff; font-size: 32rpx; font-weight: 600;
  border-radius: var(--radius-lg); border: none; letter-spacing: 2rpx;
}
.submit-btn::after { border: none; }

.cert-success-card {
  margin: 60rpx 32rpx; text-align: center;
  padding: 48rpx 32rpx; background: var(--color-white);
  border-radius: var(--radius-lg); border: 1rpx solid var(--color-border-light);
}
.cs-icon { font-size: 72rpx; display: block; margin-bottom: 20rpx; }
.cs-title { display: block; font-size: 33rpx; font-weight: 700; color: var(--color-text-1); margin-bottom: 10rpx; }
.cs-desc { display: block; font-size: 27rpx; color: var(--color-text-4); margin-bottom: 36rpx; }
.go-publish-btn {
    display: inline-block; padding: 20rpx 56rpx;
    background: #1677ff; color: #fff; font-size: 29rpx; font-weight: 600;
    border-radius: var(--radius-xl); border: none;
  }
.go-publish-btn::after { border: none; }
</style>
