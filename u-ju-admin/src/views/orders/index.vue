<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">订单管理</div>
      <el-select v-model="statusFilter" placeholder="全部状态" clearable style="width: 140px" @change="loadData">
        <el-option label="待支付" :value="0" />
        <el-option label="已支付" :value="1" />
        <el-option label="已完成" :value="2" />
        <el-option label="退款中" :value="4" />
      </el-select>
    </div>

    <div class="data-card">
      <el-table :data="tableData" v-loading="loading" stripe :header-cell-style="{ background: '#fafbfc', color: '#6b7280', fontWeight: '600' }">
        <el-table-column prop="id" label="订单号" width="80" />
        <el-table-column prop="eventName" label="活动名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="buyerPhone" label="买家手机号" min-width="130" />
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }"><span style="font-weight: 600; color: var(--color-danger)">{{ row.amount }}</span></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }"><el-tag :type="(['warning','info','success','info','danger','warning'] as const)[row.status]" size="small">{{ ['待支付','已支付','已完成','已取消','退款中','已退款'][row.status] }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="下单时间" width="170" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 4" type="primary" size="small" link @click="handleApproveRefund(row.id)">审批退款</el-button>
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
    const res: any = await adminApi.getOrders({ page: page.value, size: size.value, status: statusFilter.value })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch {} finally { loading.value = false }
}

const handleApproveRefund = async (id: number) => {
  await ElMessageBox.confirm('确认批准退款？', '退款审批', { type: 'warning' })
  try { await adminApi.approveRefund(id); ElMessage.success('退款已处理'); loadData() } catch {}
}

onMounted(loadData)
</script>
