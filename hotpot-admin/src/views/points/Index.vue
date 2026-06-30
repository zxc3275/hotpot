<template>
  <div class="page-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="积分商品" name="goods">
        <div class="search-bar">
          <el-button type="primary" @click="openGoodsDialog()">新增商品</el-button>
        </div>
        <el-table :data="goodsList" border stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column label="图片" width="80"><template #default="{row}"><el-avatar :src="row.image" shape="square" /></template></el-table-column>
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="points" label="所需积分" width="100" />
          <el-table-column prop="stock" label="库存" width="80" />
          <el-table-column label="状态" width="80"><template #default="{row}"><el-switch :model-value="row.status===1" disabled /></template></el-table-column>
          <el-table-column label="操作" width="160">
            <template #default="{row}">
              <el-button size="small" @click="openGoodsDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="delGoods(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="兑换记录" name="exchanges">
        <el-table :data="exchanges" border stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="userId" label="用户ID" width="80" />
          <el-table-column prop="goodsName" label="商品" />
          <el-table-column prop="points" label="消耗积分" width="100" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="createTime" label="兑换时间" width="170" />
          <el-table-column label="状态" width="100"><template #default="{row}"><el-tag :type="row.status===1?'success':'warning'">{{row.status===1?'已处理':'待处理'}}</el-tag></template></el-table-column>
          <el-table-column label="操作" width="160">
            <template #default="{row}">
              <el-button v-if="row.status===0" size="small" type="primary" @click="handleExchange(row.id,1)">标记处理</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="goodsDialogVisible" :title="isEdit?'编辑商品':'新增商品'" width="500px">
      <el-form :model="goodsForm" label-width="80px">
        <el-form-item label="名称"><el-input v-model="goodsForm.name" /></el-form-item>
        <el-form-item label="商品图片">
          <div style="display:flex;align-items:flex-start;gap:12px">
            <el-upload :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false"
              :on-success="handleUpload" :before-upload="beforeUpload" accept="image/*"
              @click="uploadTarget=goodsForm">
              <div class="upload-box">
                <img v-if="goodsForm.image" :src="goodsForm.image" class="upload-img" />
                <div v-else class="upload-tip"><el-icon><Plus /></el-icon><span>上传</span></div>
              </div>
            </el-upload>
            <el-input v-model="goodsForm.image" placeholder="或输入图片URL" style="flex:1" />
          </div>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="goodsForm.description" type="textarea" /></el-form-item>
        <el-form-item label="所需积分"><el-input-number v-model="goodsForm.points" :min="0" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="goodsForm.stock" :min="0" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="goodsForm.sort" :min="0" /></el-form-item>
        <el-form-item label="状态"><el-switch :model-value="goodsForm.status===1" @change="v=>goodsForm.status=v?1:0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="goodsDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="saveGoods">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/api/request'

const uploadUrl = '/api/common/upload'
const uploadHeaders = { Authorization: 'Bearer ' + localStorage.getItem('token') }
let uploadTarget = null

const beforeUpload = (file) => {
  if (!file.type.startsWith('image/')) { ElMessage.error('只能上传图片'); return false }
  if (file.size / 1024 / 1024 > 10) { ElMessage.error('图片不能超过10MB'); return false }
  return true
}

const handleUpload = (response) => {
  if (response.code === 200 && uploadTarget) {
    uploadTarget.image = response.data.url
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const activeTab = ref('goods')
const goodsList = ref([])
const exchanges = ref([])
const goodsDialogVisible = ref(false)
const isEdit = ref(false)
const goodsForm = ref({})

const fetchGoods = async () => { const res = await request.get('/admin/points/goods'); goodsList.value = res.data }
const fetchExchanges = async () => { const res = await request.get('/admin/points/exchanges'); exchanges.value = res.data }

const openGoodsDialog = (row) => {
  isEdit.value = !!row
  goodsForm.value = row ? { ...row } : { name:'', image:'', description:'', points:100, stock:0, sort:0, status:1 }
  goodsDialogVisible.value = true
}

const saveGoods = async () => {
  await request.post('/admin/points/goods/save', goodsForm.value)
  ElMessage.success('保存成功')
  goodsDialogVisible.value = false
  fetchGoods()
}

const delGoods = async (id) => {
  await ElMessageBox.confirm('确定删除?', '提示', { type: 'warning' })
  await request.delete('/admin/points/goods/delete/' + id)
  ElMessage.success('删除成功')
  fetchGoods()
}

const handleExchange = async (id, status) => {
  await request.put('/admin/points/exchange/' + id, { status, remark: '' })
  ElMessage.success('处理成功')
  fetchExchanges()
}

onMounted(() => { fetchGoods(); fetchExchanges() })
</script>

<style scoped>
.upload-box { width: 100px; height: 100px; border: 2px dashed #dcdfe6; border-radius: 8px; display: flex; align-items: center; justify-content: center; cursor: pointer; overflow: hidden; transition: border-color 0.3s; }
.upload-box:hover { border-color: #409EFF; }
.upload-img { width: 100%; height: 100%; object-fit: contain; display: block; }
.upload-tip { text-align: center; color: #8c939d; }
.upload-tip .el-icon { font-size: 24px; display: block; margin-bottom: 2px; }
.upload-tip span { font-size: 11px; }
</style>
