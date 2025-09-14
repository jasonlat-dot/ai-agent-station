<template>
  <!-- 通知组件 -->
  <Teleport to="body">
    <div 
      v-if="visible" 
      :class="[
        'fixed top-4 right-4 z-50 max-w-sm w-full transform transition-all duration-300 ease-in-out',
        visible ? 'translate-x-0 opacity-100' : 'translate-x-full opacity-0'
      ]"
    >
      <div 
        :class="[
          'rounded-lg shadow-lg p-4 flex items-start gap-3',
          typeClasses[type]
        ]"
      >
        <!-- 图标 -->
        <div class="flex-shrink-0">
          <svg 
            v-if="type === 'success'" 
            class="h-5 w-5 text-green-600" 
            fill="none" 
            viewBox="0 0 24 24" 
            stroke="currentColor"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
          <svg 
            v-else-if="type === 'error'" 
            class="h-5 w-5 text-red-600" 
            fill="none" 
            viewBox="0 0 24 24" 
            stroke="currentColor"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
          <svg 
            v-else-if="type === 'warning'" 
            class="h-5 w-5 text-yellow-600" 
            fill="none" 
            viewBox="0 0 24 24" 
            stroke="currentColor"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
          </svg>
          <svg 
            v-else 
            class="h-5 w-5 text-blue-600" 
            fill="none" 
            viewBox="0 0 24 24" 
            stroke="currentColor"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        
        <!-- 内容 -->
        <div class="flex-1 min-w-0">
          <h4 v-if="title" class="text-sm font-semibold mb-1" :class="titleClasses[type]">
            {{ title }}
          </h4>
          <p class="text-sm" :class="messageClasses[type]">
            {{ message }}
          </p>
        </div>
        
        <!-- 关闭按钮 -->
        <button 
          @click="close"
          class="flex-shrink-0 text-gray-400 hover:text-gray-600 transition-colors duration-200"
        >
          <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

/**
 * 通知组件
 * 功能：显示操作结果通知，支持不同类型和自动关闭
 */

// Props
const props = defineProps({
  type: {
    type: String,
    default: 'info',
    validator: (value) => ['success', 'error', 'warning', 'info'].includes(value)
  },
  title: {
    type: String,
    default: ''
  },
  message: {
    type: String,
    required: true
  },
  duration: {
    type: Number,
    default: 3000
  },
  autoClose: {
    type: Boolean,
    default: true
  }
})

// 事件
const emit = defineEmits(['close'])

// 响应式数据
const visible = ref(false)
let timer = null

// 样式映射
const typeClasses = {
  success: 'bg-green-50 border border-green-200',
  error: 'bg-red-50 border border-red-200',
  warning: 'bg-yellow-50 border border-yellow-200',
  info: 'bg-blue-50 border border-blue-200'
}

const titleClasses = {
  success: 'text-green-800',
  error: 'text-red-800',
  warning: 'text-yellow-800',
  info: 'text-blue-800'
}

const messageClasses = {
  success: 'text-green-700',
  error: 'text-red-700',
  warning: 'text-yellow-700',
  info: 'text-blue-700'
}

/**
 * 显示通知
 */
const show = () => {
  visible.value = true
  
  if (props.autoClose && props.duration > 0) {
    timer = setTimeout(() => {
      close()
    }, props.duration)
  }
}

/**
 * 关闭通知
 */
const close = () => {
  visible.value = false
  
  if (timer) {
    clearTimeout(timer)
    timer = null
  }
  
  // 延迟触发关闭事件，等待动画完成
  setTimeout(() => {
    emit('close')
  }, 300)
}

// 组件挂载时显示
onMounted(() => {
  // 延迟显示，确保动画效果
  setTimeout(() => {
    show()
  }, 100)
})

// 组件卸载时清理定时器
onUnmounted(() => {
  if (timer) {
    clearTimeout(timer)
  }
})

// 暴露方法
defineExpose({
  show,
  close
})
</script>

<style scoped>
/* 过渡动画 */
.transition-all {
  transition: all 0.3s ease-in-out;
}

/* 确保在最顶层显示 */
.z-50 {
  z-index: 50;
}

/* 响应式设计 */
@media (max-width: 640px) {
  .fixed {
    left: 1rem;
    right: 1rem;
    top: 1rem;
  }
  
  .max-w-sm {
    max-width: none;
  }
}
</style>