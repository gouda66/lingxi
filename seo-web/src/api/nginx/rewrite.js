import request from '@/utils/request'

// 查询重写规则列表
export function listRewriteRule(query) {
  return request({
    url: '/seo/nginx/rewrite/list',
    method: 'get',
    params: query
  })
}

// 获取重写规则详情
export function getRewriteRule(id) {
  return request({
    url: '/seo/nginx/rewrite/' + id,
    method: 'get'
  })
}

// 添加重写规则
export function addRewriteRule(data) {
  return request({
    url: '/seo/nginx/rewrite',
    method: 'post',
    data: data
  })
}

// 修改重写规则
export function updateRewriteRule(data) {
  return request({
    url: '/seo/nginx/rewrite',
    method: 'put',
    data: data
  })
}

// 删除重写规则
export function delRewriteRule(ids) {
  return request({
    url: '/seo/nginx/rewrite/' + ids,
    method: 'delete'
  })
}

// 生成 Nginx 配置
export function generateNginxConfig() {
  return request({
    url: '/seo/nginx/rewrite/generate',
    method: 'get'
  })
}

// 应用 Nginx 配置
export function applyNginxConfig(data) {
  return request({
    url: '/seo/nginx/rewrite/apply',
    method: 'post',
    data: data
  })
}

// 同步配置到 Nginx 服务器
export function syncToNginx() {
  return request({
    url: '/seo/nginx/rewrite/sync',
    method: 'post'
  })
}
