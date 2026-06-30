<template>
  <div class="login-wrapper">
    <!-- Decorative background elements -->
    <div class="bg-decoration">
      <div class="steam steam-1"></div>
      <div class="steam steam-2"></div>
      <div class="steam steam-3"></div>
      <div class="steam steam-4"></div>
      <div class="steam steam-5"></div>
      <div class="steam steam-6"></div>
      <div class="pepper pepper-1">🌶️</div>
      <div class="pepper pepper-2">🌶️</div>
      <div class="pepper pepper-3">🌶️</div>
      <div class="pepper pepper-4">🌶️</div>
      <div class="hotpot-icon">🍲</div>
      <div class="fire fire-1">🔥</div>
      <div class="fire fire-2">🔥</div>
    </div>

    <div class="login-container">
      <div class="brand-header">
        <span class="brand-icon">🍲</span>
        <h1 class="brand-title">Hotpot Haven</h1>
        <p class="brand-subtitle">热辣鲜香 · 即刻畅享</p>
      </div>

      <el-card class="login-card" shadow="always">
        <el-tabs v-model="activeTab" class="auth-tabs" :stretch="true">
          <!-- Login Tab -->
          <el-tab-pane label="登录" name="login">
            <el-form
              ref="loginFormRef"
              :model="loginForm"
              :rules="loginRules"
              label-position="top"
              @keyup.enter="handleLogin"
            >
              <el-form-item label="用户名" prop="username">
                <el-input
                  v-model="loginForm.username"
                  placeholder="请输入用户名"
                  prefix-icon="User"
                  size="large"
                  clearable
                />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="请输入密码"
                  prefix-icon="Lock"
                  size="large"
                  show-password
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="danger"
                  size="large"
                  class="submit-btn"
                  :loading="loginLoading"
                  @click="handleLogin"
                >
                  {{ loginLoading ? '登录中...' : '登 录' }}
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- Register Tab -->
          <el-tab-pane label="注册" name="register">
            <el-form
              ref="registerFormRef"
              :model="registerForm"
              :rules="registerRules"
              label-position="top"
              @keyup.enter="handleRegister"
            >
              <el-form-item label="用户名" prop="username">
                <el-input
                  v-model="registerForm.username"
                  placeholder="请输入用户名"
                  prefix-icon="User"
                  size="large"
                  clearable
                />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input
                  v-model="registerForm.password"
                  type="password"
                  placeholder="请输入密码"
                  prefix-icon="Lock"
                  size="large"
                  show-password
                />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="registerForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入密码"
                  prefix-icon="Lock"
                  size="large"
                  show-password
                />
              </el-form-item>
              <el-form-item label="昵称" prop="nickname">
                <el-input
                  v-model="registerForm.nickname"
                  placeholder="请输入昵称"
                  prefix-icon="UserFilled"
                  size="large"
                  clearable
                />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input
                  v-model="registerForm.phone"
                  placeholder="请输入手机号"
                  prefix-icon="Phone"
                  size="large"
                  clearable
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="danger"
                  size="large"
                  class="submit-btn"
                  :loading="registerLoading"
                  @click="handleRegister"
                >
                  {{ registerLoading ? '注册中...' : '注 册' }}
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const router = useRouter()
const activeTab = ref('login')

// ── Login ────────────────────────────────────────
const loginFormRef = ref(null)
const loginLoading = ref(false)
const loginForm = reactive({
  username: '',
  password: ''
})
const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  try {
    await loginFormRef.value.validate()
  } catch {
    return
  }
  loginLoading.value = true
  try {
    const res = await request.post('/auth/login', {
      username: loginForm.username,
      password: loginForm.password
    })
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('username', loginForm.username)
    ElMessage.success('登录成功，欢迎回来！')
    router.push('/')
  } catch {
    // Error already handled by interceptor
  } finally {
    loginLoading.value = false
  }
}

// ── Register ─────────────────────────────────────
const registerFormRef = ref(null)
const registerLoading = ref(false)
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  phone: ''
})

const validateConfirmPassword = (_rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  try {
    await registerFormRef.value.validate()
  } catch {
    return
  }
  registerLoading.value = true
  try {
    // Register
    await request.post('/auth/register', {
      username: registerForm.username,
      password: registerForm.password,
      confirmPassword: registerForm.confirmPassword,
      nickname: registerForm.nickname,
      phone: registerForm.phone
    })
    // Auto-login after register
    const loginRes = await request.post('/auth/login', {
      username: registerForm.username,
      password: registerForm.password
    })
    localStorage.setItem('token', loginRes.data.token)
    localStorage.setItem('username', registerForm.username)
    ElMessage.success('注册成功，欢迎加入！')
    router.push('/')
  } catch {
    // Error already handled by interceptor
  } finally {
    registerLoading.value = false
  }
}
</script>

<style scoped>
/* ── Full-screen wrapper ──────────────────────── */
.login-wrapper {
  position: relative;
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #5d1010 0%, #a3320b 25%, #c73e1d 50%, #d44824 75%, #8b1a0e 100%);
  overflow: hidden;
}

/* ── Decorative background ───────────────────── */
.bg-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

/* Steam / vapor wisps */
.steam {
  position: absolute;
  bottom: -10%;
  width: 80px;
  height: 120px;
  background: radial-gradient(ellipse at center, rgba(255,255,255,0.12) 0%, transparent 70%);
  border-radius: 50%;
  animation: rise ease-in-out infinite;
}
.steam-1 { left: 10%; animation-duration: 4s; animation-delay: 0s; width: 60px; height: 90px; }
.steam-2 { left: 25%; animation-duration: 5s; animation-delay: 1.5s; width: 80px; height: 110px; }
.steam-3 { left: 45%; animation-duration: 4.5s; animation-delay: 3s; width: 70px; height: 100px; }
.steam-4 { left: 65%; animation-duration: 5.5s; animation-delay: 0.8s; width: 75px; height: 105px; }
.steam-5 { left: 80%; animation-duration: 4.2s; animation-delay: 2.2s; width: 65px; height: 95px; }
.steam-6 { left: 92%; animation-duration: 5.8s; animation-delay: 3.8s; width: 55px; height: 85px; }

@keyframes rise {
  0% { transform: translateY(0) scale(1); opacity: 0.3; }
  50% { opacity: 0.8; }
  100% { transform: translateY(-110vh) scale(2.5); opacity: 0; }
}

/* Chili peppers floating */
.pepper {
  position: absolute;
  font-size: 2rem;
  animation: float 6s ease-in-out infinite;
  opacity: 0.5;
}
.pepper-1 { top: 8%; left: 5%; animation-duration: 5s; animation-delay: 0s; }
.pepper-2 { top: 15%; right: 8%; animation-duration: 6s; animation-delay: 1s; }
.pepper-3 { bottom: 20%; left: 3%; animation-duration: 5.5s; animation-delay: 2s; }
.pepper-4 { bottom: 12%; right: 5%; animation-duration: 6.5s; animation-delay: 0.5s; }

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  33% { transform: translateY(-15px) rotate(5deg); }
  66% { transform: translateY(-8px) rotate(-5deg); }
}

/* Hotpot icon */
.hotpot-icon {
  position: absolute;
  top: 5%;
  right: 12%;
  font-size: 4rem;
  animation: float 5s ease-in-out infinite;
  opacity: 0.35;
}

/* Fire decorations */
.fire {
  position: absolute;
  font-size: 2.5rem;
  animation: flicker 1.5s ease-in-out infinite;
  opacity: 0.4;
}
.fire-1 { bottom: 25%; left: 15%; animation-delay: 0s; }
.fire-2 { bottom: 18%; right: 18%; animation-delay: 0.75s; }

@keyframes flicker {
  0%, 100% { transform: scale(1); opacity: 0.4; }
  50% { transform: scale(1.2); opacity: 0.7; }
}

/* ── Card container ──────────────────────────── */
.login-container {
  position: relative;
  z-index: 1;
  width: 440px;
  max-width: 90vw;
  animation: fadeInUp 0.8s ease-out;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Brand header */
.brand-header {
  text-align: center;
  margin-bottom: 24px;
}

.brand-icon {
  font-size: 3rem;
  display: block;
  margin-bottom: 8px;
  animation: float 4s ease-in-out infinite;
}

.brand-title {
  margin: 0;
  font-size: 2rem;
  font-weight: 800;
  color: #fff;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.brand-subtitle {
  margin: 6px 0 0;
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.75);
  letter-spacing: 4px;
}

/* Login card */
.login-card {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.35), 0 0 0 1px rgba(255, 255, 255, 0.1);
  overflow: hidden;
}

.login-card :deep(.el-card__body) {
  padding: 28px 32px 32px;
}

/* Tabs styling */
.auth-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.auth-tabs :deep(.el-tabs__item) {
  font-size: 1.05rem;
  font-weight: 600;
  color: #999;
  transition: color 0.3s;
}

.auth-tabs :deep(.el-tabs__item.is-active) {
  color: #c73e1d;
}

.auth-tabs :deep(.el-tabs__active-bar) {
  background: linear-gradient(90deg, #c73e1d, #d44824);
  height: 3px;
  border-radius: 2px;
}

/* Form item overrides */
:deep(.el-form-item__label) {
  color: #444;
  font-weight: 600;
  font-size: 0.9rem;
  padding-bottom: 4px;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #e0e0e0 inset;
  transition: box-shadow 0.3s, border-color 0.3s;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c73e1d inset;
}

:deep(.el-input.is-focus .el-input__wrapper) {
  box-shadow: 0 0 0 1px #c73e1d inset, 0 0 0 3px rgba(199, 62, 29, 0.12);
}

/* Submit button */
.submit-btn {
  width: 100%;
  border-radius: 10px;
  font-size: 1.05rem;
  font-weight: 700;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #c73e1d, #a3320b);
  border: none;
  height: 48px;
  margin-top: 8px;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(199, 62, 29, 0.35);
}

.submit-btn:hover {
  background: linear-gradient(135deg, #d44824, #b83a18);
  box-shadow: 0 6px 20px rgba(199, 62, 29, 0.5);
  transform: translateY(-1px);
}

.submit-btn:active {
  transform: translateY(0);
}

.submit-btn.is-loading {
  transform: none;
}
</style>
