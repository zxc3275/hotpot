<template>
  <div class="page-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="火锅类型" name="types">
        <div class="search-bar">
          <el-button type="primary" @click="openTypeDialog()">新增类型</el-button>
        </div>
        <el-table :data="types" border stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="description" label="描述" />
          <el-table-column label="操作" width="160">
            <template #default="{row}">
              <el-button size="small" @click="openTypeDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="delType(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="锅底管理" name="bases">
        <div class="search-bar">
          <el-button type="primary" @click="openBaseDialog()">新增锅底</el-button>
        </div>
        <el-table :data="bases" border stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column label="图片" width="80"><template #default="{row}"><el-avatar :src="row.image" shape="square" /></template></el-table-column>
          <el-table-column prop="name" label="名称" />
          <el-table-column label="价格" width="100"><template #default="{row}">{{(row.price/100).toFixed(2)}}元</template></el-table-column>
          <el-table-column prop="potType" label="锅型" width="100"><template #default="{row}"><el-tag>{{{single:'单锅',double:'鸳鸯',nine:'九宫格'}[row.potType]||row.potType}}</el-tag></template></el-table-column>
          <el-table-column label="操作" width="160">
            <template #default="{row}">
              <el-button size="small" @click="openBaseDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="delBase(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="火锅指南" name="guides">
        <div class="search-bar">
          <el-select v-model="guideType" placeholder="指南类型" clearable style="width:150px" @change="fetchGuides">
            <el-option label="涮烫指南" value="cooking" />
            <el-option label="蘸料指南" value="dipping" />
            <el-option label="烹饪指南" value="blanch" />
          </el-select>
          <el-button type="primary" @click="openGuideDialog()">新增指南</el-button>
        </div>
        <el-table :data="guides" border stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="type" label="类型" width="100"><template #default="{row}"><el-tag>{{{cooking:'涮烫',dipping:'蘸料',blanch:'烹饪'}[row.type]}}</el-tag></template></el-table-column>
          <el-table-column label="操作" width="160">
            <template #default="{row}">
              <el-button size="small" @click="openGuideDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="delGuide(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- Type Dialog -->
    <el-dialog v-model="typeDialogVisible" :title="isTypeEdit?'编辑类型':'新增类型'" width="500px">
      <el-form :model="typeForm" label-width="80px">
        <el-form-item label="名称"><el-input v-model="typeForm.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="typeForm.description" /></el-form-item>
<el-form-item label="图片">
          <div style="display:flex;align-items:flex-start;gap:12px">
            <el-upload :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false"
              :on-success="handleImageUploaded" :before-upload="beforeUpload" accept="image/*"
              @click="currentUploadTarget=typeForm">
              <div class="upload-box">
                <img v-if="typeForm.image" :src="typeForm.image" class="upload-img" />
                <div v-else class="upload-tip"><el-icon><Plus /></el-icon><span>上传</span></div>
              </div>
            </el-upload>
            <el-input v-model="typeForm.image" placeholder="或输入图片URL" style="flex:1" />
          </div>
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="typeForm.sort" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="saveType">保存</el-button>
      </template>
    </el-dialog>

    <!-- Base Dialog -->
    <el-dialog v-model="baseDialogVisible" :title="isBaseEdit?'编辑锅底':'新增锅底'" width="500px">
      <el-form :model="baseForm" label-width="80px">
        <el-form-item label="类型"><el-select v-model="baseForm.typeId"><el-option v-for="t in types" :key="t.id" :label="t.name" :value="t.id" /></el-select></el-form-item>
        <el-form-item label="图片">
          <div style="display:flex;align-items:flex-start;gap:12px">
            <el-upload :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false" :on-success="handleImageUploaded" :before-upload="beforeUpload" accept="image/*" @click="currentUploadTarget=baseForm">
              <div class="upload-box"><img v-if="baseForm.image" :src="baseForm.image" class="upload-img" /><div v-else class="upload-tip"><el-icon><Plus /></el-icon><span>上传</span></div></div>
            </el-upload>
            <el-input v-model="baseForm.image" placeholder="或输入图片URL" style="flex:1" />
          </div>
        </el-form-item>
        <el-form-item label="名称"><el-input v-model="baseForm.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="baseForm.description" type="textarea" /></el-form-item>
        
        <el-form-item label="价格(分)"><el-input-number v-model="baseForm.price" :min="0" /></el-form-item>
        <el-form-item label="辣度范围"><el-input-number v-model="baseForm.spicyMin" :min="0" :max="3" /> - <el-input-number v-model="baseForm.spicyMax" :min="0" :max="3" /></el-form-item>
        <el-form-item label="麻度范围"><el-input-number v-model="baseForm.numMin" :min="0" :max="3" /> - <el-input-number v-model="baseForm.numMax" :min="0" :max="3" /></el-form-item>
        <el-form-item label="锅型"><el-select v-model="baseForm.potType"><el-option label="单锅" value="single" /><el-option label="鸳鸯" value="double" /><el-option label="九宫格" value="nine" /></el-select></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="baseForm.sort" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="baseDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="saveBase">保存</el-button>
      </template>
    </el-dialog>

    <!-- Guide Dialog -->
    <el-dialog v-model="guideDialogVisible" :title="isGuideEdit?'编辑指南':'新增指南'" width="600px">
      <el-form :model="guideForm" label-width="80px">
        <el-form-item label="标题"><el-input v-model="guideForm.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="guideForm.content" type="textarea" :rows="6" /></el-form-item>
        
        <el-form-item label="图片">
          <div style="display:flex;align-items:flex-start;gap:12px">
            <el-upload :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false" :on-success="handleImageUploaded" :before-upload="beforeUpload" accept="image/*" @click="currentUploadTarget=guideForm">
              <div class="upload-box"><img v-if="guideForm.image" :src="guideForm.image" class="upload-img" /><div v-else class="upload-tip"><el-icon><Plus /></el-icon><span>上传</span></div></div>
            </el-upload>
            <el-input v-model="guideForm.image" placeholder="或输入图片URL" style="flex:1" />
          </div>
        </el-form-item>
        <el-form-item label="类型"><el-select v-model="guideForm.type"><el-option label="涮烫指南" value="cooking" /><el-option label="蘸料指南" value="dipping" /><el-option label="烹饪指南" value="blanch" /></el-select></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="guideForm.sort" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="guideDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="saveGuide">保存</el-button>
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
let currentUploadTarget = null

const beforeUpload = (file) => {
  if (!file.type.startsWith('image/')) { ElMessage.error('只能上传图片'); return false }
  if (file.size / 1024 / 1024 > 10) { ElMessage.error('图片不能超过10MB'); return false }
  return true
}

const handleImageUploaded = (response) => {
  if (response.code === 200 && currentUploadTarget) {
    currentUploadTarget.image = response.data.url
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const activeTab = ref('types')
const guideType = ref('')
const types = ref([])
const bases = ref([])
const guides = ref([])
const typeDialogVisible = ref(false), isTypeEdit = ref(false)
const baseDialogVisible = ref(false), isBaseEdit = ref(false)
const guideDialogVisible = ref(false), isGuideEdit = ref(false)
const typeForm = ref({}), baseForm = ref({}), guideForm = ref({})

const fetchAll = async () => {
  const [t, b, g] = await Promise.all([
    request.get('/admin/guide/types'),
    request.get('/admin/guide/bases'),
    request.get('/admin/guide/list' + (guideType.value ? '?type=' + guideType.value : ''))
  ])
  types.value = t.data; bases.value = b.data; guides.value = g.data
}

// Type CRUD
const openTypeDialog = (row) => { isTypeEdit.value = !!row; typeForm.value = row ? { ...row } : { name:'', description:'', image:'', sort:0, status:1 }; typeDialogVisible.value = true }
const saveType = async () => { await request.post('/admin/guide/type/save', typeForm.value); ElMessage.success('保存成功'); typeDialogVisible.value = false; fetchAll() }
const delType = async (id) => { await ElMessageBox.confirm('确定删除?','提示',{type:'warning'}); await request.delete('/admin/guide/type/delete/'+id); ElMessage.success('删除成功'); fetchAll() }

// Base CRUD
const openBaseDialog = (row) => { isBaseEdit.value = !!row; baseForm.value = row ? { ...row } : { name:'', description:'', image:'', price:0, spicyMin:0, spicyMax:3, numMin:0, numMax:3, potType:'single', sort:0, status:1 }; baseDialogVisible.value = true }
const saveBase = async () => { await request.post('/admin/guide/base/save', baseForm.value); ElMessage.success('保存成功'); baseDialogVisible.value = false; fetchAll() }
const delBase = async (id) => { await ElMessageBox.confirm('确定删除?','提示',{type:'warning'}); await request.delete('/admin/guide/base/delete/'+id); ElMessage.success('删除成功'); fetchAll() }

// Guide CRUD
const openGuideDialog = (row) => { isGuideEdit.value = !!row; guideForm.value = row ? { ...row } : { title:'', content:'', image:'', type:'cooking', sort:0, status:1 }; guideDialogVisible.value = true }
const saveGuide = async () => { await request.post('/admin/guide/save', guideForm.value); ElMessage.success('保存成功'); guideDialogVisible.value = false; fetchAll() }
const delGuide = async (id) => { await ElMessageBox.confirm('确定删除?','提示',{type:'warning'}); await request.delete('/admin/guide/delete/'+id); ElMessage.success('删除成功'); fetchAll() }

const fetchGuides = () => { request.get('/admin/guide/list'+(guideType.value?'?type='+guideType.value:'')).then(r => guides.value = r.data) }

onMounted(fetchAll)
</script>

<style scoped>
.upload-box {
  width: 100px; height: 100px; border: 2px dashed #dcdfe6; border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; overflow: hidden; transition: border-color 0.3s;
}
.upload-box:hover { border-color: #409EFF; }
.upload-img { width: 100%; height: 100%; object-fit: contain; display: block; }
.upload-tip { text-align: center; color: #8c939d; }
.upload-tip .el-icon { font-size: 24px; display: block; margin-bottom: 2px; }
.upload-tip span { font-size: 11px; }
</style>
