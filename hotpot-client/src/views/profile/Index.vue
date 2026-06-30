<template>
  <div class="profile-page">
    <h2 class="page-title">个人中心</h2>

    <!-- User info -->
    <el-card class="section-card">
      <template #header>
        <span>基本信息</span>
      </template>
      <el-form :model="userForm" label-width="80px" :rules="userRules" ref="userFormRef">
        <el-form-item label="头像">
          <el-input v-model="userForm.avatar" placeholder="请输入头像URL" />
          <img
            v-if="userForm.avatar"
            :src="userForm.avatar"
            class="avatar-preview"
          />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="userForm.gender">
            <el-radio label="male">男</el-radio>
            <el-radio label="female">女</el-radio>
            <el-radio label="other">其他</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveProfile" :loading="saving">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Password change -->
    <el-card class="section-card">
      <template #header>
        <span>修改密码</span>
      </template>
      <el-form :model="passwordForm" label-width="100px" :rules="passwordRules" ref="passwordFormRef">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
            placeholder="请输入旧密码"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
            placeholder="请输入新密码"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="changePassword" :loading="changingPwd">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Balance -->
    <el-card class="section-card">
      <template #header>
        <span>账户余额</span>
      </template>
      <div class="balance-display">
        <span class="balance-label">当前余额：</span>
        <span class="balance-value">{{ (balance / 100).toFixed(2) }} 元</span>
        <el-button type="warning" size="small" @click="rechargeDialogVisible = true">充值</el-button>
      </div>
    </el-card>

    <!-- Recharge dialog -->
    <el-dialog v-model="rechargeDialogVisible" title="充值" width="400px">
      <el-form :model="rechargeForm" label-width="80px">
        <el-form-item label="充值金额">
          <el-input-number
            v-model="rechargeForm.amount"
            :min="1"
            :precision="2"
            placeholder="请输入充值金额"
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="rechargeForm.payMethod" placeholder="请选择支付方式" style="width:100%">
            <el-option label="微信支付" value="wechat" />
            <el-option label="支付宝" value="alipay" />
            <el-option label="银行卡" value="bank" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rechargeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRecharge" :loading="recharging">确认充值</el-button>
      </template>
    </el-dialog>

    <!-- Recharge records -->
    <el-card class="section-card">
      <template #header>
        <span>充值记录</span>
      </template>
      <el-table :data="rechargeRecords" stripe size="small" v-loading="recordsLoading">
        <el-table-column label="金额" width="120">
          <template #default="{ row }">
            {{ (row.amount / 100).toFixed(2) }} 元
          </template>
        </el-table-column>
        <el-table-column prop="payMethod" label="支付方式" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'" size="small">
              {{ row.status === 1 ? '成功' : '处理中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" min-width="160" />
      </el-table>
      <el-empty v-if="!recordsLoading && rechargeRecords.length === 0" description="暂无充值记录" />
    </el-card>

    <!-- Feedback -->
    <el-card class="section-card">
      <template #header>
        <span>意见反馈</span>
      </template>
      <el-input
        v-model="feedbackContent"
        type="textarea"
        :rows="4"
        placeholder="请输入您的意见和建议..."
      />
      <el-button
        type="primary"
        style="margin-top:12px"
        @click="submitFeedback"
        :loading="feedbackSubmitting"
      >
        提交反馈
      </el-button>
    </el-card>

    <!-- My feedback list -->
    <el-card class="section-card">
      <template #header>
        <span>我的反馈</span>
      </template>
      <el-table :data="feedbackList" stripe size="small" v-loading="feedbackLoading">
        <el-table-column prop="content" label="内容" min-width="250" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'" size="small">
              {{ row.status === 1 ? '已处理' : row.status === 0 ? '未处理' : '处理中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reply" label="回复" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>
      <el-empty v-if="!feedbackLoading && feedbackList.length === 0" description="暂无反馈记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

// User info
const userFormRef = ref(null)
const userForm = reactive({
  avatar: '',
  nickname: '',
  phone: '',
  email: '',
  gender: ''
})
const userRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}
const saving = ref(false)

// Password
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次密码输入不一致'))
  } else {
    callback()
  }
}
const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}
const changingPwd = ref(false)

// Balance
const balance = ref(0)
const rechargeDialogVisible = ref(false)
const rechargeForm = reactive({
  amount: 100,
  payMethod: 'wechat'
})
const recharging = ref(false)

// Recharge records
const rechargeRecords = ref([])
const recordsLoading = ref(false)

// Feedback
const feedbackContent = ref('')
const feedbackSubmitting = ref(false)
const feedbackList = ref([])
const feedbackLoading = ref(false)

// Fetch user info
async function fetchUserInfo() {
  try {
    const res = await request.get('/auth/userinfo')
    const data = res.data || {}
    userForm.avatar = data.avatar || ''
    userForm.nickname = data.nickname || ''
    userForm.phone = data.phone || ''
    userForm.email = data.email || ''
    userForm.gender = data.gender || ''
    balance.value = data.balance || 0
  } catch (err) {
    ElMessage.error('获取用户信息失败')
  }
}

async function saveProfile() {
  if (!userFormRef.value) return
  try {
    await userFormRef.value.validate()
    saving.value = true
    await request.put('/client/user/update', { ...userForm })
    ElMessage.success('保存成功')
  } catch (err) {
    if (err?.message) {
      ElMessage.warning('请完善表单信息')
    } else {
      ElMessage.error('保存失败')
    }
  } finally {
    saving.value = false
  }
}

async function changePassword() {
  if (!passwordFormRef.value) return
  try {
    await passwordFormRef.value.validate()
    changingPwd.value = true
    await request.put('/client/user/password', {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    passwordFormRef.value.resetFields()
  } catch (err) {
    if (err?.message) {
      ElMessage.warning('请完善密码信息')
    } else {
      ElMessage.error('密码修改失败')
    }
  } finally {
    changingPwd.value = false
  }
}

async function submitRecharge() {
  recharging.value = true
  try {
    await request.post('/client/user/recharge', {
      amount: rechargeForm.amount,
      payMethod: rechargeForm.payMethod
    })
    ElMessage.success('充值成功')
    rechargeDialogVisible.value = false
    fetchUserInfo()
    fetchRechargeRecords()
  } catch (err) {
    ElMessage.error('充值失败')
  } finally {
    recharging.value = false
  }
}

async function fetchRechargeRecords() {
  recordsLoading.value = true
  try {
    const res = await request.get('/client/user/recharge-records')
    rechargeRecords.value = res.data || []
  } catch (err) {
    ElMessage.error('获取充值记录失败')
  } finally {
    recordsLoading.value = false
  }
}

async function submitFeedback() {
  if (!feedbackContent.value.trim()) {
    ElMessage.warning('请输入反馈内容')
    return
  }
  feedbackSubmitting.value = true
  try {
    await request.post('/client/user/feedback', { content: feedbackContent.value })
    ElMessage.success('反馈提交成功')
    feedbackContent.value = ''
    fetchFeedbackList()
  } catch (err) {
    ElMessage.error('反馈提交失败')
  } finally {
    feedbackSubmitting.value = false
  }
}

async function fetchFeedbackList() {
  feedbackLoading.value = true
  try {
    const res = await request.get('/client/user/feedback')
    feedbackList.value = res.data || []
  } catch (err) {
    ElMessage.error('获取反馈列表失败')
  } finally {
    feedbackLoading.value = false
  }
}

onMounted(() => {
  fetchUserInfo()
  fetchRechargeRecords()
  fetchFeedbackList()
})
</script>

<style scoped>
.profile-page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}
.page-title {
  margin-bottom: 20px;
  font-size: 22px;
}
.section-card {
  margin-bottom: 16px;
}
.avatar-preview {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 50%;
  margin-left: 12px;
  vertical-align: middle;
  border: 2px solid #dcdfe6;
}
.balance-display {
  display: flex;
  align-items: center;
  gap: 12px;
}
.balance-label {
  font-size: 14px;
  color: #606266;
}
.balance-value {
  font-size: 24px;
  font-weight: 700;
  color: #e6a23c;
}
</style>
