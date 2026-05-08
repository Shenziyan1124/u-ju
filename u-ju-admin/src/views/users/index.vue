<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">用户管理</div>
      <el-input v-model="keyword" placeholder="搜索手机号/用户名" clearable style="width: 240px" @clear="loadData" @keyup.enter="loadData">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>

    <div class="data-card">
      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%" :header-cell-style="{ background: '#fafbfc', color: '#6b7280', fontWeight: '600' }">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }"><el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'" size="small">{{ row.role }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="(val: any) => handleToggleStatus(row.id, val ? 1 : 0)" />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" min-width="170" />
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="total, prev, pager, next" @change="loadData" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await adminApi.getUsers({ page: page.value, size: size.value, keyword: keyword.value })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch {} finally { loading.value = false }
}

const handleToggleStatus = async (id: number, status: number) => {
  try {
    await adminApi.updateUserStatus(id, status)
    ElMessage.success('操作成功')
    loadData()
  } catch {}
}

onMounted(loadData)
</script>
