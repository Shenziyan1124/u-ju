<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">认证审核</div>
      <el-select v-model="statusFilter" placeholder="全部状态" clearable style="width: 140px" @change="loadData">
        <el-option label="待审核" :value="0" />
        <el-option label="已通过" :value="1" />
        <el-option label="已拒绝" :value="2" />
      </el-select>
    </div>

    <div class="data-card">
      <el-table :data="tableData" v-loading="loading" stripe :header-cell-style="{ background: '#fafbfc', color: '#6b7280', fontWeight: '600' }">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="companyName" label="企业名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="contactPerson" label="联系人" min-width="110" />
        <el-table-column prop="contactPhone" label="联系电话" width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="(['warning','success','danger'] as const)[row.status]" size="small">{{ ['待审核','已通过','已拒绝'][row.status] }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="提交时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button type="primary" size="small" link @click="handleReview(row.id, 1)">通过</el-button>
              <el-button type="danger" size="small" link @click="showRejectDialog(row)">拒绝</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="total, prev, pager, next" @change="loadData" />
      </div>
    </div>

    <el-dialog v-model="rejectVisible" title="拒绝认证" width="420px" :close-on-click-modal="false">
      <el-input v-model="remark" type="textarea" :rows="3" placeholder="请输入拒绝原因（选填）" />
      <template #footer><el-button @click="rejectVisible = false">取消</el-button><el-button type="danger" @click="confirmReject">确认拒绝</el-button></template>
    </el-dialog>
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
const rejectVisible = ref(false)
const remark = ref('')
let currentId = 0

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await adminApi.getCertifications({ page: page.value, size: size.value, status: statusFilter.value })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch {} finally { loading.value = false }
}

const handleReview = async (id: number, status: number) => {
  try { await adminApi.reviewCertification(id, status); ElMessage.success('操作成功'); loadData() } catch {}
}
const showRejectDialog = (row: any) => { currentId = row.id; remark.value = ''; rejectVisible.value = true }
const confirmReject = async () => {
  try { await adminApi.reviewCertification(currentId, 2, remark.value); ElMessage.success('已拒绝'); rejectVisible.value = false; loadData() } catch {}
}

onMounted(loadData)
</script>
