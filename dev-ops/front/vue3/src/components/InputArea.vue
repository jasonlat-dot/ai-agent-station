<template>
  <!-- 底部输入区域 -->
  <div class="border-t border-gray-200 bg-white p-4 max-w-3xl mx-auto">
    <!-- 加载状态 -->
    <div v-if="isLoading" class="mb-4 flex items-center justify-center py-4">
      <div class="flex items-center gap-3">
        <!-- 旋转的小圆圈 -->
        <div class="loading-circle"></div>
        <span class="text-gray-600">AI正在思考中，请稍候...</span>
      </div>
    </div>

    <!-- 控制面板 -->
    <div class="mb-4 flex flex-wrap items-center gap-4 p-3 bg-gray-50 rounded-lg">
      <!-- 智能体类型选择 -->
      <div class="flex items-center gap-2">
        <label for="aiAgentSelect" class="text-sm font-medium text-gray-700">智能体类型</label>
        <select 
          id="aiAgentSelect" 
          v-model="selectedAgent"
          class="px-4 py-3 border border-gray-300 rounded-lg text-base focus:ring-2 focus:ring-primary-300 focus:border-primary-500 outline-none transition-all duration-200"
        >
          <option 
            v-for="option in agentOptions" 
            :key="option.value" 
            :value="option.value"
          >
            {{ option.label }}
          </option>
        </select>
      </div>

      <!-- 最大执行步数选择 -->
      <div class="flex items-center gap-2">
        <label for="maxStepSelect" class="text-sm font-medium text-gray-700">最大执行步数</label>
        <select 
          id="maxStepSelect" 
          v-model="maxSteps"
          class="px-4 py-3 border border-gray-300 rounded-lg text-base focus:ring-2 focus:ring-primary-300 focus:border-primary-500 outline-none transition-all duration-200"
        >
          <option 
            v-for="option in maxStepsOptions" 
            :key="option.value" 
            :value="option.value"
          >
            {{ option.label }}
          </option>
        </select>
      </div>

      <!-- 提问案例选择 -->
      <div class="flex items-center gap-2">
        <label for="exampleSelect" class="text-sm font-medium text-gray-700">提问案例</label>
        <select 
          id="exampleSelect" 
          v-model="selectedExample"
          @change="handleExampleChange"
          class="px-4 py-3 border border-gray-300 rounded-lg text-base focus:ring-2 focus:ring-primary-300 focus:border-primary-500 outline-none transition-all duration-200"
        >
          <option 
            v-for="option in exampleOptions" 
            :key="option.value" 
            :value="option.value"
          >
            {{ option.label }}
          </option>
        </select>
      </div>
    </div>

    <!-- 输入框和发送按钮 -->
    <div class="flex gap-3">
      <input 
        ref="messageInput"
        type="text" 
        v-model="message"
        @keypress="handleKeyPress"
        placeholder="请输入您的问题..." 
        maxlength="1000"
        :disabled="isLoading"
        class="flex-1 px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-300 focus:border-primary-500 outline-none transition-all duration-200 text-gray-700 placeholder-gray-400 disabled:opacity-50 disabled:cursor-not-allowed"
      >
      <button 
        @click="handleSend"
        :disabled="isLoading || !message.trim()"
        class="px-6 py-3 bg-primary-600 text-white rounded-lg hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 transition-colors duration-200 font-medium flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
        </svg>
        发送
      </button>
    </div>


  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { agentOptions, maxStepsOptions, exampleOptions, getDefaultValues } from '@/config/selectOptions.js'

/**
 * 底部输入区域组件
 * 功能：包含控制面板、消息输入和发送功能
 */

// Props
const props = defineProps({
  isLoading: {
    type: Boolean,
    default: false
  },
  sessionId: {
    type: String,
    default: ''
  }
})

// 事件
const emit = defineEmits(['send-message'])

// 响应式数据
const message = ref('')
const messageInput = ref(null)

// 从配置文件获取默认值
const defaultValues = getDefaultValues()
const selectedAgent = ref(defaultValues.selectedAgent)
const maxSteps = ref(defaultValues.maxSteps)
const selectedExample = ref(defaultValues.selectedExample)

/**
 * 处理发送消息
 */
const handleSend = () => {
  const messageText = message.value.trim()
  if (!messageText || props.isLoading) {
    return
  }

  // 构建请求数据
  const requestData = {
    aiAgentId: selectedAgent.value,
    message: messageText,
    sessionId: props.sessionId,
    maxStep: parseInt(maxSteps.value)
  }

  // 发送消息事件
  emit('send-message', requestData)

  // 清空输入框
  message.value = ''
}

/**
 * 处理键盘事件
 * @param {KeyboardEvent} event - 键盘事件
 */
const handleKeyPress = (event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    handleSend()
  }
}

/**
 * 处理示例选择变化
 */
const handleExampleChange = () => {
  if (selectedExample.value) {
    message.value = selectedExample.value
    selectedExample.value = ''
    // 聚焦输入框
    nextTick(() => {
      messageInput.value?.focus()
    })
  }
}

/**
 * 聚焦输入框
 */
const focusInput = () => {
  nextTick(() => {
    messageInput.value?.focus()
  })
}

/**
 * 清空输入框
 */
const clearInput = () => {
  message.value = ''
  focusInput()
}

// 组件挂载后聚焦输入框
onMounted(() => {
  focusInput()
})

// 暴露方法给父组件
defineExpose({
  focusInput,
  clearInput
})
</script>

<style scoped>
/* 加载动画 */
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.animate-spin {
  animation: spin 1s linear infinite;
}

/* 过渡效果 */
.transition-all {
  transition: all 0.2s ease;
}

.transition-colors {
  transition-property: color, background-color, border-color;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 200ms;
}

/* 焦点样式 */
.focus\:ring-2:focus {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.3);
}

.focus\:ring-primary-300:focus {
  box-shadow: 0 0 0 2px rgba(147, 197, 253, 0.5);
}

.focus\:border-primary-500:focus {
  border-color: #3b82f6;
}

.focus\:ring-primary-500:focus {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.5);
}

.focus\:ring-offset-2:focus {
  box-shadow: 0 0 0 2px #ffffff, 0 0 0 4px rgba(59, 130, 246, 0.5);
}

/* 悬停效果 */
.hover\:bg-primary-700:hover {
  background-color: #1d4ed8;
}

/* 禁用状态 */
.disabled\:opacity-50:disabled {
  opacity: 0.5;
}

.disabled\:cursor-not-allowed:disabled {
  cursor: not-allowed;
}

/* 颜色样式 */
.text-primary-600 {
  color: #2563eb;
}

.text-gray-700 {
  color: #374151;
}

.text-gray-500 {
  color: #6b7280;
}

.text-gray-400 {
  color: #9ca3af;
}

.text-white {
  color: #ffffff;
}

.bg-white {
  background-color: #ffffff;
}

.bg-gray-50 {
  background-color: #f9fafb;
}

.bg-primary-600 {
  background-color: #2563eb;
}

.border-gray-200 {
  border-color: #e5e7eb;
}

.border-gray-300 {
  border-color: #d1d5db;
}

.placeholder-gray-400::placeholder {
  color: #9ca3af;
}

/* 字体样式 */
.font-medium {
  font-weight: 500;
}

.font-mono {
  font-family: ui-monospace, SFMono-Regular, monospace;
}

.text-sm {
  font-size: 0.875rem;
}

.text-xs {
  font-size: 0.75rem;
}

/* 间距 */
.p-3 {
  padding: 0.75rem;
}

.p-4 {
  padding: 1rem;
}

.px-3 {
  padding-left: 0.75rem;
  padding-right: 0.75rem;
}

.px-4 {
  padding-left: 1rem;
  padding-right: 1rem;
}

.px-6 {
  padding-left: 1.5rem;
  padding-right: 1.5rem;
}

.py-2 {
  padding-top: 0.5rem;
  padding-bottom: 0.5rem;
}

.py-3 {
  padding-top: 0.75rem;
  padding-bottom: 0.75rem;
}

.mb-4 {
  margin-bottom: 1rem;
}

.mt-3 {
  margin-top: 0.75rem;
}

.gap-2 {
  gap: 0.5rem;
}

.gap-3 {
  gap: 0.75rem;
}

.gap-4 {
  gap: 1rem;
}

/* 尺寸 */
.h-5 {
  height: 1.25rem;
  width: 1.25rem;
}

/* 圆角 */
.rounded-lg {
  border-radius: 0.5rem;
}

/* 弹性布局 */
.flex {
  display: flex;
}

.flex-1 {
  flex: 1;
}

.flex-wrap {
  flex-wrap: wrap;
}

.items-center {
  align-items: center;
}

.justify-center {
  justify-content: center;
}

/* 边框 */
.border {
  border-width: 1px;
}

.border-t {
  border-top-width: 1px;
}

/* 轮廓 */
.outline-none {
  outline: none;
}

/* 文本对齐 */
.text-center {
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .flex-wrap {
    flex-direction: column;
    align-items: stretch;
  }
  
  .flex-wrap > div {
    width: 100%;
    margin-bottom: 0.5rem;
  }
  
  .flex-wrap > div:last-child {
    margin-bottom: 0;
  }
  
  .gap-4 {
    gap: 0.5rem;
  }
}

@media (max-width: 640px) {
  .px-6 {
    padding-left: 1rem;
    padding-right: 1rem;
  }
  
  .text-sm {
    font-size: 0.8rem;
  }
}

/* Loading circle animation */
.loading-circle {
  width: 20px;
  height: 20px;
  border: 2px solid #e5e7eb;
  border-top: 2px solid #2563eb;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  display: inline-block;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>