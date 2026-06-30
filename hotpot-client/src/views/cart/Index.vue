<template>
  <div class="cart-page">
    <h2 class="page-title">购物车</h2>

    <!-- Empty cart -->
    <el-empty v-if="!loading && cartList.length === 0 && !selectedBase" description="购物车是空的，快去选购吧" />

    <!-- Cart items -->
    <div v-if="cartList.length > 0 || selectedBase" class="cart-content">
      <!-- Selected Base -->
      <div v-if="selectedBase" class="base-section">
        <div class="section-title">🍲 已选锅底</div>
        <div class="cart-item base-cart-item">
          <img :src="selectedBase.image" :alt="selectedBase.name" class="item-image" />
          <div class="item-info">
            <div class="item-name">{{ selectedBase.name }}</div>
            <div class="base-config-tags">
              <el-tag size="small" type="danger">{{ potType }}</el-tag>
              <el-tag size="small">辣度 {{ spicyLevel }}</el-tag>
              <el-tag size="small">麻度 {{ numLevel }}</el-tag>
            </div>
            <div class="item-price">{{ (selectedBase.price / 100).toFixed(2) }} 元</div>
          </div>
          <el-button size="small" @click="changeBase">更换锅底</el-button>
          <el-button size="small" type="danger" :icon="Delete" circle @click="removeBase" />
        </div>
      </div>

      <!-- Dish Section -->
      <div v-if="cartList.length > 0" class="section-title">🍽 已选菜品</div>
      <div class="cart-items">
        <div
          v-for="item in cartList"
          :key="item.id"
          class="cart-item"
        >
          <el-checkbox
            v-model="item.checked"
            class="item-checkbox"
            @change="onCheckChange"
          />
          <img :src="item.dishImage" :alt="item.dishName" class="item-image" />
          <div class="item-info">
            <div class="item-name">{{ item.dishName }}</div>
            <div class="item-price">{{ ((item.dishPrice || 0) / 100).toFixed(2) }} 元</div>
          </div>
          <div class="item-quantity">
            <el-input-number
              v-model="item.quantity"
              :min="1"
              :max="99"
              size="small"
              @change="handleQuantityChange(item)"
            />
          </div>
          <div class="item-subtotal">{{ ((item.dishPrice * item.quantity) / 100).toFixed(2) }} 元</div>
          <el-button
            type="danger"
            size="small"
            :icon="Delete"
            circle
            @click="handleDeleteItem(item)"
          />
        </div>
      </div>

      <!-- Select all -->
      <div class="select-all-bar">
        <el-checkbox
          v-model="checkAll"
          :indeterminate="isIndeterminate"
          @change="handleCheckAll"
        >
          全选
        </el-checkbox>
        <el-button type="danger" size="small" plain @click="handleBatchDelete">
          删除选中 ({{ checkedCount }})
        </el-button>
      </div>

      <!-- Bottom bar -->
      <div class="bottom-bar">
        <div class="bottom-left">
          已选 <span class="count">{{ checkedCount }}</span> 件商品
        </div>
        <div class="bottom-right">
          <span class="total-label">合计：</span>
          <span class="total-price">{{ totalPrice.toFixed(2) }} 元</span>
          <el-button type="danger" size="large" @click="handleSettle">
            去结算
          </el-button>
        </div>
      </div>
    </div>

    <!-- Settlement Dialog -->
    <el-dialog v-model="settleDialogVisible" title="确认订单" width="520px">
      <el-form label-width="100px">
        <el-form-item label="锅底">
          <el-select v-model="settleForm.baseId" placeholder="请选择锅底（可选）" clearable>
            <el-option
              v-for="base in baseList"
              :key="base.id"
              :label="base.name"
              :value="base.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="辣度">
          <div class="spicy-slider">
            <span class="spicy-label">不辣</span>
            <el-slider v-model="settleForm.spicyLevel" :min="0" :max="5" style="flex:1;margin:0 12px" />
            <span class="spicy-label">特辣</span>
          </div>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="settleForm.remark"
            type="textarea"
            :rows="3"
            placeholder="有什么特殊要求吗？"
          />
        </el-form-item>
        <el-form-item label="桌号">
          <el-input
            v-model="settleForm.tableNo"
            placeholder="请输入桌号"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="settleDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="submitOrder" :loading="submitting">
          确认下单
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import request from '@/api/request'
import { useRouter } from 'vue-router'

const router = useRouter()

const cartList = ref([])
const loading = ref(false)

const selectedBase = ref(null)
const spicyLevel = ref(parseInt(sessionStorage.getItem('spicyLevel')) || 2)
const numLevel = ref(parseInt(sessionStorage.getItem('numLevel')) || 2)
const potType = ref(sessionStorage.getItem('potType') || '')

const baseList = ref([])

const settleDialogVisible = ref(false)
const submitting = ref(false)

const settleForm = reactive({
  baseId: parseInt(sessionStorage.getItem('baseId')) || null,
  spicyLevel: parseInt(sessionStorage.getItem('spicyLevel')) || 2,
  remark: '',
  tableNo: sessionStorage.getItem('tableNo') || ''
})

// Computed
const checkedCount = computed(() => cartList.value.filter(item => item.checked).length)

const totalPrice = computed(() => {
  let basePrice = selectedBase.value ? (selectedBase.value.price || 0) / 100 : 0
  let dishTotal = cartList.value
    .filter(item => item.checked)
    .reduce((sum, item) => sum + ((item.dishPrice || 0) * (item.quantity || 0)) / 100, 0)
  return basePrice + dishTotal
})

const checkAll = computed({
  get() {
    return cartList.value.length > 0 && cartList.value.every(item => item.checked)
  },
  set(val) {
    cartList.value.forEach(item => { item.checked = val })
  }
})

const isIndeterminate = computed(() => {
  const checked = checkedCount.value
  return checked > 0 && checked < cartList.value.length
})

// Methods
async function fetchCartList() {
  loading.value = true
  try {
    const res = await request.get('/client/cart/list')
    cartList.value = (res.data || []).map(item => ({
      ...item,
      checked: false
    }))
  } catch (err) {
    ElMessage.error('获取购物车列表失败')
  } finally {
    loading.value = false
  }
}

function handleCheckAll(val) {
  cartList.value.forEach(item => { item.checked = val })
}

function onCheckChange() {
  // Trigger reactivity
}

async function handleQuantityChange(item) {
  try {
    await request.put(`/client/cart/update/${item.id}`, { quantity: item.quantity })
  } catch (err) {
    ElMessage.error('更新数量失败')
    fetchCartList()
  }
}

async function handleDeleteItem(item) {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/client/cart/remove/${item.id}`)
    ElMessage.success('删除成功')
    fetchCartList()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

async function handleBatchDelete() {
  if (checkedCount.value === 0) {
    ElMessage.warning('请先选择商品')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${checkedCount.value} 件商品吗？`, '提示', {
      type: 'warning'
    })
    const ids = cartList.value.filter(item => item.checked).map(item => item.id)
    await request.post('/client/cart/remove-batch', { ids })
    ElMessage.success('删除成功')
    fetchCartList()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

function handleSettle() {
  if (checkedCount.value === 0) {
    ElMessage.warning('请先选择要结算的商品')
    return
  }
  settleDialogVisible.value = true
}

async function submitOrder() {
  submitting.value = true
  try {
    const checkedItems = cartList.value.filter(item => item.checked)
    const params = {
      items: checkedItems.map(item => ({
        dishId: item.dishId,
        quantity: item.quantity
      })),
      baseId: settleForm.baseId || sessionStorage.getItem('baseId') || null,
      spicyLevel: settleForm.spicyLevel,
      numLevel: parseInt(sessionStorage.getItem('numLevel')) || 2,
      potType: sessionStorage.getItem('potType') || 'single',
      remark: settleForm.remark,
      tableNo: settleForm.tableNo
    }
    await request.post('/client/order/create', params)
    ElMessage.success('下单成功')
    settleDialogVisible.value = false
    router.push('/orders')
  } catch (err) {
    ElMessage.error('下单失败')
  } finally {
    submitting.value = false
  }
}

// ==================== Base Methods ====================
async function fetchBaseInfo() {
  const baseId = sessionStorage.getItem('baseId')
  if (!baseId) return
  try {
    const res = await request.get('/client/base/list')
    const allBases = res.data || []
    selectedBase.value = allBases.find(b => b.id === parseInt(baseId)) || null
  } catch { /* ignore */ }
}

async function fetchBaseList() {
  try {
    const res = await request.get('/client/base/list')
    baseList.value = res.data || []
  } catch { /* ignore */ }
}

function changeBase() {
  router.push('/base-select')
}

function removeBase() {
  selectedBase.value = null
  sessionStorage.removeItem('baseId')
  sessionStorage.removeItem('potType')
  sessionStorage.removeItem('spicyLevel')
  sessionStorage.removeItem('numLevel')
  settleForm.baseId = null
  settleForm.spicyLevel = 2
  ElMessage.success('已移除锅底')
}

onMounted(() => {
  fetchCartList()
  fetchBaseInfo()
  fetchBaseList()
})
</script>

<style scoped>
.cart-page {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}
.page-title {
  margin-bottom: 20px;
  font-size: 22px;
}
.cart-content {
  display: flex;
  flex-direction: column;
  gap: 0;
}
.cart-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.cart-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}
.item-checkbox {
  flex-shrink: 0;
}
.item-image {
  width: 64px;
  height: 64px;
  object-fit: cover;
  border-radius: 6px;
  flex-shrink: 0;
}
.item-info {
  flex: 1;
  min-width: 0;
}
.item-name {
  font-size: 15px;
  font-weight: 500;
}
.item-price {
  color: #e74c3c;
  font-size: 13px;
  margin-top: 4px;
}
.item-quantity {
  flex-shrink: 0;
}
.item-subtotal {
  font-size: 15px;
  font-weight: 500;
  color: #e74c3c;
  white-space: nowrap;
}
.select-all-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px 0;
}
.bottom-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  margin-top: 12px;
  position: sticky;
  bottom: 20px;
}
.bottom-left {
  font-size: 14px;
}
.bottom-left .count {
  color: #e74c3c;
  font-weight: 600;
}
.bottom-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
.total-label {
  font-size: 14px;
}
.total-price {
  font-size: 20px;
  font-weight: 700;
  color: #e74c3c;
}
.spicy-slider {
  display: flex;
  align-items: center;
  width: 100%;
}
.spicy-label {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

/* ===== Base Section ===== */
.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 16px 0 10px;
}
.section-title:first-child {
  margin-top: 0;
}
.base-section {
  margin-bottom: 8px;
}
.base-cart-item {
  border: 1px solid #fde2d8 !important;
  background: linear-gradient(135deg, #fff8f6, #fff) !important;
}
.base-config-tags {
  display: flex;
  gap: 6px;
  margin: 4px 0;
}
</style>
