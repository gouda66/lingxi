import request from '@/utils/request'

// 查询请求日志列表
export function listPage(query) {
  return request({
    url: '/seo/nginx/requestLog/listPage',
    method: 'get',
    params: query
  })
}
