<template>
  <div class="home-page">
    <!-- 轮播横幅 -->
    <section class="banner-section">
      <el-carousel
        v-if="carousels.length"
        :interval="4000"
        :height="'350px'"
        arrow="always"
        indicator-position="outside"
      >
        <el-carousel-item v-for="item in carousels" :key="item.id" @click="goCarousel(item)" style="cursor:pointer">
          <img :src="item.imageUrl" :alt="item.title" class="carousel-img" />
          <div class="carousel-caption" v-if="item.title">
            <h3>{{ item.title }}</h3>
          </div>
        </el-carousel-item>
      </el-carousel>
      <div v-else class="banner-placeholder">
        <div class="placeholder-content">
          <span class="placeholder-icon">🍲</span>
          <h2>欢迎来到火锅点餐</h2>
          <p>翻滚的红汤，沸腾的美味</p>
        </div>
      </div>
    </section>

    <!-- 锅底选择入口 -->
    <section class="section base-section">
      <div class="section-header">
        <h2 class="section-title">🔥 选择锅底</h2>
        <router-link to="/base-select" class="section-more">
          查看全部 <el-icon><ArrowRight /></el-icon>
        </router-link>
      </div>
      <div class="type-grid" v-if="baseTypes.length">
        <el-card
          v-for="item in baseTypes"
          :key="item.id"
          shadow="hover"
          class="type-card"
          @click="$router.push('/base-select')"
        >
          <img :src="item.image" :alt="item.name" class="type-image" />
          <div class="type-info">
            <h4>{{ item.name }}</h4>
            <p class="type-desc">{{ item.description || '经典好味' }}</p>
          </div>
        </el-card>
      </div>
      <el-empty v-else description="暂无锅底类型" :image-size="80" />
    </section>

    <!-- 分类导航 -->
    <section class="section category-section">
      <div class="section-header">
        <h2 class="section-title">📂 菜品分类</h2>
      </div>
      <div class="category-grid" v-if="categories.length">
        <div
          v-for="cat in categories"
          :key="cat.id"
          class="category-card"
          @click="$router.push(`/menu?categoryId=${cat.id}`)"
        >
          <div class="category-icon-wrapper">
            <span class="category-emoji">{{ getCategoryIcon(cat.name) }}</span>
          </div>
          <span class="category-name">{{ cat.name }}</span>
          <span class="category-count" v-if="cat.dishCount">{{ cat.dishCount }}款</span>
        </div>
      </div>
      <el-empty v-else description="暂无分类" :image-size="80" />
    </section>

    <!-- 热销推荐 -->
    <section class="section recommend-section">
      <div class="section-header">
        <h2 class="section-title">🏆 热销推荐</h2>
        <router-link to="/menu" class="section-more">
          去菜单点菜 <el-icon><ArrowRight /></el-icon>
        </router-link>
      </div>
      <div class="dish-grid" v-if="topDishes.length">
        <el-card
          v-for="dish in topDishes"
          :key="dish.id"
          shadow="hover"
          class="dish-card"
        >
          <div class="dish-image-wrapper" @click="$router.push('/menu')">
            <img :src="dish.image" :alt="dish.name" class="dish-image" />
            <div class="dish-sales-tag">已售 {{ dish.sales || dish.saleCount || 0 }}</div>
          </div>
          <div class="dish-info">
            <h4 class="dish-name" @click="$router.push('/menu')">{{ dish.name }}</h4>
            <p class="dish-price">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ formatPrice(dish.price) }}</span>
            </p>
            <el-button
              type="danger"
              round
              size="small"
              class="add-cart-btn"
              @click.stop="addToCart(dish)"
            >
              加入购物车
            </el-button>
          </div>
        </el-card>
      </div>
      <el-empty v-else description="暂无推荐菜品" :image-size="80" />
    </section>

    <!-- 火锅指南 -->
    <section class="section guide-section">
      <div class="section-header">
        <h2 class="section-title">📖 火锅指南</h2>
      </div>
      <div class="guide-grid">
        <el-card
          v-for="(guide, idx) in guides"
          :key="idx"
          shadow="hover"
          class="guide-card"
          @click="openGuide(guide)"
        >
          <div class="guide-icon">{{ guide.icon }}</div>
          <h4 class="guide-title">{{ guide.title }}</h4>
          <p class="guide-desc">{{ guide.desc }}</p>
          <el-button type="danger" text size="small">
            查看详情 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </el-card>
      </div>
    </section>

    <!-- Guide Dialog -->
    <el-dialog v-model="guideDialogVisible" :title="currentGuide?.title || '火锅指南'" width="650px" destroy-on-close>
      <div v-if="guideContent.length" class="guide-content">
        <div v-for="(item, i) in guideContent" :key="i" class="guide-item">
          <h4 class="guide-item-title">{{ item.title }}</h4>
          <p class="guide-item-text">{{ item.content }}</p>
        </div>
      </div>
      <el-empty v-else description="暂无指南内容" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const router = useRouter()

const carousels = ref([])
const baseTypes = ref([])
const categories = ref([])
const topDishes = ref([])
const guideDialogVisible = ref(false)
const currentGuide = ref(null)
const guideContent = ref([])
const guides = ref([
  { type: 'cooking', icon: '🍳', title: '涮煮指南', desc: '各种食材的最佳涮煮时间，让你吃出最佳口感' },
  { type: 'dipping', icon: '🥣', title: '蘸料搭配', desc: '经典蘸料配方，调制属于你的专属味道' },
  { type: 'blanch', icon: '🥢', title: '毛肚七上八下', desc: '经典火锅食材的处理技巧与讲究' }
])

// 分类图标映射
const categoryIconMap = {
  '荤菜': '🥩',
  '素菜': '🥬',
  '菌菇': '🍄',
  '豆制品': '🫘',
  '丸滑': '🍡',
  '主食': '🍚',
  '饮品': '🥤',
  '小吃': '🍟',
  '海鲜': '🦐',
  '其他': '🍽️'
}

const getCategoryIcon = (name) => {
  return categoryIconMap[name] || '🍽️'
}

const formatPrice = (price) => {
  if (!price && price !== 0) return '0.00'
  return (price / 100).toFixed(2)
}

// 获取轮播图
const fetchCarousels = async () => {
  try {
    const res = await request.get('/admin/system/carousels', { params: { type: 1 } })
    carousels.value = res.data || []
  } catch {
    carousels.value = []
  }
}

// 获取锅底类型
const fetchBaseTypes = async () => {
  try {
    const res = await request.get('/client/base/types')
    baseTypes.value = res.data || []
  } catch {
    baseTypes.value = []
  }
}

// 获取菜品分类
const fetchCategories = async () => {
  try {
    const res = await request.get('/client/dish/categories')
    categories.value = res.data || []
  } catch {
    categories.value = []
  }
}

// 获取热销菜品
const fetchTopDishes = async () => {
  try {
    const res = await request.get('/client/dish/top', { params: { limit: 8 } })
    topDishes.value = res.data || []
  } catch {
    topDishes.value = []
  }
}

// 加入购物车
const addToCart = async (dish) => {
  try {
    await request.post('/client/cart/add', { dishId: dish.id, quantity: 1 })
    ElMessage.success(`已添加「${dish.name}」到购物车`)
  } catch {
    // 错误已在拦截器中处理
  }
}

// 打开指南
// 获取指南数据
const fetchGuides = async () => {
  try {
    const res = await request.get('/client/guide/list')
    if (res.data && res.data.length) {
      const g = {}
      res.data.forEach(item => {
        if (!g[item.type]) g[item.type] = []
        g[item.type].push(item)
      })
      guides.value.forEach(guide => {
        if (g[guide.type]) guide.desc = `共${g[guide.type].length}篇指南，点击查看详情`
      })
    }
  } catch { /* use defaults */ }
}

// 打开指南对话框
// 轮播图点击跳转
const goCarousel = (item) => {
  if (item.linkUrl) {
    if (item.linkUrl.startsWith("http")) {
      window.open(item.linkUrl, "_blank")
    } else {
      router.push(item.linkUrl)
    }
  }
}

const openGuide = async (guide) => {
  currentGuide.value = guide
  guideDialogVisible.value = true
  try {
    const res = await request.get('/client/guide/list?type=' + guide.type)
    guideContent.value = (res.data || []).map(g => ({ title: g.title, content: g.content }))
    if (!guideContent.value.length) {
      const fallback = {
        cooking: [
          { title: '毛肚/鸭肠', content: '七上八下，涮8-10秒即可，口感爽脆' },
          { title: '肥牛/牛肉', content: '涮8-12秒变色即食，肉质鲜嫩多汁' },
          { title: '虾滑/鱼丸', content: '煮2-3分钟浮起后即可食用' },
          { title: '蔬菜类', content: '叶菜涮30秒，根茎类煮1-2分钟' },
          { title: '豆制品', content: '煮2-3分钟，充分吸收汤汁更美味' }
        ],
        dipping: [
          { title: '经典蒜泥香油碟', content: '蒜泥+香油+蚝油+葱花+香菜，提鲜不掩盖食材本味' },
          { title: '麻辣干碟', content: '辣椒面+花椒面+芝麻+花生碎+盐+味精，适合重口味爱好者' },
          { title: '海鲜蘸料', content: '生抽+芥末+柠檬汁，清爽去腥，适合海鲜类食材' },
          { title: '麻酱蘸料', content: '芝麻酱+韭菜花+腐乳+香菜，北方经典蘸法，浓郁醇厚' }
        ],
        blanch: [
          { title: '七上八下技巧', content: '毛肚、鸭肠等内脏类食材，用筷子夹住在沸汤中"七上八下"共15次，约8-10秒，口感最佳' },
          { title: '肉类涮法', content: '牛肉切薄片，入锅涮至变色即可（约8-12秒），保留肉汁鲜嫩；羊肉烫至变色卷曲即可' },
          { title: '海鲜处理', content: '虾滑用勺子挖成球入锅，煮至浮起再等30秒；贝类开口即可食用' }
        ]
      }
      guideContent.value = fallback[guide.type] || []
    }
  } catch {
    guideContent.value = []
  }
}

onMounted(() => {
  fetchCarousels()
  fetchBaseTypes()
  fetchCategories()
  fetchTopDishes()
  fetchGuides()
})
</script>

<style scoped>
.home-page { max-width: 1200px; margin: 0 auto; }

/* 横幅 */
.banner-section { margin-bottom: 32px; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
.carousel-img { width: 100%; height: 350px; object-fit: cover; cursor: pointer; }
.carousel-caption {
  position: absolute; bottom: 32px; left: 50%; transform: translateX(-50%);
  background: rgba(0,0,0,0.55); backdrop-filter: blur(10px);
  padding: 12px 32px; border-radius: 30px;
}
.carousel-caption h3 { color: #fff; font-size: 18px; margin: 0; }
.banner-placeholder {
  height: 350px; background: linear-gradient(135deg, #e74c3c, #c0392b, #8b1a0e);
  display: flex; align-items: center; justify-content: center;
}
.placeholder-content { text-align: center; color: #fff; }
.placeholder-icon { font-size: 72px; display: block; margin-bottom: 12px; animation: float 3s ease-in-out infinite; }
.placeholder-content h2 { font-size: 32px; margin-bottom: 8px; }
.placeholder-content p { font-size: 16px; opacity: 0.85; }
@keyframes float { 0%,100% { transform: translateY(0); } 50% { transform: translateY(-10px); } }

/* 区块 */
.section { margin-bottom: 40px; }
.section-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.section-title { font-size: 22px; font-weight: 700; color: #2c3e50; }
.section-more { font-size: 14px; color: #e74c3c; display: flex; align-items: center; gap: 4px; transition: gap 0.3s; }
.section-more:hover { gap: 8px; }

/* 锅底类型 */
.type-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.type-card { cursor: pointer; border-radius: 14px; overflow: hidden; transition: all 0.35s; border: none; }
.type-card:hover { transform: translateY(-6px); box-shadow: 0 12px 30px rgba(231,76,60,0.18); }
.type-image { width: 100%; height: 160px; object-fit: cover; }
.type-info { padding: 12px 16px; }
.type-info h4 { font-size: 16px; font-weight: 600; margin-bottom: 4px; }
.type-desc { font-size: 13px; color: #888; }

/* 分类导航 */
.category-grid { display: grid; grid-template-columns: repeat(8, 1fr); gap: 12px; }
.category-card {
  display: flex; flex-direction: column; align-items: center; gap: 8px;
  padding: 16px 8px; background: #fff; border-radius: 14px;
  cursor: pointer; transition: all 0.3s; box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.category-card:hover { transform: translateY(-3px); box-shadow: 0 6px 20px rgba(231,76,60,0.12); }
.category-icon-wrapper {
  width: 56px; height: 56px; border-radius: 16px;
  background: linear-gradient(135deg, #fff5f5, #fadbd8);
  display: flex; align-items: center; justify-content: center;
}
.category-emoji { font-size: 28px; }
.category-name { font-size: 13px; font-weight: 500; color: #555; }

/* 推荐菜品 */
.dish-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.dish-card {
  border-radius: 14px; overflow: hidden; border: none;
  transition: all 0.35s; box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}
.dish-card:hover { transform: translateY(-6px); box-shadow: 0 14px 35px rgba(231,76,60,0.15); }
.dish-image-wrapper { position: relative; cursor: pointer; overflow: hidden; }
.dish-image { width: 100%; height: 180px; object-fit: cover; transition: transform 0.4s; }
.dish-card:hover .dish-image { transform: scale(1.06); }
.dish-sales-tag {
  position: absolute; top: 10px; right: 10px;
  background: rgba(231,76,60,0.9); color: #fff;
  font-size: 11px; padding: 3px 10px; border-radius: 12px;
}
.dish-info { padding: 14px 16px; }
.dish-name { font-size: 15px; font-weight: 600; cursor: pointer; margin-bottom: 8px; }
.dish-name:hover { color: #e74c3c; }
.dish-price { margin-bottom: 12px; }
.price-symbol { font-size: 14px; color: #e74c3c; font-weight: 600; }
.price-value { font-size: 22px; color: #e74c3c; font-weight: 700; }
.add-cart-btn { width: 100%; font-weight: 500; letter-spacing: 1px; }

/* 指南 */
.guide-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.guide-card {
  text-align: center; padding: 24px 16px; border-radius: 14px;
  cursor: pointer; transition: all 0.35s; border: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}
.guide-card:hover { transform: translateY(-5px); box-shadow: 0 12px 30px rgba(231,76,60,0.15); }
.guide-icon { font-size: 48px; margin-bottom: 12px; }
.guide-title { font-size: 18px; font-weight: 600; margin-bottom: 8px; color: #2c3e50; }
.guide-desc { font-size: 13px; color: #888; line-height: 1.6; margin-bottom: 12px; }

/* 弹窗内容 */
.guide-content { max-height: 400px; overflow-y: auto; padding: 8px 0; }
.guide-item { padding: 14px 0; border-bottom: 1px dashed #f0e0e0; }
.guide-item:last-child { border-bottom: none; }
.guide-item-title { font-size: 16px; color: #e74c3c; margin-bottom: 6px; font-weight: 600; }
.guide-item-text { font-size: 14px; color: #666; line-height: 1.7; }

/* 响应式 */
@media (max-width: 992px) {
  .type-grid { grid-template-columns: repeat(2, 1fr); }
  .dish-grid { grid-template-columns: repeat(2, 1fr); }
  .category-grid { grid-template-columns: repeat(4, 1fr); }
}
@media (max-width: 600px) {
  .type-grid { grid-template-columns: 1fr; }
  .dish-grid { grid-template-columns: 1fr; }
  .category-grid { grid-template-columns: repeat(3, 1fr); }
  .guide-grid { grid-template-columns: 1fr; }
  .carousel-img, .banner-placeholder { height: 220px; }
  .section-title { font-size: 18px; }
}
</style>
