<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="请求路径" prop="requestPath">
        <el-input
          v-model="queryParams.requestPath"
          placeholder="请输入请求路径"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="请求方法" prop="method">
        <el-select v-model="queryParams.method" placeholder="请选择" clearable>
          <el-option label="GET" value="GET" />
          <el-option label="POST" value="POST" />
          <el-option label="PUT" value="PUT" />
          <el-option label="DELETE" value="DELETE" />
        </el-select>
      </el-form-item>
      <el-form-item label="是否重写" prop="isRewritten">
        <el-select v-model="queryParams.isRewritten" placeholder="请选择" clearable>
          <el-option label="是" value="1" />
          <el-option label="否" value="0" />
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
          type="info"
          plain
          icon="Document"
          @click="handleExport"
          v-hasPermi="['nginx:request:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="requestList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="70" />
      <el-table-column label="请求路径" align="center" prop="requestPath" :show-overflow-tooltip="true" width="200" />
      <el-table-column label="请求方法" align="center" prop="method" width="100">
        <template #default="scope">
          <el-tag :type="getMethodType(scope.row.method)">
            {{ scope.row.method }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="原始 URL" align="center" prop="originalUrl" :show-overflow-tooltip="true" width="250" />
      <el-table-column label="重写后 URL" align="center" prop="rewrittenUrl" :show-overflow-tooltip="true" width="250" />
      <el-table-column label="匹配规则" align="center" prop="ruleName" :show-overflow-tooltip="true" width="120" />
      <el-table-column label="规则标志" align="center" prop="ruleFlag" width="100">
        <template #default="scope">
          <el-tag :type="getFlagType(scope.row.ruleFlag)" size="small">
            {{ scope.row.ruleFlag }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="是否重写" align="center" prop="isRewritten" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.isRewritten === 1 ? 'success' : 'info'">
            {{ scope.row.isRewritten === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态码" align="center" prop="statusCode" width="80">
        <template #default="scope">
          <el-tag :type="getStatusCodeType(scope.row.statusCode)">
            {{ scope.row.statusCode }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="响应时间 (ms)" align="center" prop="responseTime" width="100" />
      <el-table-column label="客户端 IP" align="center" prop="remoteAddr" :show-overflow-tooltip="true" width="140" />
      <el-table-column label="创建时间" align="center" prop="createdAt" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createdAt) }}</span>
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
  </div>
</template>

<script setup name="RequestLog">
import { listPage } from "@/api/nginx/requestLog";
import { parseTime } from "@/utils/ruoyi";

const { proxy } = getCurrentInstance();

const requestList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const total = ref(0);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  requestPath: undefined,
  method: undefined,
  isRewritten: undefined
});

/** 查询列表 */
function getList() {
  loading.value = true;
  listPage(queryParams).then(response => {
    requestList.value = response.records || [];
    total.value = response.total || 0;
  }).catch(error => {
    console.error('获取列表失败:', error);
  }).finally(() => {
    loading.value = false;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1;
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
}

/** 获取请求方法类型 */
function getMethodType(method) {
  const typeMap = {
    'GET': 'success',
    'POST': 'primary',
    'PUT': 'warning',
    'DELETE': 'danger'
  };
  return typeMap[method] || 'info';
}

/** 获取规则标志类型 */
function getFlagType(flag) {
  const types = {
    'last': '',
    'break': 'warning',
    'redirect': 'success',
    'permanent': 'danger'
  };
  return types[flag] || '';
}

/** 获取状态码类型 */
function getStatusCodeType(statusCode) {
  if (statusCode >= 200 && statusCode < 300) {
    return 'success';
  } else if (statusCode >= 300 && statusCode < 400) {
    return 'warning';
  } else if (statusCode >= 400 && statusCode < 500) {
    return 'danger';
  } else if (statusCode >= 500) {
    return 'danger';
  }
  return 'info';
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('nginx/requestLog/export', {
    ...queryParams
  }, `request_log_${new Date().getTime()}.xlsx`);
}

getList();
</script>

<style scoped lang="scss">
.app-container {
  background: #fff;
  padding: 20px;
}
</style>
