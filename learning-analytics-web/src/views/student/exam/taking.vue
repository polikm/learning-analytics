<template>
  <div class="exam-taking-page">
    <!-- 顶部固定栏 -->
    <div class="exam-header">
      <div class="header-left">
        <el-button text style="color: #fff" @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <span class="exam-title">{{ examInfo.title || '在线测评' }}</span>
      </div>
      <div class="header-center">
        <div class="countdown" :class="{ 'countdown-warning': remainSeconds < 300 }">
          <el-icon><Clock /></el-icon>
          <span>{{ formatTime(remainSeconds) }}</span>
        </div>
      </div>
      <div class="header-right">
        <el-button type="danger" size="small" @click="handleSubmit">交卷</el-button>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="exam-body">
      <!-- 题目内容区 -->
      <div class="question-area">
        <div v-if="currentQuestion" class="question-content">
          <!-- 题号和题型 -->
          <div class="question-header">
            <span class="question-index">{{ currentIndex + 1 }} / {{ questions.length }}</span>
            <el-tag :type="questionTypeTag(currentQuestion.type)" size="small">
              {{ questionTypeText(currentQuestion.type) }}
            </el-tag>
            <span class="question-score">（{{ currentQuestion.score }}分）</span>
          </div>

          <!-- 题目内容 -->
          <div class="question-text" v-html="currentQuestion.content"></div>

          <!-- 选项区域 -->
          <div class="question-options">
            <!-- 单选题 -->
            <el-radio-group
              v-if="currentQuestion.type === 'single'"
              v-model="answers[currentQuestion.id]"
              class="option-group"
            >
              <el-radio
                v-for="(opt, idx) in currentQuestion.options"
                :key="idx"
                :value="opt.label"
                class="option-item"
              >
                <span class="option-label">{{ opt.label }}</span>
                <span class="option-text" v-html="opt.content"></span>
              </el-radio>
            </el-radio-group>

            <!-- 多选题 -->
            <el-checkbox-group
              v-else-if="currentQuestion.type === 'multiple'"
              v-model="multipleAnswers[currentQuestion.id]"
              class="option-group"
            >
              <el-checkbox
                v-for="(opt, idx) in currentQuestion.options"
                :key="idx"
                :label="opt.label"
                :value="opt.label"
                class="option-item"
              >
                <span class="option-label">{{ opt.label }}</span>
                <span class="option-text" v-html="opt.content"></span>
              </el-checkbox>
            </el-checkbox-group>

            <!-- 判断题 -->
            <el-radio-group
              v-else-if="currentQuestion.type === 'judge'"
              v-model="answers[currentQuestion.id]"
              class="option-group"
            >
              <el-radio value="true" class="option-item">
                <el-icon color="#67C23A"><CircleCheck /></el-icon>
                <span>正确</span>
              </el-radio>
              <el-radio value="false" class="option-item">
                <el-icon color="#F56C6C"><CircleClose /></el-icon>
                <span>错误</span>
              </el-radio>
            </el-radio-group>

            <!-- 填空题 -->
            <el-input
              v-else-if="currentQuestion.type === 'fill'"
              v-model="answers[currentQuestion.id]"
              placeholder="请输入答案"
              size="large"
            />

            <!-- 简答题 -->
            <el-input
              v-else-if="currentQuestion.type === 'essay'"
              v-model="answers[currentQuestion.id]"
              type="textarea"
              :rows="6"
              placeholder="请输入答案"
              resize="vertical"
            />
          </div>
        </div>
        <el-empty v-else description="加载题目中..." />

        <!-- 上一题/下一题 -->
        <div class="question-nav-bottom">
          <el-button :disabled="currentIndex === 0" @click="prevQuestion">
            <el-icon><ArrowLeft /></el-icon> 上一题
          </el-button>
          <el-button v-if="currentIndex < questions.length - 1" type="primary" @click="nextQuestion">
            下一题 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- 题目导航 -->
      <div class="question-sidebar">
        <el-card shadow="never" class="nav-card">
          <template #header>
            <div class="flex-between">
              <span class="nav-title">答题卡</span>
              <span class="nav-progress">{{ answeredCount }} / {{ questions.length }}</span>
            </div>
          </template>
          <div class="nav-grid">
            <div
              v-for="(q, idx) in questions"
              :key="q.id"
              class="nav-item"
              :class="{
                'nav-answered': isAnswered(q.id),
                'nav-marked': markedSet.has(q.id),
                'nav-current': idx === currentIndex,
              }"
              @click="jumpToQuestion(idx)"
            >
              {{ idx + 1 }}
            </div>
          </div>
          <div class="nav-legend">
            <div class="legend-item">
              <span class="legend-dot dot-answered"></span>
              <span>已答</span>
            </div>
            <div class="legend-item">
              <span class="legend-dot dot-unanswered"></span>
              <span>未答</span>
            </div>
            <div class="legend-item">
              <span class="legend-dot dot-marked"></span>
              <span>标记</span>
            </div>
          </div>
          <div class="nav-actions">
            <el-button
              size="small"
              :type="markedSet.has(currentQuestion?.id) ? 'warning' : 'default'"
              @click="toggleMark"
              :disabled="!currentQuestion"
            >
              <el-icon><Flag /></el-icon>
              {{ markedSet.has(currentQuestion?.id) ? '取消标记' : '标记本题' }}
            </el-button>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 自动保存提示 -->
    <transition name="fade">
      <div v-if="showSaveTip" class="save-tip">
        <el-icon><Check /></el-icon>
        答案已自动保存
      </div>
    </transition>

    <!-- 交卷确认弹窗 -->
    <el-dialog v-model="submitVisible" title="交卷确认" width="360px" center>
      <div class="submit-confirm">
        <el-icon :size="48" color="#E6A23C"><WarningFilled /></el-icon>
        <p class="confirm-text">确定要提交试卷吗？</p>
        <p class="confirm-info">
          已答 <strong>{{ answeredCount }}</strong> 题，共 <strong>{{ questions.length }}</strong> 题
          <span v-if="unansweredCount > 0" class="confirm-warn">
            ，还有 {{ unansweredCount }} 题未作答
          </span>
        </p>
      </div>
      <template #footer>
        <el-button @click="submitVisible = false">继续答题</el-button>
        <el-button type="primary" @click="confirmSubmit">确认交卷</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getExamDetail, startExam, submitExam, saveAnswer } from '@/api/exam'

const route = useRoute()
const router = useRouter()
const examId = Number(route.params.examId)

const examInfo = ref<any>({})
const questions = ref<any[]>([])
const currentIndex = ref(0)
const answers = reactive<Record<string, any>>({})
const multipleAnswers = reactive<Record<string, string[]>>({})
const markedSet = ref<Set<number>>(new Set())
const remainSeconds = ref(3600)
const submitVisible = ref(false)
const showSaveTip = ref(false)
const recordId = ref(0)

let countdownTimer: ReturnType<typeof setInterval> | null = null
let autoSaveTimer: ReturnType<typeof setInterval> | null = null

const currentQuestion = computed(() => questions.value[currentIndex.value] || null)

const answeredCount = computed(() => {
  return questions.value.filter((q) => isAnswered(q.id)).length
})

const unansweredCount = computed(() => {
  return questions.value.length - answeredCount.value
})

function isAnswered(questionId: number) {
  if (multipleAnswers[questionId]) {
    return multipleAnswers[questionId].length > 0
  }
  return !!answers[questionId]
}

function questionTypeText(type: string) {
  const map: Record<string, string> = {
    single: '单选题', multiple: '多选题', judge: '判断题', fill: '填空题', essay: '简答题',
  }
  return map[type] || type
}

function questionTypeTag(type: string) {
  const map: Record<string, string> = {
    single: 'primary', multiple: 'success', judge: 'warning', fill: 'info', essay: '',
  }
  return map[type] || 'info'
}

function formatTime(seconds: number) {
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

function prevQuestion() {
  if (currentIndex.value > 0) currentIndex.value--
}

function nextQuestion() {
  if (currentIndex.value < questions.value.length - 1) currentIndex.value++
}

function jumpToQuestion(idx: number) {
  currentIndex.value = idx
}

function toggleMark() {
  if (!currentQuestion.value) return
  const id = currentQuestion.value.id
  if (markedSet.value.has(id)) {
    markedSet.value.delete(id)
  } else {
    markedSet.value.add(id)
  }
}

function handleBack() {
  ElMessage.warning('测评进行中，请先交卷再离开')
}

function handleSubmit() {
  submitVisible.value = true
}

async function confirmSubmit() {
  try {
    await submitExam(examId)
    ElMessage.success('交卷成功')
    submitVisible.value = false
    router.push('/student/scores')
  } catch (e) {
    ElMessage.error('交卷失败，请重试')
  }
}

async function autoSave() {
  try {
    for (const q of questions.value) {
      const answer = multipleAnswers[q.id]
        ? JSON.stringify(multipleAnswers[q.id])
        : answers[q.id]
      if (answer) {
        await saveAnswer(examId, {
          recordId: recordId.value,
          questionId: q.id,
          studentAnswer: String(answer),
        })
      }
    }
    showSaveTip.value = true
    setTimeout(() => { showSaveTip.value = false }, 2000)
  } catch (e) {
    console.error('自动保存失败', e)
  }
}

function startCountdown() {
  countdownTimer = setInterval(() => {
    if (remainSeconds.value <= 0) {
      clearInterval(countdownTimer!)
      ElMessage.warning('时间到，系统已自动交卷')
      confirmSubmit()
      return
    }
    remainSeconds.value--
  }, 1000)
}

async function loadExam() {
  try {
    const res = await getExamDetail(examId)
    examInfo.value = res.data || {}
    remainSeconds.value = (res.data?.duration || 60) * 60

    // 模拟题目数据（实际应从试卷接口获取）
    questions.value = [
      {
        id: 1, type: 'single', score: 5,
        content: '下列哪个数是质数？',
        options: [
          { label: 'A', content: '4' },
          { label: 'B', content: '6' },
          { label: 'C', content: '7' },
          { label: 'D', content: '9' },
        ],
      },
      {
        id: 2, type: 'multiple', score: 5,
        content: '下列哪些是中国的四大发明？',
        options: [
          { label: 'A', content: '造纸术' },
          { label: 'B', content: '蒸汽机' },
          { label: 'C', content: '印刷术' },
          { label: 'D', content: '指南针' },
        ],
      },
      {
        id: 3, type: 'judge', score: 5,
        content: '地球是太阳系中最大的行星。',
        options: [],
      },
      {
        id: 4, type: 'fill', score: 5,
        content: '中国的首都是____。',
        options: [],
      },
      {
        id: 5, type: 'essay', score: 10,
        content: '请简述水的三种状态及其特点。',
        options: [],
      },
    ]

    const startRes = await startExam(examId)
    if (startRes.data) {
      recordId.value = startRes.data.id || startRes.data.recordId || 0
    }
    startCountdown()
  } catch (e) {
    ElMessage.error('加载测评失败')
    console.error(e)
  }
}

onMounted(() => {
  loadExam()
  autoSaveTimer = setInterval(autoSave, 30000)
})

onBeforeUnmount(() => {
  if (countdownTimer) clearInterval(countdownTimer)
  if (autoSaveTimer) clearInterval(autoSaveTimer)
})
</script>

<style scoped>
.exam-taking-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #F2F3F5;
  overflow: hidden;
}

/* 顶部固定栏 */
.exam-header {
  height: 56px;
  background: linear-gradient(135deg, #303133 0%, #1a1a2e 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  flex-shrink: 0;
  z-index: 10;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}
.exam-title {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}
.header-center {
  display: flex;
  align-items: center;
}
.countdown {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #67C23A;
  font-size: 24px;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}
.countdown-warning {
  color: #F56C6C;
  animation: blink 1s ease-in-out infinite;
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* 主内容区 */
.exam-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 题目内容区 */
.question-area {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
}
.question-content {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}
.question-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}
.question-index {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
.question-score {
  font-size: 13px;
  color: #909399;
}
.question-text {
  font-size: 15px;
  color: #303133;
  line-height: 1.8;
  margin-bottom: 20px;
}

.question-options {
  margin-top: 8px;
}
.option-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}
.option-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px 16px;
  border: 1px solid #E4E7ED;
  border-radius: 8px;
  transition: all 0.2s;
  width: 100%;
}
.option-item:hover {
  border-color: #409EFF;
  background: rgba(64, 158, 255, 0.04);
}
.option-label {
  font-weight: 600;
  color: #409EFF;
  flex-shrink: 0;
  width: 20px;
}
.option-text {
  color: #303133;
  line-height: 1.6;
}

.question-nav-bottom {
  display: flex;
  justify-content: space-between;
  padding: 16px 0 8px;
}

/* 题目导航 */
.question-sidebar {
  width: 240px;
  flex-shrink: 0;
  padding: 16px 16px 16px 0;
  overflow-y: auto;
}
.nav-card {
  border-radius: 8px;
  position: sticky;
  top: 16px;
}
.nav-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}
.nav-progress {
  font-size: 13px;
  color: #409EFF;
  font-weight: 600;
}
.nav-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
  margin-bottom: 12px;
}
.nav-item {
  width: 100%;
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  border: 1px solid #E4E7ED;
  background: #F8F9FB;
  color: #909399;
  transition: all 0.2s;
}
.nav-item:hover {
  border-color: #409EFF;
}
.nav-answered {
  background: #67C23A;
  border-color: #67C23A;
  color: #fff;
}
.nav-marked {
  background: #E6A23C;
  border-color: #E6A23C;
  color: #fff;
}
.nav-current {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.3);
}

.nav-legend {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  padding: 8px 0;
  border-top: 1px solid #F2F3F5;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}
.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 3px;
}
.dot-answered { background: #67C23A; }
.dot-unanswered { background: #F2F3F5; border: 1px solid #E4E7ED; }
.dot-marked { background: #E6A23C; }

.nav-actions {
  display: flex;
  gap: 8px;
}

/* 自动保存提示 */
.save-tip {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(103, 194, 58, 0.9);
  color: #fff;
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
  z-index: 100;
}

/* 交卷确认 */
.submit-confirm {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 16px 0;
}
.confirm-text {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.confirm-info {
  font-size: 14px;
  color: #606266;
}
.confirm-warn {
  color: #F56C6C;
  font-weight: 500;
}

@media (max-width: 768px) {
  .question-sidebar {
    display: none;
  }
  .exam-header {
    padding: 0 8px;
  }
  .exam-title {
    font-size: 14px;
    max-width: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
