<template>
  <!-- 双面板内容区域 -->
  <div class="flex-1 flex overflow-hidden">
    <!-- 左侧思考过程面板 -->
    <div class="w-1/2 flex flex-col border-r border-gray-200 bg-white">
      <!-- 面板标题 -->
      <div class="px-4 py-3 bg-gray-50 border-b border-gray-200 flex items-center gap-2">
        <span class="text-lg">🧠</span>
        <h3 class="font-semibold text-gray-800">AI思考执行过程</h3>
      </div>
      
      <!-- 思考消息列表 -->
      <div 
        ref="thinkingContainer"
        class="flex-1 overflow-y-auto p-4 space-y-4 custom-scrollbar"
      >
        <!-- 欢迎消息 -->
        <div v-if="thinkingMessages.length === 0" class="flex items-start gap-3">
          <Avatar 
            name="AI"
            type="ai"
            :size="32"
          />
          <div class="flex-1 bg-primary-50 rounded-lg p-3">
            <div class="text-sm text-primary-800">
              <strong>AI助手:</strong> 您好！我是AI Auto Agent智能对话助手，请在右侧输入您的问题，我将在这里展示思考和执行过程。
            </div>
          </div>
        </div>
        
        <!-- 思考过程消息 -->
        <MessageItem 
          v-for="message in thinkingMessages" 
          :key="message.id"
          :message="message"
        />
      </div>
    </div>

    <!-- 右侧结果面板 -->
    <div class="w-1/2 flex flex-col bg-white">
      <!-- 面板标题 -->
      <div class="px-4 py-3 bg-gray-50 border-b border-gray-200 flex items-center gap-2">
        <span class="text-lg">📋</span>
        <h3 class="font-semibold text-gray-800">最终执行结果</h3>
      </div>
      
      <!-- 结果消息列表 -->
      <div 
        ref="resultContainer"
        class="flex-1 overflow-y-auto p-4 space-y-4 custom-scrollbar"
      >
        <!-- 欢迎消息 -->
        <div v-if="resultMessages.length === 0" class="flex items-start gap-3">
          <Avatar 
            name="AI"
            type="ai"
            :size="32"
          />
          <div class="flex-1 bg-primary-50 rounded-lg p-3">
            <div class="text-sm text-primary-800">
              <strong>AI助手:</strong> 这里将显示AI的最终执行结果和总结。
            </div>
          </div>
        </div>
        
        <!-- 结果消息 -->
        <MessageItem 
          v-for="message in resultMessages" 
          :key="message.id"
          :message="message"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import MessageItem from './MessageItem.vue'
import Avatar from './Avatar.vue'

/**
 * 双面板组件
 * 功能：左侧显示AI思考过程，右侧显示最终结果
 */

// Props
const props = defineProps({
  thinkingMessages: {
    type: Array,
    default: () => []
  },
  resultMessages: {
    type: Array,
    default: () => []
  }
})

// 事件
const emit = defineEmits(['scroll-to-bottom'])

// 引用
const thinkingContainer = ref(null)
const resultContainer = ref(null)

/**
 * 滚动到指定容器的底部
 * @param {HTMLElement} container - 容器元素
 */
const scrollToBottom = (container) => {
  if (container) {
    container.scrollTop = container.scrollHeight
  }
}

/**
 * 滚动思考面板到底部
 */
const scrollThinkingToBottom = () => {
  nextTick(() => {
    scrollToBottom(thinkingContainer.value)
  })
}

/**
 * 滚动结果面板到底部
 */
const scrollResultToBottom = () => {
  nextTick(() => {
    scrollToBottom(resultContainer.value)
  })
}

/**
 * 滚动两个面板到底部
 */
const scrollBothToBottom = () => {
  nextTick(() => {
    scrollToBottom(thinkingContainer.value)
    scrollToBottom(resultContainer.value)
  })
}

// 监听思考消息变化，自动滚动到底部
watch(
  () => props.thinkingMessages,
  () => {
    scrollThinkingToBottom()
  },
  { deep: true }
)

// 监听结果消息变化，自动滚动到底部
watch(
  () => props.resultMessages,
  () => {
    scrollResultToBottom()
  },
  { deep: true }
)

// 暴露方法给父组件
defineExpose({
  scrollThinkingToBottom,
  scrollResultToBottom,
  scrollBothToBottom
})
</script>

<style scoped>
/* 滚动条样式 */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}

.custom-scrollbar {
  scrollbar-width: thin;
  scrollbar-color: #d1d5db transparent;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .flex {
    flex-direction: column;
  }
  
  .w-1\/2 {
    width: 100% !important;
    height: 50%;
  }
  
  .border-r {
    border-right: none;
    border-bottom: 1px solid #e5e7eb;
  }
}

/* 面板过渡效果 */
.flex-1 {
  transition: all 0.3s ease;
}

/* 空间分布 */
.space-y-4 > * + * {
  margin-top: 1rem;
}

/* 边框样式 */
.border-gray-200 {
  border-color: #e5e7eb;
}

/* 背景色 */
.bg-gray-50 {
  background-color: #f9fafb;
}

.bg-white {
  background-color: #ffffff;
}

.bg-primary-50 {
  background-color: #eff6ff;
}

.bg-primary-600 {
  background-color: #2563eb;
}

/* 文本颜色 */
.text-gray-800 {
  color: #1f2937;
}

.text-primary-800 {
  color: #1e40af;
}

.text-white {
  color: #ffffff;
}

/* 字体样式 */
.font-semibold {
  font-weight: 600;
}

.font-bold {
  font-weight: 700;
}

.text-sm {
  font-size: 0.875rem;
}

.text-lg {
  font-size: 1.125rem;
}

/* 间距 */
.p-3 {
  padding: 0.75rem;
}

.p-4 {
  padding: 1rem;
}

.px-4 {
  padding-left: 1rem;
  padding-right: 1rem;
}

.py-3 {
  padding-top: 0.75rem;
  padding-bottom: 0.75rem;
}

.gap-2 {
  gap: 0.5rem;
}

.gap-3 {
  gap: 0.75rem;
}

/* 尺寸 */
.w-8 {
  width: 2rem;
}

.h-8 {
  height: 2rem;
}

.w-1\/2 {
  width: 50%;
}

/* 圆角 */
.rounded-lg {
  border-radius: 0.5rem;
}

.rounded-full {
  border-radius: 9999px;
}

/* 弹性布局 */
.flex {
  display: flex;
}

.flex-1 {
  flex: 1;
}

.flex-col {
  flex-direction: column;
}

.flex-shrink-0 {
  flex-shrink: 0;
}

.items-center {
  align-items: center;
}

.items-start {
  align-items: flex-start;
}

.justify-center {
  justify-content: center;
}

/* 溢出处理 */
.overflow-hidden {
  overflow: hidden;
}

.overflow-y-auto {
  overflow-y: auto;
}

/* 边框 */
.border-r {
  border-right-width: 1px;
}

.border-b {
  border-bottom-width: 1px;
}
</style>