<template>
  <div class="points-page">
    <h2 class="page-title">积分商城</h2>

    <!-- User points -->
    <div class="points-header">
      <div class="points-badge">
        <span class="points-label">我的积分</span>
        <span class="points-value">{{ userPoints }}</span>
      </div>
    </div>

    <!-- Tabs -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- Goods Tab -->
      <el-tab-pane label="积分商品" name="goods">
        <div v-if="!goodsLoading" class="goods-grid">
          <el-empty v-if="goodsList.length === 0" description="暂无商品" />
          <div
            v-for="goods in goodsList"
            :key="goods.id"
            class="goods-card"
          >
            <img :src="goods.image" :alt="goods.name" class="goods-image" />
            <div class="goods-info">
              <div class="goods-name">{{ goods.name }}</div>
              <div class="goods-points">
                <span class="points-num">{{ goods.points }}</span> 积分
              </div>
              <div class="goods-stock">库存：{{ goods.stock }}</div>
            </div>
            <el-button
              type="warning"
              size="small"
              :disabled="goods.stock <= 0"
              @click="handleExchange(goods)"
            >
              {{ goods.stock <= 0 ? '已售罄' : '立即兑换' }}
            </el-button>
          </div>
        </div>
        <div v-else class="loading-state">
          <el-skeleton :rows="4" animated />
        </div>
      </el-tab-pane>

      <!-- Exchange Records Tab -->
      <el-tab-pane label="兑换记录" name="exchanges">
        <el-table :data="exchangeList" stripe size="small" v-loading="exchangeLoading">
          <el-table-column prop="goodsName" label="商品名称" min-width="150" />
          <el-table-column prop="points" label="消耗积分" width="100" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                {{ row.status === 1 ? '已完成' : '处理中' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="兑换时间" width="180" />
        </el-table>
        <el-empty v-if="!exchangeLoading && exchangeList.length === 0" description="暂无兑换记录" />
      </el-tab-pane>

      <!-- Points Records Tab -->
      <el-tab-pane label="积分记录" name="records">
        <el-table :data="recordList" stripe size="small" v-loading="recordLoading">
          <el-table-column label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="row.type === 1 ? 'success' : 'warning'" size="small">
                {{ row.type === 1 ? '获取' : '消耗' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="points" label="积分" width="100">
            <template #default="{ row }">
              <span :class="row.type === 1 ? 'plus-points' : 'minus-points'">
                {{ row.type === 1 ? '+' : '-' }}{{ row.points }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="描述" min-width="180" />
          <el-table-column prop="createTime" label="时间" width="180" />
        </el-table>
        <el-empty v-if="!recordLoading && recordList.length === 0" description="暂无积分记录" />
      </el-tab-pane>
    </el-tabs>

    <!-- Exchange confirmation dialog -->
    <el-dialog v-model="exchangeDialogVisible" title="确认兑换" width="420px">
      <div class="exchange-confirm">
        <p>商品：<b>{{ currentGoods?.name }}</b></p>
        <p>消耗积分：<b class="points-num">{{ currentGoods?.points }}</b></p>
        <el-form-item label="兑换数量">
          <el-input-number
            v-model="exchangeQuantity"
            :min="1"
            :max="currentGoods?.stock || 1"
            size="small"
          />
        </el-form-item>
      </div>
      <template #footer>
        <el-button @click="exchangeDialogVisible = false">取消</el-button>
        <el-button type="warning" @click="submitExchange" :loading="exchangeSubmitting">
          确认兑换
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

const activeTab = ref('goods')
const userPoints = ref(0)

// Goods
const goodsList = ref([])
const goodsLoading = ref(false)

// Exchange records
const exchangeList = ref([])
const exchangeLoading = ref(false)

// Points records
const recordList = ref([])
const recordLoading = ref(false)

// Exchange dialog
const exchangeDialogVisible = ref(false)
const currentGoods = ref(null)
const exchangeQuantity = ref(1)
const exchangeSubmitting = ref(false)

async function fetchUserPoints() {
  try {
    const res = await request.get('/auth/userinfo')
    userPoints.value = res.data?.points ?? 0
  } catch (err) {
    // Silently fail, points will show as 0
  }
}

async function fetchGoods() {
  goodsLoading.value = true
  try {
    const res = await request.get('/client/points/goods')
    goodsList.value = res.data || []
  } catch (err) {
    ElMessage.error('获取商品列表失败')
  } finally {
    goodsLoading.value = false
  }
}

async function fetchExchanges() {
  exchangeLoading.value = true
  try {
    const res = await request.get('/client/points/exchanges')
    exchangeList.value = res.data || []
  } catch (err) {
    ElMessage.error('获取兑换记录失败')
  } finally {
    exchangeLoading.value = false
  }
}

async function fetchRecords() {
  recordLoading.value = true
  try {
    const res = await request.get('/client/points/records')
    recordList.value = res.data || []
  } catch (err) {
    ElMessage.error('获取积分记录失败')
  } finally {
    recordLoading.value = false
  }
}

function handleTabChange(name) {
  if (name === 'goods') fetchGoods()
  if (name === 'exchanges') fetchExchanges()
  if (name === 'records') fetchRecords()
}

function handleExchange(goods) {
  currentGoods.value = goods
  exchangeQuantity.value = 1
  exchangeDialogVisible.value = true
}

async function submitExchange() {
  exchangeSubmitting.value = true
  try {
    await request.post('/client/points/exchange', {
      goodsId: currentGoods.value.id,
      quantity: exchangeQuantity.value
    })
    ElMessage.success('兑换成功')
    exchangeDialogVisible.value = false
    fetchUserPoints()
    fetchGoods()
  } catch (err) {
    ElMessage.error('兑换失败')
  } finally {
    exchangeSubmitting.value = false
  }
}

onMounted(() => {
  fetchUserPoints()
  fetchGoods()
})
</script>

<style scoped>
.points-page {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}
.page-title {
  margin-bottom: 20px;
  font-size: 22px;
}
.points-header {
  margin-bottom: 20px;
}
.points-badge {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  background: linear-gradient(135deg, #f5a623, #f7b84e);
  color: #fff;
  padding: 14px 28px;
  border-radius: 12px;
}
.points-label {
  font-size: 14px;
  opacity: 0.9;
}
.points-value {
  font-size: 28px;
  font-weight: 700;
}
.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}
.goods-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  transition: box-shadow 0.2s;
}
.goods-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}
.goods-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 12px;
}
.goods-info {
  text-align: center;
  margin-bottom: 12px;
  width: 100%;
}
.goods-name {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 6px;
}
.goods-points {
  color: #f5a623;
  font-size: 13px;
  margin-bottom: 4px;
}
.points-num {
  font-size: 18px;
  font-weight: 700;
}
.goods-stock {
  font-size: 12px;
  color: #909399;
}
.plus-points {
  color: #67c23a;
  font-weight: 500;
}
.minus-points {
  color: #f56c6c;
  font-weight: 500;
}
.exchange-confirm p {
  margin-bottom: 12px;
  font-size: 15px;
}
.loading-state {
  padding: 20px;
}
</style>
