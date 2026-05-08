<template>
  <div class="page-container">
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="轮播图管理" name="banners">
        <div class="tab-header">
          <span class="hint">设置首页轮播图，最多5张</span>
          <el-button type="primary" @click="addBanner">+ 添加</el-button>
        </div>

        <div v-if="!banners.length" class="empty-hint">暂无轮播图，点击上方按钮添加</div>

        <draggable v-else v-model="banners" item-key="id" handle=".drag-handle" animation="200">
          <template #item="{ element: item, index }">
            <div class="banner-item">
              <div class="drag-handle"><el-icon><Rank /></el-icon></div>
              <image :src="item.imageUrl" class="banner-img" />
              <div class="banner-info">
                <input v-model="item.title" placeholder="标题（选填）" />
                <input v-model="item.linkUrl" placeholder="跳转链接（选填）" />
                <el-button type="danger" size="small" link @click="banners.splice(index, 1)">删除</el-button>
              </div>
            </div>
          </template>
        </draggable>

        <el-button type="primary" style="margin-top: 20px" @click="saveBanners" :loading="saving">保存轮播图</el-button>
      </el-tab-pane>

      <el-tab-pane label="公告管理" name="announcements">
        <div class="tab-header">
          <span class="hint">发布平台公告通知</span>
          <el-button type="primary" @click="addAnnouncement">+ 新增公告</el-button>
        </div>

        <el-table :data="announcements" stripe :header-cell-style="{ background:'#fafbfc',color:'#6b7280',fontWeight:'600' }">
          <el-table-column prop="title" label="标题" min-width="180" />
          <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
          <el-table-column prop="createdAt" label="创建时间" width="170" />
          <el-table-column label="操作" width="120">
            <template #default="{ $index }">
              <el-button type="danger" size="small" link @click="announcements.splice($index, 1)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-button type="primary" style="margin-top: 20px" @click="saveAnnouncements" :loading="saving">保存公告</el-button>
      </el-tab-pane>

      <el-tab-pane label="系统配置" name="config">
        <div class="data-card__body" style="max-width: 560px">
          <el-form label-width="120px">
            <el-form-item label="平台名称">
              <el-input v-model="config.appName" placeholder="U Ju 活动管理平台" />
            </el-form-item>
            <el-form-item label="联系邮箱">
              <el-input v-model="config.contactEmail" placeholder="support@uju.com" />
            </el-form-item>
            <el-form-item label="微信AppID">
              <el-input v-model="config.wechatAppId" placeholder="微信小程序 AppID" />
            </el-form-item>
            <el-form-item label="短信签名">
              <el-input v-model="config.smsSignName" placeholder="短信签名名称" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveConfig" :loading="saving">保存配置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { contentApi } from '@/api'
import { ElMessage } from 'element-plus'

const activeTab = ref('banners')
const saving = ref(false)
const banners = ref<any[]>([])
const announcements = ref<any[]>([])
const config = ref({ appName: '', contactEmail: '', wechatAppId: '', smsSignName: '' })

let bannerId = 1
const addBanner = () => {
  if (banners.value.length >= 5) return ElMessage.warning('最多添加5张')
  banners.value.push({ id: ++bannerId, imageUrl: '', title: '', linkUrl: '' })
}

let annId = 1
const addAnnouncement = () => {
  announcements.value.unshift({ id: ++annId, title: '', content: '', createdAt: new Date().toISOString().slice(0, 19).replace('T', ' ') })
}

const saveBanners = async () => {
  saving.value = true
  try { await contentApi.saveBanners(banners.value); ElMessage.success('已保存') }
  catch {} finally { saving.value = false }
}
const saveAnnouncements = async () => {
  saving.value = true
  try { await contentApi.saveAnnouncements(announcements.value); ElMessage.success('已保存') }
  catch {} finally { saving.value = false }
}
const saveConfig = async () => {
  saving.value = true
  try { await contentApi.saveConfig(config.value); ElMessage.success('配置已保存') }
  catch {} finally { saving.value = false }
}

onMounted(async () => {
  try {
    const [bRes, aRes, cRes] = await Promise.all([
      contentApi.getBanners(),
      contentApi.getAnnouncements(),
      contentApi.getConfig(),
    ])
    banners.value = bRes.data || []
    announcements.value = aRes.data || []
    config.value = cRes.data || {}
  } catch {}
})
</script>

<style scoped>
.tab-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.hint { font-size: var(--font-size-sm); color: var(--color-text-secondary); }

.banner-item {
  display: flex; align-items: center; gap: 16px;
  background: var(--color-bg-white); border-radius: var(--radius-md);
  padding: 16px; margin-bottom: 12px;
  border: 2rpx solid var(--color-border-light);
}
.drag-handle { cursor: grab; color: var(--color-text-placeholder); font-size: 18rpx; padding: 8rpx; }
.banner-img { width: 160px; height: 80px; border-radius: var(--radius-sm); object-fit: cover; background: var(--color-fill); flex-shrink: 0; }
.banner-info { flex: 1; display: flex; flex-direction: column; gap: 8px; }
.banner-info input {
  border: 1rpx solid var(--color-border); border-radius: var(--radius-sm);
  padding: 6rpx 12rpx; font-size: var(--font-size-base); outline: none;
}
.banner-info input:focus { border-color: var(--color-primary); }

.empty-hint { text-align: center; padding: 40rpx; color: var(--color-text-secondary); font-size: var(--font-size-base); background: var(--color-bg-white); border-radius: var(--radius-md); border: 2rpx dashed var(--color-border); }
</style>
