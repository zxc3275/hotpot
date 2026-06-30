<template>
  <div class="page-container">
    <el-card>
      <template #header><span>个人设置</span></template>
      <el-form :model="form" label-width="100px" style="max-width:500px">
        <el-form-item label="用户名"><el-input v-model="form.username" disabled /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :value="0">保密</el-radio>
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="save">保存</el-button></el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top:20px">
      <template #header><span>修改密码</span></template>
      <el-form :model="pwdForm" label-width="100px" style="max-width:500px">
        <el-form-item label="原密码"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
        <el-form-item label="确认密码"><el-input v-model="pwdForm.confirmPassword" type="password" show-password /></el-form-item>
        <el-form-item><el-button type="primary" @click="changePwd">修改密码</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const form = ref({})
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

onMounted(async () => {
  const res = await request.get('/auth/userinfo')
  form.value = res.data
})

const save = async () => {
  await request.put('/client/user/update', form.value)
  ElMessage.success('保存成功')
}

const changePwd = async () => {
  if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) {
    ElMessage.error('两次密码不一致'); return
  }
  await request.put('/client/user/password', pwdForm.value)
  ElMessage.success('密码修改成功')
  pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
}
</script>
