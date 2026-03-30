import request from '@/utils/request'

// 登录方法
export function login(username, password, code, uuid) {
  const data = {
    username,
    password,
    code,
    uuid
  }
  return request({
    url: '/seo/login',
    headers: {
      isToken: false,
      repeatSubmit: false
    },
    method: 'post',
    data: data,
    timeout: 60000
  })
}

// 注册方法
export function register(data) {
  return request({
    url: '/seo/register',
    headers: {
      isToken: false
    },
    method: 'post',
    data: data,
    timeout: 600000
  })
}

// 获取用户详细信息
export function getInfo() {
  return request({
    url: '/seo/getInfo',
    method: 'get'
  })
}

// 解锁屏幕
export function unlockScreen(password) {
  return request({
    url: '/seo/unlockscreen',
    method: 'post',
    data: { password }
  })
}

// 退出方法
export function logout() {
  return request({
    url: '/seo/logout',
    method: 'post'
  })
}

// 获取验证码
export function getCodeImg() {
  return request({
    url: '/seo/captchaImage',
    headers: {
      isToken: false
    },
    method: 'get',
    timeout: 60000
  })
}