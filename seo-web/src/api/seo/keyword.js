import request from '@/utils/request'

// 查询关键词列表
export function list(query) {
  return request({
    url: '/seo/keyword/list',
    method: 'get',
    params: query
  })
}

// 查询关键词详细
export function get(id) {
  return request({
    url: '/seo/keyword/' + id,
    method: 'get'
  })
}

// 新增关键词
export function add(data) {
  return request({
    url: '/seo/keyword',
    method: 'post',
    data: data
  })
}

// 修改关键词
export function update(data) {
  return request({
    url: '/seo/keyword',
    method: 'put',
    data: data
  })
}

// 删除关键词
export function remove(id) {
  return request({
    url: '/seo/keyword/' + id,
    method: 'delete'
  })
}
