<template>
  <view class="container">
    <view class="menu-list">
      <view class="menu-item" @click="changePassword"><text>修改密码</text><text class="arrow">›</text></view>
      <view class="menu-item" @click="clearCache"><text>清除缓存</text><text class="arrow">›</text></view>
    </view>
    <view class="version">U Ju v1.0.0</view>
  </view>
</template>

<script setup lang="ts">
const changePassword = () => { uni.showModal({ title: '修改密码', editable: true, placeholderText: '输入新密码', success: () => {} }) }
const clearCache = () => { uni.clearStorageSync(); uni.showToast({ title: '已清除', icon: 'success' }) }
</script>

<style scoped>
.container { padding: 30rpx; }
.menu-list { background: #fff; border-radius: 20rpx; overflow: hidden; }
.menu-item { display: flex; justify-content: space-between; align-items: center; padding: 30rpx; border-bottom: 1rpx solid #f5f5f5; font-size: 28rpx; }
.arrow { color: #ccc; }
.version { text-align: center; color: #999; font-size: 24rpx; margin-top: 60rpx; }
</style>
