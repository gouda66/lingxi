<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="规则名称" prop="ruleName">
        <el-input
          v-model="queryParams.ruleName"
          placeholder="请输入规则名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分类" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择分类" clearable>
          <el-option label="通用" value="GENERAL" />
          <el-option label="SEO" value="SEO" />
          <el-option label="API" value="API" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['nginx:rewrite:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['nginx:rewrite:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['nginx:rewrite:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Refresh"
          @click="handleSyncNginx"
          v-hasPermi="['nginx:rewrite:sync']"
        >同步配置</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ruleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="70" />
      <el-table-column label="规则名称" align="center" prop="ruleName" :show-overflow-tooltip="true" />
      <el-table-column label="原始模式" align="center" prop="originalPattern" :show-overflow-tooltip="true" />
      <el-table-column label="替换 URL" align="center" prop="replacementUrl" :show-overflow-tooltip="true" />
      <el-table-column label="标志" align="center" prop="flag" width="100">
        <template #default="scope">
          <el-tag :type="getFlagType(scope.row.flag)">{{ scope.row.flag }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="分类" align="center" prop="category" width="100">
        <template #default="scope">
          <el-tag :type="getCategoryType(scope.row.category)">{{ getCategoryLabel(scope.row.category) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sortOrder" width="70" />
      <el-table-column label="状态" align="center" prop="isEnabled" width="70">
        <template #default="scope">
          <el-switch
            v-model="scope.row.isEnabled"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['nginx:rewrite:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['nginx:rewrite:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="规则名称" prop="ruleName">
              <el-input v-model="form.ruleName" placeholder="请输入规则名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择分类">
                <el-option label="通用" value="GENERAL" />
                <el-option label="SEO" value="SEO" />
                <el-option label="API" value="API" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="24">
            <el-form-item label="原始模式" prop="originalPattern">
              <el-input v-model="form.originalPattern" placeholder="例如：^/old/(.*)$" />
              <div class="form-tip">支持正则表达式，如：^/old/(.*)$</div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="24">
            <el-form-item label="替换 URL" prop="replacementUrl">
              <el-input v-model="form.replacementUrl" placeholder="例如：/new/$1" />
              <div class="form-tip">使用 $1, $2 等引用捕获组</div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="标志" prop="flag">
              <el-select v-model="form.flag" placeholder="请选择标志">
                <el-option label="last" value="last" />
                <el-option label="break" value="break" />
                <el-option label="redirect" value="redirect" />
                <el-option label="permanent" value="permanent" />
              </el-select>
              <div class="form-tip">last: 完成匹配; break: 终止; redirect: 临时重定向; permanent: 永久重定向</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input v-model="form.description" type="textarea" placeholder="请输入规则描述" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="状态" prop="isEnabled">
              <el-radio-group v-model="form.isEnabled">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
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

    <!-- Nginx 配置生成对话框 -->
    <el-dialog title="Nginx 配置文件" v-model="configOpen" width="900px" append-to-body>
      <el-alert
        title="使用说明"
        type="info"
        :closable="false"
        show-icon
        class="mb-4"
      >
        <p>1. 点击下方按钮复制配置内容</p>
        <p>2. 在 Ubuntu 服务器上编辑 Nginx 配置文件（通常位于 /etc/nginx/conf.d/rewrite.conf）</p>
        <p>3. 粘贴配置内容并保存</p>
        <p>4. 重启 Nginx: sudo systemctl reload nginx</p>
      </el-alert>
      <pre class="config-content">{{ configContent }}</pre>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="copyConfig">复制配置</el-button>
          <el-button @click="applyConfig">应用配置</el-button>
          <el-button @click="configOpen = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="RewriteRule">
import { listRewriteRule, getRewriteRule, addRewriteRule, updateRewriteRule, delRewriteRule, generateNginxConfig, applyNginxConfig, syncToNginx } from "@/api/nginx/rewrite";
import { ElMessage, ElMessageBox } from 'element-plus';

const { proxy } = getCurrentInstance();

const ruleList = ref([]);
const open = ref(false);
const configOpen = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const configContent = ref("");

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    ruleName: undefined,
    category: undefined
  }
});

const { queryParams, form } = toRefs(data);

const rules = {
  ruleName: [{ required: true, message: "规则名称不能为空", trigger: "blur" }],
  originalPattern: [{ required: true, message: "原始模式不能为空", trigger: "blur" }],
  replacementUrl: [{ required: true, message: "替换 URL 不能为空", trigger: "blur" }],
  flag: [{ required: true, message: "标志不能为空", trigger: "change" }]
};

/** 查询列表 */
function getList() {
  loading.value = true;
  listRewriteRule(queryParams.value).then(response => {
    console.log('获取列表成功:', response);
    // MyBatis-Plus Page 对象返回的数据结构
    ruleList.value = response.records || response.data?.records || [];
    total.value = response.total || response.data?.total || 0;
  }).catch(error => {
    console.error('获取列表失败:', error);
    ruleList.value = [];
    total.value = 0;
  }).finally(() => {
    loading.value = false;
  });
}

/** 获取标志类型 */
function getFlagType(flag) {
  const types = {
    'last': '',
    'break': 'warning',
    'redirect': 'success',
    'permanent': 'danger'
  };
  return types[flag] || '';
}

/** 获取分类类型 */
function getCategoryType(category) {
  const types = {
    'GENERAL': '',
    'SEO': 'success',
    'API': 'warning'
  };
  return types[category] || '';
}

/** 获取分类标签 */
function getCategoryLabel(category) {
  const labels = {
    'GENERAL': '通用',
    'SEO': 'SEO',
    'API': 'API'
  };
  return labels[category] || category;
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 表单重置 */
function reset() {
  form.value = {
    id: undefined,
    ruleName: undefined,
    originalPattern: undefined,
    replacementUrl: undefined,
    flag: 'last',
    description: undefined,
    sortOrder: 0,
    isEnabled: 1,
    category: 'GENERAL'
  };
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加重写规则";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  console.log('修改行数据:', row);
  reset();
  const id = row.id || ids.value[0];
  console.log('获取规则详情，ID:', id);
  getRewriteRule(id).then(response => {
    console.log('获取到的详情:', response);
    form.value = response.data || response;
    open.value = true;
    title.value = "修改重写规则";
  }).catch(error => {
    console.error('获取规则详情失败:', error);
    ElMessage.error('获取规则详情失败');
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["formRef"].validate(valid => {
    if (valid) {
      console.log('表单验证通过，提交数据:', form.value);
      if (form.value.id) {
        // 修改操作
        updateRewriteRule(form.value).then(response => {
          console.log('修改响应:', response);
          ElMessage.success("修改成功");
          open.value = false;
          getList();
        }).catch(error => {
          console.error('修改失败:', error);
          ElMessage.error('修改失败：' + (error.message || '未知错误'));
        });
      } else {
        // 新增操作
        addRewriteRule(form.value).then(response => {
          console.log('新增响应:', response);
          ElMessage.success("新增成功");
          open.value = false;
          getList();
        }).catch(error => {
          console.error('新增失败:', error);
          ElMessage.error('新增失败：' + (error.message || '未知错误'));
        });
      }
    } else {
      console.log('表单验证未通过');
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const idsToDelete = row.id || ids.value.join(',');
  ElMessageBox.confirm(`是否确认删除重写规则编号为"${idsToDelete}"的数据项？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    return delRewriteRule(idsToDelete);
  }).then(() => {
    getList();
    ElMessage.success("删除成功");
  }).catch(() => {});
}

/** 状态修改 */
function handleStatusChange(row) {
  let text = row.isEnabled === 1 ? "启用" : "禁用";
  ElMessageBox.confirm(`确认要"${text}""${row.ruleName}"规则吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    return updateRewriteRule({ id: row.id, isEnabled: row.isEnabled });
  }).then(() => {
    ElMessage.success(text + "成功");
  }).catch(() => {
    row.isEnabled = row.isEnabled === 1 ? 0 : 1;
  });
}

/** 生成配置 */
function handleGenerateConfig() {
  generateNginxConfig().then(response => {
    configContent.value = response.data.configContent;
    configOpen.value = true;
  });
}

/** 复制配置 */
function copyConfig() {
  navigator.clipboard.writeText(configContent.value).then(() => {
    ElMessage.success("配置已复制到剪贴板");
  }).catch(() => {
    ElMessage.error("复制失败");
  });
}

/** 应用配置 */
function applyConfig() {
  applyNginxConfig({
    configContent: configContent.value,
    version: new Date().getTime().toString(),
    description: '通过管理系统生成的 Nginx 重写规则配置'
  }).then(response => {
    ElMessage.success(response.msg);
    configOpen.value = false;
  });
}

/** 同步配置到 Nginx */
function handleSyncNginx() {
  ElMessageBox.confirm('此操作将把当前所有启用的重写规则同步到 Nginx 服务器并重新加载配置，是否继续？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    loading.value = true;
    syncToNginx().then(response => {
      ElMessage.success(response.msg || '同步成功');
      getList();
    }).catch(error => {
      ElMessage.error(error.message || '同步失败');
    }).finally(() => {
      loading.value = false;
    });
  }).catch(() => {});
}

getList();
</script>

<style scoped lang="scss">
.config-content {
  background-color: #1e1e1e;
  color: #d4d4d4;
  padding: 20px;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 14px;
  line-height: 1.5;
  max-height: 500px;
  overflow: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.mb-4 {
  margin-bottom: 16px;
}
</style>
