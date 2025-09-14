<template>
  <!-- 顶部导航栏组件 -->
  <nav class="border-b bg-white px-4 py-3 flex items-center gap-3 shadow-sm sticky top-0 z-30">
    <!-- 左侧标题区域 -->
    <div class="flex items-center gap-3">
      <div class="flex items-center gap-2">
        <div class="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center">
          <span class="text-white font-bold text-sm">🤖</span>
        </div>
        <div 
          @click="goToHome" 
          :title="'点击跳转到github - ' + title"
          class="nav-title-button group relative px-3 py-2 mx-1 mb-1 rounded-lg cursor-pointer transition-all duration-300 border-2 transform bg-white border-gray-100 text-base font-bold text-gray-800"
        >
          {{ title }}
        </div>
      </div>
    </div>

    <!-- 中间状态指示器 -->
    <div class="flex-1 flex items-center justify-center">
      <div class="flex items-center gap-3 text-gray-600">
        <span class="w-3 h-3 bg-green-500 rounded-full animate-pulse"></span>
        <span class="font-medium">智能对话助手 - 实时流式交互体验</span>
        <span class="text-xl">🐏</span>
      </div>
    </div>

    <!-- 右侧操作区域 -->
    <div class="flex items-center gap-2">
      <!-- 可以在这里添加其他全局操作按钮 -->
      <div class="text-xs text-gray-500">
        <!-- 预留空间用于未来的全局操作 -->
      </div>
    </div>
  </nav>
</template>

<script setup>
/**
 * 顶部导航栏组件
 * 功能：显示应用标题和状态指示器
 */

// 定义标题
const title = 'AI Auto Agent By Jasonlat'

// 点击标题跳转到github的方法
const goToGithub = () => {
  // 跳转到github
  window.open('https://github.com/jasonlat-dot/ai-agent-station', '_blank')
}

// 注意：新建对话和清空对话功能已移动到侧边栏组件中
</script>

<style scoped>
/* 自定义悬停效果样式 */
.nav-title-button:hover {
  border-color: #3b82f6 !important; /* blue-500 */
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06) !important;
  transform: scale(1.02) !important;
  color: #1d4ed8 !important; /* blue-700 */
}

.nav-title-button {
  transition: all 0.3s ease !important;
}

/* 组件特定样式 */
.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  h1 {
    font-size: 1rem;
  }
  
  .flex-1 span {
    font-size: 0.875rem;
  }
  
  button {
    padding: 0.5rem;
  }
  
  button span {
    display: none;
  }
}

@media (max-width: 640px) {
  .flex-1 {
    display: none;
  }
}
</style>