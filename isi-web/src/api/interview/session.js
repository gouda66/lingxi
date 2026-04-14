import request from '@/utils/request'

// 查询面试会话列表
export function listSession(query) {
  return request({
    url: '/interview/session/list',
    method: 'get',
    params: query
  })
}

// 查询面试会话详细
export function getSession(id) {
  return request({
    url: '/interview/session/' + id,
    method: 'get'
  })
}

// 创建面试会话
export function createSession(data) {
  return request({
    url: '/interview/session/create',
    method: 'post',
    data: data
  })
}

// 生成面试问题
export function generateQuestions(sessionId) {
  return request({
    url: '/interview/session/generate-questions',
    method: 'post',
    data: { sessionId }
  })
}

// 获取会话问题列表
export function getSessionQuestions(sessionId) {
  return request({
    url: '/interview/question/list',
    method: 'get',
    params: { sessionId }
  })
}

// HR修改题目
export function updateQuestion(data) {
  return request({
    url: '/interview/question/update',
    method: 'put',
    data: data
  })
}

// 提交回答并评分
export function submitAnswer(data) {
  return request({
    url: '/interview/answer/submit',
    method: 'post',
    data: data
  })
}

// 生成面试报告
export function generateReport(data) {
  return request({
    url: '/interview/session/generate-report',
    method: 'post',
    data: data
  })
}

// 关闭面试间
export function closeSession(sessionId) {
  return request({
    url: '/interview/session/close/' + sessionId,
    method: 'put'
  })
}

// 获取在线人数
export function getOnlineUsers(roomId) {
  return request({
    url: '/interview/session/online-users/' + roomId,
    method: 'get'
  })
}
