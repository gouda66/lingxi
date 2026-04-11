import request from '@/utils/request'

/**
 * 语音识别
 * @param {FormData} data - 包含音频文件的FormData对象
 */
export function recognizeSpeech(data) {
  return request({
    url: '/interview/voice/recognize',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 流式语音识别
 * @param {FormData} data - 包含音频文件的FormData对象
 */
export function recognizeSpeechStream(data) {
  return request({
    url: '/interview/voice/recognize-stream',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 健康检查
 */
export function voiceHealth() {
  return request({
    url: '/interview/voice/health',
    method: 'get'
  })
}
/**
 * 启动实时语音识别会话
 * @param {string} sessionId - 会话ID
 */
export function startVoiceSession(sessionId) {
  return request({
    url: '/interview/voice/session/start',
    method: 'post',
    params: { sessionId }
  })
}

/**
 * 发送音频帧到识别会话
 * @param {string} sessionId - 会话ID
 * @param {ArrayBuffer|Blob} audioFrame - PCM音频帧数据
 */
export function sendAudioFrame(sessionId, audioFrame) {
  return request({
    url: '/interview/voice/session/send',
    method: 'post',
    params: { sessionId },
    data: audioFrame,
    headers: {
      'Content-Type': 'application/octet-stream'
    }
  })
}

/**
 * 停止语音识别会话并获取结果
 * @param {string} sessionId - 会话ID
 */
export function stopVoiceSession(sessionId) {
  return request({
    url: '/interview/voice/session/stop',
    method: 'post',
    params: { sessionId }
  })
}

/**
 * 取消语音识别会话
 * @param {string} sessionId - 会话ID
 */
export function cancelVoiceSession(sessionId) {
  return request({
    url: '/interview/voice/session/cancel',
    method: 'delete',
    params: { sessionId }
  })
}

