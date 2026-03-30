<template>
  <div class="app-container">
    <!-- 数据统计卡片 -->
    <el-row :gutter="20" class="mb-20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Document /></el-icon>
              <span>总请求数</span>
            </div>
          </template>
          <div class="card-value">{{ statistics.totalRequests }}</div>
          <div class="card-footer">总访问次数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Refresh /></el-icon>
              <span>重写次数</span>
            </div>
          </template>
          <div class="card-value">{{ statistics.totalRewrites }}</div>
          <div class="card-footer">重写率：{{ statistics.rewriteRate }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <el-icon><Calendar /></el-icon>
            <span>今日请求</span>
          </template>
          <div class="card-value">{{ statistics.todayRequests }}</div>
          <div class="card-footer">今日访问</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <el-icon><Timer /></el-icon>
            <span>平均响应</span>
          </template>
          <div class="card-value">{{ statistics.avgResponseTime }}<small>ms</small></div>
          <div class="card-footer">成功率：{{ statistics.successRate }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 请求趋势图表 -->
    <el-card shadow="hover" class="mb-20">
      <template #header>
        <div class="card-header">
          <span>请求趋势</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </div>
      </template>
      <div ref="trendChartRef" style="height: 400px;"></div>
    </el-card>

    <!-- 图表行 -->
    <el-row :gutter="20" class="mb-20">
      <!-- 请求方法分布 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>请求方法分布</span>
          </template>
          <div ref="methodChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <!-- 状态码分布 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>响应状态码分布</span>
          </template>
          <div ref="statusCodeChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- TOP 排行 -->
    <el-row :gutter="20">
      <!-- 热门路径 TOP10 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>热门路径 TOP10</span>
          </template>
          <el-table :data="topPaths" border size="small">
            <el-table-column type="index" label="排名" width="50" align="center" />
            <el-table-column prop="path" label="路径" :show-overflow-tooltip="true" />
            <el-table-column prop="count" label="请求次数" align="center" width="100" />
          </el-table>
        </el-card>
      </el-col>
      <!-- 重写规则 TOP10 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>重写规则 TOP10</span>
          </template>
          <el-table :data="rewriteRules" border size="small">
            <el-table-column type="index" label="排名" width="50" align="center" />
            <el-table-column prop="ruleName" label="规则名称" :show-overflow-tooltip="true" />
            <el-table-column prop="count" label="匹配次数" align="center" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Analysis">
import * as echarts from 'echarts';
import { 
  getStatistics, 
  getTrendData as getTrendDataApi, 
  getMethodDistribution, 
  getStatusCodeDistribution, 
  getTopPaths, 
  getRewriteStatistics 
} from "@/api/nginx/analysis";

const { proxy } = getCurrentInstance();

const statistics = ref({
  totalRequests: 0,
  totalRewrites: 0,
  rewriteRate: '0%',
  todayRequests: 0,
  avgResponseTime: '0',
  successRate: '0%'
});

const dateRange = ref([]);
const trendChartRef = ref(null);
const methodChartRef = ref(null);
const statusCodeChartRef = ref(null);
const topPaths = ref([]);
const rewriteRules = ref([]);

let trendChart = null;
let methodChart = null;
let statusCodeChart = null;

/** 获取统计数据 */
function getStatisticsData() {
  getStatistics().then(response => {
    // 若依框架返回的数据结构：{code: 1, msg: null, data: {...}}
    statistics.value = response || {};
  }).catch(error => {
    console.error('获取统计数据失败:', error);
  });
}

/** 获取趋势数据 */
function getTrendData() {
  const params = {
    startDate: dateRange.value?.[0] || '',
    endDate: dateRange.value?.[1] || ''
  };
  
  getTrendDataApi(params).then(response => {
    console.log('趋势数据 response:', response);
    const data = response || [];
    if (data.length > 0) {
      renderTrendChart(data);
    }
  }).catch(error => {
    console.error('获取趋势数据失败:', error);
  });
}

/** 获取请求方法分布 */
function getMethodData() {
  getMethodDistribution().then(response => {
    console.log('请求方法 response:', response);
    const data = response || [];
    if (data.length > 0) {
      renderMethodChart(data);
    }
  }).catch(error => {
    console.error('获取请求方法数据失败:', error);
  });
}

/** 获取状态码分布 */
function getStatusCodeData() {
  getStatusCodeDistribution().then(response => {
    console.log('状态码 response:', response);
    const data = response || [];
    if (data.length > 0) {
      renderStatusCodeChart(data);
    }
  }).catch(error => {
    console.error('获取状态码数据失败:', error);
  });
}

/** 获取热门路径 */
function getTopPathsData() {
  getTopPaths().then(response => {
    console.log('热门路径 response:', response);
    topPaths.value = response || [];
  }).catch(error => {
    console.error('获取热门路径数据失败:', error);
  });
}

/** 获取重写规则统计 */
function getRewriteStatisticsData() {
  getRewriteStatistics().then(response => {
    console.log('重写规则 response:', response);
    rewriteRules.value = response || [];
  }).catch(error => {
    console.error('获取重写规则数据失败:', error);
  });
}

/** 渲染趋势图表 */
function renderTrendChart(data) {
  console.log('开始渲染趋势图表，数据:', data);
  console.log('DOM 元素:', trendChartRef.value);
  
  if (!trendChartRef.value) {
    console.error('趋势图表 DOM 元素不存在');
    return;
  }
  
  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value);
    console.log('趋势图表初始化完成');
  }

  const dates = data.map(item => item.date);
  const requests = data.map(item => item.requests);
  const rewrites = data.map(item => item.rewrites);
  const avgTimes = data.map(item => parseFloat(item.avgResponseTime));

  console.log('处理后的数据 - dates:', dates, 'requests:', requests, 'rewrites:', rewrites, 'avgTimes:', avgTimes);

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['请求数', '重写数', '平均响应时间 (ms)']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: [
      {
        type: 'value',
        name: '次数'
      },
      {
        type: 'value',
        name: '时间 (ms)'
      }
    ],
    series: [
      {
        name: '请求数',
        type: 'line',
        smooth: true,
        data: requests,
        itemStyle: { color: '#409EFF' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64,158,255,0.5)' },
            { offset: 1, color: 'rgba(64,158,255,0.05)' }
          ])
        }
      },
      {
        name: '重写数',
        type: 'line',
        smooth: true,
        data: rewrites,
        itemStyle: { color: '#67C23A' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103,194,58,0.5)' },
            { offset: 1, color: 'rgba(103,194,58,0.05)' }
          ])
        }
      },
      {
        name: '平均响应时间 (ms)',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: avgTimes,
        itemStyle: { color: '#E6A23C' }
      }
    ]
  };

  console.log('设置图表配置');
  trendChart.setOption(option);
  console.log('趋势图表渲染完成');
}

/** 渲染请求方法分布图 */
function renderMethodChart(data) {
  if (!methodChart) {
    methodChart = echarts.init(methodChartRef.value);
  }

  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '请求方法',
        type: 'pie',
        radius: '50%',
        data: data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: {
          formatter: '{b}: {c} ({d}%)'
        }
      }
    ]
  };

  methodChart.setOption(option);
}

/** 渲染状态码分布图 */
function renderStatusCodeChart(data) {
  if (!statusCodeChart) {
    statusCodeChart = echarts.init(statusCodeChartRef.value);
  }

  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '状态码',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: data.map(item => ({
          ...item,
          itemStyle: {
            color: getStatusColor(item.name)
          }
        }))
      }
    ]
  };

  statusCodeChart.setOption(option);
}

/** 获取状态码颜色 */
function getStatusColor(statusCode) {
  const code = parseInt(statusCode);
  if (code >= 200 && code < 300) return '#67C23A';
  if (code >= 300 && code < 400) return '#409EFF';
  if (code >= 400 && code < 500) return '#E6A23C';
  if (code >= 500) return '#F56C6C';
  return '#909399';
}

/** 日期改变 */
function handleDateChange() {
  getTrendData();
}

/** 窗口大小改变时重绘图表 */
function handleResize() {
  window.addEventListener('resize', () => {
    trendChart && trendChart.resize();
    methodChart && methodChart.resize();
    statusCodeChart && statusCodeChart.resize();
  });
}

onMounted(() => {
nextTick(() => {
    getStatisticsData();
    getTrendData();
    getMethodData();
    getStatusCodeData();
    getTopPathsData();
    getRewriteStatisticsData();
    handleResize();
  });
});

onBeforeUnmount(() => {
  trendChart && trendChart.dispose();
  methodChart && methodChart.dispose();
  statusCodeChart && statusCodeChart.dispose();
});
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-value {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  text-align: center;
  padding: 15px 0;
  
  small {
    font-size: 14px;
    font-weight: normal;
    color: #909399;
  }
}

.card-footer {
  text-align: center;
  color: #909399;
  font-size: 13px;
  padding: 8px 0;
  border-top: 1px solid #f0f0f0;
}

.mb-20 {
  margin-bottom: 20px;
}

.app-container {
  background: #fff;
  padding: 20px;
}
</style>
