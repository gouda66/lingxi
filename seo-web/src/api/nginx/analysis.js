import request from '@/utils/request'

// 获取统计数据
export function getStatistics() {
  return request({
    url: '/seo/nginx/analysis/statistics',
    method: 'get'
  })
}

// 获取趋势数据
export function getTrendData(params) {
  return request({
    url: '/seo/nginx/analysis/trend',
    method: 'get',
    params
  })
}

// 获取请求方法分布
export function getMethodDistribution() {
  return request({
    url: '/seo/nginx/analysis/methodDistribution',
    method: 'get'
  })
}

// 获取状态码分布
export function getStatusCodeDistribution() {
  return request({
    url: '/seo/nginx/analysis/statusCodeDistribution',
    method: 'get'
  })
}

// 获取热门路径 TOP10
export function getTopPaths() {
  return request({
    url: '/seo/nginx/analysis/topPaths',
    method: 'get'
  })
}

// 获取重写规则统计
export function getRewriteStatistics() {
  return request({
    url: '/seo/nginx/analysis/rewriteStatistics',
    method: 'get'
  })
}
