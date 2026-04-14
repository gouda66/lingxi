<template>
  <div class="interview-session-container">
    <!-- 顶部信息栏 -->
    <div class="session-header">
      <div class="header-left">
        <el-tag type="success" size="large">面试中</el-tag>
        <span class="session-code">房间号: {{ sessionInfo.sessionCode }}</span>
        <span class="job-position">职位: {{ sessionInfo.jobPosition }}</span>
      </div>
      <div class="header-right">
        <span class="online-count">
          <el-icon><User /></el-icon>
          在线: {{ onlineUsers.length }}人
        </span>
        <el-button 
          v-if="canCloseSession" 
          type="danger" 
          @click="handleCloseSession"
        >
          关闭面试间
        </el-button>
      </div>
    </div>

    <!-- 主体内容区 -->
    <div class="session-body">
      <!-- 面试主区域（全屏视频） -->
      <div class="main-area">
        <!-- 视频区域（填充满整个容器） -->
        <div class="video-container">
          <video
            ref="videoRef"
            class="interview-video"
            autoplay
            playsinline
            muted
            style="transform: scaleX(-1);"
          ></video>
          
          <!-- 弹幕层（覆盖在视频上） -->
          <div v-if="danmakuEnabled" class="danmaku-overlay">
            <transition-group name="danmaku">
              <div
                v-for="msg in visibleMessages"
                :key="msg.id"
                class="danmaku-item"
                :style="{ top: msg.position + '%' }"
              >
                <span class="danmaku-speaker">{{ msg.speaker }}:</span>
                <span class="danmaku-text">{{ msg.text }}</span>
              </div>
            </transition-group>
          </div>
          
          <!-- 题目卡片（悬浮在视频上方） -->
          <div class="question-overlay">
            <div class="question-card">
              <div class="question-header">
                <el-tag :type="getQuestionTypeTag(currentQuestion?.questionType)">
                  {{ getQuestionTypeName(currentQuestion?.questionType) }}
                </el-tag>
                <span class="question-seq">第 {{ currentQuestion?.sequenceNo }} 题</span>
              </div>
              <div class="question-content">
                {{ currentQuestion?.content || '等待面试官出题...' }}
              </div>
            </div>
          </div>

          
          <!-- 回答输入区（仅面试者可见，悬浮） -->
          <div v-if="!isHR" class="answer-area-overlay">
            <el-input
              v-model="userAnswer"
              type="textarea"
              :rows="6"
              placeholder="请输入你的回答..."
              maxlength="2000"
              show-word-limit
            />
            <div class="answer-actions">
              <el-button 
                type="primary" 
                size="large"
                :loading="submitting"
                :disabled="!userAnswer.trim()"
                @click="handleSubmitAnswer"
              >
                提交回答
              </el-button>
            </div>
          </div>

          <!-- HR操作区（悬浮） -->
          <div v-if="isHR" class="hr-actions-overlay">
            <el-button type="primary" @click="showEditQuestion = true">
              修改题目
            </el-button>
            <el-button 
              :type="isLastQuestion ? 'danger' : 'success'" 
              @click="handleNextOrEnd"
            >
              {{ isLastQuestion ? '面试完成' : '下一题' }}
            </el-button>
          </div>
          
          <!-- 视频控制按钮 -->
          <div class="video-controls">
            <el-button
              :type="cameraEnabled ? 'danger' : 'success'"
              circle
              size="large"
              @click="toggleCamera"
            >
              <el-icon><VideoCamera v-if="!cameraEnabled" /><VideoPause v-else /></el-icon>
            </el-button>
            <el-button
              :type="isRecording ? 'danger' : (hasRecordedCurrentQuestion ? 'info' : 'warning')"
              circle
              size="large"
              :disabled="hasRecordedCurrentQuestion && !isRecording"
              @click="toggleVoiceRecognition"
            >
              <el-icon><Microphone v-if="!isRecording" /><TurnOff v-else /></el-icon>
            </el-button>
          </div>
          
          <!-- 用户信息标签 -->
          <div class="user-info-tag">
            <el-avatar :size="40" :src="userInfo.avatar" />
            <span class="username">{{ userInfo.name }}</span>
            <el-tag size="small" :type="isHR ? 'warning' : 'success'">
              {{ isHR ? '面试官' : '面试者' }}
            </el-tag>
          </div>
          
          <!-- 在线人数标签 -->
          <div class="online-count-tag">
            <el-icon><User /></el-icon>
            <span>{{ onlineUsers.length }}人在线</span>
          </div>
        </div>

        <!-- 实时评分雷达图（右下角透明背景） -->
        <div class="radar-chart-overlay">
          <div ref="radarChartRef" class="radar-chart"></div>
          <div class="total-score" v-if="totalScore > 0">
            <div class="score-value">{{ totalScore.toFixed(2) }}</div>
            <div class="score-label">综合评分</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑题目对话框 -->
    <el-dialog
      v-model="showEditQuestion"
      title="修改面试题目"
      width="600px"
    >
      <el-form :model="editQuestionForm" label-width="100px">
        <el-form-item label="题目内容">
          <el-input
            v-model="editQuestionForm.content"
            type="textarea"
            :rows="4"
          />
        </el-form-item>
        <el-form-item label="参考答案">
          <el-input
            v-model="editQuestionForm.referenceAnswer"
            type="textarea"
            :rows="6"
          />
        </el-form-item>
        <el-form-item label="修改原因">
          <el-input
            v-model="editQuestionForm.modifyReason"
            type="textarea"
            :rows="2"
            placeholder="选填"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditQuestion = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateQuestion">确定</el-button>
      </template>
    </el-dialog>

    <!-- 简历选择对话框 -->
    <el-dialog
      v-model="showResumeSelect"
      title="选择面试简历"
      width="800px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
    >
      <div v-loading="resumeLoading">
        <el-table
          :data="resumeList"
          highlight-current-row
          @current-change="handleResumeChange"
          max-height="400"
        >
          <el-table-column type="radio" label="选择" width="60" align="center" />
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="fullName" label="姓名" width="100" />
          <el-table-column prop="jobTitle" label="求职意向" min-width="150" />
          <el-table-column prop="phone" label="联系电话" width="120" />
          <el-table-column prop="email" label="邮箱" min-width="180" />
          <el-table-column prop="workYears" label="工作年限" width="100" align="center">
            <template #default="scope">
              {{ scope.row.workYears ? scope.row.workYears + '年' : '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="location" label="所在地" width="120" />
          <el-table-column label="解析状态" width="100" align="center">
            <template #default="scope">
              <el-tag v-if="scope.row.parseStatus === 2" type="success">已解析</el-tag>
              <el-tag v-else-if="scope.row.parseStatus === 1" type="warning">解析中</el-tag>
              <el-tag v-else-if="scope.row.parseStatus === 3" type="danger">解析失败</el-tag>
              <el-tag v-else type="info">待解析</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button @click="handleCancelResume">取消</el-button>
        <el-button type="primary" @click="handleConfirmResume">确认选择</el-button>
      </template>
    </el-dialog>

    <!-- 右侧历史记录面板（透明悬浮） -->
    <div v-if="historyList.length > 0" class="history-panel">
      <div class="history-header">
        <el-icon><Clock /></el-icon>
        <span>面试记录</span>
        <el-tag size="small" type="info">{{ historyList.length }}/{{ questions.length }}</el-tag>
      </div>
      <div class="history-content" ref="historyContentRef">
        <div
          v-for="(item, index) in historyList"
          :key="index"
          class="history-item"
        >
          <!-- 题目部分 -->
          <div class="question-section">
            <div class="section-header">
              <el-tag type="primary" size="small">问题 {{ index + 1 }}</el-tag>
              <span class="question-type">{{ getQuestionTypeName(item.questionType) }}</span>
            </div>
            <div class="question-text">{{ item.question }}</div>
          </div>
          
          <!-- 回答部分 -->
          <div v-if="item.answer" class="answer-section">
            <div class="section-header">
              <el-tag type="success" size="small">语音识别结果</el-tag>
            </div>
            <div class="answer-text">{{ item.answer }}</div>
          </div>
          <div v-else class="answer-section empty-answer">
            <el-tag type="info" size="small">暂无回答</el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup name="InterviewSession">
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { VideoCamera, VideoPause, User, ChatDotRound, ChatLineRound, Microphone, TurnOff, Clock } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import {
  getSession,
  createSession,
  generateQuestions,
  getSessionQuestions,
  updateQuestion,
  submitAnswer,
  generateReport,
  closeSession,
  getOnlineUsers
} from '@/api/interview/session'

import { listResume } from '@/api/interview/resume'
import { startVoiceSession, sendAudioFrame, stopVoiceSession } from '@/api/interview/voice'
import useUserStore from '@/store/modules/user'
import { bufferToWave } from '@/utils/audioUtils'
import { generateInterviewReport } from '@/api/interview/template'


// 在 script setup 顶部添加
let audioContext = null
let mediaStreamSource = null
let recorderNode = null
let pcmData = []

const route = useRoute()
const userStore = useUserStore()

// 响应式数据
const sessionInfo = ref({})
const questions = ref([])
const currentQuestionIndex = ref(0)
const userAnswer = ref('')
const submitting = ref(false)
const onlineUsers = ref([])
const messages = ref([])
const visibleMessages = ref([])
const danmakuEnabled = ref(true)
const showEditQuestion = ref(false)
const isRecording = ref(false)
const historyList = ref([])
const hasRecordedCurrentQuestion = ref(false) // 跟踪当前题目是否已录音
const editQuestionForm = ref({
  id: null,
  content: '',
  referenceAnswer: '',
  modifyReason: ''
})
const radarData = ref({
  indicators: ['专业准确性', '逻辑清晰度', '知识深度', '表达流畅度', '项目结合度', '拓展思考力'],
  values: []
})
const totalScore = ref(0)
const historyContentRef = ref(null)

// 简历选择相关
const showResumeSelect = ref(false)
const resumeList = ref([])
const selectedResumeId = ref(null)
const resumeLoading = ref(false)
const resumeQueryParams = ref({
  pageNum: 1,
  pageSize: 10
})

const videoRef = ref(null)
const cameraEnabled = ref(false)
let localStream = null

// 实时语音识别相关
let voiceSessionId = null
let scriptProcessorNode = null
let pcmBuffer = []
let frameInterval = null



// 用户信息
const userInfo = computed(() => ({
  name: userStore.name || userStore.nickName || '用户',
  avatar: userStore.avatar || ''
}))

// Refs
const radarChartRef = ref(null)
const danmakuContainerRef = ref(null)
let radarChart = null
let stompClient = null
let messageTimer = null

// 计算属性
const currentQuestion = computed(() => {
  return questions.value[currentQuestionIndex.value] || {}
})

const isHR = computed(() => {
  return userStore.roles?.includes('hr') || userStore.roles?.includes('admin')
})

const canCloseSession = computed(() => {
  return onlineUsers.value.length <= 1 && isHR.value
})

// 判断是否是最后一题
const isLastQuestion = computed(() => {
  return questions.value.length > 0 && currentQuestionIndex.value >= questions.value.length - 1
})

// WebSocket 连接
function connectWebSocket() {
  const roomId = sessionInfo.value.sessionCode
  if (!roomId) return

  const socket = new SockJS('/ws/interview')
  stompClient = Stomp.over(socket)
  
  stompClient.connect({}, () => {
    console.log('WebSocket 连接成功')
    
    // 订阅房间消息
    stompClient.subscribe(`/topic/interview/${roomId}`, (message) => {
      const data = JSON.parse(message.body)
      addDanmakuMessage(data)
      
      // 如果是评分消息，更新雷达图
      if (data.type === 'SCORE') {
        // 处理评分数据
        if (data.dimensions) {
          updateRadarChart({
            accuracy: data.dimensions.accuracy,
            logic: data.dimensions.logic,
            depth: data.dimensions.depth,
            expression: data.dimensions.expression,
            projectCombine: data.dimensions.projectCombine,
            extension: data.dimensions.extension,
            totalScore: data.totalScore
          })
        } else if (data.radar && data.radar.values) {
          // 兼容 radar.values 格式
          const values = data.radar.values
          updateRadarChart({
            accuracy: values[0] || 0,
            logic: values[1] || 0,
            depth: values[2] || 0,
            expression: values[3] || 0,
            projectCombine: values[4] || 0,
            extension: values[5] || 0,
            totalScore: data.totalScore
          })
        }
      }
    })
  }, (error) => {
    console.error('WebSocket 连接失败:', error)
    ElMessage.error('实时通信连接失败')
  })
}

// 添加弹幕消息
function addDanmakuMessage(msg) {
  const message = {
    id: Date.now() + Math.random(),
    speaker: msg.speaker || '系统',
    text: msg.text,
    position: Math.random() * 60 + 10, // 10%-70% 随机位置
    timestamp: Date.now()
  }
  
  messages.value.push(message)
  visibleMessages.value.push(message)
  
  // 限制显示的消息数量
  if (visibleMessages.value.length > 20) {
    visibleMessages.value.shift()
  }
  
  // 5秒后自动消失
  setTimeout(() => {
    const index = visibleMessages.value.findIndex(m => m.id === message.id)
    if (index > -1) {
      visibleMessages.value.splice(index, 1)
    }
  }, 5000)
}

// 初始化雷达图
function initRadarChart() {
  if (!radarChartRef.value) return
  
  radarChart = echarts.init(radarChartRef.value)
  const option = {
    backgroundColor: 'transparent',
    radar: {
      indicator: radarData.value.indicators.map((name, index) => ({
        name,
        max: 10
      })),
      radius: '55%',
      center: ['50%', '50%'],
      axisName: {
        color: '#fff',
        fontSize: 11,
        formatter: (value) => {
          return value.length > 4 ? value.slice(0, 4) + '\n' + value.slice(4) : value
        }
      },
      splitArea: {
        areaStyle: {
          color: ['rgba(255,255,255,0.02)', 'rgba(255,255,255,0.05)']
        }
      },
      axisLine: {
        lineStyle: {
          color: 'rgba(255,255,255,0.2)'
        }
      },
      splitLine: {
        lineStyle: {
          color: 'rgba(255,255,255,0.2)'
        }
      }
    },
    series: [{
      type: 'radar',
      data: [{
        value: radarData.value.values,
        name: '能力评估',
        areaStyle: {
          color: 'rgba(64, 158, 255, 0.25)'
        },
        lineStyle: {
          color: '#409eff',
          width: 2
        },
        itemStyle: {
          color: '#409eff',
          borderColor: '#fff',
          borderWidth: 1
        }
      }]
    }]
  }
  
  radarChart.setOption(option)
}

// 更新雷达图
function updateRadarChart(scoreData) {
  if (!scoreData || !radarChart) {
    console.warn('雷达图更新失败:', { scoreData, radarChart: !!radarChart })
    return
  }
  
  console.log('更新雷达图数据:', scoreData)
  
  const values = [
    scoreData.accuracy || 0,
    scoreData.logic || 0,
    scoreData.depth || 0,
    scoreData.expression || 0,
    scoreData.projectCombine || 0,
    scoreData.extension || 0
  ]
  
  console.log('雷达图数值:', values)
  
  radarData.value.values = values
  
  // 计算总分
  if (scoreData.totalScore) {
    totalScore.value = scoreData.totalScore
    console.log('综合评分:', totalScore.value)
  }
  
  radarChart.setOption({
    series: [{
      data: [{
        value: values,
        name: '能力评估'
      }]
    }]
  })
  
  console.log('雷达图已更新')
}

// 提交回答
async function handleSubmitAnswer() {
  if (!userAnswer.value.trim()) {
    ElMessage.warning('请输入回答内容')
    return
  }
  
  submitting.value = true
  try {
    const response = await submitAnswer({
      sessionId: sessionInfo.value.id,
      questionId: currentQuestion.value.id,
      contentText: userAnswer.value
    })
    
    ElMessage.success('回答提交成功')
    userAnswer.value = ''
    
    // 通过 WebSocket 推送回答
    if (stompClient && stompClient.connected) {
      stompClient.send('/app/interview/answer', {}, JSON.stringify({
        roomId: sessionInfo.value.sessionCode,
        sessionId: sessionInfo.value.id,
        questionId: currentQuestion.value.id,
        userAnswer: userAnswer.value
      }))
    }
  } catch (error) {
    ElMessage.error('提交失败: ' + error.message)
  } finally {
    submitting.value = false
  }
}

// HR修改题目
async function handleUpdateQuestion() {
  try {
    await updateQuestion({
      id: currentQuestion.value.id,
      content: editQuestionForm.value.content,
      referenceAnswer: editQuestionForm.value.referenceAnswer,
      modifyReason: editQuestionForm.value.modifyReason
    })
    
    ElMessage.success('题目修改成功')
    showEditQuestion.value = false
    
    // 刷新题目列表
    await loadQuestions()
  } catch (error) {
    ElMessage.error('修改失败: ' + error.message)
  }
}

// 下一题或结束面试
async function handleNextOrEnd() {
  if (isLastQuestion.value) {
    // 最后一题，结束面试
    await handleCloseSession()
  } else {
    // 不是最后一题，继续下一题
    handleNextQuestion()
  }
}

// 下一题
function handleNextQuestion() {
  if (currentQuestionIndex.value < questions.value.length - 1) {
    currentQuestionIndex.value++
    
    // 重置当前题目的录音状态
    hasRecordedCurrentQuestion.value = false
    
    // 推送新题目
    if (stompClient && stompClient.connected) {
      stompClient.send('/app/interview/push-question', {}, JSON.stringify({
        roomId: sessionInfo.value.sessionCode,
        question: currentQuestion.value.content
      }))
    }
  } else {
    ElMessage.warning('已经是最后一题了')
  }
}

// 关闭面试间
async function handleCloseSession() {
  try {
    await ElMessageBox.confirm(
      '关闭后将生成面试报告，确定要关闭吗？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    console.log('开始生成面试报告...')
    // 1. 调用后端接口生成并保存报告
    const reportResult = await generateReport({
      sessionId: sessionInfo.value.id,
      scoreJson: JSON.stringify(radarData.value.values),
      qaHistory: buildQaHistory()
    })
    console.log('后端报告生成成功:', reportResult)
    
    // 2. 使用前端JS方法生成并下载HTML报告
    const report = reportResult.data?.report || reportResult.report
    if (report) {
      console.log('开始生成HTML报告...')
      // 构建CSV格式数据: id,breadthScore,depthScore,,,duration,,,,,,advantages,disadvantages,,evaluation,,date
      const csvData = [
        report.sessionId || sessionInfo.value.id,  // id
        report.technicalScore ? Math.round(report.technicalScore) : 70,  // breadthScore (技术广度)
        report.logicScore ? Math.round(report.logicScore) : 65,  // depthScore (技术深度)
        '',  // 占位
        '',  // 占位
        calculateDuration(),  // duration (分钟)
        '',  // 占位
        '',  // 占位
        '',  // 占位
        '',  // 占位
        report.strengths || '暂无数据',  // advantages
        report.weaknesses || '暂无数据',  // disadvantages
        '',  // 占位
        report.overallEvaluation || '暂无数据',  // evaluation
        '',  // 占位
        '',  // 占位
        report.generatedAt || new Date().toISOString()  // date
      ].join(',')
      
      // 调用前端JS方法生成并下载报告
      const result = generateInterviewReport(csvData)
      if (result && result.success) {
        console.log('HTML报告已下载:', result.filename)
        ElMessage.success('面试报告已生成并下载')
      } else {
        console.warn('HTML报告生成失败，但后端报告已保存')
        ElMessage.warning('报告已保存到系统，下载功能异常')
      }
    } else {
      console.warn('未获取到报告数据')
      ElMessage.warning('报告已生成，但未获取到详细信息')
    }
    
    console.log('开始关闭会话...')
    // 3. 关闭会话
    await closeSession(sessionInfo.value.id)
    console.log('会话已关闭')
    
    // 断开 WebSocket
    if (stompClient) {
      stompClient.disconnect()
    }
    
    // 返回列表页
    setTimeout(() => {
      window.history.back()
    }, 1500)
  } catch (error) {
    // 用户取消操作
    if (error === 'cancel') {
      console.log('用户取消了关闭操作')
      return
    }
    
    // 其他错误
    console.error('关闭会话失败:', error)
    console.error('错误详情:', {
      message: error?.message,
      msg: error?.msg,
      response: error?.response,
      responseData: error?.response?.data
    })
    const errorMsg = error?.message || error?.msg || error?.response?.data?.msg || '未知错误'
    ElMessage.error('关闭失败: ' + errorMsg)
  }
}

// 计算面试时长(分钟)
function calculateDuration() {
  if (sessionInfo.value.startTime && sessionInfo.value.actualEndTime) {
    const start = new Date(sessionInfo.value.startTime)
    const end = new Date(sessionInfo.value.actualEndTime)
    return ((end - start) / 60000).toFixed(2)
  }
  return '0'
}

// 构建问答历史
function buildQaHistory() {
  if (historyList.value.length > 0) {
    // 使用实际的语音识别历史记录
    return historyList.value.map((item, index) => {
      return `Q${index + 1}: ${item.question}\nA${index + 1}: ${item.answer || '未回答'}`
    }).join('\n\n')
  } else {
    // 如果没有历史记录，使用题目列表
    return questions.value.map((q, index) => {
      return `Q${index + 1}: ${q.content}\nA${index + 1}: [待补充]`
    }).join('\n\n')
  }
}

// 加载会话信息
async function loadSessionInfo() {
  const sessionId = route.params.id || route.query.sessionId
  if (!sessionId) {
    ElMessage.error('缺少会话ID')
    return
  }
  
  try {
    const response = await getSession(sessionId)
    sessionInfo.value = response.data
    
    // 连接 WebSocket（在获取会话信息后立即连接）
    connectWebSocket()
    
    // 获取在线用户（确保 sessionCode 已加载）
    await loadOnlineUsers()
  } catch (error) {
    ElMessage.error('加载会话失败: ' + error.message)
  }
}

// 加载问题列表
async function loadQuestions() {
  console.log('加载问题列表...')
  try {
    const response = await getSessionQuestions(sessionInfo.value.id)
    questions.value = response || []
    
    // 重置录音状态
    hasRecordedCurrentQuestion.value = false
  } catch (error) {
    console.error('加载问题失败:', error)
  }
}

// 生成面试问题
async function handleGenerateQuestions() {
  try {
    await generateQuestions(sessionInfo.value.id)
    ElMessage.success('问题生成成功')
    await loadQuestions()
  } catch (error) {
    ElMessage.error('生成问题失败: ' + error.message)
  }
}

// 加载在线用户
async function loadOnlineUsers() {
  // 检查 sessionCode 是否存在
  if (!sessionInfo.value.sessionCode) {
    console.warn('会话代码未加载，跳过获取在线用户')
    return
  }
  
  try {
    const response = await getOnlineUsers(sessionInfo.value.sessionCode)
    onlineUsers.value = response.data || []
  } catch (error) {
    console.error('加载在线用户失败:', error)
  }
}

// 显示简历选择弹窗
async function showResumeSelectDialog() {
  showResumeSelect.value = true
  await loadResumeList()
}

// 加载简历列表
async function loadResumeList() {
  resumeLoading.value = true
  try {
    const response = await listResume(resumeQueryParams.value)
    const pageData = response || {}
    resumeList.value = pageData.records || pageData.list || []
  } catch (error) {
    console.error('加载简历列表失败:', error)
    ElMessage.error('加载简历列表失败')
  } finally {
    resumeLoading.value = false
  }
}

// 确认选择简历
async function handleConfirmResume() {
  if (!selectedResumeId.value) {
    ElMessage.warning('请选择一份简历')
    return
  }

  try {
    // 获取选中的简历信息
    const selectedResume = resumeList.value.find(r => r.id === selectedResumeId.value)
    if (!selectedResume) {
      ElMessage.error('未找到选中的简历')
      return
    }

    // 调用后端接口创建会话
    const createResponse = await createSession({
      resumeId: selectedResumeId.value,
      jobPosition: selectedResume.jobTitle || '未指定职位',
      candidateId: userStore.userId
    })

    // 获取创建的会话信息
    const { sessionId, sessionCode, jobPosition } = createResponse

    // 更新会话信息
    sessionInfo.value = {
      id: sessionId,
      sessionCode,
      jobPosition
    }

    // 关闭简历选择弹窗
    showResumeSelect.value = false

    // 连接 WebSocket
    connectWebSocket()

    // 获取在线用户
    await loadOnlineUsers()

    // 直接调用生成题目接口
    await handleGenerateQuestions()

    ElMessage.success('会话创建成功，开始面试')
  } catch (error) {
    ElMessage.error('创建会话失败: ' + error.message)
  }
}


// 取消选择简历
function handleCancelResume() {
  ElMessage.warning('请先选择简历再继续面试')
  showResumeSelect.value = false
  // 可以选择返回列表页或保持当前状态
  setTimeout(() => {
    window.history.back()
  }, 1000)
}

// 工具函数
function getQuestionTypeTag(type) {
  const tags = {
    1: 'danger',   // 技术问题
    2: 'warning',  // 项目经验
    3: 'success',  // 情景题
    4: 'info',     // 基础素质
    5: 'primary'   // 算法题
  }
  return tags[type] || 'info'  // 默认使用 info
}

function getQuestionTypeName(type) {
  const names = {
    1: '技术问题',
    2: '项目经验',
    3: '情景题',
    4: '基础素质',
    5: '算法题'
  }
  return names[type] || '未知'
}

// 摄像头控制
async function toggleCamera() {
  if (cameraEnabled.value) {
    // 关闭摄像头
    stopCamera()
  } else {
    // 开启摄像头
    await startCamera()
  }
}

// 语音识别控制
let mediaRecorder = null
let audioChunks = []

async function toggleVoiceRecognition() {
  if (isRecording.value) {
    // 停止录音
    stopVoiceRecognition()
  } else {
    // 检查当前题目是否已经录过音
    if (hasRecordedCurrentQuestion.value) {
      ElMessage.warning('当前题目已完成录音，请等待下一题')
      return
    }
    // 开始录音
    await startVoiceRecognition()
  }
}



async function startVoiceRecognition() {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({
      audio: {
        sampleRate: 16000,
        channelCount: 1,
        echoCancellation: true,
        noiseSuppression: true
      }
    })

    audioContext = new (window.AudioContext || window.webkitAudioContext)({
      sampleRate: 16000
    })

    mediaStreamSource = audioContext.createMediaStreamSource(stream)

    // 创建脚本处理器节点（缓冲区大小 4096，约 256ms）
    scriptProcessorNode = audioContext.createScriptProcessor(4096, 1, 1)

    pcmBuffer = []
    let frameCounter = 0

    scriptProcessorNode.onaudioprocess = (e) => {
      const input = e.inputBuffer.getChannelData(0)

      // 将 Float32 转换为 Int16 PCM
      const int16Data = new Int16Array(input.length)
      for (let i = 0; i < input.length; i++) {
        const s = Math.max(-1, Math.min(1, input[i]))
        int16Data[i] = s < 0 ? s * 0x8000 : s * 0x7FFF
      }

      pcmBuffer.push(int16Data.buffer)
      frameCounter++

      // 每 4 帧（约 1 秒）发送一次
      if (frameCounter >= 4) {
        sendPCMFrame()
        frameCounter = 0
      }
    }

    mediaStreamSource.connect(scriptProcessorNode)
    scriptProcessorNode.connect(audioContext.destination)

    // 启动语音识别会话
    voiceSessionId = `voice_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
    await startVoiceSession(voiceSessionId)

    isRecording.value = true
    ElMessage.success('开始实时语音识别...')

  } catch (error) {
    console.error('启动录音失败:', error)
    ElMessage.error('无法访问麦克风: ' + error.message)
  }
}

// 发送 PCM 音频帧
async function sendPCMFrame() {
  if (pcmBuffer.length === 0 || !voiceSessionId) return

  // 合并所有缓冲的帧
  const totalLength = pcmBuffer.reduce((acc, buf) => acc + buf.byteLength, 0)
  const mergedBuffer = new Uint8Array(totalLength)
  let offset = 0

  pcmBuffer.forEach(buf => {
    mergedBuffer.set(new Uint8Array(buf), offset)
    offset += buf.byteLength
  })

  // 清空缓冲
  pcmBuffer = []

  try {
    // 发送音频帧（使用 Blob 避免直接发送 ArrayBuffer）
    const blob = new Blob([mergedBuffer], { type: 'application/octet-stream' })
    await sendAudioFrame(voiceSessionId, blob)
  } catch (error) {
    console.error('发送音频帧失败:', error)
  }
}

function stopVoiceRecognition() {
  // 断开音频处理
  if (scriptProcessorNode) {
    scriptProcessorNode.disconnect()
    scriptProcessorNode = null
  }

  if (mediaStreamSource) {
    mediaStreamSource.disconnect()
    mediaStreamSource = null
  }

  if (audioContext) {
    audioContext.close()
    audioContext = null
  }

  isRecording.value = false
  ElMessage.info('正在获取识别结果...')

  // 停止语音识别会话并获取结果
  if (voiceSessionId) {
    stopVoiceSession(voiceSessionId)
        .then(async response => {
            const recognizedText = response.text
            if (recognizedText.trim()) {
              addVoiceRecognitionResult(recognizedText)
              ElMessage.success('识别成功')
              
              // 自动调用评分接口
              await scoreVoiceAnswer(recognizedText)
            } else {
              ElMessage.warning('未识别到内容')
            }
          })
        .catch(error => {
          console.error('停止语音识别失败:', error)
          ElMessage.error('获取识别结果失败')
        })
        .finally(() => {
          voiceSessionId = null
        })
  }
}

// 添加语音识别结果到历史记录
function addVoiceRecognitionResult(text) {
  const currentQ = currentQuestion.value
  if (currentQ) {
    historyList.value.push({
      question: currentQ.content,
      questionType: currentQ.questionType,
      answer: text,
      timestamp: Date.now()
    })
    
    // 标记当前题目已录音
    hasRecordedCurrentQuestion.value = true
    
    // 滚动到底部
    nextTick(() => {
      if (historyContentRef.value) {
        historyContentRef.value.scrollTop = historyContentRef.value.scrollHeight
      }
    })
  }
}

// 语音识别答案评分
async function scoreVoiceAnswer(recognizedText) {
  const currentQ = currentQuestion.value
  if (!currentQ || !currentQ.id) {
    console.warn('当前没有题目，无法评分')
    return
  }
  
  try {
    const response = await submitAnswer({
      sessionId: sessionInfo.value.id,
      questionId: currentQ.id,
      contentText: recognizedText
    })
    
    console.log('评分结果:', response)
    ElMessage.success('评分完成')
    
    // 更新雷达图数据（如果返回了评分）
    if (response) {
      const scoreData = response
      
      // 优先使用 dimensions 字段（后端返回的标准格式）
      if (scoreData.dimensions) {
        updateRadarChart({
          accuracy: scoreData.dimensions.accuracy,
          logic: scoreData.dimensions.logic,
          depth: scoreData.dimensions.depth,
          expression: scoreData.dimensions.expression,
          projectCombine: scoreData.dimensions.projectCombine,
          extension: scoreData.dimensions.extension,
          totalScore: scoreData.totalScore
        })
      } else if (scoreData.radar && scoreData.radar.values) {
        // 兼容 radar.values 格式
        const values = scoreData.radar.values
        updateRadarChart({
          accuracy: values[0] || 0,
          logic: values[1] || 0,
          depth: values[2] || 0,
          expression: values[3] || 0,
          projectCombine: values[4] || 0,
          extension: values[5] || 0,
          totalScore: scoreData.totalScore
        })
      }
    }
  } catch (error) {
    console.error('评分失败:', error)
    ElMessage.error('评分失败: ' + (error.message || '未知错误'))
  }
}


// 简历选择行变化
function handleResumeChange(row) {
  if (row) {
    selectedResumeId.value = row.id
  }
}

async function startCamera() {
  try {
    localStream = await navigator.mediaDevices.getUserMedia({
      video: {
        width: { ideal: 1280 },
        height: { ideal: 720 },
        facingMode: 'user'
      },
      audio: true
    })
    
    if (videoRef.value) {
      videoRef.value.srcObject = localStream
      cameraEnabled.value = true
      ElMessage.success('摄像头已开启')
      
      // 通过 WebSocket 通知其他人
      if (stompClient && stompClient.connected) {
        stompClient.send('/app/interview/camera-status', {}, JSON.stringify({
          roomId: sessionInfo.value.sessionCode,
          userId: userStore.userId,
          userName: userInfo.value.name,
          status: 'on'
        }))
      }
    }
  } catch (error) {
    console.error('开启摄像头失败:', error)
    ElMessage.error('无法访问摄像头，请检查权限设置')
  }
}

function stopCamera() {
  if (localStream) {
    localStream.getTracks().forEach(track => track.stop())
    localStream = null
  }
  
  if (videoRef.value) {
    videoRef.value.srcObject = null
  }
  
  cameraEnabled.value = false
  ElMessage.info('摄像头已关闭')
  
  // 通过 WebSocket 通知其他人
  if (stompClient && stompClient.connected) {
    stompClient.send('/app/interview/camera-status', {}, JSON.stringify({
      roomId: sessionInfo.value.sessionCode,
      userId: userStore.userId,
      userName: userInfo.value.name,
      status: 'off'
    }))
  }
}

// 生命周期
onMounted(() => {
  // 检查是否有 sessionId，如果没有则显示简历选择弹窗
  const sessionId = route.params.id || route.query.sessionId
  if (sessionId) {
    // 已有 sessionId，直接加载会话信息
    loadSessionInfo()
  } else {
    // 没有 sessionId，先显示简历选择弹窗
    showResumeSelectDialog()
  }
  
  // 初始化雷达图
  nextTick(() => {
    initRadarChart()
  })
  
  // 定时刷新在线用户
  setInterval(loadOnlineUsers, 10000)
  
  // 窗口大小变化时重新渲染图表
  window.addEventListener('resize', () => {
    radarChart?.resize()
  })
})

onUnmounted(() => {
  // 清理资源
  if (stompClient) {
    stompClient.disconnect()
  }
  if (radarChart) {
    radarChart.dispose()
  }
  // 关闭摄像头
  stopCamera()

  // 停止语音识别
  if (isRecording.value) {
    stopVoiceRecognition()
  }
})
</script>

<style scoped lang="scss">
.interview-session-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: transparent;
  color: #fff;
}

.session-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  background: transparent;
  backdrop-filter: blur(10px);
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .session-code, .job-position {
      font-size: 14px;
      opacity: 0.9;
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .online-count {
      display: flex;
      align-items: center;
      gap: 5px;
      font-size: 14px;
    }
  }
}

.session-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.main-area {
  flex: 1;
  position: relative;
  overflow: hidden;
}

// 视频容器（填充满整个主区域）
.video-container {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 100%;
  background: #000;
  overflow: hidden;
  
  .interview-video {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  // 弹幕层（覆盖在视频上）
  .danmaku-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    z-index: 5;
    overflow: hidden;
  }
  
  // 题目卡片（悬浮在视频上方）
  .question-overlay {
    position: absolute;
    top: 80px;
    left: 20px;
    right: 20px;
    max-width: 600px;
    z-index: 8;
    pointer-events: auto;
    
    .question-card {
      background: transparent;
      backdrop-filter: blur(15px);
      border-radius: 12px;
      padding: 20px;
      color: #fff;
      border: 1px solid rgba(255, 255, 255, 0.15);
      box-shadow: none;
      
      .question-header {
        display: flex;
        align-items: center;
        gap: 15px;
        margin-bottom: 12px;
        
        .question-seq {
          font-weight: bold;
          font-size: 14px;
          color: rgba(255, 255, 255, 0.9);
        }
      }
      
      .question-content {
        font-size: 16px;
        line-height: 1.6;
        min-height: 50px;
        color: rgba(255, 255, 255, 0.95);
      }
      
      .reference-answer {
        margin-top: 15px;
        padding-top: 12px;
        font-size: 13px;
        color: rgba(255, 255, 255, 0.7);
        line-height: 1.5;
        
        :deep(.el-divider__text) {
          color: rgba(255, 255, 255, 0.8);
        }
        
        :deep(.el-divider) {
          border-color: rgba(255, 255, 255, 0.2);
        }
      }
    }
  }
  
  // 视频控制按钮
  .video-controls {
    position: absolute;
    bottom: 20px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    gap: 15px;
    z-index: 10;
    
    :deep(.el-button) {
      width: 56px;
      height: 56px;
      font-size: 24px;
      background: transparent;
      border: 2px solid rgba(255, 255, 255, 0.3);
      backdrop-filter: blur(10px);
      transition: all 0.3s ease;
      
      &:hover {
        transform: scale(1.1);
        border-color: rgba(255, 255, 255, 0.6);
      }
    }
  }
  
  // 用户信息标签
  .user-info-tag {
    position: absolute;
    top: 20px;
    left: 20px;
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px 15px;
    background: transparent;
    backdrop-filter: blur(10px);
    border-radius: 25px;
    border: 1px solid rgba(255, 255, 255, 0.15);
    z-index: 10;
    
    .username {
      color: #fff;
      font-size: 14px;
      font-weight: 500;
      max-width: 120px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
  
  // 在线人数标签
  .online-count-tag {
    position: absolute;
    top: 20px;
    right: 20px;
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 15px;
    background: transparent;
    backdrop-filter: blur(10px);
    border-radius: 20px;
    border: 1px solid rgba(255, 255, 255, 0.15);
    z-index: 10;
    color: #fff;
    font-size: 14px;
    
    .el-icon {
      font-size: 16px;
    }
  }
  
  // 弹幕项样式
  .danmaku-item {
    position: absolute;
    right: 0;
    white-space: nowrap;
    padding: 8px 15px;
    background: transparent;
    border-radius: 20px;
    backdrop-filter: blur(5px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    animation: danmaku-move 8s linear forwards;
    
    .danmaku-speaker {
      color: #409eff;
      font-weight: bold;
      margin-right: 5px;
    }
    
    .danmaku-text {
      color: #fff;
    }
  }
  
  // 回答输入区（悬浮）
  .answer-area-overlay {
    position: absolute;
    bottom: 100px;
    left: 50%;
    transform: translateX(-50%);
    width: 90%;
    max-width: 700px;
    z-index: 15;
    
    :deep(.el-textarea__inner) {
      background: transparent;
      backdrop-filter: blur(15px);
      border: 1px solid rgba(255, 255, 255, 0.2);
      color: #fff;
      font-size: 15px;
      
      &::placeholder {
        color: rgba(255, 255, 255, 0.5);
      }
    }
    
    .answer-actions {
      margin-top: 12px;
      display: flex;
      justify-content: center;
    }
  }
  
  // HR操作区（悬浮）
  .hr-actions-overlay {
    position: absolute;
    bottom: 100px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    gap: 15px;
    z-index: 15;
    padding: 15px 25px;
    background: transparent;
    backdrop-filter: blur(10px);
    border-radius: 30px;
    border: 1px solid rgba(255, 255, 255, 0.15);
  }
}

.radar-chart-overlay {
  position: absolute;
  bottom: 20px;
  left: 20px;
  width: 320px;
  height: 320px;
  background: rgba(0, 0, 0, 0.25);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border-radius: 16px;
  padding: 10px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  z-index: 20;
  
  .radar-chart {
    width: 100%;
    height: 100%;
  }
  
  .total-score {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    pointer-events: none;
    
    .score-value {
      font-size: 28px;
      font-weight: bold;
      color: #409eff;
      text-shadow: 0 2px 12px rgba(0, 0, 0, 0.5);
    }
    
    .score-label {
      font-size: 11px;
      color: rgba(255, 255, 255, 0.85);
      margin-top: 2px;
      text-shadow: 0 1px 4px rgba(0, 0, 0, 0.4);
    }
  }
}

// 右侧历史记录面板
.history-panel {
  position: fixed;
  top: 250px;
  right: 20px;
  width: 350px;
  height: 600px;
  max-height: 700px;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(15px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  z-index: 25;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  
  .history-header {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 15px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    color: #fff;
    font-weight: 500;
    font-size: 14px;
    
    .el-icon {
      font-size: 18px;
    }
  }
  
  .history-content {
    flex: 1;
    overflow-y: auto;
    padding: 15px;
    
    &::-webkit-scrollbar {
      width: 6px;
    }
    
    &::-webkit-scrollbar-thumb {
      background: rgba(255, 255, 255, 0.2);
      border-radius: 3px;
      
      &:hover {
        background: rgba(255, 255, 255, 0.3);
      }
    }
    
    .history-item {
      margin-bottom: 20px;
      padding: 12px;
      background: rgba(255, 255, 255, 0.05);
      border-radius: 8px;
      border-left: 3px solid #409eff;
      transition: all 0.3s ease;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      &:hover {
        background: rgba(255, 255, 255, 0.08);
      }
      
      .question-section {
        margin-bottom: 12px;
        
        .section-header {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 8px;
          
          .question-type {
            font-size: 11px;
            color: rgba(255, 255, 255, 0.6);
          }
        }
        
        .question-text {
          font-size: 13px;
          line-height: 1.6;
          color: rgba(255, 255, 255, 0.9);
          padding-left: 8px;
        }
      }
      
      .answer-section {
        .section-header {
          margin-bottom: 8px;
        }
        
        .answer-text {
          font-size: 13px;
          line-height: 1.6;
          color: rgba(255, 255, 255, 0.85);
          padding: 10px;
          background: rgba(255, 255, 255, 0.05);
          border-radius: 6px;
          border: 1px solid rgba(255, 255, 255, 0.1);
        }
        
        &.empty-answer {
          padding-left: 8px;
        }
      }
    }
  }
}

.danmaku-enter-active,
.danmaku-leave-active {
  transition: all 0.5s ease;
}

.danmaku-enter-from,
.danmaku-leave-to {
  opacity: 0;
  transform: translateX(100vw);
}

@keyframes danmaku-move {
  from {
    transform: translateX(100vw);
  }
  to {
    transform: translateX(-100%);
  }
}
</style>
