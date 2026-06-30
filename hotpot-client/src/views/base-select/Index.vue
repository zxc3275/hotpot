<template>
  <div class="base-select-page">
    <!-- Step Indicator -->
    <div class="step-header">
      <div class="step-number">1</div>
      <div class="step-info">
        <h2>选择锅底</h2>
        <p>选择您喜欢的锅底类型和口味，开启美味火锅之旅</p>
      </div>
    </div>

    <!-- Hotpot Type Tabs -->
    <div class="type-tabs-wrapper">
      <div class="type-tabs">
        <div
          v-for="t in types"
          :key="t.id"
          class="type-tab"
          :class="{ active: activeTypeId === t.id }"
          @click="selectType(t)"
        >
          <span class="tab-name">{{ t.name }}</span>
          <span class="tab-desc">{{ t.description }}</span>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="content-area" v-loading="loading">
      <!-- Base Grid -->
      <div class="base-grid-section">
        <el-empty v-if="!loading && bases.length === 0" description="该分类暂无锅底，请选择其他类型" />
        <div v-else class="base-grid">
          <div
            v-for="base in bases"
            :key="base.id"
            class="base-card"
            :class="{ selected: selectedBase?.id === base.id }"
            @click="selectBase(base)"
          >
            <div class="card-image-wrap">
              <img :src="base.image" :alt="base.name" class="base-image" />
              <span class="pot-type-badge">{{ getPotTypeLabel(base) }}</span>
            </div>
            <div class="card-body">
              <h3 class="base-name">{{ base.name }}</h3>
              <p class="base-desc">{{ base.description }}</p>
              <div class="level-row">
                <div class="level-item spicy">
                  <span
                    v-for="n in 3"
                    :key="'s' + n"
                    class="level-dot"
                    :class="n <= (base.spicyMax ?? 3) ? 'active' : 'inactive'"
                  ></span>
                  <span>辣度 {{ base.spicyMin ?? 0 }}-{{ base.spicyMax ?? 3 }}</span>
                </div>
                <div class="level-item num">
                  <span
                    v-for="n in 3"
                    :key="'n' + n"
                    class="level-dot"
                    :class="n <= (base.numMax ?? 3) ? 'active' : 'inactive'"
                  ></span>
                  <span>麻度 {{ base.numMin ?? 0 }}-{{ base.numMax ?? 3 }}</span>
                </div>
              </div>
              <div class="price-row">
                <span class="base-price">&yen;{{ (base.price / 100).toFixed(2) }}</span>
                <span class="price-unit">/份</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Config Panel -->
      <transition name="slide-up">
        <div class="config-panel" v-if="selectedBase">
          <div class="panel-header">
            <img :src="selectedBase.image" :alt="selectedBase.name" class="panel-thumb" />
            <div>
              <h3>{{ selectedBase.name }}</h3>
              <span class="panel-type-badge">{{ getPotTypeLabel(selectedBase) }}</span>
            </div>
          </div>

          <!-- Pot Type Selection (when multiple) -->
          <div class="config-section" v-if="potTypeOptions.length > 1">
            <label class="config-label">锅型选择</label>
            <el-radio-group v-model="potType" class="pot-radio-group">
              <el-radio-button
                v-for="pt in potTypeOptions"
                :key="pt"
                :value="pt"
              >
                {{ pt }}
              </el-radio-button>
            </el-radio-group>
          </div>

          <!-- Spicy Level -->
          <div class="config-section">
            <label class="config-label">
              辣度
              <span class="level-value">{{ spicyLevel }}</span>
            </label>
            <el-slider
              v-model="spicyLevel"
              :min="spicyMinLimit"
              :max="spicyMaxLimit"
              :step="1"
              :marks="dynamicSpicyMarks"
              show-stops
              class="level-slider"
            />
          </div>

          <!-- Num Level -->
          <div class="config-section">
            <label class="config-label">
              麻度
              <span class="level-value">{{ numLevel }}</span>
            </label>
            <el-slider
              v-model="numLevel"
              :min="numMinLimit"
              :max="numMaxLimit"
              :step="1"
              :marks="dynamicNumMarks"
              show-stops
              class="level-slider"
            />
          </div>

          <!-- Divider -->
          <el-divider />

          <!-- Price -->
          <div class="price-section">
            <span class="price-label">锅底价格</span>
            <span class="price-value">&yen;{{ totalPrice }}</span>
          </div>

          <!-- Action Button -->
          <el-button type="danger" size="large" class="action-btn" round @click="confirmSelection">
            选好了，去点菜
          </el-button>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const router = useRouter()

// ==================== State ====================
const types = ref([])
const activeTypeId = ref(null)
const bases = ref([])
const loading = ref(false)
const selectedBase = ref(null)
const potType = ref('')
const spicyLevel = ref(0)
const numLevel = ref(0)

// ==================== Pot Type Helpers ====================
const potTypeLabelMap = {
  single: '单锅',
  single_pot: '单锅',
  单锅: '单锅',
  double: '鸳鸯锅',
  double_pot: '鸳鸯锅',
  鸳鸯锅: '鸳鸯锅',
  yuanyang: '鸳鸯锅',
  nine: '九宫格',
  nine_grid: '九宫格',
  九宫格: '九宫格',
  jiugongge: '九宫格'
}

function getPotTypeLabel(base) {
  if (!base) return ''
  const raw = base.potType || (base.potTypes && base.potTypes[0]) || ''
  return potTypeLabelMap[raw] || raw
}

// Normalize potTypes array from base
const potTypeOptions = computed(() => {
  if (!selectedBase.value) return []
  const pts = selectedBase.value.potTypes
  if (Array.isArray(pts) && pts.length) {
    return pts.map(p => potTypeLabelMap[p] || p)
  }
  const single = selectedBase.value.potType
  if (single) {
    return [potTypeLabelMap[single] || single]
  }
  return []
})

// ==================== Computed Limits ====================
const spicyMinLimit = computed(() => selectedBase.value?.spicyMin ?? 0)
const spicyMaxLimit = computed(() => selectedBase.value?.spicyMax ?? 3)
const numMinLimit = computed(() => selectedBase.value?.numMin ?? 0)
const numMaxLimit = computed(() => selectedBase.value?.numMax ?? 3)

// ==================== Dynamic Slider Marks ====================
const dynamicSpicyMarks = computed(() => {
  const marks = {}
  for (let i = spicyMinLimit.value; i <= spicyMaxLimit.value; i++) {
    marks[i] = String(i)
  }
  return marks
})

const dynamicNumMarks = computed(() => {
  const marks = {}
  for (let i = numMinLimit.value; i <= numMaxLimit.value; i++) {
    marks[i] = String(i)
  }
  return marks
})

// ==================== Total Price ====================
const totalPrice = computed(() => {
  if (!selectedBase.value) return '0.00'
  return (selectedBase.value.price / 100).toFixed(2)
})

// ==================== Clamp Helper ====================
function clamp(val, min, max) {
  return Math.min(Math.max(val, min), max)
}

// ==================== Watchers ====================
watch(selectedBase, (base) => {
  if (!base) return
  // Set potType from available options
  potType.value = potTypeOptions.value.length > 0 ? potTypeOptions.value[0] : ''
  // Set levels to base minimums
  spicyLevel.value = clamp(base.spicyMin ?? 0, base.spicyMin ?? 0, base.spicyMax ?? 3)
  numLevel.value = clamp(base.numMin ?? 0, base.numMin ?? 0, base.numMax ?? 3)
})

// ==================== API Calls ====================
async function fetchTypes() {
  try {
    const res = await request.get('/client/base/types')
    types.value = res.data || []
    if (types.value.length > 0 && !activeTypeId.value) {
      activeTypeId.value = types.value[0].id
      await fetchBases()
    }
  } catch {
    // error already handled by response interceptor
  }
}

async function fetchBases() {
  if (!activeTypeId.value) return
  loading.value = true
  try {
    const res = await request.get('/client/base/list', { params: { typeId: activeTypeId.value } })
    bases.value = res.data || []
    selectedBase.value = null
  } catch {
    bases.value = []
  } finally {
    loading.value = false
  }
}

// ==================== Event Handlers ====================
function selectType(t) {
  if (activeTypeId.value === t.id) return
  activeTypeId.value = t.id
  fetchBases()
}

function selectBase(base) {
  selectedBase.value = base
}

function confirmSelection() {
  if (!selectedBase.value) {
    ElMessage.warning('请先选择一个锅底')
    return
  }
  sessionStorage.setItem('baseId', selectedBase.value.id)
  sessionStorage.setItem('potType', potType.value)
  sessionStorage.setItem('spicyLevel', String(spicyLevel.value))
  sessionStorage.setItem('numLevel', String(numLevel.value))
  router.push('/menu')
}

// ==================== Lifecycle ====================
onMounted(() => {
  fetchTypes()
})
</script>

<style scoped>
/* ===== Page Layout ===== */
.base-select-page {
  max-width: 1100px;
  margin: 0 auto;
  padding: 24px 16px 48px;
  min-height: 100vh;
}

/* ===== Step Header ===== */
.step-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: linear-gradient(135deg, #fff5f0, #ffe8e0);
  border-radius: 12px;
  margin-bottom: 20px;
  border: 1px solid #fdd;
}
.step-number {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #e74c3c;
  color: #fff;
  font-size: 22px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.step-info h2 {
  margin: 0 0 4px;
  font-size: 20px;
  color: #333;
}
.step-info p {
  margin: 0;
  font-size: 14px;
  color: #999;
}

/* ===== Type Tabs ===== */
.type-tabs-wrapper {
  margin-bottom: 20px;
  overflow-x: auto;
}
.type-tabs {
  display: flex;
  gap: 12px;
  padding-bottom: 4px;
}
.type-tab {
  flex-shrink: 0;
  padding: 12px 20px;
  background: #fff;
  border: 2px solid #f0e0d8;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.25s;
  text-align: center;
  min-width: 100px;
}
.type-tab:hover {
  border-color: #e74c3c;
  background: #fff5f0;
}
.type-tab.active {
  border-color: #e74c3c;
  background: #e74c3c;
  color: #fff;
}
.tab-name {
  display: block;
  font-size: 15px;
  font-weight: 600;
}
.tab-desc {
  display: block;
  font-size: 12px;
  margin-top: 2px;
  opacity: 0.75;
}

/* ===== Content Area ===== */
.content-area {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

/* ===== Base Grid ===== */
.base-grid-section {
  flex: 1;
  min-width: 0;
}
.base-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}

/* ===== Base Card ===== */
.base-card {
  background: #fff;
  border: 2px solid #f0e8e0;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.25s;
}
.base-card:hover {
  border-color: #e74c3c;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(231, 76, 60, 0.12);
}
.base-card.selected {
  border-color: #e74c3c;
  box-shadow: 0 0 0 3px rgba(231, 76, 60, 0.18);
}
.card-image-wrap {
  position: relative;
  height: 160px;
  background: #faf5f2;
  overflow: hidden;
}
.base-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.pot-type-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #e74c3c;
  color: #fff;
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 20px;
}
.card-body {
  padding: 14px;
}
.base-name {
  margin: 0 0 6px;
  font-size: 16px;
  color: #333;
}
.base-desc {
  margin: 0 0 10px;
  font-size: 13px;
  color: #999;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* ===== Level Indicators ===== */
.level-row {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 10px;
}
.level-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #888;
}
.level-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
  transition: background 0.2s;
}
.level-dot.active {
  background: #e74c3c;
}
.level-dot.inactive {
  background: #e0d5d0;
}
.num .level-dot.active {
  background: #f0a050;
}

/* ===== Price in Card ===== */
.price-row {
  display: flex;
  align-items: baseline;
  gap: 2px;
}
.base-price {
  font-size: 20px;
  font-weight: bold;
  color: #e74c3c;
}
.price-unit {
  font-size: 13px;
  color: #999;
}

/* ===== Config Panel ===== */
.config-panel {
  width: 300px;
  flex-shrink: 0;
  background: #fff;
  border: 1px solid #f0e8e0;
  border-radius: 12px;
  padding: 20px;
  position: sticky;
  top: 84px;
}
.panel-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}
.panel-thumb {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
  background: #faf5f2;
  flex-shrink: 0;
}
.panel-header h3 {
  margin: 0 0 4px;
  font-size: 16px;
  color: #333;
}
.panel-type-badge {
  display: inline-block;
  font-size: 12px;
  color: #fff;
  background: #e74c3c;
  padding: 2px 10px;
  border-radius: 10px;
}

/* ===== Config Sections ===== */
.config-section {
  margin-bottom: 18px;
}
.config-label {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: #555;
  margin-bottom: 10px;
}
.level-value {
  font-size: 18px;
  font-weight: bold;
  color: #e74c3c;
}
.level-slider {
  margin: 0 4px;
}
.pot-radio-group {
  display: flex;
  flex-wrap: wrap;
}

/* ===== Price & Action ===== */
.price-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.price-label {
  font-size: 15px;
  color: #666;
}
.price-value {
  font-size: 24px;
  font-weight: bold;
  color: #e74c3c;
}
.action-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  letter-spacing: 1px;
}

/* ===== Transition ===== */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}
.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(12px);
}

/* ===== Responsive ===== */
@media (max-width: 768px) {
  .content-area {
    flex-direction: column;
  }
  .config-panel {
    width: 100%;
    position: static;
  }
  .base-grid {
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  }
}
</style>
