import request from '@/utils/request'

// 查询请求列表
export function list(query) {
  return request({
    url: '/seo/request/list',
    method: 'get',
    params: query
  })
}

// 查询请求详细
export function get(id) {
  return request({
    url: '/seo/request/' + id,
    method: 'get'
  })
}

// 新增请求
export function add(data) {
  return request({
    url: '/seo/request',
    method: 'post',
    data: data
  })
}

// 修改请求
export function update(data) {
  return request({
    url: '/seo/request',
    method: 'put',
    data: data
  })
}

// 删除请求
export function remove(id) {
  return request({
    url: '/seo/request/' + id,
    method: 'delete'
  })
}
