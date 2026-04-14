<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 用户数据 -->
      <el-col :span="24">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
          <el-form-item label="用户名称" prop="userName">
            <el-input v-model="queryParams.userName" placeholder="请输入用户名称" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="手机号码" prop="phone">
            <el-input v-model="queryParams.phone" placeholder="请输入手机号码" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="用户状态" clearable style="width: 240px">
              <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5" v-if="!isOnlyHR">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:user:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5" v-if="!isOnlyHR">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['system:user:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5" v-if="!isOnlyHR">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:user:remove']">删除</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>
        <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="用户编号" align="center" key="userId" prop="userId" v-if="columns.userId.visible" width="120" />
          <el-table-column label="用户头像" align="center" key="avatar" v-if="columns.avatar.visible" width="120">
            <template #default="scope">
              <el-image
                :src="getAvatarUrl(scope.row.avatar)"
                fit="cover"
                style="width: 50px; height: 50px; border-radius: 50%;"
                :preview-src-list="[getAvatarUrl(scope.row.avatar)]"
              >
                <template #error>
                  <div style="display: flex; justify-content: center; align-items: center; width: 100%; height: 100%; background: #f5f7fa; border-radius: 50%;">
                    <el-icon :size="24" color="#909399"><User /></el-icon>
                  </div>
                </template>
              </el-image>
            </template>
          </el-table-column>
          <el-table-column label="用户名称" align="center" key="userName" prop="userName" v-if="columns.userName.visible" :show-overflow-tooltip="true" width="180" />
          <el-table-column label="角色" align="center" key="roleNames" v-if="columns.roleNames.visible" width="200">
            <template #default="scope">
              <el-tag
                v-for="(roleId, index) in parseRoleIds(scope.row.role)"
                :key="index"
                :type="getRoleTagType(roleId)"
                size="small"
                style="margin-right: 5px; margin-bottom: 3px;"
              >
                {{ getRoleName(roleId) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="用户真实名称" align="center" key="realName" prop="realName" v-if="columns.realName.visible" :show-overflow-tooltip="true" width="180" />
          <el-table-column label="性别" align="center" key="sex" prop="sex" v-if="columns.sex.visible"  width="100">
            <template #default="scope">
              <span>{{ scope.row.sex === 1 ? '男' : scope.row.sex === 2 ? '女' : '未知' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="手机号码" align="center" key="phone" prop="phone" v-if="columns.phone.visible"  width="180" />
          <el-table-column label="邮箱" align="center" key="email" prop="email" v-if="columns.email.visible"  width="180" />
          <el-table-column label="状态" align="center" key="status" prop="status" v-if="columns.status.visible" width="200">
            <template #default="scope">
              <el-switch
                  v-model="scope.row.status"
                  :active-value="0"
                  :inactive-value="1"
                  @change="handleStatusChange(scope.row, $event)"
                  active-text="启用"
                  inactive-text="停用"
              />
            </template>
          </el-table-column>

          <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
            <template #default="scope">
              <!-- 管理员或超级管理员的按钮 -->
              <template v-if="!isOnlyHR">
                <el-tooltip content="修改" placement="top">
                  <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:user:edit']"></el-button>
                </el-tooltip>
                <el-tooltip content="删除" placement="top">
                  <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:user:remove']"></el-button>
                </el-tooltip>
                <el-tooltip content="重置密码" placement="top">
                  <el-button link type="primary" icon="Key" @click="handleResetPwd(scope.row)" v-hasPermi="['system:user:resetPwd']"></el-button>
                </el-tooltip>
                <el-tooltip content="分配角色" placement="top">
                  <el-button link type="primary" icon="CircleCheck" @click="handleAuthRole(scope.row)" v-hasPermi="['system:user:edit']"></el-button>
                </el-tooltip>
              </template>

              <!-- HR角色的按钮 -->
              <template v-else>
                <el-tooltip content="下载简历" placement="top">
                  <el-button link type="primary" icon="Download" @click="handleDownloadResume(scope.row)"></el-button>
                </el-tooltip>
                <el-tooltip content="下载面试报告" placement="top">
                  <el-button link type="primary" icon="Document" @click="handleDownloadReport(scope.row)"></el-button>
                </el-tooltip>
                <el-tooltip content="发送邮件" placement="top">
                  <el-button link type="primary" icon="Message" @click="handleSendEmail(scope.row)"></el-button>
                </el-tooltip>
              </template>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-col>
    </el-row>

    <!-- 添加或修改用户配置对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form :model="form" :rules="rules" ref="userRef" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户真实名称" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入用户真实名称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名称" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入用户名称" maxlength="30" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号码" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item v-if="title.value === '修改用户'" label="用户密码" prop="password">
              <el-input v-model="form.password" placeholder="请输入用户密码" type="password" maxlength="20" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户性别">
              <el-select v-model="form.sex" placeholder="请选择">
                <el-option label="未知" :value="0" />
                <el-option label="男" :value="1" />
                <el-option label="女" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio :value="0">正常</el-radio>
                <el-radio :value="1">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-select v-model="form.roleIds" multiple placeholder="请选择">
                <el-option v-for="item in roleOptions" :key="item.roleId" :label="item.roleName" :value="item.roleId" :disabled="item.status === '1'"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="400px" append-to-body>
      <el-upload ref="uploadRef" :limit="1" accept=".xlsx, .xls" :headers="upload.headers" :action="upload.url + '?updateSupport=' + upload.updateSupport" :disabled="upload.isUploading" :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess" :on-change="handleFileChange" :on-remove="handleFileRemove" :auto-upload="false" drag>
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <div class="el-upload__tip">
              <el-checkbox v-model="upload.updateSupport" />是否更新已经存在的用户数据
            </div>
            <span>仅允许导入 xls、xlsx 格式文件。</span>
            <el-link type="primary" underline="never" style="font-size: 12px; vertical-align: baseline" @click="importTemplate">下载模板</el-link>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
          <el-button @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="User">
import { getToken } from "@/utils/auth"
import {
  changeUserStatus,
  listUser,
  resetUserPwd,
  delUser,
  getUser,
  updateUser,
  addUser,
  getRoleOptions,
  downloadResume,
  downloadInterviewReport,
  sendEmail
} from "@/api/system/user"
import { UploadFilled, User } from "@element-plus/icons-vue"
import {useRouter} from "vue-router"
import useUserStore from '@/store/modules/user'
import {generateReport} from "@/api/interview/session.js";
import {generateInterviewReport} from "@/api/interview/template.js";
import {ElMessage} from "element-plus";

const router = useRouter()
const userStore = useUserStore()
const { proxy } = getCurrentInstance()
const { sys_normal_disable, sys_user_sex } = proxy.useDict("sys_normal_disable", "sys_user_sex")

const userList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const dateRange = ref([])
const initPassword = ref(undefined)
const roleOptions = ref([])
const roleMap = ref({})

// 用户导入参数
const upload = reactive({
  open: false,
  title: "",
  isUploading: false,
  updateSupport: 0,
  headers: { Authorization: "Bearer " + getToken() },
  url: import.meta.env.VITE_APP_BASE_API + "/system/user/importData"
})


const columns = ref({
  userId: { label: '用户编号', visible: true },
  avatar: { label: '用户头像', visible: true },
  userName: { label: '用户名称', visible: true },
  roleNames: { label: '角色', visible: true },
  realName: { label: '用户真实名称', visible: true },
  phone: { label: '手机号码', visible: true },
  email: { label: '邮箱地址', visible: true },
  sex: { label: '用户性别', visible: true },
  status: { label: '状态', visible: true }
})

// 表单数据
const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userName: undefined,
    phone: undefined,
    status: undefined
  },
  rules: {
    userName: [
      { required: true, message: "用户名称不能为空", trigger: "blur" },
      { min: 2, max: 20, message: "用户名称长度必须介于 2 和 20 之间", trigger: "blur" }
    ],
    realName: [{ required: true, message: "用户真实名称不能为空", trigger: "blur" }],
    password: [
      { required: true, message: "用户密码不能为空", trigger: "blur" },
      { min: 5, max: 20, message: "用户密码长度必须介于 5 和 20 之间", trigger: "blur" },
      { pattern: /^[^<>"'|\\]+$/, message: "不能包含非法字符：< > \" ' \\ |", trigger: "blur" }
    ],
    email: [{ type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }],
    phone: [{ pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 判断当前用户是否只有HR角色(roleId = 2)
const isOnlyHR = ref(false)

function checkUserRole() {
  const userId = userStore.id
  if (!userId) return

  getUser(userId).then(response => {
    if (response && response.role) {
      const roleIds = parseRoleIds(response.role)
      // 如果只有一个角色且是HR(roleId=2)
      isOnlyHR.value = roleIds.length === 1 && roleIds.includes(2)
    }
  }).catch(error => {
    console.error('获取用户角色失败:', error)
  })
}



function handleDownloadResume(row) {
  const userId = row.userId
  downloadResume(userId).then(response => {
    if (response.code === 0 && response.data) {
      // 获取到URL后打开下载
      const fileUrl = response.data
      window.open(fileUrl, '_blank')
      proxy.$modal.msgSuccess('开始下载简历')
    } else {
      proxy.$modal.msgError(response.msg || '获取简历地址失败')
    }
  }).catch(error => {
    console.error('下载简历失败:', error)
    proxy.$modal.msgError('此人没有上传简历')
  })
}

/** 下载面试报告 */
function handleDownloadReport(row) {
  const userId = row.userId
  downloadInterviewReport(userId).then(response => {
    if (response.code === 0 && response.data) {
      // 获取报告数据
      const report = response.data

      console.log('开始生成HTML报告...')

      // 计算面试时长（分钟）
      const duration = calculateDuration(report.startTime, report.endTime)

      // 构建CSV格式数据: id,breadthScore,depthScore,,,duration,,,,,,advantages,disadvantages,,evaluation,,date
      const csvData = [
        report.sessionId || userId,  // id
        report.technicalScore ? Math.round(report.technicalScore) : 70,  // breadthScore (技术广度)
        report.logicScore ? Math.round(report.logicScore) : 65,  // depthScore (技术深度)
        '',  // 占位
        '',  // 占位
        duration,  // duration (分钟)
        '',  // 占位
        '',  // 占位
        '',  // 占位
        '',  // 占位
        report.strengths || '暂无数据',  // advantages
        report.weaknesses || '暂无数据',  // disadvantages
        '',  // 占位
        report.overallEvaluation || '暂无数据',  // evaluation
        '',  // 占位
        '',  // 占位
        report.generatedAt || new Date().toISOString()  // date
      ].join(',')

      // 调用前端JS方法生成并下载报告
      const result = generateInterviewReport(csvData)
      if (result && result.success) {
        console.log('HTML报告已下载:', result.filename)
        proxy.$modal.msgSuccess('面试报告已生成并下载')
      } else {
        console.warn('HTML报告生成失败，但后端报告已保存')
        proxy.$modal.msgWarning('报告已保存到系统，下载功能异常')
      }
    } else {
      proxy.$modal.msgError(response.msg || '查询不到此面试者已经面试');
    }
  }).catch(error => {
    console.error('下载面试报告失败:', error)
    proxy.$modal.msgError('下载面试报告失败')
  })
}

/** 计算面试时长（分钟） */
function calculateDuration(startTime, endTime) {
  if (!startTime || !endTime) return 30 // 默认30分钟

  try {
    const start = new Date(startTime)
    const end = new Date(endTime)
    const diffMs = end - start
    return Math.round(diffMs / 60000) // 转换为分钟
  } catch (e) {
    return 30 // 默认30分钟
  }
}

/** 发送邮件 */
function handleSendEmail(row) {
  proxy.$modal.confirm(`确认要给 "${row.realName || row.userName}" 发送邮件吗？`).then(() => {
    // 将 userId 转换为字符串
    sendEmail(String(row.userId)).then(() => {
      proxy.$modal.msgSuccess('邮件发送成功')
    }).catch(error => {
      console.error('邮件发送失败:', error)
      proxy.$modal.msgError('邮件发送失败')
    })
  }).catch(() => {})
}


function getAvatarUrl(avatar) {
  if (!avatar || avatar.trim() === '') {
    return null
  }
  // 如果是完整路径直接返回，否则拼接基础路径
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return avatar
  }
  return import.meta.env.VITE_APP_BASE_API + avatar
}

/** 解析角色 ID（将数字字符串拆分成数组） */
function parseRoleIds(role) {
  if (!role) return []
  const roleStr = String(role)
  const result = []
  for (let i = 0; i < roleStr.length; i++) {
    const roleId = parseInt(roleStr.charAt(i))
    if (!isNaN(roleId)) {
      result.push(roleId)
    }
  }
  return result
}

/** 根据角色 ID 获取角色名称 */
function getRoleName(roleId) {
  return roleMap.value[roleId] || '未知角色'
}

/** 根据角色 ID 获取标签颜色 */
function getRoleTagType(roleId) {
  const types = ['primary', 'success', 'warning', 'danger', 'info']
  return types[(roleId - 1) % types.length]
}

/** 查询用户列表 */
function getList() {
  loading.value = true
  // 先获取角色选项
  getRoleOptions().then(response => {
    roleOptions.value = response || []
    // 构建角色映射
    roleMap.value = {}
    response.forEach(role => {
      roleMap.value[role.roleId] = role.roleName
    })
    
    // 再获取用户列表
    listUser(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
      loading.value = false
      userList.value = res.rows
      total.value = res.total
    })
  }).catch(error => {
    console.error('获取角色列表失败:', error)
    roleOptions.value = []
    loading.value = false
  })
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = []
  proxy.resetForm("queryRef")
  handleQuery()
}

/** 删除按钮操作 */
function handleDelete(row) {
  const userIds = row.userId || ids.value
  proxy.$modal.confirm('是否确认删除用户编号为"' + userIds + '"的数据项？').then(() => {
    return delUser(userIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("system/user/export", queryParams.value, `user_${new Date().getTime()}.xlsx`)
}

/** 用户状态修改 */
function handleStatusChange(row, value) {
  // 如果是初始化触发，不处理
  if (value === undefined) {
    return;
  }
  
  const text = row.status === 0 ? "启用" : "停用"
  proxy.$modal.confirm('确认要"' + text + '""' + row.userName + '"用户吗？').then(() => {
    return changeUserStatus(row.userId, row.status)
  }).then(() => {
    proxy.$modal.msgSuccess(text + "成功")
  }).catch(() => {
    row.status = row.status === 0 ? 1 : 0
  })
}
/** 跳转角色分配 */
function handleAuthRole(row) {
  const userId = row.userId
  router.push("/system/user-auth/role/" + userId)
}

/** 重置密码按钮操作 */
function handleResetPwd(row) {
  proxy.$prompt('请输入"' + row.userName + '"的新密码', "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    closeOnClickModal: false,
    inputPattern: /^.{5,20}$/,
    inputErrorMessage: "用户密码长度必须介于 5 和 20 之间",
    inputValidator: (value) => {
      if (/[<>"'|\\]/.test(value)) {
        return "不能包含非法字符：< > \" ' \\ |"
      }
    }
  }).then(({ value }) => {
    resetUserPwd(row.userId, value).then(() => {
      proxy.$modal.msgSuccess("修改成功，新密码是：" + value)
    })
  }).catch(() => {})
}

/** 选择条数 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.userId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

/** 导入按钮操作 */
function handleImport() {
  upload.title = "用户导入"
  upload.open = true
  upload.selectedFile = null
}

/** 下载模板操作 */
function importTemplate() {
  proxy.download("system/user/importTemplate", {}, `user_template_${new Date().getTime()}.xlsx`)
}

/** 文件上传中处理 */
const handleFileUploadProgress = (event, file, fileList) => {
  upload.isUploading = true
}

/** 文件选择处理 */
const handleFileChange = (file, fileList) => {
  upload.selectedFile = file
}

/** 文件删除处理 */
const handleFileRemove = (file, fileList) => {
  upload.selectedFile = null
}

/** 文件上传成功处理 */
const handleFileSuccess = (response, file, fileList) => {
  upload.open = false
  upload.isUploading = false
  proxy.$refs["uploadRef"].handleRemove(file)
  proxy.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true })
  getList()
}

/** 提交上传文件 */
function submitFileForm() {
  const file = upload.selectedFile
  if (!file || file.length === 0 || (!file.name.toLowerCase().endsWith('.xls') && !file.name.toLowerCase().endsWith('.xlsx'))) {
    proxy.$modal.msgError("请选择后缀为xls或xlsx的文件。")
    return
  }
  proxy.$refs["uploadRef"].submit()
}


/** 重置操作表单 */
function reset() {
  form.value = {
    userId: undefined,
    deptId: undefined,
    userName: undefined,
    realName: undefined,
    password: undefined,
    phone: undefined,
    email: undefined,
    sex: undefined,
    status: "0",
    remark: undefined,
    roleIds: []
  }
  proxy.resetForm("userRef")
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  getRoleOptions().then(response => {
    console.log('角色列表 response:', response)
    roleOptions.value = response
  }).catch(error => {
    console.error('获取角色列表失败:', error)
    roleOptions.value = []
  })
  open.value = true
  title.value = "添加用户"
  form.value.password = initPassword.value
}

/** 修改按钮操作 */
function handleUpdate(row) {
  const userId = row.userId || ids.value
  getUser(userId).then(response => {
    console.log('用户信息 response:', response)
    // 先获取角色选项
    getRoleOptions().then(roleRes => {
      roleOptions.value = roleRes || []
      console.log('角色列表:', roleRes)
      console.log('用户当前角色:', response.role)

      // 确保 roleIds 是数组格式
      let userRoleIds = []
      if (response.role) {
        // 将数字字符串拆分成单个数字，如 "123" -> [1, 2, 3]
        const roleStr = String(response.role)
        for (let i = 0; i < roleStr.length; i++) {
          const roleId = parseInt(roleStr.charAt(i))
          if (!isNaN(roleId)) {
            userRoleIds.push(roleId)
          }
        }
      }

      console.log('处理后的角色 IDs:', userRoleIds)

      // 然后设置表单数据
      form.value = {
        userId: response.userId,
        userName: response.userName,
        realName: response.realName,
        password: "",
        phone: response.phone,
        email: response.email,
        sex: response.sex,
        status: response.status,
        remark: response.remark,
        roleIds: userRoleIds,
        deptId: response.deptId
      }
      open.value = true
      title.value = "修改用户"
    })
  })
}


/** 提交按钮 */
function submitForm() {
  proxy.$refs["userRef"].validate(valid => {
    if (valid) {
      if (form.value.userId !== undefined) {
        updateUser(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addUser(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

onMounted(() => {
  getList()
  proxy.getConfigKey("sys.user.initPassword").then(response => {
    initPassword.value = response.msg
  })
  // 在组件挂载时检查角色
  checkUserRole()
})
</script>
