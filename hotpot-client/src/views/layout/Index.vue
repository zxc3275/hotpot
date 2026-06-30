<template>
  <div class="app-layout">
    <header class="app-header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/')">🍲 火锅点餐</div>
        <nav class="nav-links">
          <router-link to="/">首页</router-link>
          <router-link to="/menu">点菜</router-link>
          <router-link to="/cart">
            购物车
            <el-badge v-if="cartCount" :value="cartCount" class="cart-badge" />
          </router-link>
          <router-link to="/orders">订单</router-link>
          <router-link to="/points">积分</router-link>
        </nav>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-area">
              <el-avatar :size="32" icon="UserFilled" />
              <span>{{ user.nickname || user.username || '用户' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>
    <main class="app-main">
      <router-view />
    </main>
    <footer class="app-footer">
      <p>© 2024 火锅点餐系统 | 让每一顿火锅都是一种享受</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/api/request'

const router = useRouter()
const user = ref({})
const cartCount = ref(0)

const fetchUser = async () => {
  try { const res = await request.get('/auth/userinfo'); user.value = res.data } catch {}
}
const fetchCartCount = async () => {
  try { const res = await request.get('/client/cart/list'); cartCount.value = res.data?.length || 0 } catch {}
}

const handleCommand = (cmd) => {
  if (cmd === 'logout') { localStorage.clear(); router.push('/login') }
  else if (cmd === 'profile') router.push('/profile')
}

onMounted(() => { fetchUser(); fetchCartCount() })
</script>

<style scoped>
.app-layout { min-height: 100vh; display: flex; flex-direction: column; }
.app-header {
  background: linear-gradient(135deg, #c0392b, #e74c3c);
  box-shadow: 0 2px 20px rgba(231,76,60,0.2);
  position: sticky; top: 0; z-index: 100;
}
.header-content { max-width: 1200px; margin: 0 auto; display: flex; align-items: center; padding: 0 20px; height: 64px; }
.logo { font-size: 24px; font-weight: 700; color: #fff; cursor: pointer; margin-right: 40px; white-space: nowrap; letter-spacing: 2px; }
.nav-links { display: flex; gap: 8px; flex: 1; }
.nav-links a {
  font-size: 14px; color: rgba(255,255,255,0.85); padding: 8px 16px; border-radius: 20px;
  transition: all 0.3s; position: relative; font-weight: 500;
}
.nav-links a:hover { color: #fff; background: rgba(255,255,255,0.15); }
.nav-links a.router-link-exact-active { color: #fff; background: rgba(255,255,255,0.2); }
.cart-badge { margin-left: 4px; }
.header-right { display: flex; align-items: center; }
.user-area { display: flex; align-items: center; gap: 8px; cursor: pointer; color: #fff; font-weight: 500; }
.app-main { flex: 1; }
.app-footer {
  background: linear-gradient(135deg, #2c3e50, #1a252f);
  color: #a0aec0; text-align: center; padding: 32px 20px; font-size: 13px; margin-top: 60px;
}
.app-footer p { margin: 4px 0; }
</style>
