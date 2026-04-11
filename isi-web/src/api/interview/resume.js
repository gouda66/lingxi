import request from '@/utils/request'

// 查询简历列表
export function listResume(query) {
  return request({
    url: '/interview/resume/list',
    method: 'get',
    params: query
  })
}

// 查询简历详细
export function getResume(id) {
  return request({
    url: '/interview/resume/' + id,
    method: 'get'
  })
}

// 删除简历
export function delResume(id) {
  return request({
    url: '/interview/resume/' + id,
    method: 'delete'
  })
}

// 上传简历
export function uploadResume(data) {
  return request({
    url: '/interview/resume/upload',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 重新解析简历
export function reParseResume(id) {
  return request({
    url: '/interview/resume/reparse/' + id,
    method: 'put'
  })
}
