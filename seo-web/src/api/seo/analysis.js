import request from '@/utils/request'

// 获取统计数据
export function getStatistics() {
  return request({
    url: '/seo/analysis/statistics',
    method: 'get'
  })
}

// 获取趋势数据
export function getTrendData(query) {
  return request({
    url: '/seo/analysis/trend',
    method: 'get',
    params: query
  })
}

// 获取关键词分布
export function getKeywordDistribution() {
  return request({
    url: '/seo/analysis/keyword-distribution',
    method: 'get'
  })
}
