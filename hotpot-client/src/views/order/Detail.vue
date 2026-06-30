<template>
  <div class="order-detail-page">
    <div class="header-bar">
      <el-button :icon="ArrowLeft" @click="$router.back()">返回</el-button>
      <h2 class="page-title">订单详情</h2>
    </div>

    <div v-if="!loading && order">
      <!-- Order info -->
      <el-card class="info-card">
        <template #header>
          <div class="card-header-row">
            <span>订单信息</span>
            <el-tag :type="statusTagType(order.status)">
              {{ statusLabel(order.status) }}
            </el-tag>
          </div>
        </template>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="订单编号">{{ order.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="statusTagType(order.status)" size="small">
              {{ statusLabel(order.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="总价">
            <span class="price">{{ (order.totalPrice / 100).toFixed(2) }} 元</span>
          </el-descriptions-item>
          <el-descriptions-item label="锅底">{{ order.potType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="辣度">{{ order.spicyLevel ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="桌号">{{ order.tableNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="下单时间" :span="2">{{ order.createTime }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- Order items -->
      <el-card class="items-card">
        <template #header>
          <span>菜品明细</span>
        </template>
        <el-table :data="order.items || order.orderItems || []" stripe size="small">
          <el-table-column prop="dishName" label="菜品名称" min-width="150" />
          <el-table-column label="单价" width="120">
            <template #default="{ row }">
              {{ (row.dishPrice / 100).toFixed(2) }} 元
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column label="小计" width="120">
            <template #default="{ row }">
              <span class="price">{{ ((row.dishPrice * row.quantity) / 100).toFixed(2) }} 元</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- Status timeline -->
      <el-card class="timeline-card">
        <template #header>
          <span>状态跟踪</span>
        </template>
        <el-steps :active="statusStep(order.status)" align-center>
          <el-step title="已下单" :description="order.createTime" />
          <el-step title="已支付" />
          <el-step title="制作中" />
          <el-step title="已上菜" />
          <el-step title="已完成" />
        </el-steps>
      </el-card>

      <!-- Actions -->
      <div class="detail-actions">
        <el-button
          v-if="order.status === 0"
          type="danger"
          @click="handlePay"
          :loading="actionLoading"
        >
          支付订单
        </el-button>
        <el-button
          v-if="order.status === 0"
          @click="handleCancel"
          :loading="actionLoading"
        >
          取消订单
        </el-button>
        <el-button
          v-if="order.status === 3"
          type="success"
          @click="handleConfirm"
          :loading="actionLoading"
        >
          确认已上菜
        </el-button>
      </div>
    </div>

    <div v-else-if="loading" class="loading-state">
      <el-skeleton :rows="8" animated />
    </div>

    <el-empty v-else description="订单不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import request from '@/api/request'
import { useRoute } from 'vue-router'

const route = useRoute()

const order = ref(null)
const loading = ref(false)
const actionLoading = ref(false)

const statusMap = {
  0: '待支付',
  1: '已支付',
  2: '制作中',
  3: '已上菜',
  4: '已完成',
  '-1': '已取消'
}

function statusLabel(status) {
  return statusMap[status] || '未知'
}

function statusTagType(status) {
  const map = {
    0: 'warning',
    1: 'success',
    2: 'primary',
    3: '',
    4: 'success',
    '-1': 'info'
  }
  return map[status] || 'info'
}

function statusStep(status) {
  // Map status to active step index (0-based)
  if (status === -1) return 0
  return Math.min(status, 4)
}

async function fetchDetail() {
  const id = route.params.id
  loading.value = true
  try {
    const res = await request.get(`/client/order/detail/${id}`)
    order.value = res.data
  } catch (err) {
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

async function handlePay() {
  try {
    await ElMessageBox.confirm('确认支付该订单？', '支付确认', { type: 'warning' })
    actionLoading.value = true
    await request.post(`/client/order/pay/${order.value.id}`)
    ElMessage.success('支付成功')
    fetchDetail()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('支付失败')
    }
  } finally {
    actionLoading.value = false
  }
}

async function handleCancel() {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '取消确认', { type: 'warning' })
    actionLoading.value = true
    await request.post(`/client/order/cancel/${order.value.id}`)
    ElMessage.success('订单已取消')
    fetchDetail()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('取消失败')
    }
  } finally {
    actionLoading.value = false
  }
}

async function handleConfirm() {
  try {
    await ElMessageBox.confirm('确认已收到菜品？', '确认', { type: 'warning' })
    actionLoading.value = true
    await request.post(`/client/order/confirm/${order.value.id}`)
    ElMessage.success('确认成功')
    fetchDetail()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('确认失败')
    }
  } finally {
    actionLoading.value = false
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.order-detail-page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}
.header-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}
.page-title {
  margin: 0;
  font-size: 22px;
}
.info-card,
.items-card,
.timeline-card {
  margin-bottom: 16px;
}
.card-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price {
  color: #e74c3c;
  font-weight: 600;
}
.detail-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  padding: 16px 0;
}
.loading-state {
  padding: 20px;
}
</style>
