<template>
  <div class="page-container">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- ==================== 菜品列表 ==================== -->
      <el-tab-pane label="菜品列表" name="dish">
        <div class="search-bar">
          <el-input
            v-model="dishQuery.keyword"
            placeholder="搜索菜品名称"
            clearable
            style="width: 260px"
            @keyup.enter="fetchDishList"
          />
          <el-button type="primary" @click="fetchDishList">搜索</el-button>
          <el-button type="success" @click="openDishDialog()">新增菜品</el-button>
        </div>

        <el-table :data="dishList" v-loading="dishLoading" stripe border>
          <el-table-column label="图片" width="80" align="center">
            <template #default="{ row }">
              <el-avatar v-if="row.image" :size="48" :src="row.image" shape="square" />
              <el-avatar v-else :size="48" shape="square">
                <el-icon :size="24"><PictureFilled /></el-icon>
              </el-avatar>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="名称" min-width="120" />
          <el-table-column prop="categoryName" label="分类" width="100" />
          <el-table-column label="价格" width="100" align="center">
            <template #default="{ row }">
              <span style="color: #e65d3a; font-weight: bold">
                &yen;{{ (row.price / 100).toFixed(2) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="辣度" width="140" align="center">
            <template #default="{ row }">
              <el-rate
                v-model="row.spicyLevel"
                disabled
                :max="5"
                style="height: 20px"
                v-if="row.spicyLevel != null"
              />
              <el-tag v-else type="info" size="small">未设置</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="sales" label="销量" width="80" align="center" />
          <el-table-column label="状态" width="80" align="center">
            <template #default="{ row }">
              <el-switch
                :model-value="row.status === 1"
                @change="(val) => handleStatusChange(row, val)"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="openDishDialog(row)">
                编辑
              </el-button>
              <el-button type="danger" link size="small" @click="handleDeleteDish(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div style="margin-top: 16px; display: flex; justify-content: flex-end">
          <el-pagination
            v-model:current-page="dishQuery.page"
            v-model:page-size="dishQuery.pageSize"
            :total="dishTotal"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="fetchDishList"
            @size-change="fetchDishList"
          />
        </div>
      </el-tab-pane>

      <!-- ==================== 菜品分类 ==================== -->
      <el-tab-pane label="菜品分类" name="category">
        <div class="search-bar">
          <el-button type="success" @click="openCategoryDialog()">新增分类</el-button>
        </div>

        <el-table :data="categoryList" v-loading="categoryLoading" stripe border>
          <el-table-column prop="name" label="名称" min-width="160" />
          <el-table-column label="图标" width="80" align="center">
            <template #default="{ row }">
              <el-avatar v-if="row.icon" :size="40" :src="row.icon" shape="square" />
              <el-tag v-else type="info" size="small">无</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="sort" label="排序" width="80" align="center" />
          <el-table-column label="状态" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="openCategoryDialog(row)">
                编辑
              </el-button>
              <el-button type="danger" link size="small" @click="handleDeleteCategory(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- ==================== 菜品弹窗 ==================== -->
    <el-dialog
      v-model="dishDialogVisible"
      :title="dishForm.id ? '编辑菜品' : '新增菜品'"
      width="640px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="dishFormRef" :model="dishForm" :rules="dishRules" label-width="90px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属分类" prop="categoryId">
              <el-select v-model="dishForm.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option
                  v-for="c in categoryList"
                  :key="c.id"
                  :label="c.name"
                  :value="c.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜品名称" prop="name">
              <el-input v-model="dishForm.name" placeholder="请输入菜品名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number
                v-model="dishForm.price"
                :min="0"
                :precision="2"
                :step="0.01"
                placeholder="请输入价格（元）"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原价" prop="originalPrice">
              <el-input-number
                v-model="dishForm.originalPrice"
                :min="0"
                :precision="2"
                :step="0.01"
                placeholder="请输入原价（元）"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="辣度" prop="spicyLevel">
              <el-rate v-model="dishForm.spicyLevel" :max="5" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="dishForm.sort" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="商品图片" prop="image">
          <div style="display:flex;align-items:flex-start;gap:16px">
            <div style="flex-shrink:0">
              <el-upload
                :action="uploadUrl"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleImageUploaded"
                :before-upload="beforeUpload"
                accept="image/*"
              >
                <div class="upload-area">
                  <img v-if="dishForm.image" :src="dishForm.image" class="upload-preview" />
                  <div v-else class="upload-placeholder">
                    <el-icon><Plus /></el-icon>
                    <span>点击上传</span>
                  </div>
                </div>
              </el-upload>
            </div>
            <div style="flex:1">
              <el-input v-model="dishForm.image" placeholder="或手动输入图片URL" />
              <span style="font-size:12px;color:#999">支持 jpg/png/webp，大小不超过 10MB</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="dishForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入菜品描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="dishForm.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dishDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="dishSaving" @click="handleSaveDish">保存</el-button>
      </template>
    </el-dialog>

    <!-- ==================== 分类弹窗 ==================== -->
    <el-dialog
      v-model="categoryDialogVisible"
      :title="categoryForm.id ? '编辑分类' : '新增分类'"
      width="500px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="categoryFormRef" :model="categoryForm" :rules="categoryRules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="categoryForm.icon" placeholder="请输入图标URL地址" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="categoryForm.sort" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="categoryForm.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="categorySaving" @click="handleSaveCategory">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/api/request'

// 文件上传配置
const uploadUrl = '/api/common/upload'
const uploadHeaders = { Authorization: 'Bearer ' + localStorage.getItem('token') }

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isImage) { ElMessage.error('只能上传图片文件'); return false }
  if (!isLt10M) { ElMessage.error('图片大小不能超过 10MB'); return false }
  return true
}

const handleImageUploaded = (response) => {
  if (response.code === 200) {
    dishForm.image = response.data.url
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// ==================== 菜品列表 ====================
const activeTab = ref('dish')
const dishLoading = ref(false)
const dishSaving = ref(false)
const dishList = ref([])
const dishTotal = ref(0)
const dishDialogVisible = ref(false)
const dishFormRef = ref(null)

const dishQuery = reactive({
  keyword: '',
  page: 1,
  pageSize: 10
})

const dishForm = reactive({
  id: null,
  categoryId: null,
  name: '',
  price: 0,
  originalPrice: 0,
  spicyLevel: 0,
  description: '',
  image: '',
  status: 1,
  sort: 0
})

const dishRules = {
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  name: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const fetchDishList = async () => {
  dishLoading.value = true
  try {
    const res = await request.get('/admin/dish/list', {
      params: {
        keyword: dishQuery.keyword,
        page: dishQuery.page,
        pageSize: dishQuery.pageSize
      }
    })
    const list = Array.isArray(res.data) ? res.data : (res.data.records || res.data.list || [])
    dishList.value = list
    dishTotal.value = res.data.total || list.length
  } catch {
    // error handled by interceptor
  } finally {
    dishLoading.value = false
  }
}

const openDishDialog = (row) => {
  resetDishForm()
  if (row) {
    Object.assign(dishForm, {
      id: row.id,
      categoryId: row.categoryId,
      name: row.name,
      price: row.price != null ? row.price / 100 : 0,
      originalPrice: row.originalPrice != null ? row.originalPrice / 100 : 0,
      spicyLevel: row.spicyLevel ?? 0,
      description: row.description || '',
      image: row.image || '',
      status: row.status,
      sort: row.sort ?? 0
    })
  }
  dishDialogVisible.value = true
}

const resetDishForm = () => {
  Object.assign(dishForm, {
    id: null,
    categoryId: null,
    name: '',
    price: 0,
    originalPrice: 0,
    spicyLevel: 0,
    description: '',
    image: '',
    status: 1,
    sort: 0
  })
}

const handleSaveDish = async () => {
  const valid = await dishFormRef.value?.validate().catch(() => false)
  if (!valid) return

  dishSaving.value = true
  try {
    await request.post('/admin/dish/save', {
      id: dishForm.id,
      categoryId: dishForm.categoryId,
      name: dishForm.name,
      price: Math.round(dishForm.price * 100),
      originalPrice: Math.round(dishForm.originalPrice * 100),
      spicyLevel: dishForm.spicyLevel,
      description: dishForm.description,
      image: dishForm.image,
      status: dishForm.status,
      sort: dishForm.sort
    })
    ElMessage.success(dishForm.id ? '编辑成功' : '新增成功')
    dishDialogVisible.value = false
    fetchDishList()
  } catch {
    // error handled by interceptor
  } finally {
    dishSaving.value = false
  }
}

const handleDeleteDish = (row) => {
  ElMessageBox.confirm(`确定要删除菜品「${row.name}」吗？`, '删除确认', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
    .then(async () => {
      await request.delete(`/admin/dish/delete/${row.id}`)
      ElMessage.success('删除成功')
      fetchDishList()
    })
    .catch(() => {})
}

const handleStatusChange = async (row, val) => {
  try {
    await request.post('/admin/dish/save', {
      id: row.id,
      status: val ? 1 : 0
    })
    row.status = val ? 1 : 0
    ElMessage.success(val ? '已启用' : '已禁用')
  } catch {
    // error handled by interceptor
  }
}

// ==================== 分类管理 ====================
const categoryLoading = ref(false)
const categorySaving = ref(false)
const categoryList = ref([])
const categoryDialogVisible = ref(false)
const categoryFormRef = ref(null)

const categoryForm = reactive({
  id: null,
  name: '',
  icon: '',
  sort: 0,
  status: 1
})

const categoryRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const fetchCategoryList = async () => {
  categoryLoading.value = true
  try {
    const res = await request.get('/admin/dish/categories')
    categoryList.value = res.data || []
  } catch {
    // error handled by interceptor
  } finally {
    categoryLoading.value = false
  }
}

const openCategoryDialog = (row) => {
  resetCategoryForm()
  if (row) {
    Object.assign(categoryForm, {
      id: row.id,
      name: row.name,
      icon: row.icon || '',
      sort: row.sort ?? 0,
      status: row.status
    })
  }
  categoryDialogVisible.value = true
}

const resetCategoryForm = () => {
  Object.assign(categoryForm, {
    id: null,
    name: '',
    icon: '',
    sort: 0,
    status: 1
  })
}

const handleSaveCategory = async () => {
  const valid = await categoryFormRef.value?.validate().catch(() => false)
  if (!valid) return

  categorySaving.value = true
  try {
    await request.post('/admin/dish/category/save', {
      id: categoryForm.id,
      name: categoryForm.name,
      icon: categoryForm.icon,
      sort: categoryForm.sort,
      status: categoryForm.status
    })
    ElMessage.success(categoryForm.id ? '编辑成功' : '新增成功')
    categoryDialogVisible.value = false
    fetchCategoryList()
  } catch {
    // error handled by interceptor
  } finally {
    categorySaving.value = false
  }
}

const handleDeleteCategory = (row) => {
  ElMessageBox.confirm(`确定要删除分类「${row.name}」吗？`, '删除确认', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
    .then(async () => {
      await request.delete(`/admin/dish/category/delete/${row.id}`)
      ElMessage.success('删除成功')
      fetchCategoryList()
    })
    .catch(() => {})
}

// ==================== 初始化 ====================
onMounted(() => {
  fetchDishList()
  fetchCategoryList()
})
</script>

<style scoped>
.page-container {
  padding: 0;
}

.search-bar {
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}

:deep(.el-tabs__content) {
  padding: 16px;
}
/* 图片上传 - 强约束防溢出 */
.upload-area {
  width: 120px; height: 120px; border: 2px dashed #dcdfe6; border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: border-color 0.3s; overflow: hidden;
  position: relative; box-sizing: border-box;
}
.upload-area:hover { border-color: #409EFF; }
:deep(.el-upload) { display: inline-block; width: 120px; height: 120px; vertical-align: top; }
:deep(.el-upload--text) { width: 120px; height: 120px; border: none; }
.upload-area :deep(img) { max-width: 100%; max-height: 100%; object-fit: contain; display: block; }
.upload-preview { width: 100%; height: 100%; object-fit: contain; display: block; }
.upload-placeholder { text-align: center; color: #8c939d; }
.upload-placeholder .el-icon { font-size: 28px; display: block; margin-bottom: 4px; }
.upload-placeholder span { font-size: 12px; }
</style>
