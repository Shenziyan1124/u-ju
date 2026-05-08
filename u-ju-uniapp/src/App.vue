<script>
export default {
  onLaunch() {
    console.log('U Ju App Launch')
  },
  onShow() {},
}
</script>

<style>
page {
  height: 100%;
  overflow-y: auto;

  --color-primary: #1677ff;
  --color-primary-light: #4096ff;
  --color-primary-bg: #e6f4ff;

  --color-success: #52c41a;
  --color-success-bg: #f6ffed;
  --color-warning: #faad14;
  --color-warning-bg: #fffbe6;
  --color-danger: #ff4d4f;
  --color-danger-bg: #fff2f0;

  --color-text-1: #1a1a1a;
  --color-text-2: #333333;
  --color-text-3: #666666;
  --color-text-4: #999999;
  --color-placeholder: #bfbfbf;

  --color-border: #e8e8e8;
  --color-border-light: #f0f0f0;
  --color-fill: #fafafa;
  --color-bg: #f5f5f5;
  --color-white: #ffffff;

  --radius-sm: 6rpx;
  --radius-md: 10rpx;
  --radius-lg: 14rpx;
  --radius-xl: 20rpx;
  --radius-full: 50%;

  background-color: var(--color-bg);
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', 'PingFang SC', 'Hiragino Sans GB', sans-serif;
  font-size: 28rpx;
  color: var(--color-text-2);
  line-height: 1.5;
}

view, text, image, scroll-view { box-sizing: border-box; }
</style>
