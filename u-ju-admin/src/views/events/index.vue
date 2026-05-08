<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">活动管理</div>
      <el-select v-model="statusFilter" placeholder="全部状态" clearable style="width: 140px" @change="loadData">
        <el-option label="待审核" :value="0" />
        <el-option label="已通过" :value="1" />
        <el-option label="已拒绝" :value="2" />
      </el-select>
    </div>

    <div class="data-card">
      <el-table :data="tableData" v-loading="loading" stripe :header-cell-style="{ background: '#fafbfc', color: '#6b7280', fontWeight: '600' }">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="活动名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="organizerName" label="主办方" min-width="120" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="(['info','success','danger'] as const)[row.status]" size="small">{{ ['待审核','已通过','已拒绝'][row.status] }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button type="success" size="small" link @click="handleReview(row.id, 1)">通过</el-button>
              <el-button type="danger" size="small" link @click="handleReview(row.id, 2)">拒绝</el-button>
            </template>
          </template>
        </el-table-column>
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
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const statusFilter = ref<number | undefined>(undefined)

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await adminApi.getEvents({ page: page.value, size: size.value, status: statusFilter.value })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch {} finally { loading.value = false }
}

const handleReview = async (id: number, status: number) => {
  const action = status === 1 ? '通过' : '拒绝'
  await ElMessageBox.confirm(`确认${action}该活动？`, '审核操作', { type: 'warning' })
  try { await adminApi.reviewEvent(id, status); ElMessage.success(`${action}成功`); loadData() } catch {}
}

onMounted(loadData)
</script>
