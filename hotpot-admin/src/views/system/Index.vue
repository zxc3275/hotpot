<template>
  <div class="page-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="用户管理" name="users">
        <el-table :data="users" border stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column label="头像" width="80"><template #default="{row}"><el-avatar :src="row.avatar" /></template></el-table-column>
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="nickname" label="昵称" />
          <el-table-column prop="phone" label="手机号" />
          <el-table-column label="余额" width="100"><template #default="{row}">{{(row.balance/100).toFixed(2)}}元</template></el-table-column>
          <el-table-column prop="points" label="积分" width="80" />
          <el-table-column label="状态" width="80"><template #default="{row}"><el-switch :model-value="row.status===1" disabled /></template></el-table-column>
          <el-table-column prop="createTime" label="注册时间" width="170" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="字典管理" name="dicts">
        <div class="search-bar">
          <el-button type="primary" @click="openDictDialog()">新增字典</el-button>
        </div>
        <el-table :data="dicts" border stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="dictType" label="类型" width="120" />
          <el-table-column prop="dictKey" label="键" width="150" />
          <el-table-column prop="dictValue" label="值" />
          <el-table-column prop="sort" label="排序" width="80" />
          <el-table-column label="操作" width="160">
            <template #default="{row}">
              <el-button size="small" @click="openDictDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="delDict(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="轮播图管理" name="carousels">
        <div class="search-bar">
          <el-button type="primary" @click="openCarouselDialog()">新增轮播图</el-button>
        </div>
        <el-table :data="carousels" border stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column label="图片" width="120"><template #default="{row}"><el-image :src="row.imageUrl" style="width:100px;height:50px" fit="cover" /></template></el-table-column>
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="sort" label="排序" width="80" />
          <el-table-column label="类型" width="100"><template #default="{row}"><el-tag>{{row.type===1?'用户端':'管理端'}}</el-tag></template></el-table-column>
          <el-table-column label="操作" width="160">
            <template #default="{row}">
              <el-button size="small" @click="openCarouselDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="delCarousel(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="意见反馈" name="feedbacks">
        <el-table :data="feedbacks" border stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="userId" label="用户ID" width="80" />
          <el-table-column prop="content" label="反馈内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="reply" label="回复" min-width="150" show-overflow-tooltip />
          <el-table-column label="状态" width="100"><template #default="{row}"><el-tag :type="row.status===1?'success':'warning'">{{row.status===1?'已回复':'待回复'}}</el-tag></template></el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{row}">
              <el-button v-if="row.status===0" size="small" type="primary" @click="replyFeedback(row)">回复</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- Dict Dialog -->
    <el-dialog v-model="dictDialogVisible" :title="isDictEdit?'编辑字典':'新增字典'" width="500px">
      <el-form :model="dictForm" label-width="80px">
        <el-form-item label="类型"><el-input v-model="dictForm.dictType" /></el-form-item>
        <el-form-item label="键"><el-input v-model="dictForm.dictKey" /></el-form-item>
        <el-form-item label="值"><el-input v-model="dictForm.dictValue" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="dictForm.sort" :min="0" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="dictForm.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dictDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="saveDict">保存</el-button>
      </template>
    </el-dialog>

    <!-- Carousel Dialog -->
    <el-dialog v-model="carouselDialogVisible" :title="isCarouselEdit?'编辑轮播图':'新增轮播图'" width="500px">
      <el-form :model="carouselForm" label-width="80px">
        <el-form-item label="标题"><el-input v-model="carouselForm.title" /></el-form-item>
        <el-form-item label="轮播图片">
          <div style="display:flex;align-items:flex-start;gap:12px">
            <el-upload :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false"
              :on-success="handleUpload" :before-upload="beforeUpload" accept="image/*"
              @click="upTarget=carouselForm">
              <div class="upload-box">
                <img v-if="carouselForm.imageUrl" :src="carouselForm.imageUrl" class="upload-img" />
                <div v-else class="upload-tip"><el-icon><Plus /></el-icon><span>上传</span></div>
              </div>
            </el-upload>
            <el-input v-model="carouselForm.imageUrl" placeholder="或输入图片URL" style="flex:1" />
          </div>
        </el-form-item>
        <el-form-item label="链接"><el-input v-model="carouselForm.linkUrl" /></el-form-item>
        <el-form-item label="类型"><el-select v-model="carouselForm.type"><el-option label="用户端" :value="1" /><el-option label="管理端" :value="2" /></el-select></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="carouselForm.sort" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="carouselDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="saveCarousel">保存</el-button>
      </template>
    </el-dialog>

    <!-- Feedback Reply Dialog -->
    <el-dialog v-model="feedbackDialogVisible" title="回复反馈" width="500px">
      <el-form :model="feedbackForm" label-width="80px">
        <el-form-item label="反馈内容"><p style="color:#666">{{feedbackForm.content}}</p></el-form-item>
        <el-form-item label="回复"><el-input v-model="feedbackForm.reply" type="textarea" :rows="4" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="feedbackDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitReply">提交回复</el-button>
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
let upTarget = null

const beforeUpload = (file) => {
  if (!file.type.startsWith('image/')) { ElMessage.error('只能上传图片'); return false }
  if (file.size / 1024 / 1024 > 10) { ElMessage.error('图片不能超过10MB'); return false }
  return true
}

const handleUpload = (response) => {
  if (response.code === 200 && upTarget) {
    upTarget.imageUrl = response.data.url
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const activeTab = ref('users')
const users = ref([]), dicts = ref([]), carousels = ref([]), feedbacks = ref([])
const dictDialogVisible = ref(false), isDictEdit = ref(false), dictForm = ref({})
const carouselDialogVisible = ref(false), isCarouselEdit = ref(false), carouselForm = ref({})
const feedbackDialogVisible = ref(false), feedbackForm = ref({})

const fetchAll = async () => {
  const [u, d, c, f] = await Promise.all([
    request.get('/admin/system/users'), request.get('/admin/system/dicts'),
    request.get('/admin/system/carousels'), request.get('/admin/system/feedbacks')
  ])
  users.value = u.data; dicts.value = d.data; carousels.value = c.data; feedbacks.value = f.data
}

// Dict
const openDictDialog = (row) => { isDictEdit.value = !!row; dictForm.value = row ? { ...row } : { dictType:'', dictKey:'', dictValue:'', sort:0, remark:'', status:1 }; dictDialogVisible.value = true }
const saveDict = async () => { await request.post('/admin/system/dict/save', dictForm.value); ElMessage.success('成功'); dictDialogVisible.value = false; fetchAll() }
const delDict = async (id) => { await ElMessageBox.confirm('确定删除?','提示',{type:'warning'}); await request.delete('/admin/system/dict/delete/'+id); ElMessage.success('删除成功'); fetchAll() }

// Carousel
const openCarouselDialog = (row) => { isCarouselEdit.value = !!row; carouselForm.value = row ? { ...row } : { title:'', imageUrl:'', linkUrl:'', type:1, sort:0, status:1 }; carouselDialogVisible.value = true }
const saveCarousel = async () => { await request.post('/admin/system/carousel/save', carouselForm.value); ElMessage.success('成功'); carouselDialogVisible.value = false; fetchAll() }
const delCarousel = async (id) => { await ElMessageBox.confirm('确定删除?','提示',{type:'warning'}); await request.delete('/admin/system/carousel/delete/'+id); ElMessage.success('删除成功'); fetchAll() }

// Feedback
const replyFeedback = (row) => { feedbackForm.value = { ...row, reply: row.reply || '' }; feedbackDialogVisible.value = true }
const submitReply = async () => { await request.put('/admin/system/feedback/reply/'+feedbackForm.value.id, { reply: feedbackForm.value.reply }); ElMessage.success('回复成功'); feedbackDialogVisible.value = false; fetchAll() }

onMounted(fetchAll)
</script>

<style scoped>
.upload-box { width: 100px; height: 100px; border: 2px dashed #dcdfe6; border-radius: 8px; display: flex; align-items: center; justify-content: center; cursor: pointer; overflow: hidden; transition: border-color 0.3s; }
.upload-box:hover { border-color: #409EFF; }
.upload-img { width: 100%; height: 100%; object-fit: contain; display: block; }
.upload-tip { text-align: center; color: #8c939d; }
.upload-tip .el-icon { font-size: 24px; display: block; margin-bottom: 2px; }
.upload-tip span { font-size: 11px; }
</style>
