<template>
  <div class="order-page">
    <!-- Status Filter Tabs -->
    <el-card class="filter-card">
      <el-tabs v-model="activeStatus" @tab-change="handleStatusChange">
        <el-tab-pane
          v-for="tab in statusTabs"
          :key="tab.value"
          :label="tab.label"
          :name="tab.value"
        />
      </el-tabs>
    </el-card>

    <!-- Order Table -->
    <el-card class="table-card">
      <el-table
        :data="orderList"
        v-loading="loading"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="orderNo" label="订单号" min-width="180" show-overflow-tooltip />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column label="总金额" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ (row.totalPrice / 100).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="锅底类型" width="120">
          <template #default="{ row }">
            {{ potTypeMap[row.potType] || row.potType }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusStyle(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="tableNo" label="桌号" width="80" />
        <el-table-column label="下单时间" width="180">
          <template #default="{ row }">
            {{ row.createTime }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">
              详情
            </el-button>
            <el-button type="warning" link size="small" @click="handleStatusUpdate(row)">
              状态更新
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="fetchOrders"
          @size-change="fetchOrders"
        />
      </div>
    </el-card>

    <!-- Detail Dialog -->
    <el-dialog
      v-model="detailVisible"
      title="订单详情"
      width="800px"
      destroy-on-close
    >
      <template v-if="orderDetail">
        <!-- Order Info -->
        <el-descriptions :column="2" border class="detail-section">
          <el-descriptions-item label="订单号">{{ orderDetail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ orderDetail.userId }}</el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ (orderDetail.totalPrice / 100).toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="锅底类型">{{ potTypeMap[orderDetail.potType] || orderDetail.potType }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusStyle(orderDetail.status)" size="small">
              {{ getStatusLabel(orderDetail.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="桌号">{{ orderDetail.tableNo }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ orderDetail.createTime }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ orderDetail.remark || '无' }}</el-descriptions-item>
        </el-descriptions>

        <!-- Order Items -->
        <h4 class="section-title">菜品明细</h4>
        <el-table :data="orderDetail.items || []" border size="small">
          <el-table-column prop="dishName" label="菜品名称" min-width="140" />
          <el-table-column prop="price" label="单价" width="100">
            <template #default="{ row }">¥{{ (row.dishPrice / 100).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column label="小计" width="100">
            <template #default="{ row }">¥{{ ((row.dishPrice * row.quantity) / 100).toFixed(2) }}</template>
          </el-table-column>
        </el-table>

        <!-- Status Timeline -->
        <h4 class="section-title">状态变更记录</h4>
        <el-timeline v-if="orderDetail.statusLogs && orderDetail.statusLogs.length">
          <el-timeline-item
            v-for="log in orderDetail.statusLogs"
            :key="log.id"
            :timestamp="log.createTime"
            :type="getTimelineType(log.toStatus)"
          >
            {{ getStatusLabel(log.toStatus) }}
            <span v-if="log.remark"> — {{ log.remark }}</span>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无状态变更记录" :image-size="60" />
      </template>
    </el-dialog>

    <!-- Status Update Dialog -->
    <el-dialog
      v-model="statusVisible"
      title="状态更新"
      width="500px"
      destroy-on-close
    >
      <el-form ref="statusFormRef" :model="statusForm" :rules="statusRules" label-width="80px">
        <el-form-item label="当前状态">
          <el-tag :type="getStatusStyle(currentOrder?.status)" size="small">
            {{ getStatusLabel(currentOrder?.status) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="新状态" prop="status">
          <el-select v-model="statusForm.status" placeholder="请选择新状态" style="width: 100%">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="statusForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入状态变更备注（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusVisible = false">取消</el-button>
        <el-button type="primary" :loading="statusSubmitting" @click="submitStatusUpdate">
          确认更新
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

// ---- Status Tab Configuration ----
const statusTabs = [
  { label: '全部', value: 'all' },
  { label: '待支付', value: 0 },
  { label: '已支付', value: 1 },
  { label: '制作中', value: 2 },
  { label: '已上菜', value: 3 },
  { label: '已完成', value: 4 },
  { label: '已取消', value: -1 }
]

// ---- Status Mapping ----
const statusMap = {
  0:  { label: '待支付', style: '' },        // grey/default
  1:  { label: '已支付', style: 'info' },
  2:  { label: '制作中', style: 'warning' },
  3:  { label: '已上菜', style: 'primary' },
  4:  { label: '已完成', style: 'success' },
  '-1': { label: '已取消', style: 'danger' }
}

// ---- Pot Type Mapping ----
const potTypeMap = {
  single: '单锅',
  double: '鸳鸯',
  nine: '九宫格'
}

// ---- Reactive State ----
const loading = ref(false)
const activeStatus = ref('all')
const orderList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// Detail
const detailVisible = ref(false)
const orderDetail = ref(null)
const detailLoading = ref(false)

// Status Update
const statusVisible = ref(false)
const currentOrder = ref(null)
const statusSubmitting = ref(false)
const statusFormRef = ref(null)
const statusForm = reactive({
  status: '',
  remark: ''
})
const statusRules = {
  status: [{ required: true, message: '请选择新状态', trigger: 'change' }]
}

// Status options for the dialog (exclude current status)
const statusOptions = computed(() => {
  return [
    { label: '待支付(0)', value: 0 },
    { label: '已支付(1)', value: 1 },
    { label: '制作中(2)', value: 2 },
    { label: '已上菜(3)', value: 3 },
    { label: '已完成(4)', value: 4 },
    { label: '已取消(-1)', value: -1 }
  ]
})

// ---- Helpers ----
const getStatusLabel = (status) => {
  return statusMap[status]?.label || '未知'
}

const getStatusStyle = (status) => {
  return statusMap[status]?.style || 'info'
}

const getTimelineType = (status) => {
  const map = { 0: 'info', 1: 'info', 2: 'warning', 3: 'primary', 4: 'success', '-1': 'danger' }
  return map[status] || 'info'
}

// ---- API Calls ----
const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    if (activeStatus.value !== 'all') {
      params.status = activeStatus.value
    }
    const res = await request.get('/admin/order/list', { params })
    orderList.value = res.data || []
    total.value = orderList.value.length
  } catch (error) {
    ElMessage.error(error.message || '获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const fetchOrderDetail = async (id) => {
  detailLoading.value = true
  try {
    const res = await request.get(`/admin/order/detail/${id}`)
    orderDetail.value = res.data
  } catch (error) {
    ElMessage.error(error.message || '获取订单详情失败')
  } finally {
    detailLoading.value = false
  }
}

// ---- Event Handlers ----
const handleStatusChange = () => {
  currentPage.value = 1
  fetchOrders()
}

const handleDetail = async (row) => {
  detailVisible.value = true
  await fetchOrderDetail(row.id)
}

const handleStatusUpdate = (row) => {
  currentOrder.value = row
  statusForm.status = ''
  statusForm.remark = ''
  statusVisible.value = true
}

const submitStatusUpdate = async () => {
  const valid = await statusFormRef.value.validate().catch(() => false)
  if (!valid) return

  statusSubmitting.value = true
  try {
    await request.put(`/admin/order/status/${currentOrder.value.id}`, {
      status: statusForm.status,
      remark: statusForm.remark
    })
    ElMessage.success('状态更新成功')
    statusVisible.value = false
    fetchOrders()
  } catch (error) {
    ElMessage.error(error.message || '状态更新失败')
  } finally {
    statusSubmitting.value = false
  }
}

// ---- Lifecycle ----
onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-card :deep(.el-card__body) {
  padding: 0 20px;
}

.table-card {
  flex: 1;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.price {
  color: #e6a23c;
  font-weight: 500;
}

.detail-section {
  margin-bottom: 20px;
}

.section-title {
  margin: 20px 0 12px;
  padding-left: 10px;
  border-left: 3px solid #409eff;
  font-size: 15px;
  color: #303133;
}

.dark .section-title {
  color: #e0e0e0;
}
</style>
