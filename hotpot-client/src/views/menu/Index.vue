<template>
  <div class="menu-page">
    <!-- Search Bar -->
    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="搜索菜品、锅底..."
        :prefix-icon="Search"
        clearable
        class="search-input"
        @input="onSearch"
        @clear="onSearch"
      />
    </div>

    <div class="menu-content">
      <!-- Left: Category Sidebar -->
      <aside class="category-sidebar">
        <div class="category-title">菜品分类</div>
        <div
          v-for="cat in categories"
          :key="cat.id"
          :class="['category-item', { active: activeCategory === cat.id }]"
          @click="selectCategory(cat.id)"
        >
          <span class="cat-icon">{{ cat.icon || '🍲' }}</span>
          <span class="cat-name">{{ cat.name }}</span>
          <span class="cat-count" v-if="cat.dishCount">{{ cat.dishCount }}</span>
        </div>
        <div
          v-if="!categories.length"
          class="category-empty"
        >加载中...</div>
      </aside>

      <!-- Right: Dish Grid -->
      <section class="dish-area">
        <!-- Loading -->
        <div v-if="loading" class="loading-wrap">
          <el-icon class="is-loading" :size="32"><Loading /></el-icon>
          <p>正在加载菜品...</p>
        </div>

        <!-- Empty -->
        <div v-else-if="!dishes.length" class="empty-wrap">
          <el-empty description="暂无菜品，换个分类试试吧 ~" />
        </div>

        <!-- Grid -->
        <div v-else class="dish-grid">
          <div
            v-for="dish in dishes"
            :key="dish.id"
            class="dish-card"
          >
            <div class="card-img">
              <el-image
                :src="dish.image"
                fit="cover"
                class="dish-image"
                :preview-src-list="[dish.image]"
                preview-teleported
              >
                <template #error>
                  <div class="image-slot">
                    <el-icon :size="48"><PictureFilled /></el-icon>
                  </div>
                </template>
              </el-image>
              <!-- Spicy Level -->
              <span v-if="dish.spicyLevel" class="spicy-tag" :class="'spicy-' + dish.spicyLevel">
                {{ spicyLabel(dish.spicyLevel) }}
              </span>
            </div>

            <div class="card-body">
              <h3 class="dish-name">{{ dish.name }}</h3>
              <p class="dish-desc">{{ dish.description || '暂无描述' }}</p>

              <div class="dish-meta">
                <span class="dish-sales">已售 {{ dish.sales || 0 }}</span>
              </div>

              <div class="card-footer">
                <div class="price-area">
                  <span class="price-current">￥{{ (dish.price / 100).toFixed(2) }}</span>
                  <span v-if="dish.originalPrice && dish.originalPrice !== dish.price" class="price-original">
                    ￥{{ (dish.originalPrice / 100).toFixed(2) }}
                  </span>
                </div>
                <div class="card-actions">
                  <el-button
                    :type="isFavorited(dish.id) ? 'warning' : 'default'"
                    :icon="isFavorited(dish.id) ? StarFilled : Star"
                    size="small"
                    circle
                    @click="toggleFavorite(dish.id)"
                  />
                  <el-button
                    type="primary"
                    size="small"
                    @click="addToCart(dish)"
                  >
                    加入购物车
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <!-- Floating Cart Icon -->
    <div class="floating-cart" @click="$router.push('/cart')">
      <el-badge :value="cartCount" :hidden="!cartCount" class="cart-badge-float">
        <el-button type="danger" :icon="ShoppingCartFull" circle size="large" />
      </el-badge>
      <div class="cart-total" v-if="cartTotal > 0">
        ￥{{ (cartTotal / 100).toFixed(2) }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import request from '@/api/request'
import { ElMessage } from 'element-plus'
import {
  Search,
  Loading,
  PictureFilled,
  Star,
  StarFilled,
  ShoppingCartFull
} from '@element-plus/icons-vue'

const route = useRoute()

// --- State ---
const keyword = ref('')
const categories = ref([])
const dishes = ref([])
const activeCategory = ref(route.query.categoryId ? Number(route.query.categoryId) : null)
const favorites = ref(new Set())
const cartCount = ref(0)
const cartTotal = ref(0)
const loading = ref(false)

// --- Cart total (computed elsewhere, but we fetch it) ---

// --- Methods ---

// Fetch category list
const fetchCategories = async () => {
  try {
    const res = await request.get('/client/dish/categories')
    if (res.data) {
      categories.value = res.data
      // Auto-select first category if none from URL
      if (res.data.length && !activeCategory.value) {
        activeCategory.value = res.data[0].id
      }
      // If category from URL exists, keep it; otherwise fallback to first
      if (activeCategory.value && !res.data.find(c => c.id === activeCategory.value)) {
        activeCategory.value = res.data[0].id
      }
    }
  } catch (e) {
    // console.error
  }
}

// Fetch dishes with filters
const fetchDishes = async () => {
  loading.value = true
  try {
    const params = {}
    if (activeCategory.value) params.categoryId = activeCategory.value
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    const res = await request.get('/client/dish/list', { params })
    dishes.value = res.data || []
  } catch (e) {
    dishes.value = []
  } finally {
    loading.value = false
  }
}

// Fetch user favorites
const fetchFavorites = async () => {
  try {
    const res = await request.get('/client/user/favorites')
    if (res.data) {
      favorites.value = new Set(
        Array.isArray(res.data)
          ? res.data.map(f => f.dishId || f.id)
          : []
      )
    }
  } catch (e) {
    // console.error
  }
}

// Fetch cart count and total
const fetchCartInfo = async () => {
  try {
    const res = await request.get('/client/cart/list')
    const items = res.data || []
    cartCount.value = Array.isArray(items) ? items.length : 0
    cartTotal.value = Array.isArray(items)
      ? items.reduce((sum, item) => sum + (item.dishPrice || 0) * (item.quantity || 1), 0)
      : 0
  } catch (e) {
    // console.error
  }
}

// Select category
const selectCategory = (id) => {
  if (activeCategory.value === id) {
    activeCategory.value = null
  } else {
    activeCategory.value = id
  }
}

// Search handler with debounce
let searchTimer = null
const onSearch = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    fetchDishes()
  }, 300)
}

// Add to cart
const addToCart = async (dish) => {
  try {
    await request.post('/client/cart/add', { dishId: dish.id, quantity: 1 })
    ElMessage.success(`已添加「${dish.name}」到购物车`)
    fetchCartInfo()
  } catch (e) {
    // Error already handled by interceptor
  }
}

// Favorite toggle
const toggleFavorite = async (dishId) => {
  const favorited = favorites.value.has(dishId)
  try {
    if (favorited) {
      await request.delete(`/client/user/favorite/${dishId}`)
      favorites.value.delete(dishId)
      ElMessage.success('已取消收藏')
    } else {
      await request.post(`/client/user/favorite/${dishId}`)
      favorites.value.add(dishId)
      ElMessage.success('已收藏')
    }
  } catch (e) {
    // Error handled by interceptor
  }
}

// Check if dish is favorited
const isFavorited = (id) => favorites.value.has(id)

// Spicy level label
const spicyLabel = (level) => {
  const map = { 1: '微辣', 2: '中辣', 3: '特辣' }
  return map[level] || '辣'
}

// Watch activeCategory and refetch
watch(activeCategory, () => {
  fetchDishes()
})

// On mount
onMounted(() => {
  fetchCategories()
  fetchFavorites()
  fetchCartInfo()
  // Once categories loaded, fetchDishes will be triggered by watch if activeCategory changes
  fetchDishes()
})
</script>

<style scoped>
.menu-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;
  min-height: calc(100vh - 120px);
  position: relative;
}

/* --- Search Bar --- */
.search-bar {
  margin-bottom: 16px;
}
.search-input {
  max-width: 480px;
}
.search-input :deep(.el-input__wrapper) {
  border-radius: 24px;
  box-shadow: 0 2px 8px rgba(231, 76, 60, 0.12);
}

/* --- Layout --- */
.menu-content {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

/* --- Category Sidebar --- */
.category-sidebar {
  width: 180px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 12px;
  padding: 12px 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 76px;
}

.category-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  padding: 8px 16px 12px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 4px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  cursor: pointer;
  transition: all 0.25s ease;
  font-size: 14px;
  color: #555;
  border-left: 3px solid transparent;
  margin: 2px 0;
}

.category-item:hover {
  background: #fff5f5;
  color: #e74c3c;
}

.category-item.active {
  background: linear-gradient(90deg, #fff0f0 0%, #fff 100%);
  color: #e74c3c;
  font-weight: 600;
  border-left-color: #e74c3c;
}

.cat-icon {
  font-size: 18px;
}
.cat-name {
  flex: 1;
}
.cat-count {
  font-size: 12px;
  color: #aaa;
  background: #f5f5f5;
  border-radius: 10px;
  padding: 1px 8px;
}

.category-empty {
  padding: 24px;
  text-align: center;
  color: #bbb;
  font-size: 13px;
}

/* --- Dish Area --- */
.dish-area {
  flex: 1;
  min-height: 400px;
}

.loading-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  color: #aaa;
  gap: 12px;
}

.empty-wrap {
  padding: 60px 0;
}

/* --- Dish Grid --- */
.dish-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

@media (min-width: 1100px) {
  .dish-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 768px) {
  .menu-content {
    flex-direction: column;
  }
  .category-sidebar {
    width: 100%;
    position: static;
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    padding: 10px;
  }
  .category-title {
    width: 100%;
    border-bottom: none;
    margin-bottom: 0;
    padding-bottom: 4px;
  }
  .category-item {
    padding: 6px 12px;
    border-radius: 16px;
    background: #f8f8f8;
    border-left: none;
  }
  .category-item.active {
    background: #e74c3c;
    color: #fff;
  }
  .dish-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* --- Dish Card --- */
.dish-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.dish-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(231, 76, 60, 0.15);
}

.card-img {
  position: relative;
  width: 100%;
  height: 150px;
  overflow: hidden;
  background: #f8f8f8;
}

.dish-image {
  width: 100%;
  height: 100%;
}

.dish-image :deep(img) {
  width: 100%;
  height: 150px;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.dish-card:hover .dish-image :deep(img) {
  transform: scale(1.08);
}

.image-slot {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  color: #ccc;
}

/* Spicy Tag */
.spicy-tag {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 12px;
  color: #fff;
  pointer-events: none;
}

.spicy-1 { background: rgba(255, 152, 0, 0.85); }
.spicy-2 { background: rgba(244, 67, 54, 0.85); }
.spicy-3 { background: rgba(183, 28, 28, 0.85); }

/* Card Body */
.card-body {
  padding: 12px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.dish-name {
  font-size: 15px;
  font-weight: 600;
  color: #222;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dish-desc {
  font-size: 12px;
  color: #999;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 8px;
  min-height: 34px;
}

.dish-meta {
  margin-bottom: 10px;
}

.dish-sales {
  font-size: 12px;
  color: #bbb;
}

/* Card Footer */
.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
}

.price-area {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.price-current {
  font-size: 18px;
  font-weight: 700;
  color: #e74c3c;
}

.price-original {
  font-size: 12px;
  color: #bbb;
  text-decoration: line-through;
}

.card-actions {
  display: flex;
  align-items: center;
  gap: 6px;
}

.card-actions .el-button--small {
  padding: 5px 10px;
  font-size: 12px;
}

/* --- Floating Cart --- */
.floating-cart {
  position: fixed;
  bottom: 40px;
  right: 40px;
  z-index: 200;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  transition: transform 0.25s ease;
}

.floating-cart:hover {
  transform: scale(1.08);
}

.cart-badge-float :deep(.el-badge__content) {
  border: 2px solid #fff;
}

.cart-total {
  font-size: 13px;
  font-weight: 600;
  color: #e74c3c;
  background: #fff;
  padding: 2px 10px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  white-space: nowrap;
}
</style>
