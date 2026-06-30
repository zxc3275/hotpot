<template>
  <div class="order-list-page">
    <h2 class="page-title">我的订单</h2>

    <!-- Status filter -->
    <div class="filter-bar">
      <el-radio-group v-model="statusFilter" @change="fetchOrders">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="0">待支付</el-radio-button>
        <el-radio-button label="1">已支付</el-radio-button>
        <el-radio-button label="2">制作中</el-radio-button>
        <el-radio-button label="3">已上菜</el-radio-button>
        <el-radio-button label="4">已完成</el-radio-button>
        <el-radio-button label="-1">已取消</el-radio-button>
      </el-radio-group>
    </div>

    <!-- Order cards -->
    <div v-if="!loading" class="order-cards">
      <el-empty v-if="orderList.length === 0" description="暂无订单" />

      <div
        v-for="order in orderList"
        :key="order.id"
        class="order-card"
        @click="goDetail(order.id)"
      >
        <div class="card-header">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <el-tag :type="statusTagType(order.status)" size="small">
            {{ statusLabel(order.status) }}
          </el-tag>
        </div>
        <div class="card-body">
          <div class="card-info">
            <span>总价：<b class="price">{{ (order.totalPrice / 100).toFixed(2) }} 元</b></span>
            <span v-if="order.potType">锅底：{{ order.potType }}</span>
            <span v-if="order.tableNo">桌号：{{ order.tableNo }}</span>
            <span class="time">{{ order.createTime }}</span>
          </div>
        </div>
        <div class="card-actions" @click.stop>
          <el-button
            v-if="order.status === 0"
            type="danger"
            size="small"
            @click="handlePay(order)"
          >
            支付
          </el-button>
          <el-button
            v-if="order.status === 0"
            type="default"
            size="small"
            @click="handleCancel(order)"
          >
            取消订单
          </el-button>
          <el-button
            v-if="order.status === 3"
            type="success"
            size="small"
            @click="handleConfirm(order)"
          >
            确认已上菜
          </el-button>
        </div>
      </div>
    </div>

    <div v-else class="loading-state">
      <el-skeleton :rows="4" animated />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'
import { useRouter } from 'vue-router'

const router = useRouter()

const orderList = ref([])
const loading = ref(false)
const statusFilter = ref('')

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

async function fetchOrders() {
  loading.value = true
  try {
    const params = {}
    if (statusFilter.value !== '') {
      params.status = statusFilter.value
    }
    const res = await request.get('/client/order/list', { params })
    orderList.value = res.data || []
  } catch (err) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

function goDetail(id) {
  router.push(`/order/${id}`)
}

async function handlePay(order) {
  try {
    await ElMessageBox.confirm('确认支付该订单？', '支付确认', { type: 'warning' })
    await request.post(`/client/order/pay/${order.id}`)
    ElMessage.success('支付成功')
    fetchOrders()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('支付失败')
    }
  }
}

async function handleCancel(order) {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '取消确认', { type: 'warning' })
    await request.post(`/client/order/cancel/${order.id}`)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

async function handleConfirm(order) {
  try {
    await ElMessageBox.confirm('确认已收到菜品？', '确认', { type: 'warning' })
    await request.post(`/client/order/confirm/${order.id}`)
    ElMessage.success('确认成功')
    fetchOrders()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('确认失败')
    }
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-list-page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}
.page-title {
  margin-bottom: 20px;
  font-size: 22px;
}
.filter-bar {
  margin-bottom: 20px;
}
.order-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.order-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  padding: 16px 20px;
  cursor: pointer;
  transition: box-shadow 0.2s;
}
.order-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.order-no {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}
.card-body {
  margin-bottom: 10px;
}
.card-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 13px;
  color: #606266;
}
.card-info .price {
  color: #e74c3c;
  font-size: 15px;
}
.card-info .time {
  color: #909399;
}
.card-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
.loading-state {
  padding: 20px;
}
</style>
