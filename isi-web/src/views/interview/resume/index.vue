<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 用户数据 -->
      <el-col :span="24">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
          <el-form-item label="姓名" prop="fullName">
            <el-input v-model="queryParams.fullName" placeholder="请输入姓名" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="手机号码" prop="phone">
            <el-input v-model="queryParams.phone" placeholder="请输入手机号码" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="求职意向" prop="jobTitle">
            <el-input v-model="queryParams.jobTitle" placeholder="请输入求职意向" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Upload" @click="handleUpload">上传简历</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="resumeList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="简历 ID" align="center" key="id" prop="id" v-if="columns.id.visible" width="100" />
          <el-table-column label="简历名称" align="center" key="resumeName" prop="resumeName" v-if="columns.resumeName.visible" :show-overflow-tooltip="true" width="200" />
          <el-table-column label="姓名" align="center" key="fullName" prop="fullName" v-if="columns.fullName.visible" :show-overflow-tooltip="true" width="120" />
          <el-table-column label="性别" align="center" key="gender" prop="gender" v-if="columns.gender.visible" width="80">
            <template #default="scope">
              <span>{{ scope.row.gender === 1 ? '男' : scope.row.gender === 2 ? '女' : '未知' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="手机号码" align="center" key="phone" prop="phone" v-if="columns.phone.visible" width="130" />
          <el-table-column label="邮箱" align="center" key="email" prop="email" v-if="columns.email.visible" :show-overflow-tooltip="true" width="180" />
          <el-table-column label="求职意向" align="center" key="jobTitle" prop="jobTitle" v-if="columns.jobTitle.visible" :show-overflow-tooltip="true" width="180" />
          <el-table-column label="工作年限" align="center" key="workYears" prop="workYears" v-if="columns.workYears.visible" width="100">
            <template #default="scope">
              <span>{{ scope.row.workYears !== null ? scope.row.workYears + '年' : '未填写' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="所在地" align="center" key="location" prop="location" v-if="columns.location.visible" :show-overflow-tooltip="true" width="120" />
          <el-table-column label="文件类型" align="center" key="fileType" prop="fileType" v-if="columns.fileType.visible" width="80">
            <template #default="scope">
              <el-tag size="small">{{ scope.row.fileType?.toUpperCase() || '未知' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="解析状态" align="center" key="parseStatus" prop="parseStatus" v-if="columns.parseStatus.visible" width="100">
            <template #default="scope">
              <el-tag :type="getParseStatusTagType(scope.row.parseStatus)" size="small">
                {{ getParseStatusLabel(scope.row.parseStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="是否默认" align="center" key="isDefault" prop="isDefault" v-if="columns.isDefault.visible" width="80">
            <template #default="scope">
              <el-tag :type="scope.row.isDefault === 1 ? 'success' : 'info'" size="small">
                {{ scope.row.isDefault === 1 ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" key="createdAt" prop="createdAt" v-if="columns.createdAt.visible" width="160">
            <template #default="scope">
              <span>{{ parseDateTime(scope.row.createdAt) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
            <template #default="scope">
              <el-tooltip content="重新解析" placement="top">
                <el-button link type="warning" icon="Refresh" @click="handleReParse(scope.row)"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"></el-button>
              </el-tooltip>
              <el-tooltip content="下载简历" placement="top">
                <el-button link type="success" icon="Download" @click="handleDownload(scope.row)"></el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-col>
    </el-row>

    <!-- 简历详情对话框 -->
    <el-dialog title="简历详情" v-model="detailOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="简历名称">{{ detailForm.resumeName }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailForm.fullName }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ detailForm.gender === 1 ? '男' : detailForm.gender === 2 ? '女' : '未知' }}
        </el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ detailForm.birthDate }}</el-descriptions-item>
        <el-descriptions-item label="手机号码">{{ detailForm.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detailForm.email }}</el-descriptions-item>
        <el-descriptions-item label="所在地">{{ detailForm.location }}</el-descriptions-item>
        <el-descriptions-item label="工作年限">{{ detailForm.workYears !== null ? detailForm.workYears + '年' : '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="求职意向">{{ detailForm.jobTitle }}</el-descriptions-item>
        <el-descriptions-item label="期望薪资">{{ detailForm.expectedSalary }}</el-descriptions-item>
        <el-descriptions-item label="文件类型">{{ detailForm.fileType?.toUpperCase() }}</el-descriptions-item>
        <el-descriptions-item label="文件大小">{{ formatFileSize(detailForm.fileSize) }}</el-descriptions-item>
        <el-descriptions-item label="解析状态">
          <el-tag :type="getParseStatusTagType(detailForm.parseStatus)" size="small">
            {{ getParseStatusLabel(detailForm.parseStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="是否默认">
          <el-tag :type="detailForm.isDefault === 1 ? 'success' : 'info'" size="small">
            {{ detailForm.isDefault === 1 ? '是' : '否' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseDateTime(detailForm.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ parseDateTime(detailForm.updatedAt) }}</el-descriptions-item>
        <el-descriptions-item label="自我评价" :span="2">{{ detailForm.selfEvaluation || '无' }}</el-descriptions-item>
        <el-descriptions-item label="教育背景" :span="2">
          <pre v-if="detailForm.educationJson" style="white-space: pre-wrap; word-wrap: break-word;">{{ formatEducation(detailForm.educationJson) }}</pre>
          <span v-else>无</span>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailOpen = false">关 闭</el-button>
          <el-button type="primary" @click="handleDownloadFromDetail">下载简历</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 上传简历对话框 -->
    <el-dialog :title="uploadTitle" v-model="uploadOpen" width="600px" append-to-body>
      <el-upload
        ref="uploadRef"
        drag
        :limit="1"
        accept=".pdf,.doc,.docx,.txt"
        :auto-upload="false"
        :on-change="handleFileChange"
        :on-remove="handleFileRemove"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <span>仅允许 PDF、DOC、DOCX、TXT 格式文件</span>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitUpload">确 定</el-button>
          <el-button @click="uploadOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Resume">
import {
  listResume,
  getResume,
  delResume,
  uploadResume,
  reParseResume
} from "@/api/interview/resume"
import { UploadFilled } from '@element-plus/icons-vue'
import { ref, reactive, toRefs, getCurrentInstance, onMounted } from 'vue'

const { proxy } = getCurrentInstance()

const resumeList = ref([])
const detailOpen = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const dateRange = ref([])
const currentResumeId = ref(null)
const uploadOpen = ref(false)
const uploadTitle = ref("上传简历")
const uploadFile = ref(null)
const uploadRef = ref(null)

const columns = ref({
  id: { label: '简历 ID', visible: true },
  resumeName: { label: '简历名称', visible: true },
  fullName: { label: '姓名', visible: true },
  gender: { label: '性别', visible: true },
  phone: { label: '手机号码', visible: true },
  email: { label: '邮箱', visible: true },
  jobTitle: { label: '求职意向', visible: true },
  workYears: { label: '工作年限', visible: true },
  location: { label: '所在地', visible: true },
  fileType: { label: '文件类型', visible: true },
  parseStatus: { label: '解析状态', visible: true },
  isDefault: { label: '是否默认', visible: true },
  createdAt: { label: '创建时间', visible: true }
})

// 表单数据
const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    fullName: undefined,
    phone: undefined,
    jobTitle: undefined
  },
  rules: {}
})

const { queryParams, rules } = toRefs(data)

// 详情数据
const detailForm = ref({})



/** 获取解析状态标签 */
function getParseStatusLabel(parseStatus) {
  const labels = {
    0: '待解析',
    1: '解析中',
    2: '解析成功',
    3: '解析失败'
  }
  return labels[parseStatus] || '未知'
}

/** 获取解析状态标签类型 */
function getParseStatusTagType(parseStatus) {
  const types = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return types[parseStatus] || 'info'
}

/** 格式化文件大小 */
function formatFileSize(bytes) {
  if (!bytes || bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

/** 解析日期时间 */
function parseDateTime(dateTimeStr) {
  if (!dateTimeStr) return ''
  // 如果是字符串，直接返回或格式化
  if (typeof dateTimeStr === 'string') {
    return dateTimeStr.replace('T', ' ').substring(0, 19)
  }
  return dateTimeStr
}

/** 格式化教育背景显示 */
function formatEducation(eduJson) {
  try {
    if (!eduJson) return '无'
    const eduList = typeof eduJson === 'string' ? JSON.parse(eduJson) : eduJson
    if (!Array.isArray(eduList) || eduList.length === 0) return '无'
    
    return eduList.map(edu => {
      const parts = []
      if (edu.school) parts.push(edu.school)
      if (edu.major) parts.push(edu.major)
      if (edu.degree) parts.push(edu.degree)
      if (edu.startDate && edu.endDate) {
        parts.push(`${edu.startDate} - ${edu.endDate}`)
      }
      return parts.join(' | ')
    }).join('\n\n')
  } catch (e) {
    console.error('解析教育背景失败:', e)
    return eduJson || '无'
  }
}

/** 查询简历列表 */
function getList() {
  loading.value = true
  listResume(queryParams.value).then(res => {
    console.log('获取简历列表成功:', res)
    loading.value = false
    // 后端返回的是 Page 对象，包含 records 和 total
    if (res) {
      resumeList.value = res.records || []
      total.value = res.total || 0
    } else {
      resumeList.value = []
      total.value = 0
    }
  }).catch(error => {
    console.error('获取简历列表失败:', error)
    loading.value = false
    resumeList.value = []
  })
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

/** 删除按钮操作 */
function handleDelete(row) {
  const resumeIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除简历编号为"' + resumeIds + '"的数据项？').then(() => {
    return delResume(resumeIds)
  }).then(response => {
      getList()
      proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 下载简历按钮操作 */
function handleDownload(row) {
  if (!row.originalFileUrl) {
    proxy.$modal.msgError("暂无可下载的简历文件")
    return
  }
  // 对 URL 中的文件名部分进行编码，处理中文和特殊字符
  const url = row.originalFileUrl
  // 提取文件名部分并编码
  const lastSlashIndex = url.lastIndexOf('/')
  if (lastSlashIndex !== -1) {
    const baseUrl = url.substring(0, lastSlashIndex + 1)
    const fileName = url.substring(lastSlashIndex + 1)
    // 对文件名进行编码
    const encodedFileName = encodeURIComponent(fileName)
    window.open(baseUrl + encodedFileName, '_blank')
  } else {
    // 如果没有路径分隔符，直接编码整个 URL
    window.open(encodeURIComponent(url), '_blank')
  }
}

/** 从详情页下载简历 */
function handleDownloadFromDetail() {
  if (currentResumeId.value) {
    getResume(currentResumeId.value).then(response => {
      if (response && response.data && response.data.originalFileUrl) {
        const url = import.meta.env.VITE_APP_BASE_API + response.data.originalFileUrl
        window.open(url, '_blank')
      } else {
        proxy.$modal.msgError("暂无可下载的简历文件")
      }
    })
  }
}

/** 选择条数 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

/** 重新解析按钮操作 */
function handleReParse(row) {
  proxy.$modal.confirm('是否确认重新解析该简历？').then(() => {
    return reParseResume(row.id)
  }).then(response => {
      getList()
      proxy.$modal.msgSuccess("重新解析成功")
  }).catch(() => {})
}

/** 上传简历按钮操作 */
function handleUpload() {
  uploadOpen.value = true
}

/** 文件选择变化处理 */
function handleFileChange(file) {
  uploadFile.value = file.raw
}

/** 文件移除处理 */
function handleFileRemove() {
  uploadFile.value = null
}

/** 提交上传 */
function submitUpload() {
  if (!uploadFile.value) {
    proxy.$modal.msgError("请选择要上传的简历文件")
    return
  }
  
  const formData = new FormData()
  formData.append('file', uploadFile.value)
  
  uploadResume(formData).then(response => {
      proxy.$modal.msgSuccess("上传成功，正在解析...")
      uploadOpen.value = false
      uploadFile.value = null
      getList()
  }).catch(error => {
    proxy.$modal.msgError("上传失败：" + error.message)
  })
}

/** 提交按钮 */
function submitForm() {
  // 这里可以处理上传逻辑
}

onMounted(() => {
  getList()
})
</script>
